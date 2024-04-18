package com.example.smartocr.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    protected lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
        addObserver()
        addAction()
        initData()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (onBackPressed()) {
                        return
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    protected open fun initViewModel() {}
    protected open fun addObserver() {}
    protected abstract fun getLayoutId(): Int
    protected open fun initView() {}
    protected open fun addAction() {}
    open fun initData() {}
    open fun onBackPressed(): Boolean {
        return false
    }


    open fun finish() {
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    fun finishAllowingStateLoss() {
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }

    fun showChildFragment(child: Fragment, container: Int, name: String) {
        childFragmentManager.beginTransaction()
            .replace(container, child, name)
            .commit()
    }

    fun toastShort(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun removeChildFragment(name: String) {
        val child = childFragmentManager.findFragmentByTag(name)
        if (child != null) {
            childFragmentManager.beginTransaction().remove(child).commit()
        }
    }

    fun navigate(id: Int, extras: Bundle? = null) {
        findNavController().navigate(id, extras, navOptions {
            anim {
                enter = androidx.navigation.ui.R.anim.nav_default_enter_anim
                exit = androidx.navigation.ui.R.anim.nav_default_exit_anim

            }
        })
    }

    fun navigateUp() {
        parentFragmentManager.popBackStackImmediate()
    }

    open fun hideKeyboard(clearFocusView: Boolean = true) {
        (activity as? BaseActivity<*>)?.hideKeyboard(clearFocusView)
    }
}