/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.google.wearhealthmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import com.google.wearhealthmonitor.presentation.ui.HealthPermissionManager
import com.google.wearhealthmonitor.presentation.ui.health.HealthDashboardScreen
import com.google.wearhealthmonitor.presentation.ui.health.HealthViewModel
import com.google.wearhealthmonitor.presentation.theme.WearHealthMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HealthViewModel by viewModels()
    private lateinit var healthPermissionManager: HealthPermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        setContent { WearApp(viewModel) }

        healthPermissionManager = HealthPermissionManager(
            activity = this,
            onPermissionsReady = { viewModel.startObserving() }
        )

        healthPermissionManager.onCreate()
    }

    override fun onResume() {
        super.onResume()
        healthPermissionManager.onResume()
    }
}

@Composable
fun WearApp(viewModel: HealthViewModel) {
    WearHealthMonitorTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            HealthDashboardScreen(viewModel)
        }
    }
}