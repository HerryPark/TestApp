package com.herry.test.app.checker.password_setting

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/7/7
 **/
interface PasswordSettingContract {

    interface View : MVPView<Presenter> {
        fun onDisplayPassword(password: String)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun setPassword(password: String?)
        abstract fun isChangedPassword(): Boolean
    }
}