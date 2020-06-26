package com.fikrimus35.laplip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.fikrimus35.laplip.constant.Constants
import com.fikrimus35.laplip.fragment.LoginFragment

class AuthActivity : AppCompatActivity() {
    private lateinit var mFragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val deviceId = intent.getIntExtra(Constants.DEVICE_ID_EXTRA, -1)

        mFragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val arguments = Bundle()
        arguments.putInt(Constants.DEVICE_ID_EXTRA, deviceId)
        loginFragment.arguments = arguments
        mFragmentManager
            .beginTransaction()
            .add(R.id.auth_frame, loginFragment, LoginFragment::class.java.simpleName)
            .commit()
    }
}