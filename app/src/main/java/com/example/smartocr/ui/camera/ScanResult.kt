package com.example.smartocr.ui.camera

import android.os.Parcelable
import com.example.smartocr.data.model.OcrCCCD
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ScanResult(
    open var mode: Int = CameraFragment.MODE_TEMPLATE
) : Parcelable {

    @Parcelize
    data class CCCDResult(
        override var mode: Int = CameraFragment.MODE_CCCD,
        var ocrCCCD: OcrCCCD
    ) : ScanResult(mode), Parcelable


    @Parcelize
    data class TemplateResult(
        override var mode: Int = CameraFragment.MODE_TEMPLATE,
        var templateId: String,
        var fileUrl: String
    ) : ScanResult(mode), Parcelable

    @Parcelize
    data class SimpleResult(
        override var mode: Int = CameraFragment.MODE_WITHOUT_TEMPLATE,
        var result: String
    ) : ScanResult(mode), Parcelable

    @Parcelize
    data class TableResult(
        override var mode: Int = CameraFragment.MODE_TABLE,
        var fileUrl: String
    ) : ScanResult(mode), Parcelable
}

