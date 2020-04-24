package com.ishanknijhawan.covid.client

import okhttp3.OkHttpClient
import okhttp3.Request

object GlobalClient {
    private val okHttpClient = OkHttpClient()
    private val request = Request
        .Builder()
        .url("https://api.covid19api.com/summary")
        .build()

    val apiGlobal = okHttpClient.newCall(
        request
    )
}