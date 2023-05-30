package com.example.sampletakehome.dependencygraph

import com.example.sampletakehome.networking.NetworkingModule
import com.example.sampletakehome.repository.UsersRepository
import dagger.Component
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@ApplicationScope
@Component(modules = [NetworkingModule::class])
interface ApplicationComponent {
    fun usersRepository(): UsersRepository
}

@Scope
@Retention(RUNTIME)
annotation class ApplicationScope