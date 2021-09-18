package com.gh.comm.library.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gh.comm.library.live.BaseRepository
import com.gh.comm.library.live.RxResult
import com.gh.comm.library.live.interfaces.CallBack
import com.gh.lib.net.exception.ApiException


/**
 * 版本更新处理
 *
 * @author gh
 */
class UpdateApkUtil {
   /* companion object {
        fun Update(context: AppCompatActivity, callBack: CallBack?) {
            if (callBack == null) toastTest(context, "正在获取最新版本号信息，请稍等......")
            val baseRepository = BaseRepository()
            baseRepository.setmContext(context)
            baseRepository.sendPost("newestversion", null, VersionBean::class.java, object :
                CallBack {
                override fun onNext(result: RxResult) {
                    val bean: VersionBean = result.getResult()
                    if (bean.isUpdate(context) && bean.isAlert) {
                        UpdateDialog.Builder(context).setContent(bean).setUpdateInterface(object : UpdateInterface {
                            override fun succeed() {
                                callBack?.onNext(result)
                            }
                        }).show()
                    } else {
                        if (callBack != null) {
                            callBack.onNext(result)
                        } else {
                            toastTest(context, "您已经是最新版本了！！")
                        }
                    }
                }

                override fun onError(e: ApiException) {
                    if (callBack != null) {
                        callBack.onError(e)
                    } else {
                        toastTest(context, e.displayMessage)
                    }
                }
            })
        }

        private fun toastTest(context: Context, msg: String?) {
            Thread(Runnable {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    //放在UI线程弹Toast
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }).start()
        }
    }*/
}