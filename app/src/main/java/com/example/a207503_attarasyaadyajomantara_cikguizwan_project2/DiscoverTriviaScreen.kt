package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase.CommunityKouleej
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network.TriviaQuestion

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  DISCOVER — two things to discover, one tab:
 *
 *   • "Community quizzes" → Kouleejes other users have shared
 *                           (PILLAR 2 · Firebase Firestore)
 *   • "Practice questions" → live trivia pulled from the internet
 *                           (PILLAR 3 · Retrofit / Open Trivia DB)
 *
 *  Both mandatory pillars live here. The screen has the shared bottom nav bar
 *  (no back button) so it behaves like a top-level destination.
 * ─────────────────────────────────────────────────────────────────────────────
 */

// Open Trivia DB category ids relevant to education.
private val CATEGORIES = listOf(
    "General" to 9,
    "Science" to 17,
    "Computers" to 18,
    "Maths" to 19,
    "Geography" to 22,
    "History" to 23
)

@Composable
fun DiscoverScreen(
    community: List<CommunityKouleej>,
    isFirebaseAvailable: Boolean,
    triviaState: TriviaUiState,
    onLoadTrivia: (Int) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onJoinByPin: (Int) -> Unit,
    onScanQr: () -> Unit
) {
    // 0 = Community quizzes (Firestore), 1 = Practice questions (Retrofit)
    var tab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableIntStateOf(18) }
    var showJoinDialog by remember { mutableStateOf(false) }

    // Fetch trivia the first time the Practice tab is opened.
    LaunchedEffect(tab) {
        if (tab == 1 && triviaState is TriviaUiState.Idle) onLoadTrivia(selectedCategory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ── Title row (no back button — navigation is via the bottom bar) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Discover", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.weight(1f))
            if (tab == 1) {
                IconButton(onClick = { onLoadTrivia(selectedCategory) }) {
                    Icon(Icons.Default.Refresh, "Reload", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // ── Tab toggle ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = tab == 0,
                onClick = { tab = 0 },
                label = { Text("Community quizzes") }
            )
            FilterChip(
                selected = tab == 1,
                onClick = { tab = 1 },
                label = { Text("Practice questions") }
            )
        }

        // ── Content area ────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            if (tab == 0) {
                CommunityContent(community = community, isFirebaseAvailable = isFirebaseAvailable)
            } else {
                PracticeContent(
                    state = triviaState,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it; onLoadTrivia(it) }
                )
            }
        }

        // ── Shared bottom nav bar ───────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home,   "Home",     onClick = onNavigateHome)
            NavItem(Icons.Default.Search, "Discover", active = true)
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { showJoinDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, "Join a Kouleej", tint = Color.White)
            }
            NavItem(Icons.Default.Add,  "Create",  onClick = onNavigateToCreate)
            NavItem(Icons.Default.Menu, "Library", onClick = onNavigateToLibrary)
        }

        if (showJoinDialog) {
            JoinKouleejDialog(
                onDismiss   = { showJoinDialog = false },
                onJoinByPin = { id -> showJoinDialog = false; onJoinByPin(id) },
                onScanQr    = { showJoinDialog = false; onScanQr() }
            )
        }
    }
}

// ── Community quizzes (Firestore) ────────────────────────────────
@Composable
private fun CommunityContent(community: List<CommunityKouleej>, isFirebaseAvailable: Boolean) {
    when {
        !isFirebaseAvailable -> InfoCard(
            title = "Cloud not configured",
            body  = "Add your google-services.json to enable Firebase Firestore. " +
                    "The rest of the app still works in the meantime."
        )
        community.isEmpty() -> InfoCard(
            title = "No shared quizzes yet",
            body  = "Open one of your Kouleejes and tap “Share to community” to publish the first one."
        )
        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(community, key = { it.id }) { item -> CommunityCard(item) }
        }
    }
}

@Composable
private fun CommunityCard(item: CommunityKouleej) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "${item.subject} · ${item.questionCount} questions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (item.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(item.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountCircle, contentDescription = null,
                    modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "by ${item.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Practice questions (Retrofit / Open Trivia DB) ───────────────
@Composable
private fun PracticeContent(
    state: TriviaUiState,
    selectedCategory: Int,
    onSelectCategory: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CATEGORIES.forEach { (label, id) ->
                FilterChip(
                    selected = selectedCategory == id,
                    onClick = { onSelectCategory(id) },
                    label = { Text(label) }
                )
            }
        }

        when (state) {
            is TriviaUiState.Loading, TriviaUiState.Idle -> CenterBox { CircularProgressIndicator() }
            is TriviaUiState.Error -> CenterBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { onSelectCategory(selectedCategory) }) { Text("Retry") }
                }
            }
            is TriviaUiState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(state.questions) { q -> QuestionCard(q) }
            }
        }
    }
}

@Composable
private fun QuestionCard(question: TriviaQuestion) {
    var revealed by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { revealed = !revealed },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${question.category} · ${question.difficulty}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(question.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            question.allAnswers.forEach { answer ->
                val isCorrect = revealed && answer == question.correctAnswer
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "•  $answer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isCorrect) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.CheckCircle, contentDescription = "Correct answer",
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (!revealed) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tap to reveal the answer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(16.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CenterBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}
