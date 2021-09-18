package com.gh.comm.library.live.interfaces

import com.gh.comm.library.live.RxResult
import com.gh.lib.net.exception.ApiException

interface CallBack {
    /**
     * @param result
     */
    fun onNext(result: RxResult)

    /**
     * @param e
     */
    fun onError(e: ApiException)
}