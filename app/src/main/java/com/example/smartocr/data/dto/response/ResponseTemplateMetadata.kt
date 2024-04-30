package com.example.smartocr.data.dto.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.example.smartocr.data.model.Metadata

@Parcelize
data class ResponseTemplateMetadata(
    @SerializedName("metadata")
    var metadata: Metadata = Metadata()
) : Parcelable