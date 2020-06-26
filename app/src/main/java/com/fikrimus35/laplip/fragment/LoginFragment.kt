package com.fikrimus35.laplip.fragment

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.fikrimus35.laplip.R
import com.fikrimus35.laplip.constant.Constants
import com.fikrimus35.laplip.helper.HttpRequest
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment() {
    private var parent: View? = null
    private lateinit var pbLoading: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent = container as View
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pbLoading = view.findViewById(R.id.pb_loading)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val etEmail = view.findViewById<EditText>(R.id.et_email)

        val deviceId = arguments?.getInt(Constants.DEVICE_ID_EXTRA)
        var email: String

        val httpClient = HttpRequest.getInstance(activity?.applicationContext as Context)

        btnLogin.setOnClickListener {
            email = etEmail.text.toString()
            if(email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                btnLogin.isEnabled = false
                pbLoading.visibility = View.VISIBLE
                val requestLogin = object : StringRequest(
                    Method.POST,
                    Constants.SERVER_HOST + Constants.LOGIN_URI,
                    Response.Listener {response ->
                        try {
                            pbLoading.visibility = View.INVISIBLE
                            val responseObj = JSONObject(response)
                            if(responseObj.getString(Constants.RESPONSE_STATUS_KEY) == Constants.RESPONSE_OK)
                            {
                                pbLoading.visibility = View.INVISIBLE
                                btnLogin.isEnabled = true
                                val authFragment =
                                    AuthenticateFragment()
                                val arguments = Bundle()
                                arguments.putInt(Constants.DEVICE_ID_EXTRA, deviceId ?: -1)
                                arguments.putString(Constants.EMAIL_EXTRA, email)
                                authFragment.arguments = arguments

                                val fm = fragmentManager as FragmentManager
                                fm.beginTransaction()
                                    .replace(R.id.auth_frame, authFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                            else
                            {
                                pbLoading.visibility = View.INVISIBLE
                                btnLogin.isEnabled = true
                                Snackbar.make(parent as View, getString(R.string.connection_error_message), Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                                    .show()
                            }
                        }
                        catch (e: JSONException)
                        {
                            e.printStackTrace()
                            pbLoading.visibility = View.INVISIBLE
                            btnLogin.isEnabled = true
                            Snackbar.make(parent as View, getString(R.string.connection_error_message), Snackbar.LENGTH_LONG)
                                .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                                .show()
                        }
                    },
                    Response.ErrorListener {error->
                        pbLoading.visibility = View.INVISIBLE
                        btnLogin.isEnabled = true
                        Snackbar.make(parent as View, getString(R.string.connection_error_message), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                            .show()
                        if(error.networkResponse.statusCode == 403)
                        {
                            try {
                                val errObj = JSONObject(String(error.networkResponse.data))
                                if(errObj.getJSONArray(Constants.RESPONSE_ERRORS_KEY).getString(0) == Constants.RESPONSE_TOKEN_REQUEST_TOO_OFTEN_ERROR)
                                {
                                    Snackbar.make(parent as View, resources.getString(R.string.access_too_many_message)
                                            + " " + errObj.getJSONArray(Constants.RESPONSE_ARGUMENTS_KEY).getInt(0).toString() + " "
                                            + resources.getString(R.string.seconds)
                                        , Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                                        .show()

                                    pbLoading.visibility = View.INVISIBLE
                                    btnLogin.isEnabled = true
                                    val authFragment =
                                        AuthenticateFragment()
                                    val arguments = Bundle()
                                    arguments.putInt(Constants.DEVICE_ID_EXTRA, deviceId ?: -1)
                                    arguments.putString(Constants.EMAIL_EXTRA, email)
                                    authFragment.arguments = arguments

                                    val fm = fragmentManager as FragmentManager
                                    fm.beginTransaction()
                                        .replace(R.id.auth_frame, authFragment)
                                        .addToBackStack(null)
                                        .commit()
                                }
                            }
                            catch (e: JSONException)
                            {
                                btnLogin.isEnabled = true
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params[Constants.LOGIN_EMAIL_FIELD] = email
                        params[Constants.LOGIN_DEVICE_ID_FIELD] = deviceId.toString()
                        return params
                    }
                }

                httpClient.addRequest(requestLogin)
            }
            else {
                Snackbar.make(parent as View, resources.getString(R.string.email_invalid_message), Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                    .show()
            }
        }
    }
}