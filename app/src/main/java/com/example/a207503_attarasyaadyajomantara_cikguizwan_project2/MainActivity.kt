package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.ui.theme.A207503_AttarasyaAdyaJomantara_CikguIzwan_Project2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by remember { mutableStateOf(true) }
            A207503_AttarasyaAdyaJomantara_CikguIzwan_Project2Theme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                KahootNavGraph(
                    navController = navController,
                    darkTheme     = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Screen 1 – Home
// ─────────────────────────────────────────────────────────
@Preview
@Composable
fun KahootHomeScreen(
    darkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDiscover: () -> Unit = {},
    onNavigateToQrScan: () -> Unit = {},
    onJoinByPin: (Int) -> Unit = {}
) {
    var searchText       by remember { mutableStateOf("") }
    var submittedSearch  by remember { mutableStateOf("") }
    var expandedMenu     by remember { mutableStateOf("") }
    var showJoinDialog   by remember { mutableStateOf(false) }

    if (submittedSearch.isNotEmpty()) {
        SearchResultsScreen(
            query         = submittedSearch,
            onBack        = { submittedSearch = ""; searchText = "" },
            darkTheme     = darkTheme,
            onToggleTheme = onToggleTheme
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // ── Search bar ──────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    ) {
                        TextField(
                            value       = searchText,
                            onValueChange = { searchText = it },
                            placeholder = {
                                Text(
                                    "Find a Kouleej about...",
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon  = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier           = Modifier.clickable(enabled = searchText.isNotEmpty()) {
                                        submittedSearch = searchText
                                    }
                                )
                            },
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier           = Modifier.clickable { searchText = "" }
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                                focusedIndicatorColor   = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor             = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier         = Modifier.fillMaxSize(),
                            singleLine       = true,
                            keyboardOptions  = androidx.compose.foundation.text.KeyboardOptions(
                                imeAction = androidx.compose.ui.text.input.ImeAction.Search
                            ),
                            keyboardActions  = androidx.compose.foundation.text.KeyboardActions(
                                onSearch = { if (searchText.isNotEmpty()) submittedSearch = searchText }
                            )
                        )
                    }

                    // Profile icon navigates to Profile Setup screen
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint               = if (darkTheme) Color.Yellow else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // ── Category chips ──────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip("Topics",            expandedMenu) { expandedMenu = it }
                    CategoryChip("Special occasions", expandedMenu) { expandedMenu = it }
                    CategoryChip("K!",                expandedMenu) { expandedMenu = it }
                }

                // ── Banner card ─────────────────────────────────────
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier        = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "HOW STUDENTS CAN USE",
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Kouleej!",
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }
                }

                // partner header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Trusted Partners", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "See All",
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                // partner cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PartnerCard("NASA",    Color(0xFF85BFFF))
                    PartnerCard("BBC",     Color(0xFFFF2222))
                    PartnerCard("TED-Dd",  Color(0xFFE53935))
                    PartnerCard("Oxford",  Color(0xFF1B5E20))
                    PartnerCard("YELLOW",  Color(0xFFFFC107))
                }

                // featured kahoots
                Text(
                    "Today's Featured Kouleej",
                    style    = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
                )
                FeaturedKahootCard("01", "Math for Dummies",    "by Prof. Wesley Den Braber", "I'm Wesley and I can't read for the life of me!")
                FeaturedKahootCard("02", "World Capitals",      "by Joclyn Penicillin",       "There's no pepper in the grinder...")
                FeaturedKahootCard("03", "Science Trivia",      "by Matthew Markos Chew",     "I ran out of ideas")

                // placeholder grid
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier  = Modifier.weight(1f).height(160.dp),
                        shape     = RoundedCornerShape(10.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {}
                    Card(
                        modifier  = Modifier.weight(1f).height(160.dp),
                        shape     = RoundedCornerShape(10.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {}
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kouleej", style = MaterialTheme.typography.titleMedium)
                    Text("See All", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(9) {
                        Card(
                            modifier  = Modifier.fillMaxWidth().height(80.dp),
                            shape     = RoundedCornerShape(10.dp),
                            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {}
                    }
                }
            }

            // bottom nav bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                NavItem(Icons.Default.Home,   "Home", active = true)
                NavItem(Icons.Default.Search, "Discover", onClick = onNavigateToDiscover)
                Box(
                    modifier        = Modifier
                        .size(55.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { showJoinDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, "Join a Kouleej", tint = Color.White)
                }
                NavItem(Icons.Default.Add,  "Create", onClick = onNavigateToCreate)
                NavItem(Icons.Default.Menu, "Library", onClick = onNavigateToLibrary)
            }

            // ── Join flow: tapping the center play button opens this dialog,
            //    letting the user enter a PIN (Kouleej #) or scan a QR code ──
            if (showJoinDialog) {
                JoinKouleejDialog(
                    onDismiss   = { showJoinDialog = false },
                    onJoinByPin = { id -> showJoinDialog = false; onJoinByPin(id) },
                    onScanQr    = { showJoinDialog = false; onNavigateToQrScan() }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Reusable composables
// ─────────────────────────────────────────────────────────

@Composable
fun PartnerCard(name: String, color: Color) {
    Card(
        modifier  = Modifier
            .width(115.dp)
            .height(90.dp),
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FeaturedKahootCard(number: String, title: String, subtitle: String, details: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness    = Spring.StiffnessLow
                )
            ),
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    modifier = Modifier.size(40.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(number, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                }
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(title,    style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                }
                Icon(
                    imageVector        = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint               = MaterialTheme.colorScheme.primary
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(details, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {},
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Play Now")
                }
            }
        }
    }
}

@Composable
fun JoinKouleejDialog(
    onDismiss: () -> Unit,
    onJoinByPin: (Int) -> Unit,
    onScanQr: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join a Kouleej", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Enter a game PIN (the Kouleej #) to jump straight into a quiz, " +
                        "or scan its QR code.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { input -> pin = input.filter { it.isDigit() }.take(6) },
                    label = { Text("Game PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        "  or  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onScanQr,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan QR code")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { pin.toIntOrNull()?.let(onJoinByPin) },
                enabled = pin.isNotBlank()
            ) { Text("Join") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun QuickAction(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, active: Boolean = false, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            icon, null,
            tint = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            label,
            color    = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CategoryChip(title: String, expandedMenu: String, onExpand: (String) -> Unit) {
    Box {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .clickable { onExpand(if (expandedMenu == title) "" else title) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurface)
            Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.onSurface)
        }
        DropdownMenu(
            expanded          = expandedMenu == title,
            onDismissRequest  = { onExpand("") }
        ) {
            val items = when (title) {
                "Topics"            -> listOf("Algebra", "Biology", "History")
                "Special occasions" -> listOf("Christmas", "Halloween", "Valentine")
                else                -> listOf("Kouleej Originals", "Premium")
            }
            items.forEach {
                DropdownMenuItem(
                    text    = { Text(it, fontWeight = FontWeight.Bold) },
                    onClick = { onExpand("") }
                )
            }
        }
    }
}
