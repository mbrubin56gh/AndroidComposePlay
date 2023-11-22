package com.example.sampletakehome.networking

import com.example.sampletakehome.dependencygraph.AppScope
import com.example.sampletakehome.dependencygraph.SingleIn
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import javax.inject.Inject

@Module
@ContributesTo(AppScope::class)
object FlipperNetworkingModule {
    @SingleIn(AppScope::class)
    @Provides
    fun providesNetworkFlipperPlugin(): NetworkFlipperPlugin {
        return NetworkFlipperPlugin()
    }
}

@ContributesMultibinding(
    scope = AppScope::class,
    boundType = Interceptor::class
)
@SingleIn(AppScope::class)
class FlipperInterceptor @Inject constructor(
    private val networkFlipperPlugin: NetworkFlipperPlugin
) : Interceptor by FlipperOkhttpInterceptor(networkFlipperPlugin)

@ContributesTo(AppScope::class)
interface NetworkFlipperPluginComponentInterface {
    fun networkFlipperPlugin(): NetworkFlipperPlugin
}
