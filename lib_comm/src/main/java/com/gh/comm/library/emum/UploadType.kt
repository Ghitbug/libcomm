package com.gh.comm.library.emum

enum class UploadType(var ecode: Int, val eurl: String, val ename: String) {
    IMAGE(1, "platform/uploadimage", "图片"), VIDEO(2, "platform/uploadvideo", "视频");
}