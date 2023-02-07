package net.huray.isenssdk.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import net.huray.isenssdk.model.IsensDeviceType
import net.huray.isenssdk.model.IsensError
import net.huray.isenssdk.R
import net.huray.isenssdk.common.BaseActivity
import net.huray.isenssdk.databinding.ActivityDeviceRegisterBinding
import net.huray.isenssdk.model.Device
import net.huray.isenssdk.model.DeviceConnectionState
import net.huray.isenssdk.ui.transfer.DeviceTransferActivity
import net.huray.isenssdk.utils.Const

class DeviceRegisterActivity : BaseActivity(), ScannedItemClickListener {
    private lateinit var binding: ActivityDeviceRegisterBinding

    private lateinit var adapter: DeviceRegisterAdapter
    private lateinit var isensDeviceType: IsensDeviceType

    private val viewModel: DeviceRegisterViewModel by viewModelsFactory {
        val deviceId = intent.getIntExtra(Const.EXTRA_DEVICE_TYPE, 0)
        isensDeviceType = IsensDeviceType.fromId(deviceId)

        DeviceRegisterViewModel(isensDeviceType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initObservers()
        initViews()
    }

    override fun onDeviceClickListener(device: Device) {
        viewModel.connectDevice(address = device.address)
    }

    private fun initObservers() {
        viewModel.connectionEvent.observe(this) { state ->
            when (state) {
                is DeviceConnectionState.Scanning -> handleScanningEvent()
                is DeviceConnectionState.OnScanned -> adapter.updateDevices(state.discoveredDevices)
                is DeviceConnectionState.Failed -> handleCancelEvent(state.reason)
                is DeviceConnectionState.ConnectionSuccess -> handleSuccessEvent()
                else -> {}
            }
        }
    }

    private fun initViews() {
        adapter = DeviceRegisterAdapter(this)

        binding.tvScanTitle.text = isensDeviceType.modelName
        binding.rvScannedDeviceList.adapter = adapter
    }

    private fun handleCancelEvent(error: IsensError) {
        Toast.makeText(this, error.name, Toast.LENGTH_SHORT).show()
        setViewForReadyToScan()
    }

    private fun handleScanningEvent() {
        binding.tvScanDescription.text = getString(R.string.scanning_device)
    }

    private fun handleSuccessEvent() {
        Toast.makeText(
            this,
            getString(R.string.connection_success),
            Toast.LENGTH_SHORT
        ).show()

        moveToTransferActivity()
    }

    private fun setViewForReadyToScan() {
        binding.btnScan.text = getString(R.string.start_scan_device)
        binding.tvScanDescription.text = getString(R.string.click_device_scan_button)
    }

    private fun moveToTransferActivity() {
        val intent = Intent(this, DeviceTransferActivity::class.java)
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, isensDeviceType.id)
        startActivity(intent)
        finish()
    }
}