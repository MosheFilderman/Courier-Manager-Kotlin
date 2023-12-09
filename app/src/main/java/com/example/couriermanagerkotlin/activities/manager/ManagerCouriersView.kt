package com.example.couriermanagerkotlin.activities.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.assignOrdersToCouriers
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.couriers
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getAllCouriers
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.activities.EditUserDetails
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getAllCustomers
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getAllShipments
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getAvailablePickupCities
import com.google.android.material.navigation.NavigationView

class ManagerCouriersView : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    lateinit var shrd: SharedPreferences
    lateinit var courierList: ListView
    lateinit var emptyListMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_couriers_view)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchOrder -> {
                    startActivity(Intent(this@ManagerCouriersView, ManagerOrdersView::class.java))
                    true
                }
                R.id.addEmployee -> {
                    startActivity(Intent(this@ManagerCouriersView, AddEmployee::class.java))
                    true
                }

                R.id.assignOrders -> {
                    assignOrdersToCouriers(this@ManagerCouriersView)
                    true
                }

                R.id.reports -> {
                    startActivity(Intent(this@ManagerCouriersView, ManagerReports::class.java))
                    true
                }

                R.id.editInfo -> {
                    startActivity(Intent(this@ManagerCouriersView, EditUserDetails::class.java))
                    drawerLayout.close()
                    true
                }

                R.id.setMeasures -> {
                    startActivity(Intent(this@ManagerCouriersView, ManagerSetMeasures::class.java))
                    true
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
                    true
                }

                else -> false
            }
        }

        userFullName = headerView.findViewById(R.id.userFullName)
        userEmail = headerView.findViewById(R.id.userEmail)

        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        val strUserFullName =
            "${shrd.getString("firstName", "Not")} ${shrd.getString("lastName", "Signed !")}"
        userFullName.text = strUserFullName
        userEmail.text = shrd.getString("email", "courierManager@courierManager")

        courierList = findViewById(R.id.couriersListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        courierList.setOnItemClickListener { parent, view, position, id ->
            intent = Intent(this@ManagerCouriersView, ShipmentsByCourier::class.java)
            intent.putExtra("chosenCourier", couriers[position])
            startActivity(intent)
        }

        getAllCouriers(this@ManagerCouriersView, courierList, emptyListMsg)
        getAllCustomers(this@ManagerCouriersView)
        getAvailablePickupCities(this@ManagerCouriersView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}