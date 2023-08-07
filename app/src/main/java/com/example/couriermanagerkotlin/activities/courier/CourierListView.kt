package com.example.couriermanagerkotlin.activities.courier

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.couriermanagerkotlin.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CourierListView : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var shipmentsList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var navigationView: BottomNavigationView

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003

    /* Top Right corner logout button */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Exit")
                builder.setMessage("Are you sure you wish to logout?")
                builder.setIcon(R.drawable.baseline_close_24)
                builder.setPositiveButton("YES") { dialogInterface, i ->
                    val shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = shrd.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this@CourierListView, Login::class.java))
                    finish()
                }
                builder.setNegativeButton("NO") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_list_view)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        shipmentsList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        shrd= getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName.text = shrd.getString("firstName", "Not")
        lastName.text = shrd.getString("lastName", "Signed!")

        navigationView = findViewById(R.id.nav)
        navigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.listView ->{
                    true
                }
                R.id.mapView ->{
                    startActivity(Intent(this@CourierListView, CourierMapView::class.java))
                    finish()
                    true
                }
                R.id.calculateRoute -> {
                    // Add the function which calculate the route
                    true
                }
                else -> false
            }
            true
        }
        navigationView.selectedItemId = R.id.listView

        // Check if there's permission to use location, if isn't request the permission
        if(ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }

        getShipmentsByCourier(this, shrd.getString("email", "none").toString(), shipmentsList, emptyListMsg)
    }
}