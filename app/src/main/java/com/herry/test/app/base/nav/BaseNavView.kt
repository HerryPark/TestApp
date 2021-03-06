package com.herry.test.app.base.nav

import android.os.Bundle
import com.herry.libs.app.activity_caller.module.ACError
import com.herry.libs.mvp.MVPPresenter
import com.herry.libs.mvp.MVPPresenterViewModelFactory
import com.herry.libs.mvp.MVPView
import com.herry.libs.mvp.MVPViewCreation

@Suppress("unused")
abstract class BaseNavView<V: MVPView<P>, P: MVPPresenter<V>>: BaseNavFragment(), MVPView<P>, MVPViewCreation<V, P> {

    override var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.presenter = this.presenter ?: run {
            val viewModel = MVPPresenterViewModelFactory.create(this, this)
            viewModel?.presenter?.apply {
                reloaded(viewModel.reloaded)
            }
        }
        this.presenter?.onAttach(onCreatePresenterView()) ?: finishAndResults(null)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.onDetach()
    }

    override fun onResume() {
        super.onResume()

        presenter?.onLaunch()
    }

    override fun onPause() {
        presenter?.onPause()

        super.onPause()
    }

    override fun error(throwable: Throwable) {
        activityCaller?.call(
            ACError.Caller(throwable) {
                onError(it)
            }
        )
    }

    open fun onError(throwable: Throwable) {

    }

    override fun onTransitionStart() {
        super.onTransitionStart()

        if(presenter is BaseNavPresent<*>) {
            (presenter as BaseNavPresent<*>).navTransitionStart()
        }
    }

    override fun onTransitionEnd() {
        super.onTransitionEnd()

        if(presenter is BaseNavPresent<*>) {
            (presenter as BaseNavPresent<*>).navTransitionEnd()
        }
    }

    override fun showViewLoading() {
        showLoading()
    }

    override fun hideViewLoading(success: Boolean) {
        hideLoading()
    }
}