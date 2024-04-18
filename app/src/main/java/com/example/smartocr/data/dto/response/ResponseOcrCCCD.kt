package com.example.smartocr.data.dto.response

import android.os.Parcelable
import com.example.smartocr.data.model.OcrCCCD
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseOcrCCCD(
    val result: OcrCCCD? = null
) : Parcelable