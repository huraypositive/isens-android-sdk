package net.huray.isenssdk.ui.device_list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.huray.isenssdk.databinding.ItemDeviceListBinding

class DeviceListViewHolder(
    containerView: View,
) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemDeviceListBinding.bind(containerView)
    val viewGroup = binding.vgDeviceItem
    val tvDevice = binding.tvDeviceItem
    val ivConnectionIndicator = binding.ivConnectionIndicator
}