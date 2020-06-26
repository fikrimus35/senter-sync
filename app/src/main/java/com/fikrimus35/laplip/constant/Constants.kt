package com.fikrimus35.laplip.constant

internal class Constants {
    companion object
    {
        /** Device Id Extra */
        const val DEVICE_ID_EXTRA = "extra_device_id"
        /** Email Extra */
        const val EMAIL_EXTRA = "extra_email"
        /** User Extra */
        const val USER_EXTRA = "extra_user"

        /** Shared Preference Key */
        const val APP_PREF_KEY = "laplip_preferences"
        /** Device ID Preference Key */
        const val PREF_DEVICE_ID_KEY = "pref_device_id"
        /** Token Preference Key */
        const val PREF_TOKEN_KEY = "pref_token"

        /** Server hostname */
        const val SERVER_HOST = "https://laplip.fikrimus35.web.id"
        /** Register Device URI */
        const val REGISTER_DEVICE_URI = "/application/auth/devices/register"
        /** Login URI */
        const val LOGIN_URI = "/application/auth/login"
        /** Verify Code URI */
        const val VERIFY_URI = "/application/auth/verify"
        /** User URI */
        const val USER_URI = "/application/auth/user"
        /** Logout URI */
        const val LOGOUT_URI = "/application/auth/logout"

        fun makeUrl(uri: String): String
        {
            return SERVER_HOST + uri
        }

        /** Response Status Key */
        const val RESPONSE_STATUS_KEY = "status"
        /** Response Data key */
        const val RESPONSE_DATA_KEY = "data"
        /** Response Errors key */
        const val RESPONSE_ERRORS_KEY = "errors"
        /** Response Info key */
        const val RESPONSE_INFO_KEY = "info"
        /** Response Arguments key */
        const val RESPONSE_ARGUMENTS_KEY = "arguments"
        /** Response Device Id key */
        const val RESPONSE_DEVICE_ID_KEY = "device_id"
        /** Response Access Token key */
        const val RESPONSE_ACCESS_TOKEN_KEY = "access_token"

        /** OK message from server */
        const val RESPONSE_OK = "ok"
        /** Error message from server */
        const val RESPONSE_ERROR = "error"
        /** Token Requst too Often message from server */
        const val RESPONSE_TOKEN_REQUEST_TOO_OFTEN_ERROR = "TOKEN_REQUEST_TOO_OFTEN"
        /** Token Invalid response */
        const val RESPONSE_TOKEN_INVALID = "TOKEN_INVALID"
        /** Access Granted response */
        const val RESPONSE_ACCESS_GRANTED = "ACCESS_GRANTED"

        /** Device Information Field */
        const val REGISTER_DEVICE_INFO_FIELD = "info"
        /** OS Field */
        const val REGISTER_DEVICE_OS_FIELD = "os"
        /** OS Version Field */
        const val REGISTER_DEVICE_OS_VERSION_FIELD = "os_version"
        /** Device Language Field */
        const val REGISTER_DEVICE_LANG_FIELD = "language"
        /** Email Field */
        const val LOGIN_EMAIL_FIELD = "email"
        /** Device Id Field */
        const val LOGIN_DEVICE_ID_FIELD = "device_id"
        /** Email Field */
        const val VERIFY_EMAIL_FIELD = "email"
        /** Device Id Field */
        const val VERIFY_DEVICE_ID_FIELD = "device_id"
        /** Token Field */
        const val VERIFY_TOKEN_FIELD = "token"

    }

}