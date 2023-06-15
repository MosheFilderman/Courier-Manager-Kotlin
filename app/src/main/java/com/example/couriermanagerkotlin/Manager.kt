package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class Manager : AppCompatActivity() {
    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var menu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        shrd = getSharedPreferences("savefile", Context.MODE_PRIVATE)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        menu = findViewById(R.id.nav)

        firstName.text = shrd.getString("firstName", "")
        lastName.text = shrd.getString("lastName", "")



        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.addEmployee -> {
                    startActivity(Intent(this,AddEmployee::class.java))
                    true
                }

                R.id.courierList -> {

                    true
                }

                R.id.reports -> {

                    true
                }

                else -> false
            }
            true
        }



    }
}