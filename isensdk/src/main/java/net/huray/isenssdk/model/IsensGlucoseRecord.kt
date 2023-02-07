package net.huray.isenssdk.model

data class IsensGlucoseRecord(
    val sequenceNumber: Int,
    val time: Long,
    val glucoseData: Double,
    val period: IsensRecordPeriod
)