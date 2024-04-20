package com.example.smartocr

import android.Manifest
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.example.smartocr.base.BaseActivity
import com.example.smartocr.ui.auth.AuthViewModel
import com.example.smartocr.util.gone
import com.example.smartocr.util.visible
import com.hjq.permissions.XXPermissions
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    private val authViewModel by viewModels<AuthViewModel>()


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        val graph = navController.navInflater.inflate(R.navigation.app_nav)
        if (authViewModel.isUserReady()) {
            graph.setStartDestination(R.id.homeFragment)
        } else {
            graph.setStartDestination(R.id.authFragment)
        }
        navController.setGraph(graph, null)
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun addObserver() {
        super.addObserver()
        navController.addOnDestinationChangedListener { nav, des, extras ->
            if (des.id in listOf(
                    R.id.authFragment,
                    R.id.cameraFragment,
                    R.id.cameraResultFragment,
                    R.id.viewScannedCCCDFragment
                )
            ) {
                binding.bottomNav.gone()
                binding.btScan.gone()
            } else {
                binding.bottomNav.visible()
                binding.btScan.visible()
            }
        }
    }

    override fun addAction() {
        super.addAction()
        binding.btScan.setOnClickListener {
            XXPermissions.with(this)
                .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .request { _, allGranted ->
                    if (allGranted) {
                        navController.navigate(R.id.cameraFragment, null, navOptions {
                            anim { enter = androidx.navigation.ui.R.anim.nav_default_enter_anim }
                        })
                    } else {
                        toastShort("Permission denied")
                    }
                }
        }
    }

}