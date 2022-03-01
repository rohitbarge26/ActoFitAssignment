package com.example.actofitassignment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Matcher
import java.util.regex.Pattern

lateinit var inputLayoutUserName: TextInputLayout
lateinit var editTextUserName: EditText
lateinit var inputLayoutMobileNumber: TextInputLayout
lateinit var editTextMobileNumber: EditText
lateinit var btnLogIn: Button
var mobileNumber = ""
var userName = ""
lateinit var sharedPreferences: SharedPreferences
var isLogin = false
var isNotLogin = ""

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isLogin = sharedPreferences.getBoolean("LOGIN", false);
        isNotLogin = sharedPreferences.getString("MobileNumber", "").toString()

        inputLayoutUserName = findViewById(R.id.inputLayoutUserName)
        inputLayoutMobileNumber = findViewById(R.id.inputLayoutMobileNumber)
        editTextUserName = findViewById(R.id.editTextUserName)
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber)
        btnLogIn = findViewById(R.id.btnLogin)

        if (isLogin) {
            inputLayoutUserName.visibility = View.GONE
        } else {
            inputLayoutUserName.visibility = View.VISIBLE
        }

        btnLogIn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                isLogin = sharedPreferences.getBoolean("LOGIN", false);
                if (isLogin) {
                    mobileNumber = editTextMobileNumber.text.toString()
                    isNotLogin = sharedPreferences.getString("MobileNumber", "").toString()
                    println("Shared Mobile Number: $isNotLogin")
                    if (isNotLogin != mobileNumber) {
                        inputLayoutUserName.visibility = View.VISIBLE
                        println("If Mobile Number: $mobileNumber")
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("MobileNumber", mobileNumber)
                        editor.putBoolean("LOGIN", false)
                        editor.apply()
                        println("In side Shared Mobile Number: $isNotLogin")
                    } else {
                        println("else Mobile Number: $mobileNumber")
                        if (validateMobile()) {
                            startActivity(Intent(this, LocationActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    if (validateEntry()) {
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("UserName", userName)
                        editor.putString("MobileNumber", mobileNumber)
                        editor.putBoolean("LOGIN", true)
                        editor.apply()
                        println("Else Mobile Number: $mobileNumber")
                        startActivity(Intent(this, LocationActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun validateMobile(): Boolean {
        mobileNumber = editTextMobileNumber.text.toString()

        val regex: String = getString(R.string.mobileNumberPattern)
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(mobileNumber)
        if (!matcher.matches()) {
            inputLayoutMobileNumber.error = getString(R.string.mobileNumberError)
            return false
        } else {
            inputLayoutMobileNumber.isErrorEnabled = false
        }
        return true
    }

    private fun validateEntry(): Boolean {
        userName = editTextUserName.text.toString()
        mobileNumber = editTextMobileNumber.text.toString()

        val regex: String = getString(R.string.mobileNumberPattern)
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(mobileNumber)

        if (userName.equals("", ignoreCase = true)) {
            inputLayoutUserName.error = "Please Enter User Name to Continue"
            return false
        } else {
            inputLayoutUserName.isErrorEnabled = false
        }

        if (!matcher.matches()) {
            inputLayoutMobileNumber.error = getString(R.string.mobileNumberError)
            return false
        } else {
            inputLayoutMobileNumber.isErrorEnabled = false
        }
        return true
    }
}