package com.example.couriermanagerkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class OrderListView(context: Context, orders: ArrayList<Order>): BaseAdapter() {


    var myContext = context
    var orders = orders


    init {
        this.myContext = context
        this.orders = orders
    }

    override fun getCount(): Int {
        return orders.size
    }

    override fun getItem(p0: Int): Any {
        return orders[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = LayoutInflater.from(myContext).inflate(R.layout.list_view,parent,false)
        var contactName: TextView? = null
        var contactPhone: TextView? = null
        var contactEmail : TextView? = null
        var status: TextView? = null
        var comment: TextView?= null

        contactName = view.findViewById(R.id.firstColumn)
        contactPhone = view.findViewById(R.id.secondColumn)
        contactEmail= view.findViewById(R.id.thirdColumn)
        status = view.findViewById(R.id.fourthColumn)
        comment = view.findViewById(R.id.fifthColumn)

        contactName.text = orders[position].name
        contactPhone.text = orders[position].phone
        contactEmail.text = orders[position].email
        status.text = orders[position].status.name
        comment.text = orders[position].comment

        return view
    }
}