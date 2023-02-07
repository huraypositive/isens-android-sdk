package net.huray.isenssdk.ui.transfer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.huray.isenssdk.databinding.ItemRecordDataBinding

class RecordViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemRecordDataBinding.bind(containerView)
    val tvTimeStamp = binding.tvRecordTime
}