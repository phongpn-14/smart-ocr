package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TemplateKey(
    @SerializedName("document")
    var document: Document = Document(),
    @SerializedName("id_")
    var id: String = ""
) : Parcelable