package net.huray.isenssdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import android.util.SparseArray
import com.isens.standard.ble.*
import net.huray.isenssdk.model.*

@SuppressLint("MissingPermission")
class IsensManager(
    context: Context,
    callback: IsensCallback
) : IsensBleDeviceManager, IBLE_Callback {
    private val bluetoothAdapter: BluetoothAdapter
    private val isensCallback: IsensCallback
    private var scanCallback: ScanCallback? = null
    private val discoveredDevices = mutableSetOf<DiscoveredIsensDevice>()

    private var isensDeviceType: IsensDeviceType? = null

    init {
        IBLE_Manager.getInstance().SetCallback(this)
        initSdk(context)
        
        val bleManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bleManager.adapter
        isensCallback = callback
    }

    override fun CallbackInitSDK(version: Int) {
        Log.d(TAG, "CallbackInitSDK")
    }

    override fun CallbackConnectedDevice() {
        Log.d(TAG, "CallbackConnectedDevice")
    }

    override fun CallbackDisconnectedDevice() {
        Log.d(TAG, "CallbackDisconnectedDevice")
    }

    override fun CallbackRequestTimeSync() {
        Log.d(TAG, "CallbackRequestTimeSync")
    }

    override fun CallbackRequestRecordsComplete(records: SparseArray<IBLE_GlucoseRecord>?) {
        Log.d(TAG, "CallbackRequestRecordsComplete")
        val glucoseRecords = convertRecordsOf(records)

        if (glucoseRecords.isEmpty()) {
            isensCallback.onDataEmpty()
            return
        }

        isensCallback.onReceiveData(glucoseRecords)
    }

    override fun CallbackReadDeviceInfo(device: IBLE_Device?) {
        Log.d(TAG, "CallbackReadDeviceInfo")
        isensCallback.onConnectionSuccess()
    }

    override fun CallbackError(error: IBLE_Error?) {
        Log.d(TAG, "CallbackError = $error")
        isensCallback.onError(error.convertToIsensError())
    }

    override fun startScan(deviceType: IsensDeviceType) {
        this.isensDeviceType = deviceType
        discoveredDevices.clear()
        scanCallback = getScanCallback()

        bluetoothAdapter.bluetoothLeScanner.flushPendingScanResults(scanCallback)
        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        bluetoothAdapter.bluetoothLeScanner.startScan(emptyList(), getScanSetting(), scanCallback)
    }

    override fun connectDevice(address: String) {
        IBLE_Manager.getInstance().ConnectDevice(address)
    }

    override fun requestRecordsAfter(sequenceNumber: Int) {
        IBLE_Manager.getInstance().RequestRecordAfterSequence(sequenceNumber - 1)
    }

    override fun requestAllRecords() {
        IBLE_Manager.getInstance().RequestAllRecords()
    }

    override fun requestTimeSync() {
        IBLE_Manager.getInstance().RequestTimeSync()
    }

    override fun stopScan() {
        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
    }

    override fun disconnect() {
        IBLE_Manager.getInstance().DisconnectDevice()
    }

    private fun updateScanResult(result: ScanResult, callback: IsensCallback) {
        if (isDeviceCareSens(result)) {
            val device = DiscoveredIsensDevice(
                name = result.device.name,
                address = result.device.address,
            )

            discoveredDevices.add(device)
        }

        callback.onScanned(discoveredDevices.toList())
    }

    private fun convertRecordsOf(records: SparseArray<IBLE_GlucoseRecord>?): List<IsensGlucoseRecord> {
        if (records == null) {
            return emptyList()
        }

        val result = mutableListOf<IsensGlucoseRecord>()
        for (i in 1 until records.size()) {
            val data = records.valueAt(i)
            val record = IsensGlucoseRecord(
                sequenceNumber = data.sequenceNumber,
                time = data.time,
                glucoseData = data.glucoseData,
                period = getPeriodOf(data)
            )
            result.add(record)
        }

        return result
    }

    private fun IBLE_Error?.convertToIsensError(): IsensError {
        return if (this == null) IsensError.UNKNOWN_ERROR
        else IsensError.values().firstOrNull { it.name == this.name }
            ?: IsensError.UNKNOWN_ERROR
    }

    private fun getPeriodOf(record: IBLE_GlucoseRecord): IsensRecordPeriod {
        if (record.flag_fasting == 1) {
            return IsensRecordPeriod.FASTING
        }

        return when (record.flag_meal) {
            -1 -> IsensRecordPeriod.BEFORE_MEAL
            1 -> IsensRecordPeriod.AFTER_MEAL
            else -> IsensRecordPeriod.UNKNOWN
        }
    }

    private fun isDeviceCareSens(result: ScanResult): Boolean {
        val deviceType: IsensDeviceType = isensDeviceType
            ?: throw java.lang.IllegalStateException("IsensDeviceType should not be null")

        return try {
            result.device.name.startsWith(deviceType.category)
        } catch (e: java.lang.NullPointerException) {
            false
        }
    }

    private fun getScanCallback() = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                updateScanResult(result, isensCallback)
            }
        }
    }

    private fun getScanSetting() : ScanSettings {
        return ScanSettings
            .Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .build()
    }

    companion object {
        private const val TAG = "[IsensSDK]"
        private var isManagerInitialized = false

        fun getIsensManager(context: Context, callback: IsensCallback): IsensBleDeviceManager {
            return IsensManager(context, callback)
        }

        private fun initSdk(context: Context) {
            if (isManagerInitialized.not()) {
                isManagerInitialized = true
                IBLE_Manager.getInstance().InitSDK(context.applicationContext)
            }
        }
    }
}

interface IsensBleDeviceManager {
    fun startScan(deviceType: IsensDeviceType)

    fun connectDevice(address: String)

    fun requestRecordsAfter(sequenceNumber: Int)

    fun requestAllRecords()

    fun requestTimeSync()

    fun stopScan()

    fun disconnect()
}
