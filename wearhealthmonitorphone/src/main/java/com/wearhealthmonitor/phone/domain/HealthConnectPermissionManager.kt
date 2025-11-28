package com.wearhealthmonitor.phone.domain

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HealthConnectPermissionManager @Inject constructor(
    private val client: HealthConnectClient
) {
    private val required: Set<String> = setOf(
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
    )

    fun requiredPermissions(): Set<String> = required

    suspend fun getAvailability(context: Context): Int =
        withContext(Dispatchers.IO) { HealthConnectClient.getSdkStatus(context) }

    suspend fun getGrantedPermissions(): Set<String> =
        withContext(Dispatchers.IO) { client.permissionController.getGrantedPermissions() }

    fun createRequestPermissionResultContract() =
        PermissionController.createRequestPermissionResultContract()
}