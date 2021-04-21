package com.alexthekap.mvp_dagger_save_presenter_sample.utils

import android.util.Log

/**
 * created on 04.04.2021 15:51
 */
fun logException(any: Any, throwable: Throwable) {
    Log.d("Log ${any.javaClass.simpleName}", "err msg: ${throwable.message} $throwable")
}

fun logMessage(sourceClass: Any, msg: String) {
    Log.d("Log ${sourceClass.javaClass.simpleName}", "msg: $msg")
}