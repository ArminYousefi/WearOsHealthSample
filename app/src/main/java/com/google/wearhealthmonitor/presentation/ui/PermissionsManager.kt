package com.google.wearhealthmonitor.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord

/**
 * مدیر متمرکز مجوزهای سلامت (Runtime + Health Connect) برای اپ.
 *
 * - ابتدا مجوزهای Runtime (ACTIVITY_RECOGNITION و مجوز ضربان قلب وابسته به نسخه‌ی SDK)
 *   را بررسی و در صورت نیاز درخواست می‌کند؛ سپس در صورت در دسترس بودن Health Connect،
 *   شیت مجوزهای Health Connect (ضربان قلب + خواب) را فقط یک‌بار در هر سشن نمایش می‌دهد.
 *   سناریوی fallback (حالت استاتیک یا رفتن به تنظیمات) را مدیریت می‌کند.
 * - متدهای onCreate و onResume باید از Activity فراخوانی شوند تا پس از بازگشت
 *   از دیالوگ‌ها، شیت‌ها یا Settings وضعیت مجوزها دوباره ارزیابی شود.
 *
 * @param activity اکتیویتی میزبان که برای لانچ ActivityResultها و دیالوگ‌ها استفاده می‌شود
 * @param onPermissionsReady کال‌بکی که فقط وقتی تمام مجوزهای لازم (سیستمی و Health Connect)
 */
class HealthPermissionManager(
    private val activity: ComponentActivity,
    private val onPermissionsReady: () -> Unit
) {

    private val runtimePermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            Log.d("PermDebug", "Result from runtime launcher: $result")
            evaluatePermissionsAndStart()
        }

    private val healthPermissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    private val healthPermissionLauncher =
        activity.registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { granted: Set<String> ->
            Log.d("PermDebug", "Result from HealthConnect launcher: $granted")
            evaluatePermissionsAndStart()
        }

    private var hasRequestedHealthThisSession = false
    private var hasShownHrSettingsDialog = false

    fun onCreate() {
        checkAndRequestRuntimePermissions()
    }

    fun onResume() {
        evaluatePermissionsAndStart()
    }

    private fun checkAndRequestRuntimePermissions() {
        val needed = mutableListOf<String>()

        val activityOk = isGranted(Manifest.permission.ACTIVITY_RECOGNITION)
        val heartRateOk = isHeartRatePermissionGranted()

        if (!activityOk) {
            needed += Manifest.permission.ACTIVITY_RECOGNITION
        }

        if (!heartRateOk && Build.VERSION.SDK_INT < 35) {
            needed += Manifest.permission.BODY_SENSORS
        }

        if (needed.isEmpty()) {
            Log.d("PermDebug", "No runtime permission request needed")
            evaluatePermissionsAndStart()
        } else {
            Log.d("PermDebug", "Requesting runtime permissions: $needed")
            runtimePermissionLauncher.launch(needed.toTypedArray())
        }
    }

    private fun evaluatePermissionsAndStart() {
        val activityOk = isGranted(Manifest.permission.ACTIVITY_RECOGNITION)
        val heartRateOk = isHeartRatePermissionGranted()

        Log.d("PermDebug", "evaluatePermissions: ACTIVITY=$activityOk HR=$heartRateOk")

        when {
            activityOk && heartRateOk -> {
                Log.d("PermDebug", "All required perms granted → onPermissionsReady()")
                hasRequestedHealthThisSession = false
                hasShownHrSettingsDialog = false
                onPermissionsReady()
            }

            activityOk && Build.VERSION.SDK_INT >= 35 -> {
                val status = HealthConnectClient.getSdkStatus(activity)
                val hcAvailable = status == HealthConnectClient.SDK_AVAILABLE
                Log.d("PermDebug", "HealthConnect SDK status = $status, available=$hcAvailable")

                if (hcAvailable && !hasRequestedHealthThisSession) {
                    hasRequestedHealthThisSession = true
                    Log.d("PermDebug", "Launching HealthConnect HR+sleep permission sheet")
                    healthPermissionLauncher.launch(healthPermissions)
                } else if (!hasShownHrSettingsDialog) {
                    hasShownHrSettingsDialog = true
                    showHeartRateSettingsDialog()
                }
            }

            else -> {
                Log.d("PermDebug", "Critical perms missing → static mode")
            }
        }
    }

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED

    private fun isHeartRatePermissionGranted(): Boolean {
        val hrPermission =
            if (Build.VERSION.SDK_INT >= 35) {
                "android.permission.health.READ_HEART_RATE"
            } else {
                Manifest.permission.BODY_SENSORS
            }

        val granted = isGranted(hrPermission)
        Log.d("PermDebug", "Heart-rate permission ($hrPermission) granted = $granted")
        return granted
    }

    private fun showHeartRateSettingsDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Health permissions needed")
            .setMessage(
                "To show live heart-rate and real sleep data, your watch must allow this app " +
                        "to access Fitness & wellness permissions in system settings. " +
                        "If you denied the request, you can enable them there."
            )
            .setPositiveButton("Open settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Use static data") { _, _ ->
                Log.d("PermDebug", "User chose static mode (no health permissions)")
            }
            .setCancelable(false)
            .show()
    }

    @SuppressLint("WearRecents")
    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
    }
}