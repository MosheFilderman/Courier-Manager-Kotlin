package com.example.couriermanagerkotlin.activities.manager

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.eStatus
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.avgHoursByStatusInDateRange
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.ordersPassed24HFromCreation

class ManagerReports : AppCompatActivity() {

    lateinit var errorMessage: TextView
    lateinit var errorMessageLayout: LinearLayout
    lateinit var statusPicker: LinearLayout
    lateinit var dateRangePicker: LinearLayout

    lateinit var startDate: DatePicker
    lateinit var endDate: DatePicker
    lateinit var spinnerStatusPicker: Spinner
    lateinit var spinnerGeneralReports: Spinner

    lateinit var strStatus: String

    //    lateinit var strCustomer: String
//    lateinit var strCourier: String
    lateinit var strGeneralReport: String

    private val statuses = ArrayList<String>()
//    private val couriersNames = ArrayList<String>()
//    private val customersNames = ArrayList<String>()

    lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_reports)

        calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        spinnerGeneralReports = findViewById(R.id.spinnerGeneralReports)
        errorMessageLayout = findViewById(R.id.errorMessageLayout)
        errorMessage = findViewById(R.id.errorMessage)
        statusPicker = findViewById(R.id.statusPicker)
        dateRangePicker = findViewById(R.id.dateRangePicker)
        spinnerStatusPicker = findViewById(R.id.spinnerStatusPicker)
        startDate = findViewById(R.id.fromDatePicker)
        endDate = findViewById(R.id.toDatePicker)

        statuses.add("Choose status")
        eStatus.values().forEach { status ->
            statuses.add(status.name)
        }

        /* General reports Spinner */
        val pickupCityArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.generalReports)
        )

        spinnerGeneralReports.adapter = pickupCityArrayAdapter

        spinnerGeneralReports.setSelection(0, false)

        spinnerGeneralReports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                errorMessageLayout.visibility = View.GONE
                statusPicker.visibility = View.GONE
                dateRangePicker.visibility = View.GONE
                if (parent != null) {
                    strGeneralReport = parent.getItemAtPosition(position).toString()
                    when (strGeneralReport) {
                        "Orders passed 24H from status" -> {
                            statusPicker.visibility = View.VISIBLE
                        }

                        "Avg hours by status in date range" -> {
                            statusPicker.visibility = View.VISIBLE
                            dateRangePicker.visibility = View.VISIBLE
                        }

                        "Amount of shipments by courier per city" -> {
                            dateRangePicker.visibility = View.VISIBLE
                        }

                        "Amount of shipments by courier by status" -> {
                            dateRangePicker.visibility = View.VISIBLE
                        }

                        else -> {
                            errorMessageLayout.visibility = View.VISIBLE
                            errorMessage.text = "To get report, must choose report type"
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* Status picker Spinner */
        val byCustomerArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)

        spinnerStatusPicker.adapter = byCustomerArrayAdapter

        spinnerStatusPicker.setSelection(0, false)

        spinnerStatusPicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strStatus = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        startDate.updateDate(currentYear, currentMonth, currentDay)
        endDate.updateDate(currentYear, currentMonth, currentDay)
        startDate.maxDate = calendar.timeInMillis
        endDate.maxDate = calendar.timeInMillis

        val newHeightInPixels =
            resources.getDimensionPixelSize(R.dimen.new_datepicker_height) // Define your desired height dimension
        val params = endDate.layoutParams as LinearLayout.LayoutParams
        params.height = newHeightInPixels
        endDate.layoutParams = params
        startDate.layoutParams = params

        startDate.init(
            currentYear,
            currentMonth,
            currentDay
        ) { view, year, monthOfYear, dayOfMonth ->
            val fromDate = Calendar.getInstance()
            fromDate.set(year, monthOfYear, dayOfMonth)
            if (fromDate.after(Calendar.getInstance())) {
                // Adjust "to date" when "from date" is after "to date"
                endDate.updateDate(year, monthOfYear, dayOfMonth)
            }
        }

        endDate.init(currentYear, currentMonth, currentDay) { view, year, monthOfYear, dayOfMonth ->
            val toDate = Calendar.getInstance()
            toDate.set(year, monthOfYear, dayOfMonth)
            if (toDate.before(Calendar.getInstance())) {
                // Adjust "from date" when "to date" is before "from date"
                startDate.updateDate(year, monthOfYear, dayOfMonth)
            }
        }
    }

    fun getReportParameters(view: View) {
        val startDate = "${startDate.year}-${startDate.month + 1}-${startDate.dayOfMonth}"
        val endDate ="${endDate.year}-${endDate.month + 1}-${endDate.dayOfMonth}"


        when (strGeneralReport) {
            "Orders passed 24H from status" -> {
                ordersPassed24HFromCreation(this@ManagerReports,strStatus,errorMessage)
            }

            "Avg hours by status in date range" -> {
                avgHoursByStatusInDateRange(this@ManagerReports,strStatus,startDate,endDate)

            }

            "Amount of shipments by courier per city" -> {
                Toast.makeText(this@ManagerReports, "in third case", Toast.LENGTH_SHORT).show()
            }

            "Amount of shipments by courier by status" -> {
                Toast.makeText(this@ManagerReports, "in fourth case", Toast.LENGTH_SHORT).show()
            }

            else -> {
                errorMessageLayout.visibility = View.VISIBLE
                errorMessage.text = "To get report, must choose report type"
            }
        }
    }
}

