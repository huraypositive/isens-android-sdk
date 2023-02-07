package net.huray.isenssdk

import net.huray.isenssdk.model.DiscoveredIsensDevice
import net.huray.isenssdk.model.IsensError
import net.huray.isenssdk.model.IsensGlucoseRecord

interface IsensCallback {
    fun onScanned(devices: List<DiscoveredIsensDevice>)

    fun onConnectionSuccess()

    fun onReceiveData(records: List<IsensGlucoseRecord>)

    fun onError(error: IsensError)

    fun onDataEmpty()
}