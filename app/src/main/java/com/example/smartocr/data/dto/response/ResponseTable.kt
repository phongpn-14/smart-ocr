package com.example.smartocr.data.dto.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseTable (
    @SerializedName("file_url")
    val fileUrl: String
): Parcelable {
}