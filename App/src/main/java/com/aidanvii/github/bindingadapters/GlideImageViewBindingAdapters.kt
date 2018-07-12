package com.aidanvii.github.bindingadapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.VisibleForTesting
import android.widget.ImageView
import com.aidanvii.toolbox.Provider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions

typealias GlideWith = (context: Context) -> RequestManager

class GlideImageViewBindingAdapters(
    @param:VisibleForTesting
    private val glideWith: GlideWith = { Glide.with(it) },
    @param:VisibleForTesting
    private val requestOptions: Provider<RequestOptions> = { RequestOptions() }
) : ImageViewBindingAdapters() {

    override fun ImageView.loadImage(imageViewParams: ImageViewBindingAdapters.ImageBindingParams) {
        glideWith(context)
            .load(imageViewParams.imageUrl)
            .transition(withCrossFade())
            .applyOptionsFrom(imageViewParams)
            .into(this)
    }

    override fun ImageView.cancelPendingRequest() {
        glideWith(context).clear(this)
    }

    private fun RequestBuilder<Drawable>.applyOptionsFrom(newParams: ImageBindingParams): RequestBuilder<Drawable> =
        newParams.run {
            apply(
                requestOptions()
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            )
        }
}