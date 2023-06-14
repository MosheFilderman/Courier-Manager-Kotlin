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
    lateinit var search: EditText


    var contactsNames = mutableListOf<String>()
    var contactsPhones = mutableListOf<String>()
    var contactsEmails = mutableListOf<String>()
    var ordersStatus = mutableListOf<String>()
    var ordersComment = mutableListOf<String>()

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

        var adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsEmails)
        orderList.adapter = adapter


        getCustomerOrders()

//        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (contactsEmails.contains(query)) {
//                    adapter.filter.filter(query)
//                    Toast.makeText(
//                        this@CustomerOrderList,
//                        contactsEmails.contains(query).toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
////                   Toast.makeText(this@CustomerOrderList,contactsEmails.toString(),Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(
//                        this@CustomerOrderList,
//                        contactsEmails.contains(query).toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
////                    Toast.makeText(this@CustomerOrderList,contactsEmails.toString(),Toast.LENGTH_LONG).show()
//                }
//                return false
//            }

//            override fun onQueryTextChange(p0: String?): Boolean {
//                adapter.filter.filter(p0)
////                Toast.makeText(this@CustomerOrderList,"text change",Toast.LENGTH_LONG).show()
//                return false
//            }
//
//
//        })

//        search.addTextChangedListener(object: TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                adapter.filter.filter(p0)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

    fun getCustomerOrders() {
        val url: String = "http://192.168.93.141/courier_project/getCustomerOrders.php"

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (!response.toString().trim().equals("empty")) {
                    orderList.visibility = View.VISIBLE
                    val strRes = response.toString()
                    val jsonArray = JSONArray(strRes)
                    val jsonResponse = jsonArray.getJSONObject(0)
                    val jsonArrayOrders = jsonResponse.getJSONArray("orders")

                    for (i in 0 until jsonArrayOrders.length()) {
                        var jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                        contactsNames.add(jsonInner.get("contactName").toString())
                        contactsPhones.add(jsonInner.get("contactPhone").toString())
                        contactsEmails.add(jsonInner.get("contactEmail").toString())
                        ordersStatus.add(jsonInner.get("eStatus").toString())
                        ordersComment.add(jsonInner.get("comment").toString())
                    }

                    orderList.adapter = OrderListView(
                        this,
                        contactsNames as ArrayList<String>,
                        contactsEmails as ArrayList<String>,
                        contactsPhones as ArrayList<String>,
                        ordersStatus as ArrayList<String>,
                        ordersComment as ArrayList<String>
                    )
                } else {
                    emptyListMsg.visibility = View.VISIBLE
                    emptyListMsg.text = "Your order list still empty"
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@CustomerOrderList, error.toString(), Toast.LENGTH_SHORT)
                    .show()
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


    fun search() {

    }
}
