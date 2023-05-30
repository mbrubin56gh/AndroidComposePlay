package com.example.sampletakehome

import android.app.Application
import com.example.sampletakehome.dependencygraph.ApplicationComponent
import com.example.sampletakehome.dependencygraph.DaggerApplicationComponent

class SampleUsersApplication : Application() {
    companion object {
        val applicationComponent: ApplicationComponent = DaggerApplicationComponent.create()
    }
}