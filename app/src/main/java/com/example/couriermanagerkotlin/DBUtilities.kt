package com.example.couriermanagerkotlin

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class DBUtilities {

    companion object {
        const val ipv4Address: String = "10.0.0.7"
        var orders = ArrayList<Order>()
        var streets = ArrayList<String>()

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
                        val jsonInner: JSONObject = jsonArrayUser.getJSONObject(0)
                        val editor: SharedPreferences.Editor = shrd!!.edit()
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


        fun cancelOrder(context: Context, orderId: String, status: eStatus) {
            val url: String = "http://${ipv4Address}/courier_project/deleteOrder.php"
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
            orderList: ListView,
            emptyListMsg: TextView,
            email: String
        ) {
            val url: String = "http://${ipv4Address}/courier_project/getCustomerOrders.php"
            orders.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("no data")) {
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
                                jsonInner.get("pickupCity").toString(),
                                jsonInner.get("pickupStreet").toString(),
                                jsonInner.get("pickupBuild").toString(),
                                jsonInner.get("deliveryCity").toString(),
                                jsonInner.get("deliveryStreet").toString(),
                                jsonInner.get("deliveryBuild").toString(),
                                jsonInner.get("comment").toString()
                            )
                            orders.add(tmpOrder)
                        }
                    } else {
                        emptyListMsg.visibility = View.VISIBLE
                        emptyListMsg.text = "Your order list still empty"
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

        fun createOrder(context: Context, order: Order, userEmail: String, errorMassage: TextView) {
            val url: String = "http://${ipv4Address}/courier_project/newOrder.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    errorMassage.text = response
                }, Response.ErrorListener { error ->
                    errorMassage.visibility = View.VISIBLE
                    errorMassage.text = error.toString()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["orderId"] = order.orderId
                        params["email"] = userEmail
                        params["contactName"] = order.name
                        params["contactPhone"] = order.phone
                        params["contactEmail"] = order.email
                        params["eStatus"] = order.status.name
                        params["pickupCity"] = order.pickupCity
                        params["pickupStreet"] = order.pickupStreet
                        params["pickupBuild"] = order.pickupBuild
                        params["deliveryCity"] = order.deliveryCity
                        params["deliveryStreet"] = order.deliveryStreet
                        params["deliveryBuild"] = order.deliveryBuild
                        params["comment"] = order.comment

                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }


        fun getStreetByCity(context: Context, city: String) {
            val url: String = "http://${ipv4Address}/courier_project/getStreetByCity.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("error")) {
                        streets.clear()
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayStreets = jsonResponse.getJSONArray("streets")
                        for (i in 0 until jsonArrayStreets.length()) {
                            var jsonInner: JSONObject = jsonArrayStreets.getJSONObject(i)
                            streets.add(jsonInner.get("streetName").toString())
                        }
                    } else {
                        Toast.makeText(context, "There isn't any street that match this name.", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["cityName"] = city
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

    }
}
