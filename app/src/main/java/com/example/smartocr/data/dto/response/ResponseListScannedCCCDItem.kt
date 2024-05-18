package com.example.smartocr.data.dto.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.example.smartocr.data.model.OcrCCCD

@Parcelize
data class ResponseListScannedCCCDItem(
    @SerializedName("document")
    var document: OcrCCCD = OcrCCCD(),
    @SerializedName("id_")
    var id: String = ""
) : Parcelable