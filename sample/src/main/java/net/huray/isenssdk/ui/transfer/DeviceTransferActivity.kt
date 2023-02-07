package net.huray.isenssdk.ui.transfer

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import net.huray.isenssdk.model.IsensDeviceType
import net.huray.isenssdk.model.IsensError
import net.huray.isenssdk.R
import net.huray.isenssdk.common.BaseActivity
import net.huray.isenssdk.databinding.ActivityRequestDataBinding
import net.huray.isenssdk.model.DeviceConnectionState
import net.huray.isenssdk.utils.Const

class DeviceTransferActivity : BaseActivity() {
    private lateinit var binding: ActivityRequestDataBinding

    private lateinit var adapter: DeviceTransferAdapter
    private lateinit var isensDeviceType: IsensDeviceType

    private val viewModel: DeviceTransferViewModel by viewModelsFactory {
        val deviceId = intent.getIntExtra(Const.EXTRA_DEVICE_TYPE, 0)
        isensDeviceType = IsensDeviceType.fromId(deviceId)

        DeviceTransferViewModel(isensDeviceType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.tvRequestTitle.text = isensDeviceType.modelName
        binding.tvDisconnectDevice.setOnClickListener { showConfirmDialog() }

        adapter = DeviceTransferAdapter(isensDeviceType)
        binding.rvRequestedDataList.adapter = adapter
    }

    private fun initObservers() {
        viewModel.connectionEvent.observe(this) { state ->
            when (state) {
                is DeviceConnectionState.Failed -> handleFailureEvent(state.reason)
                is DeviceConnectionState.NoDataTransferred -> {
                    Toast.makeText(
                        this,
                        getString(R.string.no_data_to_bring),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DeviceConnectionState.TransferSuccess -> {
                    adapter.updateRecords(state.records)
                    Toast.makeText(
                        this,
                        getString(R.string.success_to_receive_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    private fun handleFailureEvent(error: IsensError) {
        Toast.makeText(
            this,
            error.name,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showConfirmDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(getString(R.string.alert))
        dialog.setMessage(getString(R.string.sure_to_disconnect))

        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.cacel)
        ) { _: DialogInterface?, _: Int -> dialog.dismiss() }

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.disconnect)
        ) { _: DialogInterface?, _: Int ->
            viewModel.disconnectDevice()
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }
}