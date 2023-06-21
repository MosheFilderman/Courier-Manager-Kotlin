package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.couriermanagerkotlin.DButilities.Companion.getCustomerOrders
import com.example.couriermanagerkotlin.DButilities.Companion.orders
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
                val editor: SharedPreferences.Editor = shrd.edit()
                editor.putBoolean("connected", false)
                editor.putString("firstName", "")
                editor.putString("lastName", "")
                editor.putString("email", "")
                editor.putString("eRole", "")
                editor.apply()

                startActivity(Intent(this@CustomerOrderList, Login::class.java))
                emptyListMsg.visibility = View.GONE
                orderList.visibility = View.GONE
                finish()
                true
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
        orderList.setOnItemClickListener { parent, view, position, id ->
            var builder = AlertDialog.Builder(this)
            var inflater = layoutInflater
            var dialogLayout = inflater.inflate(R.layout.order_full_info, null)
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
                DButilities.deleteOrder(this@CustomerOrderList, orderId, eStatus.CANCELLED)
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
                    //finish()
                    true
                }

                R.id.orderList -> {
                    Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
            true
        }

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