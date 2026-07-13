package com.klim.typeless

import android.app.Application
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TypeLessApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeAds()
    }

    private fun initializeAds() {
        MobileAds.initialize(this, object : InitializationListener {
            override fun onInitializationCompleted() {}
        })
    }
}