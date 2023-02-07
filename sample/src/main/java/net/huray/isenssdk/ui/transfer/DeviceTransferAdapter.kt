package net.huray.isenssdk.ui.transfer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.huray.isenssdk.model.IsensDeviceType
import net.huray.isenssdk.model.IsensGlucoseRecord
import net.huray.isenssdk.R

class DeviceTransferAdapter(
    private val deviceType: IsensDeviceType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val glucoseRecords = mutableListOf<IsensGlucoseRecord>()

    override fun getItemViewType(position: Int): Int {
        return deviceType.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemRes = R.layout.item_record_data

        val view = LayoutInflater
            .from(parent.context)
            .inflate(itemRes, parent, false)

        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setBpDatView(
            holder = holder as RecordViewHolder,
            record = glucoseRecords[position]
        )
    }

    override fun getItemCount(): Int = glucoseRecords.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecords(data: List<IsensGlucoseRecord>) {
        glucoseRecords.clear()
        glucoseRecords.addAll(data)
        notifyDataSetChanged()
    }

    private fun setBpDatView(holder: RecordViewHolder, record: IsensGlucoseRecord) {
        holder.tvTimeStamp.text = record.toString()
    }
}