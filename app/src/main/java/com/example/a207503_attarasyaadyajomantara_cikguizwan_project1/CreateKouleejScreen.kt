package com.example.a207503_attarasyaadyajomantara_cikguizwan_project1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Screen 5 — Create a new Kouleej (form) ──────────────────────
// Visual language mirrors ProfileSetupScreen: same top bar, same
// labels, same TextField / ExposedDropdownMenuBox / Button styling.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKouleejScreen(
    onBack: () -> Unit,
    onSubmit: (title: String, subject: String, questionCount: Int, description: String) -> Unit
) {
    var title            by remember { mutableStateOf("") }
    var subject          by remember { mutableStateOf("") }
    var questionCountStr by remember { mutableStateOf("") }
    var description      by remember { mutableStateOf("") }
    var showError        by remember { mutableStateOf(false) }

    val subjectOptions = listOf(
        "Mathematics", "Science", "History", "English", "Geography",
        "Computer Science", "Bahasa Melayu"
    )
    var subjectExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ── Top bar (matches ProfileSetupScreen) ───────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                "Create a Kouleej",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Title field ────────────────────────────────────
            Column {
                Text(
                    "Title",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                TextField(
                    value = title,
                    onValueChange = { title = it; showError = false },
                    placeholder = { Text("e.g. Pop Quiz: Algebra Basics", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    isError = showError && title.isBlank(),
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showError && title.isBlank()) {
                    Text("Title is required", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            // ── Subject dropdown ───────────────────────────────
            Column {
                Text(
                    "Subject",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    TextField(
                        value = subject,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select subject", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(subjectExpanded) },
                        isError = showError && subject.isBlank(),
                        colors = fieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        subjectOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    subject = option
                                    subjectExpanded = false
                                    showError = false
                                }
                            )
                        }
                    }
                }
                if (showError && subject.isBlank()) {
                    Text("Please select a subject", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            // ── Question count ────────────────────────────────
            Column {
                Text(
                    "Number of questions",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                TextField(
                    value = questionCountStr,
                    onValueChange = { new ->
                        // keep only digits so parse is always safe
                        questionCountStr = new.filter { it.isDigit() }
                        showError = false
                    },
                    placeholder = { Text("e.g. 10", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    isError = showError && (questionCountStr.isBlank() || questionCountStr.toIntOrNull() == null),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showError && (questionCountStr.isBlank() || questionCountStr.toIntOrNull() == null)) {
                    Text("Enter a number", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            // ── Description (multi-line) ──────────────────────
            Column {
                Text(
                    "Description (optional)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("What is this Kouleej about?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = fieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Submit button ─────────────────────────────────
            Button(
                onClick = {
                    val count = questionCountStr.toIntOrNull()
                    if (title.isBlank() || subject.isBlank() || count == null) {
                        showError = true
                    } else {
                        onSubmit(title, subject, count, description)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Publish to Library",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedTextColor        = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
    focusedIndicatorColor   = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor             = MaterialTheme.colorScheme.onSurface
)
