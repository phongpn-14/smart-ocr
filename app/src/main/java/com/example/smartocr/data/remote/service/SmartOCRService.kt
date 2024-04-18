package com.example.smartocr.data.remote.service

import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseOcrCCCD
import com.example.smartocr.data.model.OcrCCCD
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SmartOCRService {
    @GET("/")
    suspend fun helloWorld(): Response<ResponseHelloWorld>

    @Multipart
    @POST("/api/ocr_cccd")
    suspend fun processOcrCCCD(@Part image: MultipartBody.Part): Response<ResponseOcrCCCD>

}