package com.example.sampletakehome.dependencygraph

import com.squareup.anvil.annotations.MergeComponent
import kotlin.reflect.KClass

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface ApplicationComponent

abstract class AppScope private constructor()

annotation class SingleIn(val scope: KClass<*>)
