package com.google.wearhealthmonitor.data.repository

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.*
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.unregisterMeasureCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * دریافت متریک های ورزشی با MeasureClient ولی این متریک ها تنها در حالت exercise در دسترس هستند
 */
@Singleton
class HealthServicesRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val measureClient: MeasureClient =
        HealthServices.getClient(context).measureClient

    private val ioScope = CoroutineScope(Dispatchers.IO)

    init {
        // Log once what data types MeasureClient really supports
        ioScope.launch {
            val caps = measureClient.getCapabilities()
            Log.d("HSRepoCaps", "supportedDataTypesMeasure=${caps.supportedDataTypesMeasure}")
        }
    }

    fun heartRateFlow(): Flow<Double> =
        createSampleFlow(DataType.HEART_RATE_BPM)

    fun speedFlow(): Flow<Double> =
        createSampleFlow(DataType.SPEED)

    fun paceFlow(): Flow<Double> =
        createSampleFlow(DataType.PACE)

    fun stepsPerMinFlow(): Flow<Long> =
        createSampleFlow(DataType.STEPS_PER_MINUTE)

    fun stepsFlow(): Flow<Long> =
        createIntervalFlow(DataType.STEPS)

    fun caloriesFlow(): Flow<Double> =
        createIntervalFlow(DataType.CALORIES)

    fun distanceFlow(): Flow<Double> =
        createIntervalFlow(DataType.DISTANCE)

    fun elevationGainFlow(): Flow<Double> =
        createIntervalFlow(DataType.ELEVATION_GAIN)

    fun floorsFlow(): Flow<Double> =
        createIntervalFlow(DataType.FLOORS)

    private fun <T : Number> createSampleFlow(
        type: DeltaDataType<T, SampleDataPoint<T>>
    ): Flow<T> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                Log.d("HSRepo", "Availability ${dataType.name}: $availability")
            }

            override fun onDataReceived(data: DataPointContainer) {
                val points = data.getData(type)
                Log.d("HSRepo", "Data for ${type.name}: size=${points.size}")
                val first = points.firstOrNull() ?: return
                Log.d("HSRepo", "First value ${type.name}: ${first.value}")
                trySend(first.value)
            }
        }

        measureClient.registerMeasureCallback(type, callback)

        awaitClose {
            ioScope.launch {
                measureClient.unregisterMeasureCallback(type, callback)
            }
        }
    }

    private fun <T : Number> createIntervalFlow(
        type: DeltaDataType<T, IntervalDataPoint<T>>
    ): Flow<T> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                Log.d("HSRepo", "Availability ${dataType.name}: $availability")
            }

            override fun onDataReceived(data: DataPointContainer) {
                val points = data.getData(type)
                Log.d("HSRepo", "Data for ${type.name}: size=${points.size}")
                val first = points.firstOrNull() ?: return
                Log.d("HSRepo", "First value ${type.name}: ${first.value}")
                trySend(first.value)
            }
        }

        measureClient.registerMeasureCallback(type, callback)

        awaitClose {
            ioScope.launch {
                measureClient.unregisterMeasureCallback(type, callback)
            }
        }
    }
}
