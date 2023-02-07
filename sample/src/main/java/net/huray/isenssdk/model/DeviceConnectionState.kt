package net.huray.isenssdk.model

sealed class DeviceConnectionState {
    object Idle : DeviceConnectionState()

    object Scanning : DeviceConnectionState()

    data class Failed(
        val reason: IsensError
    ) : DeviceConnectionState()

    data class OnScanned(
        val discoveredDevices: List<DiscoveredIsensDevice>
    ) : DeviceConnectionState()

    object Connecting : DeviceConnectionState()

    object ConnectionSuccess : DeviceConnectionState()

    data class TransferSuccess(
        val records: List<IsensGlucoseRecord>
    ) : DeviceConnectionState()

    object NoDataTransferred : DeviceConnectionState()
}
