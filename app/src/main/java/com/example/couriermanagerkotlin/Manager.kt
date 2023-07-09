package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class Manager : AppCompatActivity() {

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
                startActivity(Intent(this@Manager, Login::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var menu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        menu = findViewById(R.id.nav)

        firstName.text = shrd.getString("firstName", "")
        lastName.text = shrd.getString("lastName", "")

//        Toast.makeText(this,shrd.getString("firstName", "fff"),Toast.LENGTH_SHORT).show()
//        Toast.makeText(this,shrd.getString("lastName", "lll"),Toast.LENGTH_SHORT).show()
//        Toast.makeText(this,shrd.getString("email", "eee"),Toast.LENGTH_SHORT).show()
//        Toast.makeText(this,shrd.getString("phone", "ppp"),Toast.LENGTH_SHORT).show()
//        Toast.makeText(this,shrd.getBoolean("connected", false).toString(),Toast.LENGTH_SHORT).show()



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