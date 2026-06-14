package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Single source of truth for ViewModel construction. Pulls the
 * [KouleejApplication]'s container and hands the repository to the ViewModel.
 *
 * Mirrors the `AppViewModelProvider` from the Inventory codelab.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val container = kouleejApplication().container
            KouleejViewModel(
                repository          = container.kouleejRepository,
                communityRepository = container.communityRepository,
                triviaRepository    = container.triviaRepository
            )
        }
    }
}

/**
 * Convenience extension to grab the [KouleejApplication] from a
 * [CreationExtras] inside a ViewModel initializer.
 */
fun CreationExtras.kouleejApplication(): KouleejApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KouleejApplication)
