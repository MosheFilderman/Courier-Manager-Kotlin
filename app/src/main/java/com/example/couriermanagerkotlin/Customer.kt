package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Customer : AppCompatActivity() {

    lateinit var navigationBar: BottomNavigationView
    lateinit var selected: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        supportFragmentManager.beginTransaction().replace(R.id.frameContainer, CustomerOrderListFragment()).commit()

        navigationBar = findViewById(R.id.navigationBar)
        navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newOrder -> {
                    selected = NewOrderFragment()
                    true
                }
                R.id.orderList -> {
                    selected = CustomerOrderListFragment()
                    true
                }
                R.id.logout -> {
                    startActivity(Intent(this@Customer, Login::class.java))
                    finish()
                    true
                } else -> false
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.frameContainer, selected).commit()
        true
    }
}