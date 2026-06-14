package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.KouleejEntity

@Composable
fun LibraryScreen(
    kouleejes: List<KouleejEntity>,
    onBack: () -> Unit,
    onKouleejTap: (Int) -> Unit,
    onCreateNew: () -> Unit,
    onJoinByPin: (Int) -> Unit = {},
    onScanQr: () -> Unit = {}
) {
    var showJoinDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ── Top bar (no back button — navigation is via the bottom bar) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Library",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // ── Scrolling body so the new "My Kouleejes" group fits ──
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // ── First group: Kahoots & Reports ───────────────────
            LibraryGroup {
                LibraryItem(icon = Icons.Default.Person, label = "Kahoots")
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                LibraryItem(icon = Icons.Default.Star,   label = "Reports")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Second group: Study groups, Courses, Groups, Learning ─
            LibraryGroup {
                LibraryItem(icon = Icons.Default.List,       label = "Study groups")
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                LibraryItem(icon = Icons.Default.PlayArrow,  label = "Courses")
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                LibraryItem(icon = Icons.Default.AccountBox, label = "Groups")
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                LibraryItem(icon = Icons.Default.Home,       label = "Your Learning")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Third group: My Kouleejes (Project 1 addition) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "My Kouleejes",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (kouleejes.isEmpty()) {
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        "No kouleejes yet. Tap Create to make your first one.",
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LibraryGroup {
                    kouleejes.forEachIndexed { index, kouleej ->
                        KouleejRow(kouleej = kouleej, onClick = { onKouleejTap(kouleej.id) })
                        if (index != kouleejes.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Bottom nav bar ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home,   "Home",     onClick = onBack)
            NavItem(Icons.Default.Search, "Discover")
            Box(
                modifier        = Modifier
                    .size(55.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { showJoinDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, "Join a Kouleej", tint = Color.White)
            }
            NavItem(Icons.Default.Add,  "Create", onClick = onCreateNew)
            NavItem(Icons.Default.Menu, "Library", active = true)
        }

        // Center play button opens the Join dialog (enter a PIN or scan a QR),
        // matching the Home screen — it is no longer a duplicate Create button.
        if (showJoinDialog) {
            JoinKouleejDialog(
                onDismiss   = { showJoinDialog = false },
                onJoinByPin = { id -> showJoinDialog = false; onJoinByPin(id) },
                onScanQr    = { showJoinDialog = false; onScanQr() }
            )
        }
    }
}

// ── Reusable card group wrapper ──────────────────────────────────
@Composable
private fun LibraryGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(content = content)
    }
}

// ── Single row item (original Lab 4 placeholders) ────────────────
@Composable
private fun LibraryItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint               = MaterialTheme.colorScheme.onSurface,
            modifier           = Modifier.size(24.dp)
        )
        Text(
            text       = label,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            modifier   = Modifier.padding(start = 16.dp)
        )
    }
}

// ── Row representing one user-created Kouleej (Room-backed) ──────
@Composable
private fun KouleejRow(kouleej: KouleejEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = Icons.Default.PlayArrow,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text       = kouleej.title,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
            Text(
                text  = "${kouleej.subject} · ${kouleej.questionCount} questions",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Icon(
            imageVector        = Icons.Default.KeyboardArrowRight,
            contentDescription = "Open",
            tint               = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// NavItem is defined in MainActivity.kt and shared across the package
