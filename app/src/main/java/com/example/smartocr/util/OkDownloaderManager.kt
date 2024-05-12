package com.example.smartocr.util

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import java.io.File

object OkDownloaderManager {
    fun download(
        downloadPath: List<String>,
        urls: List<String>,
        onSuccess: (name: String, downloadPath: String) -> Unit = { _, _ -> },
        onFailure: (name: String) -> Unit = {},
        onProgress: (name: String, progress: Float) -> Unit = { _, _ -> }
    ) {
        val tasks = mutableListOf<DownloadTask>()
        tasks.addAll(downloadPath.mapIndexedNotNull { index, _ ->
            if (!urls[index].startsWith("http")) null
            else {
                DownloadTask.Builder(urls[index], File(downloadPath[index]))
                    .setMinIntervalMillisCallbackProcess(100)
                    .setPassIfAlreadyCompleted(true)
                    .setReadBufferSize(8192)
                    .setFlushBufferSize(32768)
                    .build()
            }
        })
        DownloadTask.enqueue(tasks.toTypedArray(), object : DownloadListener1() {
            override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
            }

            override fun taskEnd(
                task: DownloadTask,
                cause: EndCause,
                realCause: Exception?,
                model: Listener1Assist.Listener1Model
            ) {
                if (cause == EndCause.COMPLETED) {
                    onSuccess(task.filename!!, task.file!!.absolutePath)
                } else {
                    onFailure(task.filename!!)
                }
            }

            override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
            }

            override fun connected(
                task: DownloadTask,
                blockCount: Int,
                currentOffset: Long,
                totalLength: Long
            ) {
            }

            override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
                onProgress(task.filename!!, currentOffset.toFloat() / totalLength)
            }

        })
    }
}