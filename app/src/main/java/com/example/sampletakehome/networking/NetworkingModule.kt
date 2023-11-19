package com.example.sampletakehome.networking

import com.example.sampletakehome.dependencygraph.AppScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@ContributesTo(AppScope::class)
object NetworkingModule {
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun providesUsersRetrofitBuilder(moshi: Moshi): Retrofit.Builder = Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))

    @Provides
    fun providesUsersRetrofit(builder: Retrofit.Builder): Retrofit = builder
        .baseUrl("https://dummyjson.com/")
        .build()

    @Provides
    fun providesUsersService(usersRetrofit: Retrofit): UsersService = usersRetrofit.create(UsersService::class.java)
}