package com.example.couriermanagerkotlin

import java.io.Serializable

data class Order(val name: String, val phone: String, val email: String, val status: String, val comment: String): Serializable {

}