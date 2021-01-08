package com.herry.test.app.checker.password_setting

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/7/7
 **/
interface PasswordSettingContract {

    interface View : IMvpView<Presenter>, INodeRoot

    abstract class Presenter : BasePresent<View>() {
        abstract fun refresh()
    }

    data class PasswordModel(
        val password: String
    )
}