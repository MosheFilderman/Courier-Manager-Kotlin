package com.example.couriermanagerkotlin.listViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.couriermanagerkotlin.Order
import com.example.couriermanagerkotlin.R

class ShipmentsAdapter(context: Context, shipments: ArrayList<Order>): BaseAdapter() {

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
        val view = LayoutInflater.from(myContext).inflate(R.layout.customer_order_row,parent,false)
        var contactName: TextView? = null
        var contactPhone: TextView? = null
        var contactEmail : TextView? = null
        var status: TextView? = null
        var comment: TextView? = null

        contactName = view.findViewById(R.id.firstColumn)
        contactPhone = view.findViewById(R.id.secondColumn)
        contactEmail= view.findViewById(R.id.thirdColumn)
        status = view.findViewById(R.id.fourthColumn)
        comment = view.findViewById(R.id.fifthColumn)

        contactName.text = shipments[position].name
        contactPhone.text = shipments[position].phone
        contactEmail.text = shipments[position].email
        status.text = shipments[position].status.name
        comment.text = shipments[position].comment

        return view
    }
}