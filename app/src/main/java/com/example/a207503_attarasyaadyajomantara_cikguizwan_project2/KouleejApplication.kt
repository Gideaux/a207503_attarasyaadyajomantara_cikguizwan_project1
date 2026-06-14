package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import android.app.Application
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.AppContainer
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.AppDataContainer

/**
 * Custom Application class that owns the [AppContainer]. Registered in the
 * AndroidManifest via android:name=".KouleejApplication" so it's instantiated
 * before MainActivity, giving the rest of the app a single place to grab
 * shared dependencies (the Room repository).
 */
class KouleejApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
