package com.example.couriermanagerkotlin.activities.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.measures
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getMeasures
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.setMeasures
import com.example.couriermanagerkotlin.R

class AppSettings : AppCompatActivity() {

    lateinit var height: EditText
    lateinit var width: EditText
    lateinit var length: EditText
    lateinit var weight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)

        height = findViewById(R.id.height)
        width = findViewById(R.id.width)
        length = findViewById(R.id.length)
        weight = findViewById(R.id.weight)

        getMeasures(this@AppSettings, height, width, length, weight)
    }

    fun updateMeasures(view: View) {
        measures.height = height.text.toString().toInt()
        measures.width = width.text.toString().toInt()
        measures.length = length.text.toString().toInt()
        measures.weight = weight.text.toString().toInt()

        setMeasures(this@AppSettings)

        startActivity(Intent(this@AppSettings, Manager::class.java))
        finish()
    }
}