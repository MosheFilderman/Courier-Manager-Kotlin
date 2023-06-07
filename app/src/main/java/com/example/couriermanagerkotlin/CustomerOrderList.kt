package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


class CustomerOrderList : AppCompatActivity() {
    lateinit var shrd: SharedPreferences

    /* Menu toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.customer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newOrder -> {
                startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
                finish()
            }

            R.id.orderList -> Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT)
                .show()

            R.id.logout -> {
                var editor: SharedPreferences.Editor = shrd!!.edit()
                editor.putBoolean("connected", false)
                editor.putString("firstName", "")
                editor.putString("lastName", "")
                editor.putString("email", "")
                editor.putString("eRole", "")
                editor.commit()
                startActivity(Intent(this@CustomerOrderList, Login::class.java))
                finish()


            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_order_list)
        shrd = getSharedPreferences("savefile", Context.MODE_PRIVATE)

    }

    fun getCustomerOrders(view: View) {
        val url: String = "http://10.100.102.234/courier_project/getCustomerOrders.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->

                if (!response.toString().trim().equals("error")) {
                    val strRes = response.toString()
                    val jsonArray = JSONArray(strRes)
                    val jsonResponse = jsonArray.getJSONObject(0)
                    val jsonArray_user = jsonResponse.getJSONArray("orders")
                    var jsonInner: JSONObject = jsonArray_user.getJSONObject(0)


                } else {
                    Toast.makeText(this@CustomerOrderList, response.toString(), Toast.LENGTH_SHORT)
                        .show()

                }
            },
            Response.ErrorListener { error ->

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

data class OrderList(
    var id: Int,
    var contactName: String,
    val contactEmail: String?,
    var ContactPhone: String?
) : Serializable