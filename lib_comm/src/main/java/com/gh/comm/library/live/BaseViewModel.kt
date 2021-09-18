package com.gh.comm.library.live

import android.app.Application
import com.gh.comm.library.live.interfaces.CallBack
import com.gh.lib.net.exception.ApiException
import com.gh.mylibrary.live.AbsViewModel


open class BaseViewModel<T : BaseRepository>(application: Application) : AbsViewModel<T>(application),
    CallBack {
    init {
        mRepository?.setCallBack(this)
    }
    override fun onNext(result: RxResult) {
        if (result.getResult<Any?>() == null) {
            succeed(result.msg)
        } else {
            postData(result)
        }
    }

    override fun onError(e: ApiException) {
        e.displayMessage?.let { kotlin.error(it) }
    }
}