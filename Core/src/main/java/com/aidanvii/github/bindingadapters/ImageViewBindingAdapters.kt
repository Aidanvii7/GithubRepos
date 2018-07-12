package com.aidanvii.github.bindingadapters

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.aidanvii.github.core.R
import com.aidanvii.toolbox.databinding.trackValue

abstract class ImageViewBindingAdapters {

    @BindingAdapter(
        "imageUrl",
        "placeHolder", requireAll = false
    )
    fun ImageView.bind(
        imageUrl: String?,
        placeHolder: Drawable?
    ) {
        trackValue(
            newValue = if (imageUrl != null && imageUrl.isNotEmpty()) {
                ImageBindingParams(
                    imageUrl = imageUrl,
                    placeholder = placeHolder
                )
            } else null,
            valueResId = R.id.image_binding_params,
            onNewValue = { loadImage(it) },
            onOldValue = {
                cancelPendingRequest()
                setImageDrawable(placeHolder)
            }
        )
    }

    protected abstract fun ImageView.loadImage(imageViewParams: ImageBindingParams)

    protected abstract fun ImageView.cancelPendingRequest()

    protected data class ImageBindingParams(
        val imageUrl: String,
        val placeholder: Drawable?
    )
}