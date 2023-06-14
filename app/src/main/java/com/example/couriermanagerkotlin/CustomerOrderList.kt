package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.SearchView.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import android.widget.SearchView.OnQueryTextListener as OnQueryTextListener1


class CustomerOrderList : AppCompatActivity() {
    lateinit var shrd: SharedPreferences
    lateinit var emptyListMsg: TextView
    lateinit var orderList: ListView
    lateinit var menu: BottomNavigationView
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var search: SearchView

    var orders = ArrayList<Order>()
    var searchOrderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_order_list)
        shrd = getSharedPreferences("savefile", Context.MODE_PRIVATE)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        firstName.text = shrd.getString("firstName", "")
        lastName.text = shrd.getString("lastName", "")
        search = findViewById(R.id.search)

        orderList = findViewById(R.id.orderList)
        orderList.setOnItemClickListener { parent, view, position, id ->
            var builder = AlertDialog.Builder(this)
            var inflater = layoutInflater
            var dialogLayout = inflater.inflate(R.layout.list_view, null)

            builder.setView(dialogLayout)

            val name: TextView = dialogLayout.findViewById(R.id.firstColumn)
            val phone: TextView = dialogLayout.findViewById(R.id.secondColumn)
            val email: TextView = dialogLayout.findViewById(R.id.thirdColumn)
            val status: TextView = dialogLayout.findViewById(R.id.fourthColumn)
            val comment: TextView = dialogLayout.findViewById(R.id.fifthColumn)

            name.text = orders[position].name
            phone.text = orders[position].phone
            email.text = orders[position].email
            status.text = orders[position].status.name
            comment.text = orders[position].comment

            builder.setPositiveButton("Cancel Order"){dialogInterface, i ->
                // change current order status to CANCELED
            }

            builder.setNegativeButton("Close"){dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }

        emptyListMsg = findViewById(R.id.emptyListMsg)
        menu = findViewById(R.id.nav)

        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newOrder -> {
                    startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
                    emptyListMsg.visibility = View.GONE
                    orderList.visibility = View.GONE
                    finish()
                    true
                }

                R.id.orderList -> {
                    Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT).show()
                    true
                }

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
                else -> false
            }
            true
        }

        getCustomerOrders()

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

    fun getCustomerOrders() {
        val url: String = "http://10.0.0.7/courier_project/getCustomerOrders.php"

        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                if (!response.toString().trim().equals("empty")) {
                    orderList.visibility = View.VISIBLE
                    val strRes = response.toString()
                    val jsonArray = JSONArray(strRes)
                    val jsonResponse = jsonArray.getJSONObject(0)
                    val jsonArrayOrders = jsonResponse.getJSONArray("orders")

                    for (i in 0 until jsonArrayOrders.length()) {
                        var jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                        var tmpOrder = Order(
                            jsonInner.get("order_id").toString(),
                            jsonInner.get("contactName").toString(),
                            jsonInner.get("contactPhone").toString(),
                            jsonInner.get("contactEmail").toString(),
                            eStatus.findStatus(jsonInner.get("eStatus").toString()),
                            jsonInner.get("comment").toString()
                        )
                        orders.add(tmpOrder)
                    }
                } else {
                    emptyListMsg.visibility = View.VISIBLE
                    emptyListMsg.text = "Your order list still empty"
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this@CustomerOrderList, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["email"] = shrd.getString("email", "none").toString()
                    return params
                }
            }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}