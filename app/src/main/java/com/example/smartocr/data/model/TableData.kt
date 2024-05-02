package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

data class TableData(
    @SerializedName("column_number")
    var columnNumber: Int = 0,
    @SerializedName("columns_coordinates")
    var columnsCoordinates: List<ColumnsCoordinate> = listOf(),
    @SerializedName("row_number")
    var rowNumber: Int = 0,
    @SerializedName("rows")
    var rows: List<List<Any>> = listOf(),
    @SerializedName("rows_coordinates")
    var rowsCoordinates: List<RowsCoordinate> = listOf()
)