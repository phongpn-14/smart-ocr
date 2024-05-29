package com.example.smartocr.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.PopUpToBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.R

abstract class BaseDialog<T : ViewDataBinding> : DialogFragment() {
    protected lateinit var binding: T


    fun showDialog(activity: AppCompatActivity) {
        show(activity.supportFragmentManager, null)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        addAction()
        addData()
    }

    protected abstract fun getLayoutId(): Int

    protected open fun initView() {
    }

    protected open fun addAction() {
    }

    protected open fun addData() {
    }


    fun toastShort(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun navigate(
        id: Int,
        extras: Bundle? = null,
        @IdRes popUpTo: Int? = null,
        popUpToBuilder: PopUpToBuilder.() -> Unit = {}
    ) {
        findNavController().navigate(id, extras, navOptions {
            anim {
                enter = R.anim.nav_default_enter_anim
                exit = R.anim.nav_default_exit_anim
            }
        })
    }

    fun navigate(
        directions: NavDirections,
        @IdRes popUpTo: Int? = null,
        popUpToBuilder: PopUpToBuilder.() -> Unit = {}
    ) {
        findNavController().navigate(directions, navOptions {
            anim {
                enter = R.anim.nav_default_enter_anim
                exit = R.anim.nav_default_exit_anim
            }
        })
    }

    fun navigateUp() {
        parentFragmentManager.popBackStackImmediate()
    }

}
