package com.yeyint.composebase.di

import android.content.Context
import com.airbnb.lottie.compose.BuildConfig
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.network.exception.NetworkExceptionInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesSharePrefUtils(context: Context) =
        MyPreference(context)

    @Provides
    @Named("bearer_token")
    fun providesBearerToken(sharePrefUtils: MyPreference) =
        sharePrefUtils.load(MyPreference.KEYS.ACCESS_TOKEN)

    @Suppress("DEPRECATION")
    @Provides
    @Named("primary")
    fun providesPrimaryRetrofitBuilder(
        gson: Gson,
        @Named("base_url") baseUrl: String
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
    }

    @Suppress("DEPRECATION")
    @Provides
    @Named("auth")
    fun providesAuthRetrofitBuilder(
        gson: Gson,
        @Named("base_url") baseUrl: String
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object Providers {
        @Singleton
        @Provides
        @Named("okhttp")
        fun providesOkHttpClientBuilder(context: Context): OkHttpClient.Builder {
            return OkHttpClient.Builder().apply {
                val loggerInterceptor = HttpLoggingInterceptor().apply {
                    level = when (BuildConfig.DEBUG) {
                        true -> HttpLoggingInterceptor.Level.HEADERS
                        false -> HttpLoggingInterceptor.Level.NONE
                    }
                }
                addInterceptor(loggerInterceptor)
                    .addInterceptor(
                        ChuckerInterceptor.Builder(
                            context
                        ).alwaysReadResponseBody(false).build()
                    )
                   // .addInterceptor(OkHttpProfilerInterceptor())
                    .addInterceptor(NetworkExceptionInterceptor())
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .cache(null)
            }
        }

        @Singleton
        @Provides
        @Named("authOkhttp")
        fun providesAuthOkHttpClientBuilder(context: Context): OkHttpClient.Builder {
            return OkHttpClient.Builder().apply {
                val loggerInterceptor = HttpLoggingInterceptor().apply {
                    level = when (BuildConfig.DEBUG) {
                        true -> HttpLoggingInterceptor.Level.HEADERS
                        false -> HttpLoggingInterceptor.Level.NONE
                    }
                }
                addInterceptor(loggerInterceptor)
                    .addInterceptor(
                        ChuckerInterceptor.Builder(
                            context
                        ).alwaysReadResponseBody(false).build()
                    )
                  //  .addInterceptor(OkHttpProfilerInterceptor())
                    .addInterceptor(NetworkExceptionInterceptor())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .cache(null)
            }
        }
    }

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setLenient()
        .create()
}