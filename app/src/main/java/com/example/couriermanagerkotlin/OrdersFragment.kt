package com.example.couriermanagerkotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.util.zip.Inflater

class OrdersFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        return inflater.inflate(R.layout.list_view,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}