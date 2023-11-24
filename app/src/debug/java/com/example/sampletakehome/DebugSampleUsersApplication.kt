package com.example.sampletakehome

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader

class DebugSampleUsersApplication : SampleUsersApplication() {
    override fun onCreate() {
        super.onCreate()
        // Super annoying. FLipper's soLoader throws on a background thread
        // when the unit tests are run.
        if (!isUnitTesting()) {
            initializeFlipper(this)
        }
    }

    private fun initializeFlipper(context: Context) {
        SoLoader.init(this, false)
        val plugin = applicationComponent.networkFlipperPlugin()
        AndroidFlipperClient.getInstance(this).apply {
            addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            addPlugin(plugin)
        }.start()
    }

    private fun isUnitTesting(): Boolean {
        return try {
            Class.forName("org.junit.Test") is Class
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}