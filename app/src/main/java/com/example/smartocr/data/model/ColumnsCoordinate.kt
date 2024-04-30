package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ColumnsCoordinate(
    @SerializedName("column_id")
    var columnId: Int = 0,
    @SerializedName("coordinates")
    var coordinates: List<Int> = listOf(),
    @SerializedName("label")
    var label: String = ""
) : Parcelable