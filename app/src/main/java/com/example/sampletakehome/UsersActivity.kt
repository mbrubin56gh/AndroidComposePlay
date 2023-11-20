package com.example.sampletakehome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sampletakehome.SampleUsersApplication.Companion.applicationComponent
import com.example.sampletakehome.circuits.UsersScreen
import com.example.sampletakehome.theme.MyApplicationTheme
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator

class UsersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val backstack = rememberSaveableBackStack { push(UsersScreen) }
                val navigator = rememberCircuitNavigator(backstack)
                CircuitCompositionLocals(applicationComponent.circuit()) {
                    NavigableCircuitContent(
                        navigator = navigator,
                        backstack = backstack
                    )
                }
            }
        }
    }
}
