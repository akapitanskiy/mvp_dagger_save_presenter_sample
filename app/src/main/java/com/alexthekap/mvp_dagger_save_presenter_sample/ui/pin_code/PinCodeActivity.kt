package com.alexthekap.mvp_dagger_save_presenter_sample.ui.pin_code

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alexthekap.mvp_dagger_save_presenter_sample.databinding.ActivityPinCodeBinding
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.AppConstants

class PinCodeActivity : AppCompatActivity() {

    private lateinit var b: ActivityPinCodeBinding

    companion object {

        fun changePinCode(context: Context) {
            startPinCodeActivity(context, AppConstants.PinCodeMode.CHANGE)
        }

        fun createPinCode(context: Context) {
            startPinCodeActivity(context, AppConstants.PinCodeMode.CREATE)
        }

        private fun startPinCodeActivity(context: Context, pinCodeMode: AppConstants.PinCodeMode) {
            val intent = Intent(context, PinCodeActivity::class.java)
            intent.putExtra(AppConstants.EXTRA_MODE, pinCodeMode)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPinCodeBinding.inflate(layoutInflater)
        setContentView(b.root)



    }
}