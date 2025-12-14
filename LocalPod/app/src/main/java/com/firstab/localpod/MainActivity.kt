package com.firstab.localpod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.firstab.localpod.ui.AppNavigation
import com.firstab.localpod.ui.theme.LocalPodTheme

import androidx.activity.viewModels
import com.firstab.localpod.SharedViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalPodTheme {
                AppNavigation(viewModel)
            }
        }
    }
}