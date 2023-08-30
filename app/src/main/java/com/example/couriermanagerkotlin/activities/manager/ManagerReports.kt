package com.example.couriermanagerkotlin.activities.manager

import android.content.Context
import android.content.Intent
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
import androidx.core.content.FileProvider
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.utilities.DBUtilities
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.amountOfShipmentsByCourierByStatus
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.amountOfShipmentsByCourierPerCity
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.avgHoursByStatusInDateRange
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.ordersPassed24HFromStatus
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class ManagerReports : AppCompatActivity() {

    lateinit var spinnerGeneralReports: Spinner
    lateinit var errorMessageLayout: LinearLayout
    lateinit var errorMessage: TextView

    lateinit var singleStatusLayout: LinearLayout
    lateinit var spinnerSingleStatus: Spinner

    lateinit var statusRangeLayout: LinearLayout
    lateinit var startStatusLayout: LinearLayout
    lateinit var endStatusLayout: LinearLayout
    lateinit var spinnerStartStatus: Spinner
    lateinit var spinnerEndStatus: Spinner

    lateinit var dateRangeLayout: LinearLayout
    lateinit var endDateLayout: LinearLayout
    lateinit var calendar: Calendar
    lateinit var startDate: DatePicker
    lateinit var endDate: DatePicker

    lateinit var strGeneralReport: String
    lateinit var strSingleStatus: String
    lateinit var strStartStatus: String
    lateinit var strEndStatus: String

    private val startingStatus =
        arrayOf("Choose status", "NEW", "SCHEDULED", "COLLECTED")
    private val endingStatus = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_reports)
        endingStatus.add("Choose status")

        calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        spinnerGeneralReports = findViewById(R.id.spinnerGeneralReports)
        errorMessageLayout = findViewById(R.id.errorMessageLayout)
        errorMessage = findViewById(R.id.errorMessage)

        singleStatusLayout = findViewById(R.id.singleStatusLayout)
        spinnerSingleStatus = findViewById(R.id.spinnerSingleStatus)

        statusRangeLayout = findViewById(R.id.statusRangeLayout)
        startStatusLayout = findViewById(R.id.startStatusLayout)
        endStatusLayout = findViewById(R.id.endStatusLayout)
        spinnerStartStatus = findViewById(R.id.spinnerStartStatus)
        spinnerEndStatus = findViewById(R.id.spinnerEndStatus)

        dateRangeLayout = findViewById(R.id.dateRangeLayout)
        startDate = findViewById(R.id.startDatePicker)
        endDate = findViewById(R.id.endDatePicker)

        /* General reports Spinner */
        val generalReportArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.generalReports)
        )

        spinnerGeneralReports.adapter = generalReportArrayAdapter

        spinnerGeneralReports.setSelection(0, false)

        spinnerGeneralReports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                errorMessageLayout.visibility = View.GONE
                singleStatusLayout.visibility = View.GONE
                statusRangeLayout.visibility = View.GONE
                dateRangeLayout.visibility = View.GONE

                if (parent != null) {
                    strGeneralReport = parent.getItemAtPosition(position).toString()
                    when (strGeneralReport) {
                        "Orders passed 24H from status" -> {
                            singleStatusLayout.visibility = View.VISIBLE
                        }

                        "Avg hours by status in date range" -> {
                            statusRangeLayout.visibility = View.VISIBLE
                            dateRangeLayout.visibility = View.VISIBLE
                        }

                        "Amount of shipments by courier per city" -> {
                            dateRangeLayout.visibility = View.VISIBLE
                        }

                        "Amount of shipments by courier by status" -> {
                            dateRangeLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* Single status Spinner */
        val singleStatusArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, startingStatus)

        spinnerSingleStatus.adapter = singleStatusArrayAdapter

        spinnerSingleStatus.setSelection(0, false)

        spinnerSingleStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strSingleStatus = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* Start status Spinner */
        val startStatusArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, startingStatus)

        spinnerStartStatus.adapter = startStatusArrayAdapter

        spinnerStartStatus.setSelection(0, false)

        spinnerStartStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strStartStatus = parent.getItemAtPosition(position).toString()
                    endingStatus.clear()
                    endingStatus.add("Choose status")

                    if (!strStartStatus.equals("COLLECTED")) {
                        startingStatus.copyOfRange(
                            startingStatus.indexOf(strStartStatus) + 1,
                            startingStatus.size
                        ).forEach {
                            endingStatus.add(it)
                        }
                    }

                    endingStatus.add("DELIVERED")

                    endStatusLayout.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* End status Spinner */
        val endStatusArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, endingStatus)

        spinnerEndStatus.adapter = endStatusArrayAdapter

        spinnerEndStatus.setSelection(0, false)

        spinnerEndStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strEndStatus = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        /* Setting the Dates picker's */
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

        endDate.init(
            currentYear,
            currentMonth,
            currentDay
        ) { view, year, monthOfYear, dayOfMonth ->
            val toDate = Calendar.getInstance()
            toDate.set(year, monthOfYear, dayOfMonth)
            if (toDate.before(Calendar.getInstance())) {
                // Adjust "from date" when "to date" is before "from date"
                startDate.updateDate(year, monthOfYear, dayOfMonth)
            }
        }
    }

    fun getReportParameters(view: View) {
        Toast.makeText(this@ManagerReports, "Getting the data ready", Toast.LENGTH_SHORT).show()

        val startDate = "${startDate.year}-${
            if (startDate.month + 1 < 10) {
                "0${startDate.month + 1}"
            } else {
                startDate.month + 1
            }
        }-${startDate.dayOfMonth} 00:00:00"
        val endDate = "${endDate.year}-${
            if (endDate.month + 1 < 10) {
                "0${endDate.month + 1}"
            } else {
                endDate.month + 1
            }
        }-${endDate.dayOfMonth} 23:59:59"

        when (strGeneralReport) {
            "Orders passed 24H from status" -> {
                ordersPassed24HFromStatus(
                    this@ManagerReports,
                    strSingleStatus,
                    errorMessageLayout,
                    errorMessage,
                    object : DBUtilities.Companion.ReportCallback {
                        override fun onSuccess(file_name: String, title: String, data: List<List<String>>) {
                            // Handle the retrieved data (data contains the list of rows)
                            // For example, you can export it to an Excel file
                            val fileName = file_name
                            exportToExcelAndOpen(this@ManagerReports, data, fileName, title)
                        }

                        override fun onError(
                            errorMessageLayout: LinearLayout,
                            errorMessage: TextView,
                            message: String
                        ) {
                            // Handle the error (message contains the error message)
                            errorMessageLayout.visibility = View.VISIBLE
                            errorMessage.text = message
                        }
                    }
                )
                Thread.sleep(1000)
            }

            "Avg hours by status in date range" -> {
                avgHoursByStatusInDateRange(
                    this@ManagerReports,
                    strStartStatus,
                    strEndStatus,
                    startDate,
                    endDate,
                    errorMessageLayout,
                    errorMessage
                )
                Thread.sleep(1000)
            }

            "Amount of shipments by courier per city" -> {
                amountOfShipmentsByCourierPerCity(
                    this@ManagerReports,
                    startDate,
                    endDate,
                    errorMessageLayout,
                    errorMessage,
                    object : DBUtilities.Companion.ReportCallback {
                        override fun onSuccess(file_name: String, title: String, data: List<List<String>>) {
                            // Handle the retrieved data (data contains the list of rows)
                            // For example, you can export it to an Excel file
                            val fileName = file_name
                            exportToExcelAndOpen(this@ManagerReports, data, fileName, title)
                        }

                        override fun onError(
                            errorMessageLayout: LinearLayout,
                            errorMessage: TextView,
                            message: String
                        ) {
                            // Handle the error (message contains the error message)
                            errorMessageLayout.visibility = View.VISIBLE
                            errorMessage.text = message
                        }
                    }
                )
                Thread.sleep(1000)
            }

            "Amount of shipments by courier by status" -> {
                amountOfShipmentsByCourierByStatus(
                    this@ManagerReports,
                    startDate,
                    endDate,
                    errorMessageLayout,
                    errorMessage,
                    object : DBUtilities.Companion.ReportCallback {
                        override fun onSuccess(file_name: String, title: String, data: List<List<String>>) {
                            // Handle the retrieved data (data contains the list of rows)
                            // For example, you can export it to an Excel file
                            val fileName = file_name
                            exportToExcelAndOpen(this@ManagerReports, data, fileName, title)
                        }

                        override fun onError(
                            errorMessageLayout: LinearLayout,
                            errorMessage: TextView,
                            message: String
                        ) {
                            // Handle the error (message contains the error message)
                            errorMessageLayout.visibility = View.VISIBLE
                            errorMessage.text = message
                        }
                    }
                )
                Thread.sleep(1000)
            }
        }
    }

    /**
     * Getting the full path the created report file,
     * open window with the suggested app's to open the excel file.
     */
    fun openExcelFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.startActivity(intent)
    }

    /**
     * Receive two two-dimensional list of the proper data from DB,
     * the name of the report file and the report title.
     * create and fill the excel sheet with the data from the list.
     */
    fun exportToExcelAndOpen(context: Context, data: List<List<String>>, fileName: String, title: String) {
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")

        // Create a title row
        val titleRow = sheet.createRow(0)
        val titleCell = titleRow.createCell(0)
        titleCell.setCellValue(title)

        // Calculate the number of columns in the data
        val numColumns = data.firstOrNull()?.size ?: 0

        // Merge the title row across all columns
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, numColumns - 1))

        for ((rowIndex, rowData) in data.withIndex()) {
            val row = sheet.createRow(rowIndex + 1)
            for ((cellIndex, cellValue) in rowData.withIndex()) {
                row.createCell(cellIndex).setCellValue(cellValue)
            }
        }

        val file = File(context.getExternalFilesDir(null), "$fileName.xlsx")
        val fileOut = FileOutputStream(file)
        workbook.write(fileOut)
        fileOut.close()

        openExcelFile(context, file)
    }

}

