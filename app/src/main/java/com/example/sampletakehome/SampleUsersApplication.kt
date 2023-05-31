package com.example.sampletakehome

import android.app.Application
import com.example.sampletakehome.dependencygraph.ApplicationComponent
import com.example.sampletakehome.dependencygraph.ApplicationModule
import com.example.sampletakehome.dependencygraph.DaggerApplicationComponent

class SampleUsersApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }
}