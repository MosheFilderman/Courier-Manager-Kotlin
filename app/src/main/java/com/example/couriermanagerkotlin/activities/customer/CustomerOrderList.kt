package com.example.couriermanagerkotlin.activities.customer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getCustomerOrders
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.orders
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.updateOrderStatus
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.objects.Order
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.activities.EditUserDetails
import com.example.couriermanagerkotlin.eNums.eStatus
import com.example.couriermanagerkotlin.listViewAdapters.OrdersAdapter
import com.google.android.material.navigation.NavigationView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.EnumMap


class CustomerOrderList : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    lateinit var shrd: SharedPreferences
    lateinit var statusSpinner: Spinner
    lateinit var ordersList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var search: SearchView
    var searchOrderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_order_list)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Attach behavior to each menu item
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.newOrder -> {
                    startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
                    emptyListMsg.visibility = View.GONE
                    ordersList.visibility = View.GONE
                    true
                }

                R.id.editInfo -> {
                    startActivity(Intent(this@CustomerOrderList, EditUserDetails::class.java))
                    drawerLayout.close()
                    true
                }

                R.id.logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Exit")
                    builder.setMessage("Are you sure you wish to logout?")
                    builder.setIcon(R.drawable.baseline_close_24)
                    builder.setPositiveButton("YES") { dialogInterface, _ ->
                        val shrd: SharedPreferences =
                            getSharedPreferences("shola", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = shrd.edit()
                        editor.clear()
                        editor.apply()
                        startActivity(Intent(this@CustomerOrderList, Login::class.java))
                        finish()
                    }
                    builder.setNegativeButton("NO") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                    true
                }

                else -> false
            }
        }

        userFullName = headerView.findViewById(R.id.userFullName)
        userEmail = headerView.findViewById(R.id.userEmail)

        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        val strUserFullName =
            "${shrd.getString("firstName", "Not")} ${shrd.getString("lastName", "Signed !")}"
        userFullName.text = strUserFullName
        userEmail.text = shrd.getString("email", "courierManager@courierManager")

        ordersList = findViewById(R.id.orderList)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        /* Status spinner */

        statusSpinner = findViewById(R.id.statusSpinner)
        val statusesNames = ArrayList<String>()
        statusesNames.add("Status sort by")
        eStatus.values().forEach { status ->
            statusesNames.add(status.name)
        }
        val statusArrayAdapter = ArrayAdapter(
            this@CustomerOrderList,
            android.R.layout.simple_spinner_item,
            statusesNames
        )
        statusSpinner.adapter = statusArrayAdapter

        statusSpinner.setSelection(0, false)

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    val statusSortBy = parent.getItemAtPosition(position).toString()

                    searchOrderList.clear()
                    for (tmpOrder in orders) {
                        if (
                            tmpOrder.status.name.compareTo(statusSortBy) == 0
                        ) {
                            searchOrderList.add(tmpOrder)
                        }
                    }
                    ordersList.adapter = OrdersAdapter(this@CustomerOrderList, searchOrderList)
                    ordersList.setOnItemClickListener { parent, view, position, id ->
                        showOrderFullInfo(searchOrderList[position])
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CustomerOrderList, "Area code must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Click on order open dialog with all the order details
        ordersList.setOnItemClickListener { parent, view, position, id ->
            showOrderFullInfo(orders[position])
        }

        getCustomerOrders(
            this@CustomerOrderList,
            ordersList,
            emptyListMsg,
            shrd.getString("email", "none").toString()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        // Set up search view listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchOrderList.clear()
                for (tmpOrder in orders) {
                    if (
                        tmpOrder.phone.startsWith(p0.toString())
                        || tmpOrder.email.lowercase().contains(p0.toString().lowercase())
                        || tmpOrder.name.lowercase().contains(p0.toString().lowercase())
                    ) {
                        searchOrderList.add(tmpOrder)
                    }
                }
                ordersList.adapter = OrdersAdapter(this@CustomerOrderList, searchOrderList)
                ordersList.setOnItemClickListener { parent, view, position, id ->
                    showOrderFullInfo(searchOrderList[position])
                }
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            android.R.id.home -> {
                // Handle home button press
                if (!search.isIconified) {
                    search.isIconified = true
                } else {
                    // Handle other home navigation logic here
                }
                return true
            }
            R.id.qr -> {
                startQRCodeScanning()

            }

        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Create PDF file with order's id and the current time and date,
     * with order's information.
     */
    private fun createAndOpenPdf(order: Order) {
        // Get the current time
        val currentTime = LocalDateTime.now()
        // Define a format for displaying the time
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
        // Format and print the current time
        val formattedTime = currentTime.format(formatter)

        val pdfFile = createPdfFile(this@CustomerOrderList, "Order_ID:${order.orderId.substring(0,8)}_${formattedTime}.pdf")
        val bitMatrix = generateQRCode(order.orderId, 100, 100)

        if (pdfFile != null && bitMatrix != null) {
            try {
               val qrCodeBitmap = bitMatrixToBitmap(bitMatrix)
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(400, 300, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                if (page != null) {
                    val canvas = page.canvas
                    val paint = Paint()
                    paint.color = Color.BLACK
                    canvas.drawBitmap(qrCodeBitmap,250f,50f,paint)
                    canvas.drawText("Contact name: ${order.name}", 40f, 50f, paint)
                    canvas.drawText("Contact phone: ${order.phone}", 40f, 80f, paint)
                    canvas.drawText("Contact email: ${order.email}", 40f, 110f, paint)
                    canvas.drawText("Pickup Address: ${order.pickupStreet} ${order.pickupBuild}, ${order.pickupCity}", 40f, 140f, paint)
                    canvas.drawText("Delivery Address: ${order.deliveryStreet} ${order.deliveryBuild}, ${order.deliveryCity}", 40f, 170f, paint)
                    paint.color = Color.RED
                    canvas.drawText("Comment's: ${order.comment}", 40f, 200f, paint)
                    pdfDocument.finishPage(page)
                    pdfDocument.writeTo(FileOutputStream(pdfFile))
                    pdfDocument.close()
                    openPdfFile(this@CustomerOrderList, pdfFile)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Create PDF file with the received file name, in the current app directory.
     */
    private fun createPdfFile(context: Context, filename: String): File? {
        val filePath = context.getExternalFilesDir(null)?.absolutePath
        if (filePath != null) {
            val directory = File(filePath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            return File(directory, filename)
        }
        return null
    }

    /**
     * Show apps suggested to open PDF format file.
     */
    private fun openPdfFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.startActivity(intent)
    }

    fun generateQRCode(content: String, width: Int, height: Int): BitMatrix? {
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

        try {
            return MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return null
    }

    fun bitMatrixToBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    fun showOrderFullInfo(order:Order){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.customer_order_full_info, null)
        val selectedOrder = order
        ordersList.visibility = View.VISIBLE

        builder.setView(dialogLayout)

        val exportToPdf: ImageButton = dialogLayout.findViewById(R.id.exportToPdf)
        val orderId: TextView = dialogLayout.findViewById(R.id.orderId)
        val name: TextView = dialogLayout.findViewById(R.id.name)
        val phone: TextView = dialogLayout.findViewById(R.id.phone)
        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS)
        val email: TextView = dialogLayout.findViewById(R.id.email)
        val status: TextView = dialogLayout.findViewById(R.id.orderStatus)
        val pickupAddress: TextView = dialogLayout.findViewById(R.id.pickupAddress)
        val deliveryAddress: TextView = dialogLayout.findViewById(R.id.deliveryAddress)
        val comment: TextView = dialogLayout.findViewById(R.id.comment)

        val strPickupAddress: String =
            selectedOrder.pickupCity + "," + selectedOrder.pickupStreet + " " + selectedOrder.deliveryBuild
        val strDeliveryAddress: String =
            "${selectedOrder.deliveryCity}, ${selectedOrder.deliveryStreet} ${selectedOrder.deliveryBuild}"

        orderId.text = selectedOrder.orderId.substring(0,8)
        name.text = selectedOrder.name
        phone.text = selectedOrder.phone
        email.text = selectedOrder.email
        status.text = selectedOrder.status.name
        pickupAddress.text = strPickupAddress
        deliveryAddress.text = strDeliveryAddress
        comment.text = selectedOrder.comment

        exportToPdf.setOnClickListener {
            createAndOpenPdf(selectedOrder)
        }

        if (selectedOrder.status.name.equals("NEW")) {
            builder.setPositiveButton("Cancel Order") { dialogInterface, i ->
                updateOrderStatus(
                    this@CustomerOrderList,
                    selectedOrder.orderId,
                    eStatus.CANCELLED
                )
                getCustomerOrders(
                    this@CustomerOrderList,
                    ordersList,
                    emptyListMsg,
                    shrd.getString("email", "none").toString()
                )
            }
        }

        builder.setNegativeButton("Close") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun startQRCodeScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan Order QR Code")
        integrator.setCameraId(0)  // Use the back camera
        integrator.setBeepEnabled(true) // Beep when a QR code is scanned
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            var scannedContent = result.contents
            searchOrderList.clear()
            for (order in orders) {
                if (order.orderId == scannedContent) {
                    showOrderFullInfo(order)
                    return
                }
            }
            Toast.makeText(this@CustomerOrderList, "No matching order id", Toast.LENGTH_SHORT).show()
        }
    }

    fun resetStatusSpinner(view: View) {
        statusSpinner.setSelection(0)
        ordersList.adapter = OrdersAdapter(this@CustomerOrderList, orders)
        ordersList.setOnItemClickListener { parent, view, position, id ->
            showOrderFullInfo(orders[position])
        }
    }


}