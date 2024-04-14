package com.example.smartocr.data.remote.service

import com.example.smartocr.data.dto.response.ResponseHelloWorld
import retrofit2.Response
import retrofit2.http.GET

interface SmartOCRService {
    @GET("/")
    suspend fun helloWorld(): Response<ResponseHelloWorld>

}