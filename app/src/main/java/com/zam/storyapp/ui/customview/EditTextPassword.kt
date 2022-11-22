package com.zam.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.zam.storyapp.R

class EditTextPassword : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        hint = context.getString(R.string.input_password_description)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        textSize = 14f
    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.isEmpty() == true) {
                    error = context.getString(R.string.input_password_description)
                } else {
                    if (p0?.isEmpty() == true) {
                        error = context.getString(R.string.must_not_empty)
                    } else {
                        if ((p0?.length ?: 0) < 6) {
                            error = context.getString(R.string.kata_sandi_wajib_lebih_dari_6)
                        }
                    }
                }
            }
        })
    }
}