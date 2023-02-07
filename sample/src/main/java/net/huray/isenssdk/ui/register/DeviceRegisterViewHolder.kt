package net.huray.isenssdk.ui.register

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.huray.isenssdk.databinding.ItemScannedDeviceBinding

class DeviceRegisterViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemScannedDeviceBinding.bind(containerView)
    val vgDevice = binding.vgScannedDevice
    val tvName = binding.tvScannedDeviceName
    val tvAddress = binding.tvScannedDeviceAddress
}