package com.zam.storyapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.zam.storyapp.R
import com.zam.storyapp.data.source.model.UserModel
import com.zam.storyapp.databinding.ActivityLoginBinding
import com.zam.storyapp.ui.homelist.ListStoryActivity
import com.zam.storyapp.ui.register.RegisterActivity
import com.zam.storyapp.ui.ViewModelFactory
import com.zam.storyapp.utils.ResultProcess

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupViewModel()
        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            if (valid()) {
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                loginViewModel.userLogin(email, password).observe(this) {
                    when (it) {
                        is ResultProcess.Success -> {
                            showLoad(false)
                            val response = it.data
                            saveUserData(
                                UserModel(
                                    response.loginResult?.name.toString(),
                                    response.loginResult?.token.toString(),
                                    true
                                )
                            )
                            println("test console==========================================")
                            println("nama = "+response.loginResult?.name.toString())
                            println("token = "+response.loginResult?.token.toString())

                            startActivity(Intent(this, ListStoryActivity::class.java))
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

        binding.btnDaftar.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun saveUserData(user: UserModel) {
        loginViewModel.saveUser(user)
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun showLoad(isLoad: Boolean) {
        if (isLoad){
            binding.loading.visibility = View.VISIBLE
        }
        else {
            binding.loading.visibility = View.GONE
        }
    }

    private fun valid() =
        binding.edtEmail.error == null && binding.edtPassword.error == null && !binding.edtEmail.text
            .isNullOrEmpty() && !binding.edtPassword.text.isNullOrEmpty()
}