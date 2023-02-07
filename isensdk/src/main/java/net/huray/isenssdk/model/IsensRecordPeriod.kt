package net.huray.isenssdk.model

enum class IsensRecordPeriod(text: String) {
    UNKNOWN("미입력"),
    BEFORE_MEAL("식전"),
    AFTER_MEAL("식후"),
    FASTING("공복");
}