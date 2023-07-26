package com.example.couriermanagerkotlin

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
import com.example.couriermanagerkotlin.DBUtilities.Companion.getCustomerOrders
import com.example.couriermanagerkotlin.DBUtilities.Companion.orders
import com.google.android.material.bottomnavigation.BottomNavigationView


class CustomerOrderList : AppCompatActivity() {
    lateinit var shrd: SharedPreferences
    lateinit var emptyListMsg: TextView
    lateinit var orderList: ListView
    lateinit var menu: BottomNavigationView
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var search: SearchView
    var searchOrderList = ArrayList<Order>()


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
                builder.setPositiveButton("YES") { dialogInterface, _ ->
                    val shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = shrd.edit()
                    editor.clear()
                    editor.commit()
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
        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        firstName.text = shrd.getString("firstName", "")
        lastName.text = shrd.getString("lastName", "")
        search = findViewById(R.id.search)
        emptyListMsg = findViewById(R.id.emptyListMsg)
        menu = findViewById(R.id.nav)

        orderList = findViewById(R.id.orderList)
        // Click on order open dialog with all the order details
        orderList.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.order_full_info, null)
            orderList.visibility = View.VISIBLE

            builder.setView(dialogLayout)

            val name: TextView = dialogLayout.findViewById(R.id.firstColumn)
            val phone: TextView = dialogLayout.findViewById(R.id.secondColumn)
            val email: TextView = dialogLayout.findViewById(R.id.thirdColumn)
            val status: TextView = dialogLayout.findViewById(R.id.fourthColumn)
            val pickupAddress: TextView = dialogLayout.findViewById(R.id.fifthColumn)
            val deliveryAddress: TextView = dialogLayout.findViewById(R.id.sixthColumn)
            val comment: TextView = dialogLayout.findViewById(R.id.seventhColumn)
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
                DBUtilities.cancelOrder(this@CustomerOrderList, orderId, eStatus.CANCELLED)
                orders.removeAt(position)
                if(orders.isEmpty()) {
                    emptyListMsg.visibility = View.VISIBLE
                    emptyListMsg.text = "Your order list still empty"
                } else {
                    orderList.visibility = View.VISIBLE
                    orderList.adapter = OrderListView(this, orders)
                }
            }

            builder.setNegativeButton("Close") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }

        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newOrder -> {
                    startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
                    emptyListMsg.visibility = View.GONE
                    orderList.visibility = View.GONE
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

        //MUHAMAD
        getCustomerOrders(this@CustomerOrderList, orderList, emptyListMsg, shrd.getString("email","none").toString())

        orderList.adapter = OrderListView(this, orders)

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
                    orderList.adapter = OrderListView(this@CustomerOrderList, searchOrderList)
                    return false
                }
            })
    }
}