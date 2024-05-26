package com.example.smartocr.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.User
import com.example.smartocr.util.Event
import com.example.smartocr.util.SharePreferenceExt
import com.example.smartocr.util.singleEventOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {
    private val _authState = MutableStateFlow<Resource<User>>(Resource.Idle)
    private val _authSideEffect: MutableStateFlow<Event<String?>> =
        MutableStateFlow(singleEventOf(null))

    val authState = _authState.asStateFlow()
    val authSideEffect = _authSideEffect.asStateFlow()

    init {
        viewModelScope.launch {
            if (SharePreferenceExt.username.isNotEmpty() && SharePreferenceExt.password.isNotEmpty()) {
                login(SharePreferenceExt.username, SharePreferenceExt.password)
            }
        }
    }

    fun isUserReady() = Firebase.auth.currentUser != null

    fun signOut() {
        Firebase.auth.signOut()
    }

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            if (userName.isNullOrEmpty()) {
                _authSideEffect.value = Event("Username is empty")
                return@launch
            }
            if (password.isNullOrEmpty()) {
                _authSideEffect.value = Event("Password is empty")
                return@launch
            }
            _authState.value = Resource.Loading
            dataRepositorySource.login(userName, password).collect {
                _authState.value = it
                    .whenSuccess {
                        SharePreferenceExt.username = userName
                        SharePreferenceExt.password = password
                    }
                    .map { User(userName, userName, password) }
            }
        }
    }

    fun signIn(userName: String, password: String, repassword: String, phone: String) {
        viewModelScope.launch {
            if (userName.isNullOrEmpty()) {
                _authSideEffect.value = Event("Username is empty")
                return@launch
            }
            if (password.isNullOrEmpty()) {
                _authSideEffect.value = Event("Password is empty")
                return@launch
            }
            if (repassword != password) {
                _authSideEffect.value = Event("Password not match")
                return@launch
            }
            if (phone.isNullOrEmpty()) {
                _authSideEffect.value = Event("Phone is empty")
                return@launch
            }

            if (_authState.value.isLoading) {
                _authSideEffect.value = Event("Please wait for a minute!")
                return@launch
            }

            _authState.value = Resource.Loading
            dataRepositorySource.signIn(userName, password, repassword, phone).collect {
                _authState.value = it
                    .whenSuccess {
                        SharePreferenceExt.username = userName
                        SharePreferenceExt.password = password
                    }
                    .map { User(userName, userName, password) }
            }
        }
    }
}