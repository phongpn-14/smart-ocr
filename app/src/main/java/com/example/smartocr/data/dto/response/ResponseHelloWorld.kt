package com.example.smartocr.data.dto.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseHelloWorld(
    val message: String
) : Parcelable