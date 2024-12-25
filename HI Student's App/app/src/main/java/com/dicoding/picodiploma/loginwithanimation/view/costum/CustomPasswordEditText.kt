package com.dicoding.picodiploma.loginwithanimation.view.costum

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomPasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })
    }

    private fun validatePassword(password: String) {
        val parent = parent.parent
        if (parent is TextInputLayout) {
            when {
                password.isEmpty() -> {
                    parent.error = "Password tidak boleh kosong"
                    parent.isErrorEnabled = true
                }
                password.length < 8 -> {
                    parent.error = "Password minimal 8 karakter"
                    parent.isErrorEnabled = true
                }
                !password.matches(".*[A-Z].*".toRegex()) -> {
                    parent.error = "Password harus mengandung huruf kapital"
                    parent.isErrorEnabled = true
                }
                !password.matches(".*[a-z].*".toRegex()) -> {
                    parent.error = "Password harus mengandung huruf kecil"
                    parent.isErrorEnabled = true
                }
                !password.matches(".*[0-9].*".toRegex()) -> {
                    parent.error = "Password harus mengandung angka"
                    parent.isErrorEnabled = true
                }
                !password.matches(".*[!@#\$%^&*()].*".toRegex()) -> {
                    parent.error = "Password harus mengandung simbol (!@#\$%^&*())"
                    parent.isErrorEnabled = true
                }
                else -> {
                    parent.error = null
                    parent.isErrorEnabled = false
                }
            }
        }
    }
}