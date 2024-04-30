package com.example.smartocr.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RowData(
    @SerializedName("box")
    var box: List<Int> = listOf(),
    @SerializedName("text")
    var text: String = "",
) : Parcelable