package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.databinding.ActivityMainBinding
import com.alexthekap.mvp_dagger_save_presenter_sample.di.ComponentManager
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.IView {

    private lateinit var b: ActivityMainBinding
    private val adapter = MainAdapter()

    @Inject
    lateinit var presenter: MainContract.IPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.recyclerView.layoutManager = LinearLayoutManager(this)
        b.recyclerView.adapter = adapter
//        b.recyclerView.setHasFixedSize(true)

        ComponentManager.getMainActivityComponent().inject(this)
        presenter.bindView(this)
        presenter.onViewReady(savedInstanceState == null)
        Log.d("MainRepositoryTag", "onCreate: ${savedInstanceState?.toString()?.substring(0, 100)}")
    }

    override fun updateList(list: List<PostEntity>) {
        adapter.submitList(list)
        Log.d("MainRepositoryTag", "updateList: called. size ${list.size}")
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