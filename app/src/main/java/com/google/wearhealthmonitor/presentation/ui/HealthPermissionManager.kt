package com.google.wearhealthmonitor.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

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
        ) { _ ->
            evaluatePermissions()
        }

    fun onCreate() {
        checkAndRequestRuntimePermissions()
    }

    fun onResume() {
        evaluatePermissions()
    }

    private fun checkAndRequestRuntimePermissions() {
        val needed = mutableListOf<String>()

        if (!isGranted(Manifest.permission.ACTIVITY_RECOGNITION)) {
            needed += Manifest.permission.ACTIVITY_RECOGNITION
        }

        val hrPermission =
            if (Build.VERSION.SDK_INT >= 35) {
                "android.permission.health.READ_HEART_RATE"
            } else {
                Manifest.permission.BODY_SENSORS
            }
        if (!isGranted(hrPermission)) {
            needed += hrPermission
        }

        if (needed.isEmpty()) {
            evaluatePermissions()
        } else {
            runtimePermissionLauncher.launch(needed.toTypedArray())
        }
    }

    private fun evaluatePermissions() {
        val activityOk = isGranted(Manifest.permission.ACTIVITY_RECOGNITION)
        val hrOk = isHeartRatePermissionGranted()

        if (activityOk && hrOk) {
            onPermissionsReady()
        } else {
            // اگر دوست داشتی، همان دیالوگ Settings را اینجا صدا بزن
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
        return isGranted(hrPermission)
    }
<<<<<<< HEAD

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
=======
>>>>>>> ec372bf (Fix Wear OS permissions and exercise start logic)
}
