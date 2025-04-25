package com.initialsetup.sk.utilities

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SKNetwork {

    private var baseUrl: String? = null
    private var customOkHttpBuilder: (OkHttpClient.Builder.() -> Unit)? = null

    fun setBaseUrl(url: String): SKNetwork {
        baseUrl = url
        return this
    }

    fun setCustomOkHttpConfig(config: OkHttpClient.Builder.() -> Unit): SKNetwork {
        customOkHttpBuilder = config
        return this
    }

    fun <T> getRetrofit(apiInterface: Class<T>): T {
        val builder = OkHttpClient.Builder().apply {
            customOkHttpBuilder?.invoke(this) ?: run {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl ?: throw IllegalStateException("Base URL not set"))
            .client(builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(apiInterface)
    }
}
