package com.example.healthsystem.model

import java.io.Serializable

data class User(
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val isMale: Boolean = true,
    val dateOfBirth: String = ""
): Serializable

