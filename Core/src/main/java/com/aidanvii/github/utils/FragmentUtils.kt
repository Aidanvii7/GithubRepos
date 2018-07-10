package com.aidanvii.github.utils

import android.support.v4.app.Fragment

inline fun <reified T> Fragment.appAs(): T = context!!.applicationContext as T