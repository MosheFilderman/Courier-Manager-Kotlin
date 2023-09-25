package com.example.couriermanagerkotlin.utilities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.User
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.activities.manager.Manager
import com.example.couriermanagerkotlin.eRole
import com.example.couriermanagerkotlin.eStatus
import com.example.couriermanagerkotlin.listViewAdapters.CouriersAdapter
import com.example.couriermanagerkotlin.listViewAdapters.OrdersAdapter
import com.example.couriermanagerkotlin.listViewAdapters.ShipmentsAdapter
import com.example.couriermanagerkotlin.objects.Measures
import com.example.couriermanagerkotlin.objects.Order
import com.example.couriermanagerkotlin.objects.Shipment
import org.json.JSONArray
import org.json.JSONObject

class DBUtilities {

    companion object {
        const val ipv4Address: String = "10.0.0.7"
        var measures = Measures(-1, -1, -1, -1)
        var orders = ArrayList<Order>()
        var streets = ArrayList<String>()
        var couriers = ArrayList<User>()
        var customers = ArrayList<User>()
        var shipments = ArrayList<Shipment>()
        var routeAddresses = ArrayList<String>()
        var availablePickupCities = ArrayList<String>()

        /* User Utilities */

        /**
         * Receive user data after validation, send to PHP for creation of the CUSTOMER in the DB.
         */
        fun registerUser(
            context: Context,
            firstName: String,
            lastName: String,
            email: String,
            phone: String
        ) {
            val url: String = "http://$ipv4Address/courier_project/registration.php"
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
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

        /**
         * Receive user data after validation, send to PHP for creation of the COURIER/MANAGER in the DB.
         */
        fun registerEmployee(
            context: Context,
            firstName: String,
            lastName: String,
            email: String,
            phone: String,
            eRole: String
        ) {
            val url: String = "http://$ipv4Address/courier_project/registration.php"
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["firstName"] = firstName
                    params["lastName"] = lastName
                    params["email"] = email
                    params["phone"] = phone
                    params["eRole"] = eRole
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Receive updated user details after validation
         * and update in DB user information.
         */
        fun updateUserDetail(
            context: Context,
            firstName: String,
            lastName: String,
            email: String,
            phone: String,
            errorMessage: TextView
        ){
            val url: String = "http://$ipv4Address/courier_project/updateUserDetail.php"
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                    if(context is Activity) {
                        context.finish()
                    }
                },
                Response.ErrorListener { error ->
                    errorMessage.visibility = View.VISIBLE
                    errorMessage.text = error.toString()
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["firstName"] = firstName
                    params["lastName"] = lastName
                    params["email"] = email
                    params["phone"] = phone
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         *
         */
        fun suspendUser(
            context: Context,
            email: String,
            phone: String
        ) {
            val url: String = "http://$ipv4Address/courier_project/suspendUser.php"
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["email"] = email
                    params["phone"] = phone
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Receive user email and phone, check if it's exist in the DB.
         * if exist, return users data and fill with it the Shared preferences,
         * if doesn't exist throw message to user.
         */
        fun login(
            context: Context,
            email: String,
            phone: String,
            shrd: SharedPreferences
        ) {
            val url: String = "http://$ipv4Address/courier_project/login.php"

            val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    if (response.toString().trim().compareTo("error") != 0) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayUser = jsonResponse.getJSONArray("users")
                        val jsonInner: JSONObject = jsonArrayUser.getJSONObject(0)
                        val editor: SharedPreferences.Editor = shrd.edit()
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
                    Log.e("ERROR", error.toString())
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

        /**
         * Receive CUSTOMER email and return all the opened orders which created by him
         */
        fun getCustomerOrders(
            context: Context,
            orderList: ListView,
            emptyListMsg: TextView,
            email: String
        ) {
            val url: String = "http://$ipv4Address/courier_project/getCustomerOrders.php"
            orders.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("orders")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpOrder = Order(
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
                        orderList.visibility = View.VISIBLE
                        orderList.adapter = OrdersAdapter(context, orders)
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

        /**
         * Receive CUSTOMER email and new order data as Order object,
         * create new order in the DB.
         */
        fun createOrder(
            context: Context,
            order: Order,
            userEmail: String,
            errorMassage: TextView
        ) {
            val url: String = "http://$ipv4Address/courier_project/newOrder.php"
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

        /**
         * Receive city name,
         * return all the street's from this city from DB.
         */
        fun getStreetByCity(
            context: Context,
            city: String
        ) {
            val url: String = "http://$ipv4Address/courier_project/getStreetByCity.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("error")) {
                        streets.clear()
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayStreets = jsonResponse.getJSONArray("streets")
                        for (i in 0 until jsonArrayStreets.length()) {
                            val jsonInner: JSONObject = jsonArrayStreets.getJSONObject(i)
                            streets.add(jsonInner.get("streetName").toString())
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "There isn't any street that match this name.",
                            Toast.LENGTH_SHORT
                        ).show()
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

        /**
         * Return all the available courier's from DB.
         */
        fun getAllCouriers(
            context: Context,
            courierList: ListView,
            emptyListMsg: TextView
        ) {
            val url: String = "http://$ipv4Address/courier_project/getAllCouriers.php"
            couriers.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("couriers")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpCourier = User(
                                jsonInner.get("firstName").toString(),
                                jsonInner.get("lastName").toString(),
                                jsonInner.get("email").toString(),
                                jsonInner.get("phone").toString()
                            )
                            couriers.add(tmpCourier)
                        }
                        courierList.visibility = View.VISIBLE
                        courierList.adapter = CouriersAdapter(context, couriers)
                    } else {
                        emptyListMsg.visibility = View.VISIBLE
                        emptyListMsg.text = "All the courier's left..."
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Return all the customer's from DB.
         */
        fun getAllCustomers(
            context: Context,
        ) {
            val url: String = "http://$ipv4Address/courier_project/getAllCustomers.php"
            customers.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("customers")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpCustomer = User(
                                jsonInner.get("firstName").toString(),
                                jsonInner.get("lastName").toString(),
                                jsonInner.get("email").toString(),
                                jsonInner.get("phone").toString()
                            )
                            customers.add(tmpCustomer)
                        }
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Receive COURIER email,
         * return all the not closed shipments which had assign to him.
         */
        fun getShipmentsByCourier(
            context: Context,
            email: String,
            shipmentList: ListView,
            currentShipmentAmount: TextView,
            emptyListMsg: TextView
        ) {
            val url: String = "http://$ipv4Address/courier_project/getCourierShipment.php"
            shipments.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("shipments")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpShipment = Shipment(
                                jsonInner.get("order_id").toString(),
                                jsonInner.get("pickupFirstName").toString(),
                                jsonInner.get("pickupLastName").toString(),
                                jsonInner.get("pickupPhone").toString(),
                                jsonInner.get("pickupEmail").toString(),
                                jsonInner.get("pickupCity").toString(),
                                jsonInner.get("pickupStreet").toString(),
                                jsonInner.get("pickupBuild").toString(),
                                jsonInner.get("deliveryName").toString(),
                                jsonInner.get("deliveryPhone").toString(),
                                jsonInner.get("deliveryEmail").toString(),
                                jsonInner.get("deliveryCity").toString(),
                                jsonInner.get("deliveryStreet").toString(),
                                jsonInner.get("deliveryBuild").toString(),
                                eStatus.findStatus(jsonInner.get("orderStatus").toString()),
                                jsonInner.get("comment").toString()
                            )
                            shipments.add(tmpShipment)
                        }
                        currentShipmentAmount.text = shipments.size.toString()
                        shipmentList.visibility = View.VISIBLE
                        shipmentList.adapter = ShipmentsAdapter(context, shipments)
                        routeAddresses.clear()
                        getRouteAddresses()
                    } else {
                        emptyListMsg.visibility = View.VISIBLE
                        emptyListMsg.text = "No deliveries have been assigned to this courier"
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

        /**
         * Receive COURIER email,
         * return all the not closed shipments which had assign to him.*/
        fun getShipmentsByCourier(
            context: Context,
            email: String,
            shipmentList: ListView,
            emptyListMsg: TextView
        ) {
            val url: String = "http://$ipv4Address/courier_project/getCourierShipment.php"
            shipments.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("shipments")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpShipment = Shipment(
                                jsonInner.get("order_id").toString(),
                                jsonInner.get("pickupFirstName").toString(),
                                jsonInner.get("pickupLastName").toString(),
                                jsonInner.get("pickupPhone").toString(),
                                jsonInner.get("pickupEmail").toString(),
                                jsonInner.get("pickupCity").toString(),
                                jsonInner.get("pickupStreet").toString(),
                                jsonInner.get("pickupBuild").toString(),
                                jsonInner.get("deliveryName").toString(),
                                jsonInner.get("deliveryPhone").toString(),
                                jsonInner.get("deliveryEmail").toString(),
                                jsonInner.get("deliveryCity").toString(),
                                jsonInner.get("deliveryStreet").toString(),
                                jsonInner.get("deliveryBuild").toString(),
                                eStatus.findStatus(jsonInner.get("orderStatus").toString()),
                                jsonInner.get("comment").toString()
                            )
                            shipments.add(tmpShipment)
                        }
                        shipmentList.visibility = View.VISIBLE
                        shipmentList.adapter = ShipmentsAdapter(context, shipments)
                        routeAddresses.clear()
                        getRouteAddresses()
                    } else {
                        emptyListMsg.visibility = View.VISIBLE
                        emptyListMsg.text = "No deliveries have been assigned to this courier"
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


        /**
         * Receive eStatus update to and order id which update,
         * update order by order id to received status in DB.
         */
        fun updateOrderStatus(
            context: Context,
            orderId: String,
            status: eStatus
        ) {
            val url: String = "http://$ipv4Address/courier_project/updateOrderStatus.php"
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

        /**
         * Update Measures table in the DB, by the Measures object current data.
         */
        fun setMeasures(
            context: Context
        ) {
            val url: String = "http://$ipv4Address/courier_project/setMeasures.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["height"] = measures.height.toString()
                        params["width"] = measures.width.toString()
                        params["length"] = measures.length.toString()
                        params["weight"] = measures.weight.toString()
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Return current valid measures from the DB.
         */
        fun getMeasures(
            context: Context,
            height: EditText,
            width: EditText,
            length: EditText,
            weight: EditText
        ) {
            val url: String = "http://$ipv4Address/courier_project/getMeasures.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    val strRes = response.toString()
                    val jsonArray = JSONArray(strRes)
                    val jsonResponse = jsonArray.getJSONObject(0)
                    val jsonArrayOrders = jsonResponse.getJSONArray("measures")
                    val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(0)

                    measures.height = jsonInner.get("height").toString().toInt()
                    measures.width = jsonInner.get("width").toString().toInt()
                    measures.length = jsonInner.get("length").toString().toInt()
                    measures.weight = jsonInner.get("weight").toString().toInt()

                    height.hint = "up to ${measures.height}cm"
                    width.hint = "up to ${measures.width}cm"
                    length.hint = "up to ${measures.length}cm"
                    weight.hint = "up to ${measures.weight}kg"

                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Assign order's to all available courier's.
         */
        fun assignOrdersToCouriers(
            context: Context
        ) {
            val url: String = "http://$ipv4Address/courier_project/assignOrderToCourier.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    Toast.makeText(context, "Assigned successfully", Toast.LENGTH_SHORT).show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, "Assign failed", Toast.LENGTH_SHORT).show()
                }) {
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Interface to manage the callback's
         */
        interface ReportCallback {
            fun onSuccess(fileName: String, title: String, data: List<List<String>>)
            fun onError(errorMessageLayout: LinearLayout, errorMessage: TextView, message: String)
        }

        /**
         * Receive status,
         * return all order's which exceeded 24H from received status.
         */
        fun ordersPassed24HFromStatus(
            context: Context,
            status: String,
            errorMessageLayout: LinearLayout,
            errorMessage: TextView,
            callback: ReportCallback
        ) {
            val url: String = "http://$ipv4Address/courier_project/24hFromStatusReport.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (response.toString() != "empty") {
                        val report24hException = mutableListOf<List<String>>()
                        report24hException.add(listOf("Order ID","Pickup Contact Name","Pickup Phone","Pickup Email","Pickup City","Pickup Street","Pickup Build","Delivery Contact Name","Delivery Phone","Delivery Email","Delivery City","Delivery Street","Delivery Build","Order Status","Comment's"))

                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("report")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpRow = listOf(
                                jsonInner.get("order_id").toString(),
                                jsonInner.get("pickupName").toString(),
                                jsonInner.get("pickupPhone").toString(),
                                jsonInner.get("pickupEmail").toString(),
                                jsonInner.get("pickupCity").toString(),
                                jsonInner.get("pickupStreet").toString(),
                                jsonInner.get("pickupBuild").toString(),
                                jsonInner.get("deliveryName").toString(),
                                jsonInner.get("deliveryPhone").toString(),
                                jsonInner.get("deliveryEmail").toString(),
                                jsonInner.get("deliveryCity").toString(),
                                jsonInner.get("deliveryStreet").toString(),
                                jsonInner.get("deliveryBuild").toString(),
                                jsonInner.get("orderStatus").toString(),
                                jsonInner.get("comment").toString()
                            )
                            report24hException.add(tmpRow)
                        }
                        callback.onSuccess("orders_excepted_24H_from_$status", "Orders which except 24H from status - $status", report24hException)
                    } else {
                        callback.onError(errorMessageLayout, errorMessage,"No matching orders")
                    }
                }, Response.ErrorListener { error ->
                    callback.onError(errorMessageLayout, errorMessage, error.toString())
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["status"] = status
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Return the average time in hours, between the received statuses in the received date's.
         */
        fun avgHoursByStatusInDateRange(
            context: Context,
            startStatus: String,
            endStatus: String,
            startDate: String,
            endDate: String,
            errorMessageLayout: LinearLayout,
            errorMessage: TextView
        ) {
            Log.i("report Ranges", "$startStatus $endStatus $startDate $endDate")

            val url: String = "http://$ipv4Address/courier_project/avgHoursToStatusReport.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (response.toString() != "empty") {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("report")

                        val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(0)
                        val strResponse = "The average hours difference between:\n" +
                                "Statuses $startStatus to $endStatus\n" +
                                "Dates $startDate -> $endDate\n" +
                                "is: ${jsonInner.get("avg_time_difference_hours")}"
                        showPopupWindow(context, strResponse)
                    } else {
                        errorMessageLayout.visibility = View.VISIBLE
                        errorMessage.text = "No matching orders"
                    }
                }, Response.ErrorListener { error ->
                    errorMessageLayout.visibility = View.VISIBLE
                    errorMessage.text = error.toString()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["startStatus"] = startStatus
                        params["endStatus"] = endStatus
                        params["startDate"] = startDate
                        params["endDate"] = endDate
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)

        }

        /**
         * Return the amount of shipment's,
         * ordered by courier and pick up city.
         */
        fun amountOfShipmentsByCourierPerCity(
            context: Context,
            startDate: String,
            endDate: String,
            errorMessageLayout: LinearLayout,
            errorMessage: TextView,
            callback: ReportCallback
        ) {
            val url: String = "http://$ipv4Address/courier_project/courierCityStatusReport.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (response.toString() != "empty") {
                        val reportCourierAmountShipmentsByPickupCity = mutableListOf<List<String>>()
                        reportCourierAmountShipmentsByPickupCity.add(listOf("Full name","Pickup City","SCHEDULED","COLLECTED","DELIVERED"))

                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("report")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpRow = listOf(
                                jsonInner.get("full_name").toString(),
                                jsonInner.get("pickupCity").toString(),
                                jsonInner.get("SCHEDULED").toString(),
                                jsonInner.get("COLLECTED").toString(),
                                jsonInner.get("DELIVERED").toString()
                            )
                            reportCourierAmountShipmentsByPickupCity.add(tmpRow)
                        }
                        callback.onSuccess( "courier_shipment_by_pickup_city", "Courier shipments by pickup city",reportCourierAmountShipmentsByPickupCity)
                    } else {
                        callback.onError(errorMessageLayout, errorMessage,"No matching orders")
                    }
                }, Response.ErrorListener { error ->
                    callback.onError(errorMessageLayout, errorMessage, error.toString())
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["startDate"] = startDate
                        params["endDate"] = endDate
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * return the amount of shipment's,
         * ordered by statuses(SCHEDULED, COLLECTED, DELIVERED) for each courier.
         */
        fun amountOfShipmentsByCourierByStatus(
            context: Context,
            startDate: String,
            endDate: String,
            errorMessageLayout: LinearLayout,
            errorMessage: TextView,
            callback: ReportCallback
        ) {
            val url: String = "http://$ipv4Address/courier_project/courierOrderStatusReport.php"
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (response.toString() != "empty") {
                        val reportCourierOrdersAmountByStatus = mutableListOf<List<String>>()
                        reportCourierOrdersAmountByStatus.add(listOf("Full name","Total amount","SCHEDULED","COLLECTED","DELIVERED"))

                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("report")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpRow = listOf(
                                jsonInner.get("full_name").toString(),
                                jsonInner.get("total_orders").toString(),
                                jsonInner.get("SCHEDULED").toString(),
                                jsonInner.get("COLLECTED").toString(),
                                jsonInner.get("DELIVERED").toString()
                            )
                            reportCourierOrdersAmountByStatus.add(tmpRow)
                        }
                        callback.onSuccess("courier_shipments_total_and_by_status","Courier total shipment's and spread by status",reportCourierOrdersAmountByStatus)
                    } else {
                        callback.onError(errorMessageLayout, errorMessage,"No matching orders")
                    }
                }, Response.ErrorListener { error ->
                    callback.onError(errorMessageLayout, errorMessage, error.toString())
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["startDate"] = startDate
                        params["endDate"] = endDate
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)

        }

        /**
         * Create popup window in the received activity context.
         */
        fun showPopupWindow(
            context: Context,
            information: String
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Assignment result")
            builder.setMessage(information)
            builder.setIcon(R.drawable.baseline_info_24)
            builder.setNegativeButton("CLOSE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        /**
         * Add pickup address to current pickupAddresses List
         */
//        fun getPickupAddresses() {
//            for (ship in shipments) {
//                if (ship.status.equals(eStatus.SCHEDULED)) {
//                    pickupAddresses.add(ship.pickupStreet + " " + ship.pickupBuild + " " + ship.pickupCity)
//                }
//            }
//        }

        /**
         * Remove COLLECTED shipment address from pickupAddresses List,
         * add it's delivery address to deliveryAddresses list.
         */
//        fun getDeliveryAddresses() {
//            for (ship in shipments) {
//                if (ship.status.equals(eStatus.COLLECTED)) {
//                    pickupAddresses.remove(ship.pickupStreet + " " + ship.pickupBuild + " " + ship.pickupCity)
//                    deliveryAddresses.add(ship.deliveryStreet + " " + ship.deliveryBuild + " " + ship.deliveryCity)
//                }
//            }
//        }

        fun getRouteAddresses() {
            for (ship in shipments) {
                if (ship.status.equals(eStatus.SCHEDULED)) {
                    routeAddresses.add(ship.pickupStreet + " " + ship.pickupBuild + " " + ship.pickupCity)
                }
                if (ship.status.equals(eStatus.COLLECTED)) {
                    routeAddresses.remove(ship.pickupStreet + " " + ship.pickupBuild + " " + ship.pickupCity)
                    routeAddresses.add(ship.deliveryStreet + " " + ship.deliveryBuild + " " + ship.deliveryCity)
                }
            }
        }

        /**
         * Return list of pickup cities from which there are order's in NEW status.
         */
        fun getAvailablePickupCities(
            context: Context
        ) {
            val url: String = "http://$ipv4Address/courier_project/getPickupCityList.php"
            availablePickupCities.clear()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    if (!response.toString().trim().equals("empty")) {
                        val strRes = response.toString()
                        val jsonArray = JSONArray(strRes)
                        val jsonResponse = jsonArray.getJSONObject(0)
                        val jsonArrayOrders = jsonResponse.getJSONArray("pickupCities")
                        availablePickupCities.add("Choose pickup city")

                        for (i in 0 until jsonArrayOrders.length()) {
                            val jsonInner: JSONObject = jsonArrayOrders.getJSONObject(i)
                            val tmpPickupCity = jsonInner.get("pickupCity").toString()
                            availablePickupCities.add(tmpPickupCity)
                        }
                    } else {
                        availablePickupCities.add("No NEW order's yet")
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
//                    override fun getParams(): Map<String, String> {
//                        val params: MutableMap<String, String> = HashMap()
//                        return params
//                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

        /**
         * Receive courier email and assigned orders limit,
         * set the courier email column to the received and update order status.
         */
        fun assignOrdersToCourierByPickupCity(
            context: Context,
            courierEmail: String,
            chosenPickupCity: String,
            shipmentLimit: String
        ){
            val url: String = "http://$ipv4Address/courier_project/assignOrdersToCourierByPickupCity.php"

            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["courierEmail"] = courierEmail
                        params["chosenPickupCity"] = chosenPickupCity
                        params["shipmentLimit"] = shipmentLimit
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }


        /* End of companion object */
    }
}
