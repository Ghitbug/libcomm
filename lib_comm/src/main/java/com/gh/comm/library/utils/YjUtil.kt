package com.gh.comm.library.utils

import android.os.Bundle
import com.alibaba.fastjson.JSONObject
import com.gh.mylibrary.utils.*

/**
 * 公共参数获取类
 * @auth gh
 */
class YjUtil {
    companion object {
        private var b: Bundle? = null
        //获取地址
        fun getUrl(): String? {
            return getBundle()?.getString("HTTP_URL")
        }

        //是否加密
        fun getEncrypt(): Boolean {
            return getBundle()?.getBoolean("ENABLE_DEBUG") == false
        }

        fun getHeards(registrationID: String): String {
            val http = getUrl()
            var heardsVal = ""
            val jsonObject = JSONObject()
            jsonObject["systemType"] = "1"
            jsonObject["appVersion"] = RxActivityTool.getAppVersionName()
            jsonObject["mobileCode"] = ExampleUtil.getImei(RxTool.getContext())
            if (!RxDataTool.isNullString(http)) jsonObject["version"] = http?.substring(http.indexOf("version"), http.lastIndexOf("/"))
            jsonObject["registrationID"] = registrationID
            heardsVal = jsonObject.toJSONString()
            if (getEncrypt()) {
                heardsVal = AESOperator.encrypt(heardsVal)
            }
            return heardsVal
        }

        private fun getBundle(): Bundle? {
            if (b == null) {
                b = MetaDataUtil.getMetaData()
            }
            return b
        }
    }
}