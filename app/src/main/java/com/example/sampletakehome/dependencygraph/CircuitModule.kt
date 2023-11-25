package com.example.sampletakehome.dependencygraph

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds

@ContributesTo(AppScope::class)
interface CircuitComponentInterface {
    fun circuit(): Circuit
}

@ContributesTo(AppScope::class)
@Module
interface CircuitModule {
    @Multibinds
    fun presenterFactories(): @JvmSuppressWildcards Set<Presenter.Factory>

    @Multibinds
    fun viewFactories(): @JvmSuppressWildcards Set<Ui.Factory>

    companion object {
        @Provides
        fun provideCircuit(
            presenterFactories: @JvmSuppressWildcards Set<Presenter.Factory>,
            uiFactories: @JvmSuppressWildcards Set<Ui.Factory>,
        ): Circuit = Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()
    }
}
