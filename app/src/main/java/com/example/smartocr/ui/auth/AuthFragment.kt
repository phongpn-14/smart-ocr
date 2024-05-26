package com.example.smartocr.ui.auth

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.util.repeatOnLifecycleStartState
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>() {
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private val REQ_DRIVE = 3  // Can be any integer unique to the Activity

    private var showOneTapUI = true

    val TAG = "Check signin"


    override fun getLayoutId(): Int {
        return R.layout.fragment_auth
    }

    override fun addAction() {
        super.addAction()
        binding.btGoogle.setOnClickListener {
            authViewModel.login(
                binding.edtUserName.text.toString().trim(),
                binding.edtPassword.text.toString().trim()
            )
        }

        binding.btConfigDomain.setOnClickListener {
            navigate(R.id.fragmentConfigDomain)
        }

        binding.btLogIn.setOnClickListener {
            navigate(R.id.signInFragment)
        }
    }

    override fun addObserver() {
        super.addObserver()
        repeatOnLifecycleStartState {
            launch {
                authViewModel.authState.collect {
                    it.whenLoading {
                        toastShort("Loading")
                        showLoading()
                    }.whenSuccess {
                        toastShort("Login Success")
                        findNavController().navigate(
                            R.id.homeFragment,
                            null,
                            navOptions = navOptions {
                                popUpTo(R.id.homeFragment) {
                                    inclusive = false
                                }
                            })
                    }.whenError {
                        toastShort("Failure. Message = ${it.message}")
                    }
                }
            }

            launch {
                authViewModel.authSideEffect.collect {
                    it.getValueIfNotHandle {
                        it?.let { mes -> toastShort(mes) }

                    }
                }
            }
        }
    }

    fun auth() {
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("85075210945-f5l0gevpd3s6su1vsocanop8hldh1lqd.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
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
                        null, 0, 0, 0, null
                    )
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
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            Firebase.auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        navigate(R.id.homeFragment)

                                        Log.d(TAG, "onActivityResult: $id")

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }

            REQ_DRIVE -> {

            }
        }
    }
}