package com.aidanvii.github.bindingadapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.aidanvii.github.testutils.anyNullable
import com.aidanvii.github.testutils.prepareMockForTest
import com.aidanvii.github.testutils.verifyNoMoreRealInteractions
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GlideImageViewBindingAdaptersTest {

    val requestBuilder = mock<RequestBuilder<Drawable>>().apply {
        whenever(transition(any())).thenReturn(this)
        whenever(apply(any())).thenReturn(this)
    }

    val requestManager = mock<RequestManager>().apply {
        whenever(load(any<String>())).thenReturn(requestBuilder)
    }

    val requestOptions = mock<RequestOptions>().apply {
        whenever(placeholder(anyNullable<Drawable>())).thenReturn(this)
        whenever(diskCacheStrategy(any())).thenReturn(this)
        whenever(override(any(), any())).thenReturn(this)
    }

    private val tested = GlideImageViewBindingAdapters(
        glideWith = { requestManager },
        requestOptions = { requestOptions }
    )

    val imageView = mock<ImageView>().prepareMockForTest()

    var givenImageUrl: String? = null
    var givenPlaceholder: Drawable? = null

    private fun ImageView.invokeWithParams() {
        tested.apply {
            bind(givenImageUrl, givenPlaceholder)
        }
    }

    @Nested
    inner class `when imageUrl is null` {

        init {
            givenImageUrl = null
        }

        @BeforeEach
        fun beforeEach() {
            imageView.invokeWithParams()
        }

        @Test
        fun `nothing happens`() {
            imageView.verifyNoMoreRealInteractions()
        }
    }

    @Nested
    inner class `when imageUrl is empty` {

        init {
            givenImageUrl = ""
        }

        @BeforeEach
        fun beforeEach() {
            imageView.invokeWithParams()
        }

        @Test
        fun `nothing happens`() {
            imageView.verifyNoMoreRealInteractions()
        }
    }

    @Nested
    inner class `when imageUrl is not null and not empty` {

        val nonNullImageUrl = "nonNullImageUrl"

        init {
            givenImageUrl = nonNullImageUrl
        }

        @Nested
        inner class `when invoked` {

            @BeforeEach
            fun beforeEach() {
                imageView.invokeWithParams()
            }

            @Test
            fun `loads image url with correct configuration`() {
                inOrder(requestManager, requestBuilder, requestOptions).apply {
                    verify(requestManager).load(nonNullImageUrl)
                    verify(requestBuilder).transition(any())
                    verify(requestOptions).placeholder(null)
                    verify(requestOptions).diskCacheStrategy(DiskCacheStrategy.DATA)
                    verify(requestBuilder).into(imageView)
                }
            }
        }

        @Nested
        inner class `when placeholder is not null` {

            val expectedPlaceholder = mock<Drawable>()

            init {
                givenPlaceholder = expectedPlaceholder
            }

            @BeforeEach
            fun beforeEach() {
                imageView.invokeWithParams()
            }

            @Test
            fun `loads image url with correct configuration`() {
                inOrder(requestManager, requestBuilder, requestOptions).apply {
                    verify(requestManager).load(nonNullImageUrl)
                    verify(requestBuilder).transition(any())
                    verify(requestOptions).placeholder(expectedPlaceholder)
                    verify(requestOptions).diskCacheStrategy(DiskCacheStrategy.DATA)
                    verify(requestBuilder).into(imageView)
                }
            }
        }

        @Nested
        inner class `when imageUrl is null while an in-flight request is in progress and placeholder is null` {

            @BeforeEach
            fun beforeEach() {
                givenImageUrl = nonNullImageUrl
                imageView.invokeWithParams()
                givenImageUrl = null
                imageView.invokeWithParams()
            }

            @Test
            fun `pending request is cancelled`() {
                verify(requestManager).clear(imageView)
            }

            @Test
            fun `sets drawable as null`() {
                verify(imageView).setImageDrawable(null)
            }
        }

        @Nested
        inner class `when imageUrl is null while an in-flight request is in progress and placeholder is not null` {

            val expectedPlaceholder = mock<Drawable>()

            @BeforeEach
            fun beforeEach() {
                givenImageUrl = nonNullImageUrl
                givenPlaceholder = expectedPlaceholder
                imageView.invokeWithParams()
                givenImageUrl = null
                imageView.invokeWithParams()
            }

            @Test
            fun `pending request is cancelled`() {
                verify(requestManager).clear(imageView)
            }

            @Test
            fun `sets drawable as placeholder`() {
                verify(imageView).setImageDrawable(expectedPlaceholder)
            }
        }
    }
}