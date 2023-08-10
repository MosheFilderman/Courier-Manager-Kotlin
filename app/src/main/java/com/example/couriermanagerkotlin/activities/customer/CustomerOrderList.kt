package com.example.couriermanagerkotlin.activities.customer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.couriermanagerkotlin.DBUtilities
import com.example.couriermanagerkotlin.DBUtilities.Companion.getCustomerOrders
import com.example.couriermanagerkotlin.DBUtilities.Companion.orders
import com.example.couriermanagerkotlin.DBUtilities.Companion.updateOrderStatus
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.objects.Order
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.activities.manager.AddEmployee
import com.example.couriermanagerkotlin.activities.manager.AppSettings
import com.example.couriermanagerkotlin.eStatus
import com.example.couriermanagerkotlin.listViewAdapters.OrdersAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class CustomerOrderList : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var ordersList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var menu: BottomNavigationView
    lateinit var search: SearchView
    var searchOrderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_order_list)

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
                R.id.newOrder -> {
                    startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
                    emptyListMsg.visibility = View.GONE
                    ordersList.visibility = View.GONE
                    true
                }

                R.id.orderList -> {
                    true
                }

                R.id.logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Exit")
                    builder.setMessage("Are you sure you wish to logout?")
                    builder.setIcon(R.drawable.baseline_close_24)
                    builder.setPositiveButton("YES") { dialogInterface, _ ->
                        val shrd: SharedPreferences =
                            getSharedPreferences("shola", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = shrd.edit()
                        editor.clear()
                        editor.apply()
                        startActivity(Intent(this@CustomerOrderList, Login::class.java))
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

        search = findViewById(R.id.search)
        ordersList = findViewById(R.id.orderList)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        // Click on order open dialog with all the order details
        ordersList.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.customer_order_full_info, null)
            ordersList.visibility = View.VISIBLE

            builder.setView(dialogLayout)

            val name: TextView = dialogLayout.findViewById(R.id.name)
            val phone: TextView = dialogLayout.findViewById(R.id.phone)
            val email: TextView = dialogLayout.findViewById(R.id.email)
            val status: TextView = dialogLayout.findViewById(R.id.orderStatus)
            val pickupAddress: TextView = dialogLayout.findViewById(R.id.pickupAddress)
            val deliveryAddress: TextView = dialogLayout.findViewById(R.id.deliveryAddress)
            val comment: TextView = dialogLayout.findViewById(R.id.comment)

            val strPickupAddress: String =
                orders[position].pickupCity + "," + orders[position].pickupStreet + " " + orders[position].deliveryBuild
            val strDeliveryAddress: String =
                "${orders[position].deliveryCity}, ${orders[position].deliveryStreet} ${orders[position].deliveryBuild}"

            val orderId = orders[position].orderId

            name.text = orders[position].name
            phone.text = orders[position].phone
            email.text = orders[position].email
            status.text = orders[position].status.name
            pickupAddress.text = strPickupAddress
            deliveryAddress.text = strDeliveryAddress
            comment.text = orders[position].comment

            if (orders[position].status.name.equals("NEW")) {
                builder.setPositiveButton("Cancel Order") { dialogInterface, i ->
                    updateOrderStatus(this@CustomerOrderList, orderId, eStatus.CANCELLED)
                    orders.removeAt(position)
                    isListEmpty(orders, emptyListMsg, ordersList)
                }
            }

            builder.setNegativeButton("Close") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }

        getCustomerOrders(
            this@CustomerOrderList,
            ordersList,
            emptyListMsg,
            shrd.getString("email", "none").toString()
        )

        search.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    searchOrderList.clear()
                    for (tmpOrder in orders) {
                        if (tmpOrder.email.lowercase().startsWith(p0!!.lowercase())) {
                            searchOrderList.add(tmpOrder)
                        }
                    }
                    ordersList.adapter = OrdersAdapter(this@CustomerOrderList, searchOrderList)
                    return false
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isListEmpty(list: ArrayList<Order>, emptyListMsg: TextView, listView: ListView) {
        if (list.isEmpty()) {
            emptyListMsg.visibility = View.VISIBLE
            emptyListMsg.text = getString(R.string.customer_empty_order_list)
        } else {
            listView.visibility = View.VISIBLE
            listView.adapter = OrdersAdapter(this, list)
        }
    }
}