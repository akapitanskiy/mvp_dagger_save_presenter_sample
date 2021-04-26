package com.alexthekap.mvp_dagger_save_presenter_sample.ui.pin_code

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.alexthekap.mvp_dagger_save_presenter_sample.R
import com.alexthekap.mvp_dagger_save_presenter_sample.databinding.ActivityViewImageBinding
import com.alexthekap.mvp_dagger_save_presenter_sample.di.ComponentManager
import com.github.chrisbanes.photoview.OnPhotoTapListener
import javax.inject.Inject

class ShowImageActivity : AppCompatActivity(), ShowImageView {

    @Inject
    lateinit var presenter: ShowImagePresenter

    private lateinit var b: ActivityViewImageBinding
    var isFullScreen = false

    companion object {
        const val EXTRA_ID = "com.alexthekap.mvp_dagger_save_presenter_sample.ui.main.MainActivity.EXTRA_JSONID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityViewImageBinding.inflate(layoutInflater)
        setContentView(b.root)
        ComponentManager.getMainActivityComponent().inject(this)

//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        }
        b.photoView.setOnPhotoTapListener(object : OnPhotoTapListener {
            override fun onPhotoTap(view: ImageView?, x: Float, y: Float) {
                if (isFullScreen) {
//                    supportActionBar?.show()
                    showSystemUI()
                    isFullScreen = false
                } else {
//                    supportActionBar?.hide()
                    hideSystemUI()
                    isFullScreen = true
                }
            }
        })
        presenter.bindView(this)
        val jsonId = intent.getLongExtra(EXTRA_ID, -1)
        if (jsonId != -1L) {
            presenter.fetchDbImageByJsonId(jsonId)
        } else {
            b.photoView.setImageResource(R.drawable.ic_image_placeholder2)
        }
    }

    override fun displayImage(img: ByteArray?) {
        if (img != null) {
            b.photoView.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.size))
        } else {
            b.photoView.setImageResource(R.drawable.ic_image_placeholder2)
        }
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode. For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE. Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

}