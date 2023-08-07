package com.example.couriermanagerkotlin.listViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.objects.Shipment

class ShipmentsAdapter(context: Context, shipments: ArrayList<Shipment>): BaseAdapter() {

    var myContext = context
    var shipments = shipments

    init {
        this.myContext = context
        this.shipments = shipments
    }

    override fun getCount(): Int {
        return this.shipments.size
    }

    override fun getItem(position: Int): Any {
        return shipments[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(myContext).inflate(R.layout.courier_shipment_row,parent,false)
        var orderId: TextView? = null
        var pickupAddress: TextView? = null
        var deliveryAddress: TextView? = null
        var status: TextView? = null
        var comment: TextView? = null

        val strPickupName = "${shipments[position].pickupFirstName} ${shipments[position].pickupLastName}"
        val fullPickupAddress: String = "${shipments[position].pickupStreet} ${shipments[position].pickupBuild}, ${shipments[position].pickupCity}"
        val fullDeliveryAddress: String = "${shipments[position].deliveryStreet} ${shipments[position].deliveryBuild}, ${shipments[position].deliveryCity}"

        orderId = view.findViewById(R.id.orderId)
        pickupAddress = view.findViewById(R.id.pickupAddress)
        deliveryAddress = view.findViewById(R.id.deliveryAddress)
        status = view.findViewById(R.id.orderStatus)
        comment = view.findViewById(R.id.comment)

        orderId.text = shipments[position].orderId
        pickupAddress.text = fullPickupAddress
        deliveryAddress.text = fullDeliveryAddress
        status.text = shipments[position].status.name
        comment.text = shipments[position].comment

        return view
    }
}