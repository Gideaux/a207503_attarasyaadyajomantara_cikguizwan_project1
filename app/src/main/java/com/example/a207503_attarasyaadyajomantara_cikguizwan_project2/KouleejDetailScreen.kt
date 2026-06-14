package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.KouleejEntity
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.util.QrCodeGenerator

// ─── Screen — Kouleej detail (item detail + QR share + cloud share) ──────────
@Composable
fun KouleejDetailScreen(
    kouleej: KouleejEntity?,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onPublishToCommunity: () -> Unit = {},
    isFirebaseAvailable: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ── Top bar ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                "Kouleej Details",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (kouleej != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint               = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // ── Empty state ────────────────────────────────────────
        if (kouleej == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kouleej not found.", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onBack,
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Go back")
                }
            }
            return
        }

        var showQr by remember { mutableStateOf(false) }

        // ── Scrollable body ─────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Big circular badge
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onPrimary,
                        modifier           = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = kouleej.title, style = MaterialTheme.typography.headlineLarge)
                Text(
                    text       = "${kouleej.questionCount} questions",
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailRow(label = "Kouleej #", value = kouleej.id.toString())
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    DetailRow(label = "Title",     value = kouleej.title)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    DetailRow(label = "Subject",   value = kouleej.subject)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    DetailRow(label = "Questions", value = kouleej.questionCount.toString())
                }
            }

            // Description, if present
            if (kouleej.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Description",
                    style    = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text     = kouleej.description,
                        modifier = Modifier.padding(16.dp),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = Color.White
                    )
                }
            }

            // ── QR share section (Pillar 4 — generation side) ───
            Spacer(modifier = Modifier.height(24.dp))
            if (showQr) {
                // Generate the QR bitmap for this Kouleej's payload, cached by id.
                val qrBitmap = remember(kouleej.id) {
                    QrCodeGenerator.generate(QrCodeGenerator.payloadFor(kouleej.id))
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR code for ${kouleej.title}",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(220.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Scan this with the QR scanner to open this quiz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Action buttons (uniform size, colour, and weight) ──────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Shared styling so all three buttons look identical.
            val buttonShape  = RoundedCornerShape(10.dp)
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
            val labelColor   = MaterialTheme.colorScheme.onSecondary

            // Toggle QR visibility
            Button(
                onClick  = { showQr = !showQr },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = buttonShape,
                colors   = buttonColors
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = labelColor)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (showQr) "Hide QR code" else "Share via QR code",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = labelColor
                )
            }

            // Publish to Firestore community
            Button(
                onClick  = onPublishToCommunity,
                enabled  = isFirebaseAvailable,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = buttonShape,
                colors   = buttonColors
            ) {
                Text(
                    if (isFirebaseAvailable) "Share to community" else "Community (needs Firebase)",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = labelColor
                )
            }

            // Back
            Button(
                onClick  = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(52.dp),
                shape    = buttonShape,
                colors   = buttonColors
            ) {
                Text("Back to Library", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = labelColor)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value.ifBlank { "—" },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
