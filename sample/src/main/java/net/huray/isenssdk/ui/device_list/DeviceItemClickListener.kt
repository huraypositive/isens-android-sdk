package net.huray.isenssdk.ui.device_list

interface DeviceItemClickListener {
    fun onItemClicked(isConnected: Boolean, deviceNumber: Int)
}