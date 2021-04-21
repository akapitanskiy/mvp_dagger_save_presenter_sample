package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.databinding.ActivityMainBinding
import com.alexthekap.mvp_dagger_save_presenter_sample.di.ComponentManager
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {

    @Inject
    lateinit var presenter: MainPresenter
    @Inject
    lateinit var adapter: MainAdapter

    private lateinit var b: ActivityMainBinding
    val llManager = LinearLayoutManager(this)

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
//        val llManager = LinearLayoutManager(this)
        b.recyclerView.layoutManager = llManager
        b.recyclerView.adapter = adapter

        b.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {



            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val totalItemCount = llManager.itemCount
                    val lastVisibleItem = llManager.findLastVisibleItemPosition()
//                    logMessage(this"MainActivityTag", "onScrolled: total = $totalItemCount last = $lastVisibleItem")
                    presenter.loadMore(lastVisibleItem)

//                    if ((presenter.state == State.DONE) && totalItemCount <= (lastVisibleItem + presenter.OFFSET)) {
//                        presenter.state = State.LOADING
//                        presenter.loadMore()
//                    }

                }
            }
        })
    }

}