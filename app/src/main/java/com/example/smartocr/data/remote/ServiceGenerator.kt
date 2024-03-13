package com.example.smartocr.data.remote

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.airbnb.lottie.BuildConfig
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


private const val timeoutRead = 120   //In seconds
private const val contentType = "Content-Type"
private const val contentTypeValue = "application/json"
private const val timeoutConnect = 120   //In seconds

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class ServiceGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private lateinit var retrofit: Retrofit

    private val cacheSize = (5 * 1024 * 1024).toLong() //5MB
    val myCache = Cache(File(context.cacheDir, "network").apply {
        if (!exists()) {
            mkdir()
        }
    }, cacheSize)

    private var headerInterceptor = Interceptor { chain ->
        val original = chain.request()

        val request = original.newBuilder()
            .header(contentType, contentTypeValue)
            .method(original.method, original.body)
            .build()

        chain.proceed(request)
    }

    private val cacheInterceptor = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .cacheControl(
                CacheControl.Builder()
                    .maxAge(5, TimeUnit.MINUTES)
                    .minFresh(1, TimeUnit.MINUTES)
                    .maxStale(2, TimeUnit.MINUTES)
                    .build()
            )
            .build()
        chain.proceed(request)
    }

    private val logger: HttpLoggingInterceptor
        get() {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
            }
            return loggingInterceptor
        }

    init {
        okHttpBuilder.addInterceptor(headerInterceptor)
            .addInterceptor(logger)
            .cache(myCache)
            .connectTimeout(timeoutConnect.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeoutRead.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Duration.ofSeconds(120))
    }

    fun <S> createService(serviceClass: Class<S>, baseUrl: String, cache: Boolean = true): S {
        if (cache) {
            okHttpBuilder.addInterceptor(cacheInterceptor)
        }
        val client = okHttpBuilder.build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl).client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
        return retrofit.create(serviceClass)
    }

}