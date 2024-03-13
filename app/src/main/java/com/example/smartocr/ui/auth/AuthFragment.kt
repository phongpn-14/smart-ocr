package com.example.smartocr.ui.auth

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.smartocr.base.BaseFragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    val TAG = "Check signin"


    override fun getLayoutId(): Int {
        return R.layout.fragment_auth
    }

    override fun addAction() {
        super.addAction()
        binding.btGoogle.setOnClickListener { auth() }
    }

    fun auth() {
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("85075210945-f5l0gevpd3s6su1vsocanop8hldh1lqd.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .build();

        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("85075210945-f5l0gevpd3s6su1vsocanop8hldh1lqd.apps.googleusercontent.com")
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                signUp()
                Log.d(TAG, e.localizedMessage)
            }
    }

    fun signUp() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d(TAG, "Got ID token.")
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                    // ...
                }
            }
        }
    }
}