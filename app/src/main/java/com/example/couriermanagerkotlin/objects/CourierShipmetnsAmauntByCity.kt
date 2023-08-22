package com.example.couriermanagerkotlin.objects

import java.io.Serializable

class CourierShipmetnsAmauntByCity(
    var fullName: String,
    var pickupCity: String,
    var SCHEDULED: Int,
    var COLLECTED: Int,
    var DELIVERED: Int
) : Serializable {
}