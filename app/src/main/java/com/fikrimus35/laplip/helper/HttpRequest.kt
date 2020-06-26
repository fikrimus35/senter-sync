package com.fikrimus35.laplip.helper

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/** This is the class that handle http request based on
 *  Volley library
 */
class HttpRequest private constructor(context: Context)
{
    companion object
    {
        private var INSTANCE: HttpRequest? = null
        fun getInstance(context: Context): HttpRequest
        {
            return INSTANCE ?: synchronized(this)
            {
                INSTANCE ?: HttpRequest(context).also {
                    INSTANCE = it
                }
            }
        }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    fun <T> addRequest(req: Request<T>)
    {
        requestQueue.add(req)
    }
}