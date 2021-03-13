package com.alexthekap.mvp_dagger_save_presenter_sample.utils


/**
 * created on 16.02.2021 12:35
 */
class AppConstants {



    enum class PinCodeMode {
        CREATE, CHECK, CHANGE
    }

    companion object {
        const val EXTRA_MODE = "mode"
        const val JSON_PLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com"
        const val PIXABAY_BASE_URL = "https://pixabay.com/api"
    }
}