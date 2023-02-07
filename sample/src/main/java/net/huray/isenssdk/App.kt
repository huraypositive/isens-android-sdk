package net.huray.isenssdk

import android.app.Application

class App : Application() {

    init {
        application = this
    }

    companion object {
        private lateinit var application: App

        val instance: App get() = application
    }
}