package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
    // ── Project 2 additions ──
    const val DISCOVER       = "discover"   // Pillar 2 (Firestore) + Pillar 3 (Retrofit)
    const val QR_SCANNER     = "qr_scanner" // Pillar 4 · Camera + ML Kit
}

// ─── Navigation graph (9 screens) ────────────────────────────────
@Composable
fun KahootNavGraph(
    navController: NavHostController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    // The ViewModel is Room/Retrofit/Firestore-backed — wired through
    // AppViewModelProvider which pulls the repositories from
    // KouleejApplication.container.
    viewModel: KouleejViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val user         by viewModel.user.collectAsStateWithLifecycle()
    val kouleejes    by viewModel.kouleejes.collectAsStateWithLifecycle()
    val selectedId   by viewModel.selectedKouleejId.collectAsStateWithLifecycle()
    val community    by viewModel.communityKouleejes.collectAsStateWithLifecycle()
    val triviaState  by viewModel.triviaState.collectAsStateWithLifecycle()
    val communityMsg by viewModel.communityMessage.collectAsStateWithLifecycle()

    // Surface community publish results as a Toast, then clear the message.
    val context = LocalContext.current
    LaunchedEffect(communityMsg) {
        communityMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearCommunityMessage()
        }
    }

    NavHost(
        navController    = navController,
        startDestination = KahootRoutes.HOME
    ) {

        // 1. Home
        composable(KahootRoutes.HOME) {
            KahootHomeScreen(
                darkTheme             = darkTheme,
                onToggleTheme         = onToggleTheme,
                onNavigateToProfile   = { navController.navigate(KahootRoutes.PROFILE_SETUP) },
                onNavigateToLibrary   = { navController.navigate(KahootRoutes.LIBRARY) },
                onNavigateToCreate    = { navController.navigate(KahootRoutes.CREATE_KOULEEJ) },
                onNavigateToDiscover  = { navController.navigate(KahootRoutes.DISCOVER) },
                onNavigateToQrScan    = { navController.navigate(KahootRoutes.QR_SCANNER) },
                onJoinByPin           = { id ->
                    // Treat the entered PIN as a Kouleej id and open its detail.
                    viewModel.selectKouleej(id)
                    navController.navigate(KahootRoutes.KOULEEJ_DETAIL)
                }
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

        // 4. Library — shows kouleejes from the Room database
        composable(KahootRoutes.LIBRARY) {
            LibraryScreen(
                kouleejes      = kouleejes,
                onBack         = { navController.popBackStack() },
                onKouleejTap   = { id ->
                    viewModel.selectKouleej(id)
                    navController.navigate(KahootRoutes.KOULEEJ_DETAIL)
                },
                onCreateNew    = { navController.navigate(KahootRoutes.CREATE_KOULEEJ) },
                onJoinByPin    = { id ->
                    viewModel.selectKouleej(id)
                    navController.navigate(KahootRoutes.KOULEEJ_DETAIL)
                },
                onScanQr       = { navController.navigate(KahootRoutes.QR_SCANNER) }
            )
        }

        // 5. Create a new kouleej (form) — inserts into Room via the repository.
        composable(KahootRoutes.CREATE_KOULEEJ) {
            CreateKouleejScreen(
                onBack   = { navController.popBackStack() },
                onSubmit = { title, subject, questions, description ->
                    viewModel.addKouleej(title, subject, questions, description)
                    navController.navigate(KahootRoutes.LIBRARY) {
                        popUpTo(KahootRoutes.HOME)
                    }
                }
            )
        }

        // 6. Kouleej detail (with QR share + Firestore publish)
        composable(KahootRoutes.KOULEEJ_DETAIL) {
            val selected = kouleejes.firstOrNull { it.id == selectedId }
            KouleejDetailScreen(
                kouleej              = selected,
                onBack               = { navController.popBackStack() },
                onDelete             = {
                    selected?.let { viewModel.deleteKouleej(it.id) }
                    navController.popBackStack()
                },
                onPublishToCommunity = { selected?.let { viewModel.publishToCommunity(it) } },
                isFirebaseAvailable  = viewModel.isFirebaseAvailable
            )
        }

        // 7. Discover — community quizzes (Firestore) + practice questions (Retrofit)
        composable(KahootRoutes.DISCOVER) {
            DiscoverScreen(
                community           = community,
                isFirebaseAvailable = viewModel.isFirebaseAvailable,
                triviaState         = triviaState,
                onLoadTrivia        = { category -> viewModel.loadTrivia(category) },
                onNavigateHome      = {
                    navController.navigate(KahootRoutes.HOME) {
                        popUpTo(KahootRoutes.HOME) { inclusive = true }
                    }
                },
                onNavigateToCreate  = { navController.navigate(KahootRoutes.CREATE_KOULEEJ) },
                onNavigateToLibrary = { navController.navigate(KahootRoutes.LIBRARY) },
                onJoinByPin         = { id ->
                    viewModel.selectKouleej(id)
                    navController.navigate(KahootRoutes.KOULEEJ_DETAIL)
                },
                onScanQr            = { navController.navigate(KahootRoutes.QR_SCANNER) }
            )
        }

        // 8. QR scanner — scan a Kouleej QR to open it (Camera + ML Kit)
        composable(KahootRoutes.QR_SCANNER) {
            QrScannerScreen(
                onBack           = { navController.popBackStack() },
                onKouleejScanned = { id ->
                    viewModel.selectKouleej(id)
                    navController.navigate(KahootRoutes.KOULEEJ_DETAIL) {
                        // Replace the scanner so Back returns Home, not the camera.
                        popUpTo(KahootRoutes.QR_SCANNER) { inclusive = true }
                    }
                }
            )
        }
    }
}
