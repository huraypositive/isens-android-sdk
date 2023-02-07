package net.huray.isenssdk.ui.device_list

import net.huray.isenssdk.model.IsensDeviceType

data class DeviceStatus(
    val deviceType: IsensDeviceType,
    val isConnected: Boolean,
)