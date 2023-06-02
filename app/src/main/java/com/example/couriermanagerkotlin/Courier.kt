package com.example.couriermanagerkotlin

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Courier : AppCompatActivity() {

    lateinit var navigationView: BottomNavigationView
    lateinit var selected:Fragment
    lateinit var firstName:TextView
    lateinit var lastName:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier)
        var data: SharedPreferences = getSharedPreferences("savefile", Context.MODE_PRIVATE)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        firstName.text = data.getString("firstName", "no data")
        lastName.text = data.getString("lastName", "no data")
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