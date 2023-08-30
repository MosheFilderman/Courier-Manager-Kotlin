package com.example.couriermanagerkotlin

enum class eStatus {
    NEW,
    SCHEDULED,
    COLLECTED,
    DELIVERED,
    CANCELLED;

    companion object {
        /**
         * Receive String, check if it's value fit any eStatus,
         * if found match return the eStatus,
         * if not found return CANCELLED.
         */
        fun findStatus(value: String): eStatus {
            var result: eStatus = CANCELLED

            when(value) {
                NEW.name -> result = NEW
                SCHEDULED.name -> result = SCHEDULED
                COLLECTED.name -> result = COLLECTED
                DELIVERED.name -> result = DELIVERED
                CANCELLED.name -> result = CANCELLED
            }
            return result
        }

        /**
         * Receive current eStatus,
         * return the next eStatus in order.
         */
        fun setToNext(currentStatus: eStatus) : eStatus {
            val result: eStatus = when (currentStatus) {
                SCHEDULED -> COLLECTED
                COLLECTED -> DELIVERED
                else -> currentStatus
            }
            return result
        }
    }
}