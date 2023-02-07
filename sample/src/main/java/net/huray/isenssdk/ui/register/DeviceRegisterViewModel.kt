package net.huray.isenssdk.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.huray.isenssdk.App
import net.huray.isenssdk.IsensCallback
import net.huray.isenssdk.IsensManager
import net.huray.isenssdk.model.*
import net.huray.isenssdk.utils.PrefUtils

class DeviceRegisterViewModel(
    private val deviceType: IsensDeviceType
) : ViewModel(), IsensCallback {

    private val _connectionEvent = MutableLiveData<DeviceConnectionState>()
    val connectionEvent: LiveData<DeviceConnectionState> get() = _connectionEvent

    private val _loadingEvent = MutableLiveData<Boolean>()
    val loadingEvent: LiveData<Boolean> get() = _loadingEvent

    private val isensManager = IsensManager.getIsensManager(App.instance, this)

    private var deviceAddress: String? = null

    override fun onCleared() {
        super.onCleared()
        isensManager.disconnect()
    }

    override fun onScanned(devices: List<DiscoveredIsensDevice>) {
        _connectionEvent.postValue(DeviceConnectionState.OnScanned(devices))
    }

    override fun onConnectionSuccess() {
        setLoadingState(false)
        saveAddress()

        _connectionEvent.postValue(DeviceConnectionState.ConnectionSuccess)
        isensManager.requestTimeSync()

        // 이 때 아래 코드를 호출하면 연결 직후 바로 데이터를 가져올 수도 있다.
        // (데이터는 onReceiveData() 콜백 함수에서 확인)
        // isensManager.requestDataFrom(0)
    }

    override fun onReceiveData(records: List<IsensGlucoseRecord>) { /* not use */ }

    override fun onError(error: IsensError) {
        setLoadingState(false)
        _connectionEvent.postValue(DeviceConnectionState.Failed(error))
    }

    override fun onDataEmpty() { /* not use */ }

    fun startScan() {
        isensManager.startScan(deviceType)
        _connectionEvent.value = DeviceConnectionState.Scanning
    }

    fun stopScan() {
        isensManager.stopScan()
        _connectionEvent.value = DeviceConnectionState.Idle
    }

    fun cancel() {
        isensManager.disconnect()
    }

    fun connectDevice(address: String) {
        setLoadingState(true)
        _connectionEvent.value = DeviceConnectionState.Connecting
        stopScan()
        this.deviceAddress = address
        isensManager.connectDevice(address)
    }

    private fun saveAddress() {
        requireNotNull(deviceAddress) { "deviceAddress should not be null" }
        PrefUtils.saveCareSensAddress(deviceAddress)
    }

    private fun setLoadingState(isLoading: Boolean) {
        _loadingEvent.postValue(isLoading)
    }
}