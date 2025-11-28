package com.wearhealthmonitor.phone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wearhealthmonitor.phone.presentation.SleepScreen
import com.wearhealthmonitor.phone.presentation.SleepViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SleepViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.uiState.collectAsState()

            val permissionLauncher = rememberLauncherForActivityResult(
                viewModel.permissionResultContract()
            ) { _: Set<String> ->
                viewModel.refreshGrants()
            }

            LaunchedEffect(Unit) {
                viewModel.initialize(this@MainActivity)
            }

            SleepScreen(
                state = state,
                onRequestPermission = {
                    val req = viewModel.requiredPermissions()
                    Log.d("HC_TEST", "requesting: $req")
                    permissionLauncher.launch(req)
                },
                onInsertFakeSleep = { viewModel.insertFakeSleepAndReload() }
            )
        }
    }
}