package net.huray.isenssdk.ui.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.huray.isenssdk.App
import net.huray.isenssdk.IsensCallback
import net.huray.isenssdk.IsensManager
import net.huray.isenssdk.model.*
import net.huray.isenssdk.utils.PrefUtils

class DeviceTransferViewModel(
    private val deviceType: IsensDeviceType
) : ViewModel(), IsensCallback {

    private val _loadingEvent = MutableLiveData<Boolean>()
    val loadingEvent: LiveData<Boolean> get() = _loadingEvent

    private val _connectionEvent = MutableLiveData<DeviceConnectionState>()
    val connectionEvent: LiveData<DeviceConnectionState> get() = _connectionEvent

    private val isensManager = IsensManager.getIsensManager(App.instance, this)

    override fun onCleared() {
        super.onCleared()
        isensManager.disconnect()
    }

    override fun onScanned(devices: List<DiscoveredIsensDevice>) { /* not use */ }

    override fun onConnectionSuccess() {
        val sequenceNumber = 0
        isensManager.requestRecordsAfter(sequenceNumber)
    }

    override fun onReceiveData(records: List<IsensGlucoseRecord>) {
        _loadingEvent.postValue(false)
        _connectionEvent.postValue(DeviceConnectionState.TransferSuccess(records.reversed()))
    }

    override fun onError(error: IsensError) {
        _loadingEvent.postValue(false)
        _connectionEvent.postValue(DeviceConnectionState.Failed(error))
    }

    override fun onDataEmpty() {
        _loadingEvent.postValue(false)
        _connectionEvent.postValue(DeviceConnectionState.NoDataTransferred)
    }

    fun requestData() {
        _loadingEvent.value = true
        val address = PrefUtils.getCareSensAddress()

        if (address != null) {
            isensManager.connectDevice(address)
        }
    }

    fun cancel() {
        isensManager.disconnect()
        _loadingEvent.postValue(false)
        _connectionEvent.postValue(DeviceConnectionState.Idle)
    }

    fun disconnectDevice() {
        PrefUtils.removeCareSensAddress()
    }
}