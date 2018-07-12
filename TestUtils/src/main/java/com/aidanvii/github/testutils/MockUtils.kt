package com.aidanvii.github.testutils

import org.mockito.Mockito
import org.mockito.internal.util.MockUtil

fun Any.isMock() = MockUtil.isMock(this)

inline fun <reified T> anyNullable() = Mockito.nullable(T::class.java)