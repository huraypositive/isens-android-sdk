package net.huray.isenssdk.ui.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.huray.isenssdk.model.DiscoveredIsensDevice
import net.huray.isenssdk.R
import net.huray.isenssdk.model.Device

class DeviceRegisterAdapter(
    private val clickListener: ScannedItemClickListener,
) : RecyclerView.Adapter<DeviceRegisterViewHolder>() {

    private val devices: MutableList<Device> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceRegisterViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_scanned_device,
                parent,
                false
            )

        return DeviceRegisterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceRegisterViewHolder, position: Int) {
        holder.tvName.text = devices[position].name
        holder.tvAddress.text = devices[position].address

        holder.vgDevice.setOnClickListener {
            clickListener.onDeviceClickListener(devices[position])
        }
    }

    override fun getItemCount(): Int = devices.size

    fun updateDevices(devices: List<DiscoveredIsensDevice>) {
        this.devices.clear()

        for (device in devices) {
            this.devices.add(Device(device.name, device.address))
            notifyItemChanged(this.devices.lastIndex)
        }
    }
}