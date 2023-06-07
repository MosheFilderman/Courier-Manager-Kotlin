package com.example.couriermanagerkotlin

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class OrdersBaseAdapter(private val context: Activity, private val title: Array<String>)
    : ArrayAdapter<String>(context, R.layout.list_view, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_view, null, true)

//        val titleText = rowView.findViewById(R.id.title) as TextView
//        val imageView = rowView.findViewById(R.id.icon) as ImageView
//        val subtitleText = rowView.findViewById(R.id.description) as TextView

//        titleText.text = title[position]
//        imageView.setImageResource(imgid[position])
//        subtitleText.text = description[position]

        return rowView
    }
}