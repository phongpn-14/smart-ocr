package com.example.smartocr.data.remote

import android.os.Handler
import android.os.Looper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBody(
    private val file: File,
    private val onUpload: (offsetSize: Long) -> Unit,
    private val onProcess: (process: Float) -> Unit = {}
) : RequestBody() {

    override fun contentType() = MultipartBody.FORM

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        var prevUpload = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {
                uploaded += read
                onProcess(uploaded / length.toFloat())
                onUpload(read.toLong())
                prevUpload = uploaded
                sink.write(buffer, 0, read)
            }
        }
    }

    override fun contentLength(): Long = file.length()
}