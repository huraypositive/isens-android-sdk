package net.huray.isenssdk.model

enum class IsensDeviceType(
    val id: Int,
    val modelName: String,
    val category: String,
) {
    CARE_SENS_N_PREMIER(1, "CareSens N Premier", "CareSens");

    companion object {
        fun fromId(id: Int): IsensDeviceType {
            return values().firstOrNull {
                it.id == id
            } ?: throw java.lang.IllegalStateException("$id is not supported device id")
        }
    }
}