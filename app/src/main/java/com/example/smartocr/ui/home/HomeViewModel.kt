package com.example.smartocr.ui.home

import androidx.lifecycle.ViewModel
import com.example.smartocr.data.DataRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {
    suspend fun listCCCD() = dataRepositorySource.listCCCD()

    suspend fun listFileResult() = dataRepositorySource.getAllFileResult()
}