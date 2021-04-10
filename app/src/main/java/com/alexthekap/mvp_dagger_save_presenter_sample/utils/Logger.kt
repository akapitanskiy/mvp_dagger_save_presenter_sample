package com.alexthekap.mvp_dagger_save_presenter_sample.utils

import android.util.Log

/**
 * created on 04.04.2021 15:51
 */
fun logException(throwable: Throwable, any: Any) {
    Log.d("Logger", "${any.javaClass.simpleName} msg: ${throwable.message} $throwable")
}

fun logMessage(msg: String, sourceClass: Any) {
    Log.d(sourceClass.javaClass.simpleName, "message: $msg")
}