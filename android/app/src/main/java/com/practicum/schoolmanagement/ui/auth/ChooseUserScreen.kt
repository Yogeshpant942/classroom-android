package com.practicum.schoolmanagement.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.schoolmanagement.ui.components.ServerConfigDialog
import com.practicum.schoolmanagement.ui.theme.*

@Composable
fun ChooseUserScreen(
    onNavigateToLogin: (role: String) -> Unit,
    onGuestLogin: (role: String) -> Unit
) {
    var showServerDialog by remember { mutableStateOf(false) }

    if (showServerDialog) {
        ServerConfigDialog(onDismiss = { showServerDialog = false })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryDark,
                        Slate900
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Server Config
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { showServerDialog = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Server Settings",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(PrimaryLight, Accent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "School Logo",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "School Management System",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your role to sign in or explore the campus portal",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate300
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Role Card 1: Admin
            RoleCard(
                title = "Admin",
                description = "Manage classes, teachers, students, subjects, notices and lab groups",
                icon = Icons.Default.AdminPanelSettings,
                gradient = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                onClick = { onNavigateToLogin("Admin") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Card 2: Teacher
            RoleCard(
                title = "Teacher",
                description = "Manage class attendance, publish quizzes with AI, and track assignments",
                icon = Icons.Default.PersonSearch,
                gradient = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
                onClick = { onNavigateToLogin("Teacher") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Card 3: Student
            RoleCard(
                title = "Student",
                description = "Access subjects, view attendance history, take interactive quizzes and submit homework",
                icon = Icons.Default.School,
                gradient = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
                onClick = { onNavigateToLogin("Student") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Guest Mode Option
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need a quick demo?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore the full app instantly in Guest Mode without logging in",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onGuestLogin("Admin") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Admin Demo", fontSize = 12.sp)
                        }
                        FilledTonalButton(
                            onClick = { onGuestLogin("Teacher") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Teacher Demo", fontSize = 12.sp)
                        }
                        FilledTonalButton(
                            onClick = { onGuestLogin("Student") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Student Demo", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300),
                    lineHeight = 18.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Slate300,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
