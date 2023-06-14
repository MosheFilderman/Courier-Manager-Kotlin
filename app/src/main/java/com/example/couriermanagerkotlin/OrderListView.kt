package com.example.couriermanagerkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.TextView
import org.json.JSONArray

class OrderListView(context: Context, contactsNames: ArrayList<String>, contactsPhones: ArrayList<String>, contactsEmails: ArrayList<String>, ordersStatus: ArrayList<String>,orderComments: ArrayList<String> ):BaseAdapter() {


    var myContext = context
    var contactsNames = contactsNames
    var contactsPhones = contactsPhones
    var contactsEmails = contactsEmails
    var ordersStatus = ordersStatus
    var orderComment = orderComments


    init {
        this.myContext = context
        this.contactsNames = contactsNames
        this.contactsPhones = contactsPhones
        this.contactsEmails = contactsEmails
        this.ordersStatus = ordersStatus
        this.orderComment = orderComments
    }

    override fun getCount(): Int {
        return contactsEmails.size
    }

    override fun getItem(p0: Int): Any {
        return contactsEmails[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = LayoutInflater.from(myContext).inflate(R.layout.list_view,parent,false)
        var contactName: TextView? = null
        var contactPhone: TextView? = null
        var  contactEmail : TextView? = null
        var status: TextView? = null
        var comment: TextView?= null

        contactName = view.findViewById(R.id.firstColumn)
        contactPhone = view.findViewById(R.id.secondColumn)
        contactEmail= view.findViewById(R.id.thirdColumn)
        status = view.findViewById(R.id.fourthColumn)
        comment = view.findViewById(R.id.fifthColumn)
        contactName.text = contactsNames[position]
        contactPhone.text = contactsPhones[position]
        contactEmail.text = contactsEmails[position]
        status.text = ordersStatus[position]
        comment.text =orderComment[position]


        return view
    }




}