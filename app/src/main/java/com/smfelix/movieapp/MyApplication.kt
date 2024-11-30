package com.smfelix.movieapp

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class MyApplication : Application() {
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    // Get or create a ViewModelStore for a specific category at app level
    fun getViewModelStoreOwner(category: String): ViewModelStoreOwner {
        val store = viewModelStores.getOrPut(category) { ViewModelStore() }
        return object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = store
        }
    }
}
