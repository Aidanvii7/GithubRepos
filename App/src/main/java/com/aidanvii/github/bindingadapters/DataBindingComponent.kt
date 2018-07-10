package com.aidanvii.github.bindingadapters

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil

data class DataBindingComponent(
    private val imageViewBindingAdapters: ImageViewBindingAdapters

) : DataBindingComponent {
    fun makeDefaultComponent() {
        DataBindingUtil.setDefaultComponent(this)
    }
    override fun getImageViewBindingAdapters() = imageViewBindingAdapters
}