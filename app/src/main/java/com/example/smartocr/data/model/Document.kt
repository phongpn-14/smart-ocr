package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Document(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("line")
    var line: List<String> = listOf(),
    @SerializedName("name")
    var name: String = ""
) : Parcelable