package com.yeyint.composebase.di

import androidx.annotation.NonNull
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.network.components.TokenManager
import com.yeyint.composebase.network.service.AuthenticationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @NonNull
    @Provides
    fun provideAuthenticationService(@Named("refreshTokenBuilder") retrofitBuilder: Retrofit.Builder): AuthenticationService {
        return retrofitBuilder.build().create(AuthenticationService::class.java)
    }


    @Singleton
    @NonNull
    @Named("authenticatedBuilder")
    @Provides
    fun getAuthenticatedBuilder(
        @Named("okhttp") httpClientBuilder: OkHttpClient.Builder,
        @Named("primary") retrofitBuilder: Retrofit.Builder,
        @Named("token_manager") tokenManager: TokenManager,
        preference: MyPreference
    ): Retrofit.Builder {
        val interceptor: Interceptor =
            AuthenticationInterceptor(tokenManager, preference)
        if (!httpClientBuilder.interceptors().contains(interceptor)) {
            httpClientBuilder.addInterceptor(interceptor)
        }
        return retrofitBuilder.client(httpClientBuilder.build())
    }

    @Singleton
    @NonNull
    @Named("refreshTokenBuilder")
    @Provides
    fun getRefreshTokenBuilder(
        @Named("authOkhttp") httpClientBuilder: OkHttpClient.Builder,
        @Named("auth") retrofitBuilder: Retrofit.Builder,
        preference: MyPreference
    ): Retrofit.Builder {
        val interceptor: Interceptor =
            RefreshTokenInterceptor(preference)
        if (!httpClientBuilder.interceptors().contains(interceptor)) {
            httpClientBuilder.addInterceptor(interceptor)
        }
        return retrofitBuilder.client(httpClientBuilder.build())
    }
}