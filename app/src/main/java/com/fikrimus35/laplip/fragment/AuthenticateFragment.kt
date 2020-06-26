package com.fikrimus35.laplip.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.fikrimus35.laplip.HomeActivity
import com.fikrimus35.laplip.R
import com.fikrimus35.laplip.constant.Constants
import com.fikrimus35.laplip.helper.HttpRequest
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject

class AuthenticateFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authenticate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAuth = view.findViewById<Button>(R.id.btn_auth)
        val etCode = view.findViewById<TextView>(R.id.et_code)

        var code: String

        val httpClient = HttpRequest.getInstance(activity?.applicationContext as Context)

        var request: StringRequest


        btnAuth.setOnClickListener {
            code = etCode.text.toString()
            if(code.isNotEmpty()) {
                btnAuth.isEnabled = false
                request = object : StringRequest(
                    Method.POST,
                    Constants.makeUrl(Constants.VERIFY_URI),
                    Response.Listener { response ->
                        btnAuth.isEnabled = true
                        try {
                            val responseObj = JSONObject(response)
                            if(responseObj.getJSONArray(Constants.RESPONSE_INFO_KEY).getString(0) == Constants.RESPONSE_ACCESS_GRANTED)
                            {
                                val token = responseObj.getJSONObject(Constants.RESPONSE_DATA_KEY)
                                    .getString(Constants.RESPONSE_ACCESS_TOKEN_KEY)
                                activity?.getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE)
                                    ?.edit()
                                    ?.putString(Constants.PREF_TOKEN_KEY,
                                        token)
                                    ?.apply()
                                httpClient.addRequest(makeUserRequest(token))
                            }
                        }
                        catch (e: JSONException)
                        {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {error ->
                        btnAuth.isEnabled = true
                        val statusCode = error.networkResponse.statusCode
                        val data = error.networkResponse.data
                        if(statusCode == 401)
                        {
                            try {
                                if(JSONObject(String(data)).getJSONArray(Constants.RESPONSE_ERRORS_KEY).getString(0) == Constants.RESPONSE_TOKEN_INVALID)
                                {
                                    Snackbar.make(activity?.findViewById(R.id.auth_frame) as View, getString(
                                        R.string.code_invalid_message
                                    ), Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                                        .show()
                                }
                            }
                            catch (e: JSONException)
                            {
                                e.printStackTrace()
                            }
                        }
                        else
                        {
                            Snackbar.make(activity?.findViewById(R.id.auth_frame) as View, getString(
                                R.string.connection_error_message
                            ), Snackbar.LENGTH_LONG)
                                .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                                .show()
                        }
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params[Constants.VERIFY_DEVICE_ID_FIELD] = arguments?.getInt(Constants.DEVICE_ID_EXTRA).toString()
                        params[Constants.VERIFY_EMAIL_FIELD] = arguments?.getString(Constants.EMAIL_EXTRA).toString()
                        params[Constants.VERIFY_TOKEN_FIELD] = code
                        return params
                    }
                }

                httpClient.addRequest(request)
            }
        }
    }

    private fun makeUserRequest(token: String): StringRequest
    {
        return object : StringRequest(
            Method.GET,
            Constants.makeUrl(Constants.USER_URI),
            Response.Listener { response ->
                val homeIntent = Intent(activity, HomeActivity::class.java)
                homeIntent.putExtra(Constants.USER_EXTRA, response)
                activity?.startActivity(homeIntent)
                activity?.finish()
            },
            Response.ErrorListener {

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