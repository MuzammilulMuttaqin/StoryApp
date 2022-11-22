package com.zam.storyapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.zam.storyapp.R
import com.zam.storyapp.databinding.ActivityRegisterBinding
import com.zam.storyapp.ui.ViewModelFactory
import com.zam.storyapp.ui.login.LoginActivity
import com.zam.storyapp.utils.ResultProcess

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener{
            if (valid()) {
                val name = binding.edRegisterName.text.toString()
                val email = binding.edRegisterPassword.text.toString()
                val password = binding.edRegisterPassword.text.toString()
                registerViewModel.userRegister(name, email, password).observe(this) {
                    when (it) {
                        is ResultProcess.Success -> {
                            showLoad(false)
                            Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                        }
                        is ResultProcess.Loading -> showLoad(true)
                        is ResultProcess.Error -> {
                            Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                            showLoad(false)
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.check_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun valid() =
        binding.edRegisterEmail.error == null
                && binding.edRegisterPassword.error == null
                && binding.edRegisterName.error == null
                && !binding.edRegisterEmail.text.isNullOrEmpty()
                && !binding.edRegisterPassword.text.isNullOrEmpty()
                && !binding.edRegisterName.text.isNullOrEmpty()

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun showLoad(isLoad: Boolean) {
        if (isLoad){
            binding.loading.visibility = View.VISIBLE
        }
        else {
            binding.loading.visibility = View.GONE
        }
    }
}