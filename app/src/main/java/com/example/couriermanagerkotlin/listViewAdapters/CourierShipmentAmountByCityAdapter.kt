package com.example.couriermanagerkotlin.listViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.objects.CourierShipmetnsAmauntByCity

class CourierShipmentAmountByCityAdapter(context: Context, tmpArray: ArrayList<CourierShipmetnsAmauntByCity>): BaseAdapter() {

    var myContext = context
    var myArray = tmpArray

    init {
        this.myContext = context
        this.myArray = tmpArray
    }

    override fun getCount(): Int {
        return this.myArray.size
    }

    override fun getItem(position: Int): Any {
        return this.myArray[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(this.myContext).inflate(R.layout.courier_shipment_amount_by_city,parent,false)

        var fullName: TextView? = null
        var pickupCity: TextView? = null
        var scheduled : TextView? = null
        var collected: TextView? = null
        var delivered: TextView?= null

        fullName = view.findViewById(R.id.fullName)
        pickupCity = view.findViewById(R.id.pickupCity)
        scheduled= view.findViewById(R.id.scheduled)
        collected = view.findViewById(R.id.collected)
        delivered = view.findViewById(R.id.delivered)

        fullName.text = myArray[position].fullName
        pickupCity.text = myArray[position].pickupCity
        scheduled.text = myArray[position].SCHEDULED.toString()
        collected.text = myArray[position].COLLECTED.toString()
        delivered.text = myArray[position].DELIVERED.toString()

        return view
    }
}