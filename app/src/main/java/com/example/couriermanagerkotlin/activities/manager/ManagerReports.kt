package com.example.couriermanagerkotlin.activities.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.utilities.DBUtilities
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.couriers
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.customers

class ManagerReports : AppCompatActivity() {

    lateinit var spinnerReportByCustomer: Spinner
    lateinit var spinnerReportByCourier: Spinner
    lateinit var spinnerGeneralReports: Spinner

    lateinit var strCustomer: String
    lateinit var strCourier: String
    lateinit var strGeneralReport: String

    val couriersNames = ArrayList<String>()
    val customersNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_reports)

        spinnerReportByCustomer = findViewById(R.id.spinnerReportByCustomer)
        spinnerReportByCourier = findViewById(R.id.spinnerReportByCourier)
        spinnerGeneralReports = findViewById(R.id.spinnerGeneralReports)

        customersNames.add("Choose customer")
        customers.forEach{ customer ->
            customersNames.add("${customer.firstName} ${customer.lastName}")
        }
        couriersNames.add("Choose courier")
        couriers.forEach{ courier ->
            couriersNames.add("${courier.firstName} ${courier.lastName}")
        }


        /* Reports by customer Spinner */
        val byCustomerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, customersNames
        )
        spinnerReportByCustomer.adapter = byCustomerArrayAdapter

        spinnerReportByCustomer.setSelection(0, false)

        spinnerReportByCustomer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strCustomer = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* Reports by courier Spinner */
        val byCourierArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, couriersNames
        )
        spinnerReportByCourier.adapter = byCourierArrayAdapter

        spinnerReportByCourier.setSelection(0, false)

        spinnerReportByCourier.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strCourier = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* General reports Spinner */
        val pickupCityArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.generalReports)
        )
        spinnerGeneralReports.adapter = pickupCityArrayAdapter

        spinnerGeneralReports.setSelection(0, false)

        spinnerGeneralReports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strGeneralReport = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}