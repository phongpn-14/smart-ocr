package com.example.smartocr.util

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Bitmap.toFile(): File {
    val file = File.createTempFile("image_" + System.currentTimeMillis() , ".jpg")
    val bytes = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    try {
        val fo = FileOutputStream(file)
        fo.write(bytes.toByteArray())
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

object Bitmaps {
    val EMPTY by lazy { Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888) }
}