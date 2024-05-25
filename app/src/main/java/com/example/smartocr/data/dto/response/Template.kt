package com.example.smartocr.data.dto.response

import com.google.gson.annotations.SerializedName

data class Template(
    @SerializedName("id_")
    val id: String = "",
    val document: Map<String, String> = mapOf()

) {
}