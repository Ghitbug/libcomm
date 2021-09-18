package com.gh.comm.library.live

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.gh.comm.library.common.JConstant
import com.gh.comm.library.entitys.UploadBean
import com.gh.comm.library.emum.UploadType
import com.gh.comm.library.live.interfaces.CallBack
import com.gh.comm.library.utils.AESOperator
import com.gh.lib.net.HttpBuilder
import com.gh.lib.net.HttpUtils
import com.gh.lib.net.exception.ApiException
import com.gh.lib.net.exception.CodeException
import com.gh.lib.net.interfaces.NetWorkResult
import com.gh.mylibrary.live.AbsRepository
import com.gh.mylibrary.utils.*

import kotlinx.coroutines.Job

import java.util.*
import kotlin.collections.HashMap

open class BaseRepository : AbsRepository() {
    private var jobs: ArrayList<Job> = arrayListOf()
    private var callBack: CallBack? = null

    /**
     * 多文件上传，返回list对象
     *
     * @param uploadType
     * @param paths
     * @param urls
     * @param listener
     */
    open fun uplaod(uploadType: UploadType, paths: MutableList<String?>, map: HashMap<String, Any>?, urls: MutableList<UploadBean?>, listener: CallBack) {
        val s = paths[0]
        uplaod(uploadType, s, map, object : CallBack {
            override fun onNext(result: RxResult) {
                urls.add(result.getResult())
                paths.remove(s)
                if (paths.size == 0) {
                    result.setResult(urls)
                    listener.onNext(result)
                } else {
                    uplaod(uploadType, paths, map, urls, listener)
                }
            }

            override fun onError(e: ApiException) {
                listener.onError(e)
            }
        })
    }

    open fun uplaod(uploadType: UploadType, paths: MutableList<String?>, urls: MutableList<UploadBean?>, listener: CallBack) {
        val s = paths[0]
        uplaod(uploadType, s, object : CallBack {
            override fun onNext(result: RxResult) {
                urls.add(result.getResult())
                paths.remove(s)
                if (paths.size == 0) {
                    result.setResult(urls)
                    listener.onNext(result)
                } else {
                    uplaod(uploadType, paths, urls, listener)
                }
            }

            override fun onError(e: ApiException) {
                listener.onError(e)
            }
        })
    }

    /**
     * 单文件上传返回UploadBean对象
     *
     * @param uploadType
     * @param paths
     * @param listener
     */
    open fun uplaod(uploadType: UploadType, paths: List<String?>, listener: CallBack) {
        val s = paths[0]
        uplaod(uploadType, s, object : CallBack {
            override fun onNext(result: RxResult) {
                listener.onNext(result)
            }

            override fun onError(e: ApiException) {
                listener.onError(e)
            }
        })
    }

    open fun uplaod(uploadType: UploadType, paths: List<String?>, map: HashMap<String, Any>?, listener: CallBack) {
        val s = paths[0]
        uplaod(uploadType, s, map, object : CallBack {
            override fun onNext(result: RxResult) {
                listener.onNext(result)
            }

            override fun onError(e: ApiException) {
                listener.onError(e)
            }
        })
    }

    /**
     * 多文件上传 最终返回string，通过，拼接
     *
     * @param uploadType
     * @param paths
     * @param sb
     * @param listener
     */
    open fun uplaod(uploadType: UploadType, paths: MutableList<String?>, map: HashMap<String, Any>?, sb: StringBuffer, listener: CallBack) {
        val s = paths[0]
        uplaod(uploadType, s, map, object : CallBack {
            override fun onNext(result: RxResult) {
                val uploadBean: UploadBean = result.getResult()
                sb.append(uploadBean.fileUrl).append(",")
                paths.remove(s)
                if (paths.size == 0) {
                    sb.delete(sb.length - 1, sb.length)
                    result.setResult(sb.toString())
                    listener.onNext(result)
                } else {
                    uplaod(uploadType, paths, map, sb, listener)
                }
            }

            override fun onError(e: ApiException) {
                listener.onError(e)
            }
        })
    }

    open fun uplaod(uploadType: UploadType, paths: MutableList<String?>, sb: StringBuffer, listener: CallBack) {
        val s = paths[0]
        uplaod(uploadType, s, object : CallBack {
            override fun onNext(result: RxResult) {
                val uploadBean: UploadBean = result.getResult()
                sb.append(uploadBean.fileUrl).append(",")
                paths.remove(s)
                if (paths.size == 0) {
                    sb.delete(sb.length - 1, sb.length)
                    result.setResult(sb.toString())
                    listener.onNext(result)
                } else {
                    uplaod(uploadType, paths, sb, listener)
                }
            }

            override fun onError(e: ApiException) {
                listener.onError(e)
            }
        })
    }

    open fun uplaod(uploadType: UploadType, path: String?, listener: CallBack?) {
        upload(uploadType.eurl, path, listener)
    }

    open fun uplaod(uploadType: UploadType, path: String?, map: HashMap<String, Any>?, listener: CallBack?) {
        upload(uploadType.eurl, path, map, listener)
    }

    open fun upload(method: String, path: String?, listener: CallBack?) {
        upload(method, path, null, listener)
    }

    open fun upload(method: String, path: String?, map: HashMap<String, Any>?, listener: CallBack?) {
        val builder = HttpBuilder.getBuilder(method)
        if (!RxDataTool.isNullString(path)) {
            builder.setFile("file", path!!)
        }
        if (map == null) builder.parameters = HashMap()
        send(builder, UploadBean::class.java, listener)
    }

    open fun sendPost(method: String, listener: CallBack?) {
        sendPost(method, false, listener)
    }

    open fun sendPost(method: String, isShowProgress: Boolean, listener: CallBack?) {
        sendPost(method, null, null, isShowProgress, listener)
    }

    open fun sendPost(method: String, parameters: JSONObject?, listener: CallBack?) {
        sendPost(method, parameters, null, false, listener)
    }

    open fun sendPost(method: String, parameters: JSONObject?, isShowProgress: Boolean, listener: CallBack?) {
        sendPost(method, parameters, null, isShowProgress, listener)
    }

    open fun sendPost(method: String, parameters: JSONObject?, cl: Class<*>?, listener: CallBack?) {
        sendPost(method, parameters, cl, false, listener)
    }

    open fun sendPost(method: String, parameters: JSONObject?, cl: Class<*>?, isShowProgress: Boolean, listener: CallBack?) {
        send(method, parameters, cl, isShowProgress, "", true, listener)
    }

    /**
     * @param method         调用方法
     * @param parameters     参数
     * @param cl             转换类型
     * @param isShowProgress 是否显示加载中提示
     * @param msg            加载中提示内容
     * @param isCancel
     * @param listener
     */
    open fun send(method: String, parameters: JSONObject?, cl: Class<*>?, isShowProgress: Boolean, msg: String?, isCancel: Boolean, listener: CallBack?) {
        val builder = HttpBuilder.getBuilder(method)
        builder.isShowProgress = isShowProgress
        builder.isCancel = isCancel
        builder.msg = msg
        send(builder, parameters, cl, listener)
    }

    open fun send(builder: HttpBuilder, parameters: JSONObject?, cl: Class<*>?, listener: CallBack?) {
        val map: HashMap<String, Any> = HashMap()
        if (parameters != null) {
            val keys: MutableList<String> = ArrayList()
            //处理空参数
            for (str in parameters.keys) {
                if (RxDataTool.isEmpty(parameters[str])) {
                    keys.add(str)
                }
            }
            for (key in keys) {
                parameters.remove(key)
            }
            if (parameters.size > 0) {
                val p = parameters.toJSONString()
                if (RxActivityTool.isAppDebug(RxTool.getContext())) {
                    RxLogTool.d(builder.url + "params", p)
                }
                PreferenceUtils.setValue(RxTool.getContext(), "post", p)
                if (JConstant.isEncrypt()) {
                    map["params"] = AESOperator.encrypt(p)
                } else {
                    map["params"] = parameters.toJSONString()
                }
            }
        }
        builder.parameters = map
        send(builder, cl, listener)
    }

    open fun send(builder: HttpBuilder, cl: Class<*>?, listener: CallBack?) {
        var listener = listener
        RxKeyboardTool.hideSoftInput(RxActivityTool.currentActivity())
        if (listener == null) listener = callBack
        if (RxNetTool.isNetworkAvailable(RxTool.getContext())) {
            try {
                val subscriber = RxSubscriber()
                subscriber.setmSubscriberOnNextListener(listener)
                subscriber.context = getmContext()
                subscriber.method = builder.url
                subscriber.data = cl
                subscriber.msg = builder.msg
                subscriber.setCancel(builder.isCancel)
                subscriber.setShowProgress(builder.isShowProgress)
                if (!RxDataTool.isNullString(JConstant.getToken())) builder.parameters?.let {
                    builder.parameters!!["token"] = JConstant.getToken()
                }
                builder.tag = fragmentName
                builder.setHeaders(JConstant.getHeardsVal())
                subscriber.startTime = System.currentTimeMillis()
                postForm(builder, subscriber)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("BaseRepository", e.message)
                listener!!.onError(ApiException(null, CodeException.RUNTIME_ERROR, "无网络连接，请检查网络是否正常", builder.url))
            }
        } else {
            listener!!.onError(ApiException(null, CodeException.RUNTIME_ERROR, "无网络连接，请检查网络是否正常", builder.url))
        }
    }


    fun getJsonObject(): JSONObject {
        return JSONObject()
    }


    override fun unSubscribe() {
        super.unSubscribe()
        if (jobs.isNotEmpty()) {
            for (j in jobs) {
                j.cancel()
            }
        }
    }

    open fun postForm(builder: HttpBuilder, subscriber: RxSubscriber) {
        var job = HttpUtils.postForm(builder, getViewModel(), object : NetWorkResult {
            override fun onNext(result: String) {
                subscriber.onNext(result)
            }

            override fun onError(e: Throwable) {
                subscriber.onError(e)
            }
        })
        addJob(job)
    }

    fun addJob(job: Job) {
        jobs.add(job)
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }
}