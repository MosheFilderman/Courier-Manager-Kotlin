package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class CourierListView : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var navigationView: BottomNavigationView
    lateinit var firstName: TextView
    lateinit var lastName: TextView

    /* Menu toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_search_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.logout -> {
                var editor: SharedPreferences.Editor = shrd!!.edit()
                editor.putBoolean("connected", false)
                editor.putString("firstName", "")
                editor.putString("lastName", "")
                editor.putString("email", "")
                editor.putString("eRole", "")
                editor.commit()
                startActivity(Intent(this@CourierListView, Login::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_list_view)
        var shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        firstName.text = shrd.getString("firstName", "")
        lastName.text = shrd.getString("lastName", "")

        navigationView = findViewById(R.id.nav)
        navigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.listView->{
                    Toast.makeText(this@CourierListView, "You already at this page!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.mapView->{
                    startActivity(Intent(this@CourierListView, CourierMapView::class.java))
                    true
                }
                else -> false
            }
            true
        }
    }
}