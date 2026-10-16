package com.fit10.app

import android.app.Application
import com.fit10.app.core.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Fit10Application : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // sqlcipher-android does not load its native library itself; it must happen before the database opens.
        System.loadLibrary("sqlcipher")
        container = AppContainer(this)

        // Preload seed catalog on startup if needed
        applicationScope.launch {
            container.workoutRepository.ensureSeedDataLoaded()
        }
    }
}
