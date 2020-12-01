package com.herry.test.app.pick

import android.net.Uri
import android.os.Environment
import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.BasePresent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

interface PickListContract {
    interface View : IMvpView<Presenter>, INodeRoot {
        fun onScreen(type: PickType)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun pick(type: PickType)
        abstract fun getToTakeTempFile(type: PickType): File?
        abstract fun getUriForFileProvider(file: File?): Uri?
        abstract fun picked(file: File, receivedUri: Uri?, type: PickType, success: Boolean)
    }

    enum class PickType {
        PICK_PHOTO,
        PICK_MOVIE,
        TAKE_PHOTO,
        TAKE_MOVIE
    }
}