package com.alexthekap.mvp_dagger_save_presenter_sample.ui.pin_code

import com.alexthekap.mvp_dagger_save_presenter_sample.data.repository.MainRepository
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.BasePresenter
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * created on 26.04.2021 12:31
 */
class ShowImagePresenter @Inject constructor(
    private val mainRepository: MainRepository
): BasePresenter<ShowImageView>() {

    fun fetchDbImageByJsonId(jsonId: Long) {
        mainRepository.getByJsonId(jsonId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view?.displayImage(it.img) },
                {
                    view?.displayImage(null)
                    logException(this@ShowImagePresenter, it) }
            ).addTo(disposable)
    }

    override fun onFinished() {
    }

    override fun onViewReady() {
    }
}