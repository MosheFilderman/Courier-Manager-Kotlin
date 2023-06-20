package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class DButilities {

    companion object {
        val ipv4Address: String = "10.100.102.234"

        fun registerUser(
            context: Context,
            firstName: String,
            lastName: String,
            email: String,
            phone: String
        ) {
            val url: String = "http://${ipv4Address}/courier_project/registration.php"
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(context, Login::class.java))
//                    finish()
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["firstName"] = firstName
                    params["lastName"] = lastName
                    params["email"] = email
                    params["phone"] = "+972" + phone.substring(1)
                    params["eRole"] = eRole.CUSTOMER.name
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }


        fun registerEmployee(
            context: Context,
            firstName: String,
            lastName: String,
            email: String,
            phone: String,
            eRole: String
        ) {
            val url: String = "http://${ipv4Address}/courier_project/registration.php"
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this@AddEmployee, Login::class.java))
//                    finish()
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["firstName"] = firstName
                    params["lastName"] = lastName
                    params["email"] = email
                    params["phone"] = "+972" + phone.substring(1)
                    params["eRole"] = eRole
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }


        fun login(
            context: Context,
            email: String,
            phone: String,
            shrd: SharedPreferences
        ) {
            val url: String = "http://${ipv4Address}/courier_project/login.php"

            val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    if (response.toString().trim().compareTo("error") != 0) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayUser = jsonResponse.getJSONArray("users")
                        var jsonInner: JSONObject = jsonArrayUser.getJSONObject(0)
                        var editor: SharedPreferences.Editor = shrd!!.edit()
                        editor.putString("firstName", jsonInner.get("firstName").toString())
                        editor.putString("lastName", jsonInner.get("lastName").toString())
                        editor.putString("email", jsonInner.get("email").toString())
                        editor.putString("phone", jsonInner.get("phone").toString())
                        editor.putString("eRole", jsonInner.get("eRole").toString())
                        editor.putBoolean("connected", true)
                        editor.apply()

                    } else {
                        Toast.makeText(
                            context,
                            "Email/Phone Ara Not Exist  ", Toast.LENGTH_SHORT
                        ).show()

                    }
                },
                Response.ErrorListener { error ->


                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["email"] = email
                    params["phone"] = "+972" + phone.substring(1)
                    return params
                }

            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)

        }


        fun deleteOrder(context: Context, orderId: String, status: eStatus) {
            val url: String = "http://10.100.102.234/courier_project/deleteOrder.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["orderId"] = orderId
                        params["status"] = status.name
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)


        }


        fun getCustomerOrders(
            context: Context,
            email: String,
            orders: ArrayList<Order>
        ) {
            val url: String = "http://${ipv4Address}/courier_project/getCustomerOrders.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        //  orderList.visibility = View.VISIBLE
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
//                        emptyListMsg.visibility = View.VISIBLE
//                        emptyListMsg.text = "Your order list still empty"
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["email"] = email
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)


        }
    }
}