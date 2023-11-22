package com.example.sampletakehome

import android.app.Application
import com.example.sampletakehome.dependencygraph.ApplicationComponent

open class SampleUsersApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        applicationComponent = ApplicationComponent.create(this)
    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }
}