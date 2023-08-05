package com.example.couriermanagerkotlin.activities.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.couriermanagerkotlin.DBUtilities.Companion.orders
import com.example.couriermanagerkotlin.DBUtilities.Companion.getAllCouriers
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.Order
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.listViewAdapters.OrdersAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class Manager : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var courierList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var menu: BottomNavigationView

    /* Alert dialog views list */
    lateinit var shipmentListView: ListView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_search_menu, menu)
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
                    Toast.makeText(this@Manager, "in logout", Toast.LENGTH_SHORT).show()
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
        menu = findViewById(R.id.nav)

        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.courierList -> {
                    true
                }

                R.id.addEmployee -> {
                    startActivity(Intent(this, AddEmployee::class.java))
                    true
                }

                R.id.reports -> {

                    true
                }
                else -> false
            }
            true
        }
        menu.selectedItemId = R.id.courierList

        courierList.setOnItemClickListener { parent, view, position, id ->

            if(!isListEmpty(orders, emptyListMsg, shipmentListView)) {
                shipmentListView.setOnItemClickListener { parent, view, position, id ->
                    val shipmentListBuilder = AlertDialog.Builder(this)
                    val shipmentListInflater = layoutInflater
                    val shipmentListDialogLayout = shipmentListInflater.inflate(R.layout.courier_shipment_info, null)

                    shipmentListBuilder.setView(shipmentListDialogLayout)


                }
            }
        }

        getAllCouriers(this@Manager, courierList, emptyListMsg)
        isListEmpty(orders, emptyListMsg, shipmentListView)
    }

    private fun isListEmpty(list: ArrayList<Order>, emptyListMsg: TextView, listView: ListView): Boolean {
        return if(list.isEmpty()) {
                    emptyListMsg.visibility = View.VISIBLE
                    emptyListMsg.text = getString(R.string.courier_empty_shipment_list)
                    true
                } else {
                    listView.visibility = View.VISIBLE
                    listView.adapter = OrdersAdapter(this, list)
                    false
                }
    }
}