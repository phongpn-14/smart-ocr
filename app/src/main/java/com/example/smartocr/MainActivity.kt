package com.example.smartocr

import android.Manifest
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hjq.permissions.XXPermissions
import com.proxglobal.smart_ocr.R
import com.example.smartocr.base.BaseActivity
import com.proxglobal.smart_ocr.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        binding.bottomNav.setupWithNavController(navController)
    }

    override fun addAction() {
        super.addAction()
        binding.btScan.setOnClickListener {
            XXPermissions.with(this)
                .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .request { permissions, allGranted ->
                    if (allGranted) {
                        navController.navigate(R.id.cameraFragment)
                    } else {
                        toastShort("Permission denied")
                    }
                }
        }

    }

    private fun getLottieDrawable(
        res: Int,
        view: BottomNavigationView
    ): LottieDrawable {
        return LottieDrawable().apply {
            val result = LottieCompositionFactory.fromRawRes(
                view.context.applicationContext, res
            ).addListener {
                callback = view
                composition = it
            }

        }
    }
}