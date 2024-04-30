package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Metadata(
    @SerializedName("table_metadata")
    var tableMetadata: List<TableMetadata> = listOf(),
    @SerializedName("text_metadata")
    var textMetadata: List<TextMetadata> = listOf()
) : Parcelable