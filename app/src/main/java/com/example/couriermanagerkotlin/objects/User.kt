package com.example.couriermanagerkotlin

import java.io.Serializable

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
) : Serializable {
}
