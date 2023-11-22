package com.example.sampletakehome.networking

import com.example.sampletakehome.dependencygraph.AppScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@ContributesTo(AppScope::class)
object NetworkingModule {
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun providesUsersRetrofitBuilder(
        moshi: Moshi,
        interceptors: @JvmSuppressWildcards Set<Interceptor>
    ): Retrofit.Builder = Retrofit.Builder()
        .client(OkHttpClient.Builder()
            .apply {
                for (interceptor in interceptors) {
                    addInterceptor(interceptor)
                }
            }
            .build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))

    @Provides
    fun providesUsersRetrofit(builder: Retrofit.Builder): Retrofit = builder
        .baseUrl("https://dummyjson.com/")
        .build()

    @Provides
    fun providesUsersService(usersRetrofit: Retrofit): UsersService =
        usersRetrofit.create(UsersService::class.java)
}

@Module
@ContributesTo(AppScope::class)
interface InterceptorsModule {
    @Multibinds
    fun interceptors(): @JvmSuppressWildcards Set<Interceptor>
}