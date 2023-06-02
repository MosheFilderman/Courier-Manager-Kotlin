package com.example.couriermanagerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Courier : AppCompatActivity() {

    lateinit var navigationView: BottomNavigationView
    lateinit var selected:Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier)
        supportFragmentManager.beginTransaction().replace(R.id.listmapview,CourierMapFragment()).commit()

        navigationView = findViewById(R.id.nav)
        navigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.listView->{
                    selected= CourierOrderListFragment()
                    true
                }
                R.id.mapView->{
                    selected = CourierMapFragment()
                    true
                }

                else -> false
            }
            supportFragmentManager.beginTransaction().replace(R.id.listmapview,CourierMapFragment()).commit()
            true
        }
    }
}