package com.example.smartocr.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.View
import com.example.smartocr.util.dp

class CutOffView(context: Context, attr: AttributeSet) : View(context, attr, 0) {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 2.dp.toFloat()
        style = Paint.Style.STROKE
    }
    private var mPath: Path? = null
    private val excludedWidth = 300.dp
    private val excludedHeight = 200.dp

    val centerX get() = width / 2f
    val centerY get() = height / 2f - 100.dp


    override fun onDraw(canvas: Canvas) {
        if (mPath == null) {
            mPath = Path()
            // Define the excluded rectangle
            val excludedRect = RectF().apply {
                set(
                    centerX - excludedWidth / 2f,
                    centerY - excludedHeight / 2f,
                    centerX + excludedWidth / 2f,
                    centerY + excludedHeight / 2f
                )
            }
            mPath!!.addRoundRect(
                excludedRect,
                24.dp.toFloat(),
                24.dp.toFloat(),
                Path.Direction.CW
            )

        }
        canvas.drawPath(mPath!!, mPaint)
        canvas.clipPath(mPath!!, Region.Op.DIFFERENCE)
        canvas.drawColor(Color.parseColor("#60000000"))
    }
}