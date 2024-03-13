package com.example.smartocr.util

import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.widget.TextView
import com.github.florent37.viewanimator.ViewAnimator

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.goneWithAnimation() {
    val originTranslationY = translationY
    val originAlpha = alpha
    ViewAnimator.animate(this)
        .alpha(originAlpha, 0f)
        .translationY(originTranslationY, translationY + 100f)
        .duration(180)
        .onStop {
            gone()
            alpha = originAlpha
            translationY = originTranslationY
        }
        .start()
}


fun View.setOnPressListener(
    onPress: (view: View) -> Unit,
    onRelease: (view: View) -> Unit
) {
    setOnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                onPress(v)
            }

            MotionEvent.ACTION_UP -> {
                onRelease(v)
            }

            MotionEvent.ACTION_CANCEL -> {
                onRelease(v)
            }
        }
        false
    }
}

fun View.increaseClickArea(size: Int) {
    (parent as View).post {
        val r = Rect()
        getHitRect(r)
        r.top -= size
        r.bottom += size
        r.left -= size
        r.right += size
        (parent as View).touchDelegate = TouchDelegate(r, this)
    }
}

fun View.fadOutAnimation(
    duration: Long = 300,
    visibility: Int = View.INVISIBLE,
    completion: (() -> Unit)? = null
) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.visibility = visibility
            completion?.let {
                it()
            }
        }
}

fun View.fadInAnimation(duration: Long = 300, completion: (() -> Unit)? = null) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction {
            completion?.let {
                it()
            }
        }
}

fun TextView.setTextAnimation(
    text: String,
    duration: Long = 300,
    completion: (() -> Unit)? = null
) {
    fadOutAnimation(duration) {
        this.text = text
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.setClickInText(colorLink: Int, vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = colorLink
                // toggle below value to enable/disable
                // the underline shown below the clickable text
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        if (startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

val View.screenLocation
    get(): IntArray {
        val point = IntArray(2)
        getLocationOnScreen(point)
        return point
    }

val View.centerXInLocation
    get(): Int = screenLocation[0] + width / 2

val View.centerYInLocation
    get(): Int = screenLocation[1] + height / 2

fun View.setOnClickWithDebounce(action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1200) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.centerPivot() {
    pivotX = width / 2f
    pivotY = height / 2f
}
