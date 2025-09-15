package com.krissphi.id.mykisah.ui.component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {


    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
               validateForm(s)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }

    private fun validateForm(s: CharSequence){
        if (isEmailType()) {
            error = if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                "Format email tidak valid"
            } else {
                null
            }
        }

        else if (isPasswordType()) {
            if (s.toString().length < 8) {
                setError("Password tidak boleh kurang dari 8 karakter", null)
            } else {
                error = null
            }
        }

    }

//    override fun setError(error: CharSequence?) {
//        // arahkan visual error ke TextInputLayout
//        findTIL()?.apply {
//            isErrorEnabled = !error.isNullOrEmpty()
//            this.error = error
//        }
//        // cegah EditText menampilkan ikon error di ujung kanan (biang tabrakan)
//        super.setError(null)
//    }
//
//    private fun findTIL(): TextInputLayout? {
//        var p = parent
//        while (p != null && p !is TextInputLayout) {
//            p = p.parent
//        }
//        return p
//    }

    private fun isEmailType(): Boolean =
        (inputType and InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0

    private fun isPasswordType(): Boolean =
        (inputType and (
                InputType.TYPE_TEXT_VARIATION_PASSWORD or
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                )) != 0
}