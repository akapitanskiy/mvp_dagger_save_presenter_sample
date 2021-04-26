package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.databinding.ActivityMainBinding
import com.alexthekap.mvp_dagger_save_presenter_sample.di.ComponentManager
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.show_image.ShowImageActivity
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {

    @Inject
    lateinit var presenter: MainPresenter
    @Inject
    lateinit var adapter: MainAdapter

    private lateinit var b: ActivityMainBinding
    val layoutManager = GridLayoutManager(this, 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        ComponentManager.getMainActivityComponent().inject(this)

        initRecycler()

        presenter.bindView(this)
        presenter.isFirstLaunch(savedInstanceState == null)
        presenter.onViewReady()
        logMessage(this, "onCreate: bundle = ${savedInstanceState?.toString()?.substring(0, 100)}")
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }, 1000)
    }

    override fun updateList(list: List<HitPlusImgEntity>) {
        adapter.submitList(list)
        logMessage(this, "updateList: called. size ${list.size}")
    }

    override fun updateTimer(time: String) {
        b.tvTimer.text = time
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()

        if (isFinishing) {
            presenter.onFinished()
            ComponentManager.clearMainActivityComponent()
        }
    }

    private fun initRecycler() {
        b.recyclerView.layoutManager = layoutManager
        b.recyclerView.adapter = adapter

        b.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                logMessage(this@MainActivity,"onScrollStateChanged: ${presenter.state}")
                if (presenter.state != State.LOADING
                    && ( !recyclerView.canScrollVertically(1)
                      || !recyclerView.canScrollVertically(-1) )
                ) {
                    logMessage(this@MainActivity,"onScrollStateChanged: loadMore ${layoutManager.findLastVisibleItemPosition()}")
                    presenter.state = State.DONE
                    presenter.loadMore(layoutManager.findLastVisibleItemPosition())
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    presenter.loadMore(lastVisibleItem)
                }
            }
        })

        adapter.onItemClickListener = MainAdapter.OnItemClickListener { hit ->
            val intent = Intent(this@MainActivity, ShowImageActivity::class.java)
            intent.putExtra(ShowImageActivity.EXTRA_ID, hit.jsonId)
            startActivity(intent)
        }
    }

}