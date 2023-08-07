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
        val view = LayoutInflater.from(myContext).inflate(R.layout.courier_shipment_info,parent,false)
        var pickupName: TextView? = null
        var pickupPhone: TextView? = null
        var pickupEmail: TextView? = null
        var pickupAddress: TextView? = null
        var deliveryName: TextView? = null
        var deliveryPhone: TextView? = null
        var deliveryEmail: TextView? = null
        var deliveryAddress: TextView? = null
        var status: TextView? = null
        var comment: TextView? = null

        val strPickupName = "${shipments[position].pickupFirstName} ${shipments[position].pickupLastName}"
        val fullPickupAddress: String = "${shipments[position].pickupStreet} ${shipments[position].pickupBuild}, ${shipments[position].pickupCity}"
        val fullDeliveryAddress: String = "${shipments[position].deliveryStreet} ${shipments[position].deliveryBuild}, ${shipments[position].deliveryCity}"

        pickupName = view.findViewById(R.id.pickupName)
        pickupPhone = view.findViewById(R.id.pickupPhone)
        pickupEmail = view.findViewById(R.id.pickupEmail)
        pickupAddress = view.findViewById(R.id.pickupAddress)
        deliveryName = view.findViewById(R.id.deliveryName)
        deliveryPhone = view.findViewById(R.id.deliveryPhone)
        deliveryEmail = view.findViewById(R.id.deliveryEmail)
        deliveryAddress = view.findViewById(R.id.deliveryAddress)
        status = view.findViewById(R.id.orderStatus)
        comment = view.findViewById(R.id.comment)

        pickupName.text = strPickupName
        pickupPhone.text = shipments[position].pickupPhone
        pickupEmail.text = shipments[position].pickupEmail
        pickupAddress.text = fullPickupAddress
        deliveryName.text = shipments[position].deliveryName
        deliveryPhone.text = shipments[position].deliveryPhone
        deliveryEmail.text = shipments[position].deliveryEmail
        deliveryAddress.text = fullDeliveryAddress
        status.text = shipments[position].status.name
        comment.text = shipments[position].comment

        return view
    }
}