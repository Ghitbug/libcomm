package com.gh.comm.library.common;

import android.content.Context;

import com.gh.mylibrary.utils.RxDataTool;
import com.gh.comm.library.utils.YjUtil;



/**
 * 系统变量
 */
public class JConstant {
    private static boolean encrypt = true;
    private static LoinOutInterface loinOutInterface;
    private static String token;
    private static String registrationID;
    private static String watermarkStr;
    private static String heardsVal = "";

    public static boolean isEncrypt() {
        return JConstant.encrypt;
    }

    public static void setEncrypt(boolean encrypt) {
        JConstant.encrypt = encrypt;
    }


    public static LoinOutInterface getLoinOutInterface() {
        return loinOutInterface;
    }

    public static void setLoinOutInterface(LoinOutInterface loinOutInterface) {
        JConstant.loinOutInterface = loinOutInterface;
    }

    public interface LoinOutInterface {
        void loginOut(Context context, int code, String msg);
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        JConstant.token = token;
    }

    public static String getHttpUrl() {
        String httpUrl = YjUtil.Companion.getUrl();
        encrypt = YjUtil.Companion.getEncrypt();
        return httpUrl;
    }

    public static String getRegistrationID() {
        if (!RxDataTool.isNullString(registrationID)) {
            return registrationID;
        } else {
            return "0";
        }
    }

    public static void setRegistrationID(String registrationID) {
        JConstant.registrationID = registrationID;
    }

    public static String getWatermarkStr() {
        if (watermarkStr == null) {
            return "";
        }
        return watermarkStr;
    }

    public static void setWatermarkStr(String watermarkStr) {
        JConstant.watermarkStr = watermarkStr;
    }

    public static void setHttpPostService() {
        getHttpUrl();
    }

    public static String getHeardsVal() {
        if (RxDataTool.isNullString(heardsVal)) {
            heardsVal = YjUtil.Companion.getHeards(getRegistrationID());
        }
        return heardsVal;
    }

    public static void setHeardsVal(String heardsVal) {
        JConstant.heardsVal = heardsVal;
    }
}
