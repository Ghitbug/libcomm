package com.gh.comm.library.live

import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.parser.Feature
import com.gh.comm.library.common.JConstant

import com.gh.comm.library.live.interfaces.CallBack
import com.gh.comm.library.utils.AESOperator
import com.gh.lib.net.exception.ApiException
import com.gh.lib.net.exception.CodeException
import com.gh.lib.net.interfaces.NetWorkResult
import com.gh.libbase.utils.RxActivityTool
import com.gh.libbase.utils.RxDataTool
import com.gh.libbase.utils.RxLogTool
import com.gh.libbase.view.ProgressDialogUtil


import java.math.BigDecimal

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 *
 * @version v1.0
 * @date 2017/3/14
 * @auth gh
 * @company 重庆锐云科技有限公司
 */
open class RxSubscriber : NetWorkResult {
    //    回调接口
    protected var mSubscriberOnNextListener: CallBack? = null

    //    加载框可自己定义
    /*是否能取消加载框*/
    private var cancel = false

    /*是否显示加载框*/
    private var showProgress = false

    //    加载框可自己定义
    private var progressDialog: ProgressDialogUtil? = null

    /*是否弹框*/
    var data: Class<*>? = null
    var msg: String? = null
    var method: String? = null

    var context: Context? = null
    var startTime: Long = 0

    fun setmSubscriberOnNextListener(mSubscriberOnNextListener: CallBack?) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener
    }

    fun setShowProgress(showProgress: Boolean) {
        this.showProgress = showProgress
        if (showProgress) {
            if (progressDialog == null) progressDialog = ProgressDialogUtil(context)
            progressDialog!!.setCanselable(isCancel())
        }
        showProgressDialog()
    }

    /**
     * 显示加载框
     */
    private fun showProgressDialog() {
        if (!showProgress) return
        if (progressDialog != null) progressDialog!!.show()
    }

    /**
     * 隐藏
     */
    protected fun dismissProgressDialog() {
        if (!showProgress) return
        if (progressDialog != null) {
            progressDialog!!.hide()
        }
    }

    fun getApiException(e: Exception?, JSON_ERROR: Int, msg: String?, result: String?): ApiException {
        val apiException = ApiException(e, JSON_ERROR, msg, method!!)
        apiException.data = result
        return apiException
    }

    fun getDataResult(json: String?): String? {
        var dataValue: String? = ""
        try {
            val dataObj = JSON.parseObject(json)
            if (dataObj.size == 1) {
                val sIterator: Set<String> = dataObj.keys
                for (str in sIterator) {
                    dataValue = dataObj[str].toString()
                }
            } else {
                dataValue = json
            }
        } catch (e: Exception) {
            dataValue = json
        }
        return dataValue
    }

    fun isCancel(): Boolean {
        return cancel
    }

    fun setCancel(cancel: Boolean) {
        this.cancel = cancel
    }

    fun getProgressDialog(): ProgressDialogUtil? {
        return progressDialog
    }

    fun setProgressDialog(progressDialog: ProgressDialogUtil) {
        this.progressDialog = progressDialog
    }


    fun handleResult(result: String?, baseResult: RxResult) {}


    override fun onNext(result: String) {
        if (RxActivityTool.isAppDebug(context)) {
            val endTime = System.currentTimeMillis()
            RxLogTool.d(method + "请求耗时--------------------------", "${((endTime - startTime) / 1000) / 1000}秒")
        }
        if (mSubscriberOnNextListener != null) {
            try {
                if (!RxDataTool.isNullString(result)) {
                    val baseResult = JSONObject.parseObject(result, RxResult::class.java, Feature.OrderedField)
                    if (data != null) baseResult.className = data!!.simpleName
                    var dataJson: String? = if (baseResult.getResult<Any?>() == null) "" else baseResult.getResult<Any>().toString()
                    if (!RxDataTool.isNullString(dataJson) && JConstant.isEncrypt()) {
                        dataJson = AESOperator.decrypt(dataJson!!)
                        if (RxDataTool.isNullString(dataJson)) {
                            dataJson = if (baseResult.getResult<Any?>() == null) "" else baseResult.getResult<Any>().toString()
                        }
                        dataJson = dataJson!!.replace("null", "\"\"")
                    }
                    if (!RxDataTool.isNullString(dataJson)) {
                        baseResult.setResult(dataJson)
                    }
                    if (baseResult.code == 200) {
                        if (data != null) {
                            baseResult.data = dataJson
                            RxLogTool.json(dataJson)
                            if (dataJson!!.startsWith("[")) {
                                baseResult.setResult(JSONObject.parseArray(dataJson, data))
                            } else {
                                if (data == BigDecimal::class.java || data == String::class.java || data == Int::class.java) {
                                    dataJson = getDataResult(dataJson)
                                }
                                if (data == BigDecimal::class.java) {
                                    baseResult.setResult(BigDecimal(dataJson))
                                } else if (data == String::class.java) {
                                    baseResult.setResult(dataJson)
                                } else if (data == Int::class.java) {
                                    baseResult.setResult(dataJson!!.toInt())
                                } else if (data != null) {
                                    baseResult.setResult(JSONObject.parseObject(dataJson, data, Feature.OrderedField))
                                }
                            }
                        }
                        mSubscriberOnNextListener!!.onNext(baseResult)
                    } else if (baseResult.code == 101 || baseResult.code == 102 || baseResult.code == 103) {
                        if (JConstant.getLoinOutInterface() != null) {
                            JConstant.getLoinOutInterface().loginOut(context, baseResult.code, baseResult.msg)
                        }
                    } else {
                        val apiException = getApiException(null, baseResult.code, baseResult.msg, JSONObject.toJSONString(baseResult))
                        apiException!!.businessType = baseResult.businessType
                        mSubscriberOnNextListener!!.onError(apiException!!)
                    }
                } else {
                    mSubscriberOnNextListener!!.onError(getApiException(null, CodeException.UNKNOWN_ERROR, "网络数据处理错误", result)!!)
                }
            } catch (e: java.lang.Exception) {
                mSubscriberOnNextListener!!.onError(getApiException(e, CodeException.UNKNOWN_ERROR, "网络数据处理错误", result)!!)
            } finally {
                dismissProgressDialog()
            }
        }
    }

    override fun onError(e: Throwable) {
        RxLogTool.d("errorDo$method", e.message)
        if (mSubscriberOnNextListener == null) return
        mSubscriberOnNextListener!!.onError(ApiException(e, CodeException.UNKNOWN_ERROR, "网络连接错误", method!!))
        dismissProgressDialog()
    }
}