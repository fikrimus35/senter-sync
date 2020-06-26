package com.fikrimus35.laplip

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.fikrimus35.laplip.constant.Constants
import com.fikrimus35.laplip.helper.HttpRequest

class HomeActivity : AppCompatActivity() {
    lateinit var httpClient: HttpRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        httpClient = HttpRequest.getInstance(applicationContext)
        val sharedPreferences = getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE)

        val token = sharedPreferences.getString(Constants.PREF_TOKEN_KEY, "").toString()
        val deviceId = sharedPreferences.getInt(Constants.PREF_DEVICE_ID_KEY, -1)


        val tvUserInfo = findViewById<TextView>(R.id.tv_user_info)
        tvUserInfo.text = intent.getStringExtra(Constants.USER_EXTRA)

        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener{
            btnLogout.isEnabled = false
            logout(deviceId, token)
        }
    }

    private fun logout(deviceId: Int, token: String)
    {
        val request = object : StringRequest(
            Method.GET,
            Constants.makeUrl(Constants.LOGOUT_URI),
            Response.Listener {
                getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE)
                    .edit()
                    .remove(Constants.PREF_TOKEN_KEY)
                    .apply()
                moveToAuthActivity(deviceId)
            },
            Response.ErrorListener { error->
                if(error.networkResponse.statusCode == 401)
                {
                    getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE)
                        .edit()
                        .remove(Constants.PREF_TOKEN_KEY)
                        .apply()
                    moveToAuthActivity(deviceId)
                }
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        httpClient.addRequest(request)
    }

    private fun moveToAuthActivity(deviceId: Int)
    {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constants.DEVICE_ID_EXTRA, deviceId)
        startActivity(intent)
        finish()
    }
}