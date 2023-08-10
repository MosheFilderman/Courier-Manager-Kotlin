package com.example.couriermanagerkotlin.objects

import java.io.Serializable

data class Measures(
    var height: Int,
    var width: Int,
    var length: Int,
    var weight: Int
) : Serializable {
}