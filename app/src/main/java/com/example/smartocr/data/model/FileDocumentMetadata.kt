package com.example.smartocr.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileDocumentMetadata(
    @SerializedName("created_date")
    var createdDate: String = "",
    @SerializedName("file_path")
    var filePath: String = "",
    @SerializedName("type")
    var type: String = ""
) : Parcelable