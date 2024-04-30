package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RowsCoordinate(
    @SerializedName("coordinates")
    var coordinates: List<Int> = listOf(),
    @SerializedName("label")
    var label: String = "",
    @SerializedName("row_id")
    var rowId: Int = 0
) : Parcelable