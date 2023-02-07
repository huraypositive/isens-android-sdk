package net.huray.isenssdk.ui.register

import net.huray.isenssdk.model.Device

interface ScannedItemClickListener {
    fun onDeviceClickListener(device: Device)
}