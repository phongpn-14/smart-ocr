package com.example.smartocr.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

val TAG = "Prediction20244"

fun Any.logd() {
    Log.d(TAG, toString())
}


fun Any.logd(tag: String) {
    Log.d(tag, toString())
}

fun <T : Any> mutableLiveDataOf(value: T? = null): MutableLiveData<T> =
    if (value == null) MutableLiveData()
    else MutableLiveData(value)

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

fun <T : Any> MutableLiveData<T>.postValue(block: T.() -> Unit) {
    value?.let {
        block(it)
        postValue(value)
    }
}

fun copyFile(sourceLocation: File, targetLocation: File) {
    try {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val out = FileOutputStream(targetLocation)
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> List<T>.first(count: Int, block: (T) -> Unit) {
    forEachIndexed { index, t -> if (index < count) block(t) }
}

fun <T> List<T>.first(count: Int): List<T> {
    val result = mutableListOf<T>()
    for (i in 0..count) {
        if (i >= size) {
            break
        } else {
            result.add(get(i))
        }
    }
    return result
}

fun Int.asLikeCount(): String {
    var result = StringBuilder()
    if (this >= 1_000_000) {
        result.append(String.format("%.1f", this / 1_000_000f))
        result.append("M")
    } else {
        if (this in 100_000..999_999) {
            result.append(String.format("%03d", this / 100_000))
            result.append("K")
        }
    }
    return result.toString()
}