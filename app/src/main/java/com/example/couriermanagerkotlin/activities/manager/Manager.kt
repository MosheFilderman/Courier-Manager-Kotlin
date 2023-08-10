package com.example.couriermanagerkotlin.activities.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.example.couriermanagerkotlin.DBUtilities.Companion.assignOrders
import com.example.couriermanagerkotlin.DBUtilities.Companion.couriers
import com.example.couriermanagerkotlin.DBUtilities.Companion.getAllCouriers
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class Manager : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var courierList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var menu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
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
                R.id.addEmployee -> {
                    startActivity(Intent(this@Manager, AddEmployee::class.java))
                    true
                }

                R.id.assignOrders -> {
                    assignOrders(this@Manager)
                    true
                }

                R.id.reports -> {
                    true
                }

                R.id.settings -> {
                    startActivity(Intent(this@Manager, AppSettings::class.java))
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
            intent = Intent(this@Manager, ShipmentsByCourier::class.java)
            intent.putExtra("chosenCourier", couriers[position])
            startActivity(intent)
        }
        getAllCouriers(this@Manager, courierList, emptyListMsg)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}