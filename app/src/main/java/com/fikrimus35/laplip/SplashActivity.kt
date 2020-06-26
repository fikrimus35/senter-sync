package com.fikrimus35.laplip

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.fikrimus35.laplip.constant.Constants
import com.fikrimus35.laplip.helper.HttpRequest
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler

    private var loadingDone = false
    private var networkError = false
    private var loggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Glide.with(this)
            .load(getDrawable(R.drawable.logo))
            .into(imgLogo)

        val sharedPreferences = getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE)
        var deviceId = sharedPreferences.getInt(Constants.PREF_DEVICE_ID_KEY, -1)

        val httpClient = HttpRequest.getInstance(applicationContext)

        if(deviceId == -1)
        {
            val registerDeviceRequest = object: StringRequest(
                Method.POST,
                Constants.SERVER_HOST + Constants.REGISTER_DEVICE_URI,
                Response.Listener {response ->
                    val responseJson = JSONObject(response.trim())
                    if(responseJson.getString(Constants.RESPONSE_STATUS_KEY) == Constants.RESPONSE_OK)
                    {
                        try {
                            deviceId = (responseJson.getJSONObject(Constants.RESPONSE_DATA_KEY)).getInt(Constants.RESPONSE_DEVICE_ID_KEY)
                            sharedPreferences.edit()
                                .putInt(Constants.PREF_DEVICE_ID_KEY, deviceId)
                                .apply()
                        }
                        catch (e: JSONException)
                        {
                            e.printStackTrace()
                        }
                    }
                    Log.d("NETWORK DEBUG", response)
                },
                Response.ErrorListener {
                    finish()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params[Constants.REGISTER_DEVICE_INFO_FIELD] = Build.MANUFACTURER
                    params[Constants.REGISTER_DEVICE_OS_FIELD] = "Android"
                    params[Constants.REGISTER_DEVICE_OS_VERSION_FIELD] = Build.VERSION.SDK_INT.toString()
                    params[Constants.REGISTER_DEVICE_LANG_FIELD] = Locale.getDefault().toString()
                    return params
                }
            }
            httpClient.addRequest(registerDeviceRequest)
        }



        val token = sharedPreferences.getString(Constants.PREF_TOKEN_KEY, "")
        if(token == "")
        {
            loggedIn = false
            networkError = false
            loadingDone = true
        }
        else
        {
            Log.d("TOKEN", token.toString())
            httpClient.addRequest(makeUserRequest(token.toString()))
        }

        val loadingRunnable = object: Runnable {
            override fun run() {
                if(loadingDone)
                {
                    if(!networkError)
                    {
                        if(!loggedIn)
                        {
                            val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                            intent.putExtra(Constants.DEVICE_ID_EXTRA, deviceId)
                            startActivity(intent)
                            finish()
                        }
                    }
                    else
                        finish()
                }
                else
                    handler.postDelayed(this, 2000)
            }

        }
        handler = Handler()
        handler.postDelayed(loadingRunnable, 2000)
    }

    private fun makeUserRequest(token: String): StringRequest
    {
        return object : StringRequest(
            Method.GET,
            Constants.makeUrl(Constants.USER_URI),
            Response.Listener { response ->
                val homeIntent = Intent(this, HomeActivity::class.java)
                homeIntent.putExtra(Constants.USER_EXTRA, response)
                startActivity(homeIntent)
                finish()
            },
            Response.ErrorListener {error->
                Log.e("ERROR STATUS", error.networkResponse.statusCode.toString())
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
    }
}
