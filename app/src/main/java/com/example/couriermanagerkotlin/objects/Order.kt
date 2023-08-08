package com.example.couriermanagerkotlin.objects

import com.example.couriermanagerkotlin.eStatus
import java.io.Serializable

data class Order(
    val orderId: String,
    val name: String,
    val phone: String,
    val email: String,
    val status: eStatus,
    val pickupCity: String,
    val pickupStreet: String,
    val pickupBuild: String,
    val deliveryCity: String,
    val deliveryStreet: String,
    val deliveryBuild: String,
    val comment: String
) : Serializable {

}