package com.example.sampletakehome.dependencygraph

import android.content.Context
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface ApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @ApplicationContext @BindsInstance context: Context
        ): ApplicationComponent
    }

    companion object {
        fun create(context: Context): ApplicationComponent =
            DaggerApplicationComponent.factory().create(context)

    }
}

abstract class AppScope private constructor()
