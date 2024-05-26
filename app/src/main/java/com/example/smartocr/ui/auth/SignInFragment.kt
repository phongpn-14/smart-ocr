package com.example.smartocr.ui.auth

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.util.repeatOnLifecycleStartState
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun getLayoutId(): Int {
        return R.layout.fragment_sign_in
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btSignIn.setOnClickListener {
            authViewModel.signIn(
                binding.edtUserName.text.toString().trim(),
                binding.edtPassword.text.toString().trim(),
                binding.edtRePassword.text.toString().trim(),
                binding.edtPhone.text.toString().trim()
            )
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
}