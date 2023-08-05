package com.example.couriermanagerkotlin

enum class eStatus {
    NEW,
    SCHEDULED,
    COLLECTED,
    DELIVERED,
    CANCELLED,
    UNKNOWN;

    companion object {
        fun findStatus(value: String): eStatus {
            var result: eStatus = UNKNOWN

            when(value) {
                NEW.name -> result = NEW
                SCHEDULED.name -> result = SCHEDULED
                COLLECTED.name -> result = COLLECTED
                DELIVERED.name -> result = DELIVERED
                CANCELLED.name -> result = CANCELLED
            }
            return result
        }
    }
}