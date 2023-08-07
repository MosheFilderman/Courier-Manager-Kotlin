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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.couriermanagerkotlin.DBUtilities.Companion.cancelOrder
import com.example.couriermanagerkotlin.DBUtilities.Companion.getCustomerOrders
import com.example.couriermanagerkotlin.DBUtilities.Companion.orders
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.Order
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.eStatus
import com.example.couriermanagerkotlin.listViewAdapters.OrdersAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class CustomerOrderList : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var ordersList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var menu: BottomNavigationView
    lateinit var search: SearchView
    var searchOrderList = ArrayList<Order>()


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
                builder.setPositiveButton("YES") { dialogInterface, _ ->
                    val shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)
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
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_order_list)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)

        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName.text = shrd.getString("firstName", "")
        lastName.text = shrd.getString("lastName", "")

        search = findViewById(R.id.search)
        ordersList = findViewById(R.id.orderList)
        emptyListMsg = findViewById(R.id.emptyListMsg)
        menu = findViewById(R.id.nav)

        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newOrder -> {
                    startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
                    emptyListMsg.visibility = View.GONE
                    ordersList.visibility = View.GONE
                    true
                }

                R.id.orderList -> {
                    true
                }
                else -> false
            }
            true
        }
        menu.selectedItemId = R.id.orderList

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

            val strPickupAddress: String = orders[position].pickupCity + "," + orders[position].pickupStreet + " " + orders[position].deliveryBuild
            val strDeliveryAddress: String = "${orders[position].deliveryCity}, ${orders[position].deliveryStreet} ${orders[position].deliveryBuild}"

            val orderId = orders[position].orderId

            name.text = orders[position].name
            phone.text = orders[position].phone
            email.text = orders[position].email
            status.text = orders[position].status.name
            pickupAddress.text = strPickupAddress
            deliveryAddress.text = strDeliveryAddress
            comment.text = orders[position].comment


            builder.setPositiveButton("Cancel Order") { dialogInterface, i ->
                cancelOrder(this@CustomerOrderList, orderId, eStatus.CANCELLED)
                orders.removeAt(position)
                isListEmpty(orders, emptyListMsg, ordersList)
            }

            builder.setNegativeButton("Close") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }

        //MUHAMAD
        getCustomerOrders(this@CustomerOrderList, ordersList, emptyListMsg, shrd.getString("email","none").toString())

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

    private fun isListEmpty(list: ArrayList<Order>, emptyListMsg: TextView, listView: ListView) {
        if(list.isEmpty()) {
            emptyListMsg.visibility = View.VISIBLE
            emptyListMsg.text = getString(R.string.customer_empty_order_list)
        } else {
            listView.visibility = View.VISIBLE
            listView.adapter = OrdersAdapter(this, list)
        }
    }
}