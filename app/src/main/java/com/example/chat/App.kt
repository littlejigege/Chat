package com.example.chat

import android.app.Application
import com.example.chat.di.DaggerAppComponent
import com.mobile.utils.Utils
import com.vondear.rxtools.RxTool
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * Created by jimji on 2017/10/23.
 */
class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().build()
    }

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        RxTool.init(this)
    }
}