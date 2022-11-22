package com.zam.storyapp.data.source.response

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("password")
    val password: String? = null
)