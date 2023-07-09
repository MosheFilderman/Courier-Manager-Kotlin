package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView


class CourierMapView : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var navigationView: BottomNavigationView
    lateinit var mapView: MapView

    /* Menu toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_search_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val editor: SharedPreferences.Editor = shrd.edit()
                editor.putBoolean("connected", false)
                editor.putString("firstName", "")
                editor.putString("lastName", "")
                editor.putString("email", "")
                editor.putString("eRole", "")
                editor.apply()
                startActivity(Intent(this@CourierMapView, Login::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_map_view)
        var shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)

        navigationView = findViewById(R.id.nav)
        navigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.listView->{
                    startActivity(Intent(this@CourierMapView, CourierListView::class.java))
                    true
                }
                R.id.mapView->{
                    Toast.makeText(this@CourierMapView, "You already at this page!", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
            true
        }

        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
    }
}