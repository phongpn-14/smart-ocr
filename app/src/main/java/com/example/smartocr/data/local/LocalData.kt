package com.example.smartocr.data.local

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LocalData @Inject constructor(
    val context: Context, override val coroutineContext: CoroutineContext
) : CoroutineScope {



}



