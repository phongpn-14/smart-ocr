package com.example.smartocr.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OcrCCCD(
    @SerializedName("Có giá trị đến")
    var dateIn: String = "",
    @SerializedName("Giới tính")
    var sex: String = "",
    @SerializedName("Họ và tên")
    var name: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Ngày sinh")
    var birth: String = "",
    @SerializedName("Nơi thường trú")
    var permanentAddress: String = "",
    @SerializedName("Quê quán")
    var homeTown: String = "",
    @SerializedName("Quốc tịch")
    var nationality: String = "",
    @SerializedName("thuong_tru_1")
    var thuongTru1: String = "",
    @SerializedName("thuong_tru_2")
    var thuongTru2: String = "",
    @SerializedName("objectID")
    var objectID: String = ""
) : Parcelable