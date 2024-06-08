package com.example.smartocr.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class FileDocument(
    @SerializedName("document")
    var document: FileDocumentMetadata = FileDocumentMetadata(),
    @SerializedName("id_")
    var id: String = ""
) : Parcelable