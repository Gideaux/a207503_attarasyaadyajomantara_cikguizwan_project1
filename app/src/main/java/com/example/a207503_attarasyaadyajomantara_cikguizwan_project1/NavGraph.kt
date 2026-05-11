package com.example.a207503_attarasyaadyajomantara_cikguizwan_project1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// ─── Route constants ─────────────────────────────────────────────
object KahootRoutes {
    const val HOME           = "home"
    const val PROFILE_SETUP  = "profile_setup"
    const val PROFILE_CARD   = "profile_card"
    const val LIBRARY        = "library"
    const val CREATE_KOULEEJ = "create_kouleej"
    const val KOULEEJ_DETAIL = "kouleej_detail"
}

// ─── Navigation graph (6 screens) ────────────────────────────────
@Composable
fun KahootNavGraph(
    navController: NavHostController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: KahootViewModel = viewModel()
) {
    val user         by viewModel.user.collectAsStateWithLifecycle()
    val kouleejes    by viewModel.kouleejes.collectAsStateWithLifecycle()
    val selectedId   by viewModel.selectedKouleejId.collectAsStateWithLifecycle()

    NavHost(
        navController    = navController,
        startDestination = KahootRoutes.HOME
    ) {

        // 1. Home
        composable(KahootRoutes.HOME) {
            KahootHomeScreen(
                darkTheme            = darkTheme,
                onToggleTheme        = onToggleTheme,
                onNavigateToProfile  = { navController.navigate(KahootRoutes.PROFILE_SETUP) },
                onNavigateToLibrary  = { navController.navigate(KahootRoutes.LIBRARY) },
                onNavigateToCreate   = { navController.navigate(KahootRoutes.CREATE_KOULEEJ) }
            )
        }

        // 2. Profile setup (form)
        composable(KahootRoutes.PROFILE_SETUP) {
            ProfileSetupScreen(
                onBack   = { navController.popBackStack() },
                onSubmit = { username, grade, subject ->
                    viewModel.updateUser(username, grade, subject)
                    navController.navigate(KahootRoutes.PROFILE_CARD)
                }
            )
        }

        // 3. Profile card
        composable(KahootRoutes.PROFILE_CARD) {
            ProfileCardScreen(
                user   = user,
                onBack = { navController.popBackStack() },
                onHome = {
                    navController.navigate(KahootRoutes.HOME) {
                        popUpTo(KahootRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // 4. Library (now shows user-created kouleejes)
        composable(KahootRoutes.LIBRARY) {
            LibraryScreen(
                kouleejes      = kouleejes,
                onBack         = { navController.popBackStack() },
                onKouleejTap   = { id ->
                    viewModel.selectKouleej(id)
                    navController.navigate(KahootRoutes.KOULEEJ_DETAIL)
                },
                onCreateNew    = { navController.navigate(KahootRoutes.CREATE_KOULEEJ) }
            )
        }

        // 5. Create a new kouleej (Project 1 — form)
        composable(KahootRoutes.CREATE_KOULEEJ) {
            CreateKouleejScreen(
                onBack   = { navController.popBackStack() },
                onSubmit = { title, subject, questions, description ->
                    viewModel.addKouleej(title, subject, questions, description)
                    // After adding, drop straight into the Library so the user
                    // sees the new item on a different screen — this is the
                    // rubric's required "add + display" demo.
                    navController.navigate(KahootRoutes.LIBRARY) {
                        popUpTo(KahootRoutes.HOME)
                    }
                }
            )
        }

        // 6. Kouleej detail (item detail)
        composable(KahootRoutes.KOULEEJ_DETAIL) {
            val selected = kouleejes.firstOrNull { it.id == selectedId }
            KouleejDetailScreen(
                kouleej  = selected,
                onBack   = { navController.popBackStack() },
                onDelete = {
                    selected?.let { viewModel.deleteKouleej(it.id) }
                    navController.popBackStack()
                }
            )
        }
    }
}
