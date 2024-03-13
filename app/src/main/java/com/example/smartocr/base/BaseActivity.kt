package com.example.smartocr.base

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.executePendingBindings()
        initViewModel()
        initView()
        addObserver()
        addAction()
        initData()
    }

    protected open fun initViewModel() {}

    protected open fun addObserver() {}

    protected abstract fun getLayoutId(): Int
    protected open fun initView() {}
    protected open fun addAction() {}
    protected open fun initData() {}

    protected open fun isActivityFullscreen() = false
    protected open fun getFragmentContainer(): Int {
        return R.id.content
    }

    @SuppressLint("CommitTransaction")
    fun showChildFragment(child: Fragment, name: String) {
        supportFragmentManager.beginTransaction()
            .replace(getFragmentContainer(), child, name)
            .commit()
    }

    fun toastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun hideKeyboard(clearFocusView: Boolean = true) {
        var viewFocus = currentFocus
        if (viewFocus == null) {
            viewFocus = findViewById(android.R.id.content) ?: return
        }

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(viewFocus.windowToken, 0)
        if (clearFocusView) {
            viewFocus.clearFocus()
        }
    }
}