package com.example.couriermanagerkotlin.activities.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.couriermanagerkotlin.DBUtilities.Companion.assignOrders
import com.example.couriermanagerkotlin.DBUtilities.Companion.couriers
import com.example.couriermanagerkotlin.DBUtilities.Companion.getAllCouriers
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Manager : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var courierList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var menu: BottomNavigationView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manager_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addEmployee -> {
                startActivity(Intent(this@Manager, AddEmployee::class.java))
            }
            R.id.assignOrders -> {
                assignOrders(this@Manager)
            }
            R.id.reports -> {

            }
            R.id.settings -> {

            }
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Exit")
                builder.setMessage("Are you sure you wish to logout?")
                builder.setIcon(R.drawable.baseline_close_24)
                builder.setPositiveButton("YES") { dialogInterface, i ->
                    val editor: SharedPreferences.Editor = shrd.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this, Login::class.java))
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
        setContentView(R.layout.activity_manager)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)

        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName.text = shrd.getString("firstName", "Not")
        lastName.text = shrd.getString("lastName", "Signed!")

        courierList = findViewById(R.id.couriersListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        courierList.setOnItemClickListener { parent, view, position, id ->
            intent = Intent(this@Manager, ShipmentsByCourier::class.java)
            intent.putExtra("chosenCourier", couriers[position])
            startActivity(intent)
        }

        getAllCouriers(this@Manager, courierList, emptyListMsg)
    }
}