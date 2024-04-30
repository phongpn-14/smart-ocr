package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TextMetadata(
    @SerializedName("text")
    var text: String = "",
    @SerializedName("text-region")
    var textRegion: List<Int> = listOf()
) : Parcelable