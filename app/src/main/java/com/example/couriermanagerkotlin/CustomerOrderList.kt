package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class CustomerOrderList : AppCompatActivity() {

    /* Menu toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.customer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.newOrder -> startActivity(Intent(this@CustomerOrderList, CustomerNewOrder::class.java))
            R.id.orderList -> Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT).show()
            R.id.logout -> {
                startActivity(Intent(this@CustomerOrderList, Login::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_order_list)

    }


}