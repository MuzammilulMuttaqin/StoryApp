package com.zam.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.zam.storyapp.R

class EditTextEmail : AppCompatEditText{

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        hint = context.getString(R.string.masukan_email)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        textSize = 14f
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                error = null
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()){
                    error = context.getString(R.string.type_email_not_valid)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

}

