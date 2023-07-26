package com.example.couriermanagerkotlin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class AddEmployee : AppCompatActivity() {
    lateinit var shrd: SharedPreferences
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var errorMassage: TextView
    lateinit var radioGroup : RadioGroup
    lateinit var radioButton : RadioButton
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.logout -> {
                var editor: SharedPreferences.Editor = shrd!!.edit()
                editor.putBoolean("connected", false)
                editor.putString("firstName", "")
                editor.putString("lastName", "")
                editor.putString("email", "")
                editor.putString("eRole", "")
                editor.commit()
                startActivity(Intent(this@AddEmployee, Login::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_employee)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        errorMassage = findViewById(R.id.errorMassage)
        radioGroup = findViewById(R.id.radioGroup)

    }

    fun addEmployee(view: View) {
        if(Validations.isEmpty(firstName) && Validations.isEmpty(lastName) && Validations.isEmpty(email) && Validations.checkPhoneLength(phone) )
         DBUtilities.registerEmployee(this@AddEmployee,firstName.text.toString().trim(),lastName.text.toString().trim(),email.text.toString().trim(),phone.text.toString().trim(),radioButton.text.toString())

    }

    fun checkRadioButton(view: View) {
        radioButton = findViewById(radioGroup.checkedRadioButtonId)
     //  Toast.makeText(this,radioButton.text,Toast.LENGTH_SHORT).show()
    }


}