package com.example.smartocr.ui.setting

import androidx.fragment.app.activityViewModels
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.auth.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val authViewModel by activityViewModels<AuthViewModel>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun initView() {
        super.initView()
        binding.tvEmail.text = Firebase.auth.currentUser!!.email
        binding.tvName.text = Firebase.auth.currentUser!!.displayName
    }

    override fun addAction() {
        super.addAction()
        binding.btSignOut.setOnClickListener {
            authViewModel.signOut()
            navigate(R.id.authFragment)
        }
    }
}