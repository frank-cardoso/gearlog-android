package br.edu.unisatc.gearlog.data.remote

import android.util.Log
import br.edu.unisatc.gearlog.config.AppConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FipeApiClient {
    private const val TAG = "FipeApiClient"
    private const val BASE_URL = AppConfig.FIPE_BASE_URL

    private val headerInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Subscription-Token", AppConfig.FIPE_SUBSCRIPTION_TOKEN)
        chain.proceed(requestBuilder.build())
    }

    private val authGuardInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            Log.e(TAG, "HTTP 401 - Token invalido ou expirado. Revise o FIPE_SUBSCRIPTION_TOKEN.")
        }
        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(authGuardInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val gson = GsonBuilder().create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: FipeApiService = retrofit.create(FipeApiService::class.java)
}
