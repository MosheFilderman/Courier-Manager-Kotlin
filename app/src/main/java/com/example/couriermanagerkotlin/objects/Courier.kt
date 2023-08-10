package com.example.couriermanagerkotlin

import java.io.Serializable

data class Courier(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
) : Serializable {
}
