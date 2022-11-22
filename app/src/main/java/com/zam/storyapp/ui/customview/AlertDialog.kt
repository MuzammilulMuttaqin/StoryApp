package com.zam.storyapp.ui.customview

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.zam.storyapp.R
import com.zam.storyapp.databinding.ErrorLayoutBinding

class AlertDialog(
    context: Context,
    private val message: Int,
    private val image: Int,
    private val action: (() -> Unit)? = null
): AlertDialog(context) {
    private lateinit var erorLayoutBinding: ErrorLayoutBinding
    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.error_layout)

        erorLayoutBinding.tvError.text = context.getString(message)
        erorLayoutBinding.ivEmptyUser.setImageResource(image)
        erorLayoutBinding.dismissButton.setOnClickListener {
            dismiss()
            action?.invoke()
        }
    }
}