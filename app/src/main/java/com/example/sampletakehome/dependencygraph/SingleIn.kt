package com.example.sampletakehome.dependencygraph

import kotlin.reflect.KClass

annotation class SingleIn(val scope: KClass<*>)
