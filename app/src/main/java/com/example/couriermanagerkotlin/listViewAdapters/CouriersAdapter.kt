package com.example.couriermanagerkotlin.listViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.couriermanagerkotlin.User
import com.example.couriermanagerkotlin.R

class CouriersAdapter(context: Context, couriers: ArrayList<User>): BaseAdapter()  {

    var myContext = context
    var couriers = couriers

    init {
        this.myContext = context
        this.couriers = couriers
    }

    override fun getCount(): Int {
        return this.couriers.size
    }

    override fun getItem(position: Int): Any {
        return this.couriers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(myContext).inflate(R.layout.couriers_list,parent,false)
        var courierFullName: TextView? = null
        var strFullName: String? = null
        var courierPhone : TextView? = null
        var courierEmail: TextView? = null

        courierFullName = view.findViewById(R.id.courierFullName)
        courierPhone = view.findViewById(R.id.courierPhone)
        courierEmail = view.findViewById(R.id.courierEmail)

        strFullName = "${couriers[position].firstName}  ${couriers[position].lastName}"

        courierFullName.text = strFullName
        courierPhone.text = couriers[position].phone
        courierEmail.text = couriers[position].email

        return view
    }
}