package com.el.mybasekotlin.helpers.flowbus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.el.mybasekotlin.helpers.flowbus.FlowBusInitApplication.application

object ViewModelAppProvider : ViewModelStoreOwner {
    private val eventViewModel: ViewModelStore = ViewModelStore()

    private val applicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(
            ViewModelAppProvider,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
    }

    fun <T : ViewModel> getApplicationScope(model: Class<T>): T = applicationProvider[model]
    override val viewModelStore: ViewModelStore
        get() = eventViewModel

}

