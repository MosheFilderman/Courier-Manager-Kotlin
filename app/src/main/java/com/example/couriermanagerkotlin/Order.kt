package com.example.couriermanagerkotlin

import java.io.Serializable

data class Order(val orderId: String, val name: String, val phone: String, val email: String, val status: eStatus, val comment: String): Serializable {

}