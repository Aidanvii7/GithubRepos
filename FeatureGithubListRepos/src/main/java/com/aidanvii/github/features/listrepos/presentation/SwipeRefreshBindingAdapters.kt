package com.aidanvii.github.features.listrepos.presentation

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout

@BindingAdapter("onRefresh")
internal fun SwipeRefreshLayout.bind(onRefreshListener: SwipeRefreshLayout.OnRefreshListener?) {
    setOnRefreshListener(onRefreshListener)
}

@BindingAdapter("refreshing")
internal fun SwipeRefreshLayout.bind(refreshing: Boolean?) {
    refreshing?.let { isRefreshing = it }
}