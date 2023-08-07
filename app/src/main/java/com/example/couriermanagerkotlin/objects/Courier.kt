package com.example.couriermanagerkotlin

import java.io.Serializable

class Courier(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
) : Serializable {
    override fun toString(): String {
        return "[${this.firstName}, ${this.lastName}, ${this.email}, ${this.phone}]"
    }
}
