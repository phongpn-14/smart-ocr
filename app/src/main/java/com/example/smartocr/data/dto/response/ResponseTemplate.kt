package com.example.smartocr.data.dto.response


import com.google.gson.annotations.SerializedName

data class ResponseTemplate(
    @SerializedName("file_url")
    var fileUrl: String
)