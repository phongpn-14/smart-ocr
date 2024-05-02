package com.example.smartocr.data.dto.response


import com.example.smartocr.data.model.Metadata
import com.google.gson.annotations.SerializedName

data class ResponseTemplateMetadata(
    @SerializedName("metadata")
    var metadata: Metadata = Metadata()
)