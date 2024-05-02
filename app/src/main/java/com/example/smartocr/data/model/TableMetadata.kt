package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

data class TableMetadata(
    @SerializedName("table_coordinate")
    var tableCoordinate: List<Int> = listOf(),
    @SerializedName("table_data")
    var tableData: TableData = TableData(),
//    @SerializedName("table_image")
    var tableImage: String = ""
)