package com.herry.test.app.base.nav

import android.content.Context
import android.os.Bundle
import com.herry.libs.app.activity_caller.module.ACError
import com.herry.libs.mvp.OnPresenter
import com.herry.libs.mvp.IMvpView

@Suppress("unused")
abstract class BaseNavView<V: IMvpView<P>, P: OnPresenter<V>>: BaseNavFragment(), IMvpView<P> {
    override var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.presenter = this.presenter ?: onCreatePresenter()
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

    override fun getViewContext(): Context? = context

    override fun error(throwable: Throwable) {
        activityCaller?.call(
            ACError.Caller(throwable) {
                onError(it)
            }
        )
    }

    open fun onError(throwable: Throwable) {

    }

    abstract fun onCreatePresenter(): P?

    abstract fun onCreatePresenterView(): V

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
        // implements show loading view to base fragment
    }

    override fun hideViewLoading(success: Boolean) {
        // implements hide loading view to base fragment
    }
}