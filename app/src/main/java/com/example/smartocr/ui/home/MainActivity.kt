package com.example.smartocr.ui.home

import android.Manifest
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.example.smartocr.base.BaseActivity
import com.example.smartocr.ui.auth.AuthViewModel
import com.example.smartocr.ui.camera.CCCDJob
import com.example.smartocr.ui.camera.CameraFragment
import com.example.smartocr.ui.camera.TableJob
import com.example.smartocr.ui.camera.WithoutTemplateJob
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
            if (des.id !in listOf(
                    R.id.homeFragment, R.id.settingFragment
//                    R.id.authFragment,
//                    R.id.cameraFragment,
//                    R.id.cameraResultFragment,
//                    R.id.viewScannedCCCDFragment
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
                        ScanOptionDialog().show(supportFragmentManager, null)
                    } else {
                        toastShort("Permission denied")
                    }
                }
        }

        binding.flowBtScan.setOnClickListener {
            binding.flowBtScan.gone()
        }

        binding.btScanCccd.setOnClickListener {
            toCamera(CameraFragment.MODE_CCCD)
        }

        binding.btScanTemplate.setOnClickListener {
            toCamera(CameraFragment.MODE_TEMPLATE)

        }

        binding.btScanWithoutTemplate.setOnClickListener {
            toCamera(CameraFragment.MODE_WITHOUT_TEMPLATE)
        }

        binding.btScanTable.setOnClickListener {
            toCamera(CameraFragment.MODE_TABLE)
        }
    }

    private fun toCamera(mode: Int) {
        if (mode == CameraFragment.MODE_TEMPLATE) {
            navController.navigate(R.id.savedTemplateFragment)
        } else {
            val job = when (mode) {
                CameraFragment.MODE_CCCD -> CCCDJob()
                CameraFragment.MODE_TABLE -> TableJob()
                CameraFragment.MODE_WITHOUT_TEMPLATE -> WithoutTemplateJob()
                else -> WithoutTemplateJob()
            }

            navController.navigate(R.id.cameraFragment,
                bundleOf("job" to job),
                navOptions {
                    anim { enter = androidx.navigation.ui.R.anim.nav_default_enter_anim }
                })
        }
        binding.flowBtScan.gone()
    }

}