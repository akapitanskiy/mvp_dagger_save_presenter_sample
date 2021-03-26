package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.databinding.ActivityMainBinding
import com.alexthekap.mvp_dagger_save_presenter_sample.di.ComponentManager
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.IView {

    @Inject
    lateinit var presenter: MainContract.IPresenter
    private lateinit var b: ActivityMainBinding
    private val adapter = MainAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.recyclerView.layoutManager = LinearLayoutManager(this)
        b.recyclerView.adapter = adapter
//        b.recyclerView.setHasFixedSize(true)

        ComponentManager.getMainActivityComponent().inject(this)
        adapter.setPresenter(presenter)
        presenter.bindView(this)
        presenter.isFirstLaunch(savedInstanceState == null)
        presenter.onViewReady()
        Log.d("MainActivityTag", "onCreate: ${savedInstanceState?.toString()?.substring(0, 100)}")
    }

    override fun updateList(list: List<HitPlusImgEntity>) {
        adapter.submitList(list)
        Log.d("MainActivityTag", "updateList: called. size ${list.size}")
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

}