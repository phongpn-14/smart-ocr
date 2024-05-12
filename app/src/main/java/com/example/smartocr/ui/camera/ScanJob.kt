package com.example.smartocr.ui.camera

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class ScanJob(
    open var mode: Int = CameraFragment.MODE_TEMPLATE
) : Parcelable


@Parcelize
data class CCCDJob(
    override var mode: Int = CameraFragment.MODE_CCCD
) : ScanJob(mode), Parcelable


@Parcelize
data class TemplateJob(
    override var mode: Int = CameraFragment.MODE_TEMPLATE,
    var templateId: String
) : ScanJob(mode), Parcelable

@Parcelize
data class WithoutTemplateJob(
    override var mode: Int = CameraFragment.MODE_WITHOUT_TEMPLATE,
) : ScanJob(mode), Parcelable

@Parcelize
data class TableJob(
    override var mode: Int = CameraFragment.MODE_TABLE,
) : ScanJob(mode), Parcelable