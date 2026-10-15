package com.practicum.schoolmanagement.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.schoolmanagement.data.api.RetrofitClient
import com.practicum.schoolmanagement.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    role: String? = null,
    onProfileClick: (() -> Unit)? = null,
    onVideoCallClick: (() -> Unit)? = null,
    onHelpdeskClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (role != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = role.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            if (onHelpdeskClick != null) {
                IconButton(onClick = onHelpdeskClick) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Campus Policy & Notice Helpdesk",
                        tint = Color.White
                    )
                }
            }
            if (onVideoCallClick != null) {
                IconButton(onClick = onVideoCallClick) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Live Class / Video Call",
                        tint = Color.White
                    )
                }
            }
            if (onProfileClick != null) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "My Profile",
                        tint = Color.White
                    )
                }
            }
            if (onSettingsClick != null) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Server Settings",
                        tint = Color.White
                    )
                }
            }
            if (onLogoutClick != null) {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primary
        )
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradientColors))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate300.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun BadgeChip(
    text: String,
    color: Color = Primary,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun LoadingIndicator(message: String = "Loading...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = Slate500)
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Inbox,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Slate100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate500)
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun ServerConfigDialog(
    onDismiss: () -> Unit
) {
    var urlText by remember { mutableStateOf(RetrofitClient.getBaseUrl()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backend Server Configuration") },
        text = {
            Column {
                Text(
                    "Set the Node.js backend URL. Default for Android Emulator is http://10.0.2.2:5000/",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("Server URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { urlText = "http://10.0.2.2:5000/" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Emulator", fontSize = 12.sp)
                    }
                    FilledTonalButton(
                        onClick = { urlText = "http://localhost:5000/" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Localhost", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    RetrofitClient.setBaseUrl(urlText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UserProfileDialog(
    name: String,
    role: String,
    email: String? = null,
    schoolName: String? = null,
    className: String? = null,
    rollNum: Int? = null,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                BadgeChip(text = role.uppercase(), color = Primary)
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!email.isNullOrBlank()) {
                        ProfileInfoRow(label = "Email", value = email)
                    }
                    if (!schoolName.isNullOrBlank()) {
                        ProfileInfoRow(label = "College / School", value = schoolName)
                    }
                    if (!className.isNullOrBlank()) {
                        ProfileInfoRow(label = "Class", value = className)
                    }
                    if (rollNum != null && rollNum > 0) {
                        ProfileInfoRow(label = "Roll Number", value = rollNum.toString())
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Slate500, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun LiveClassDialog(
    userRole: String,
    userName: String,
    onDismiss: () -> Unit
) {
    var roomInput by remember { mutableStateOf("Class-Room-101") }
    var isInMeeting by remember { mutableStateOf(false) }
    var isMicOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Videocam, null, tint = Primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isInMeeting) "Live Video Class ($roomInput)" else "Join Live Video Class")
            }
        },
        text = {
            if (!isInMeeting) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter a Room ID or Class ID to start/join a live video lecture session with students & teachers.",
                        fontSize = 13.sp,
                        color = Slate600
                    )
                    OutlinedTextField(
                        value = roomInput,
                        onValueChange = { roomInput = it },
                        label = { Text("Room ID / Class ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Joining as:", fontSize = 12.sp, color = Slate500)
                        BadgeChip(text = "$userName ($userRole)", color = Secondary)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E2E)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCameraOn) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.School,
                                    null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$userName's Camera Feed",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Connected to room: $roomInput",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Text(
                                text = "Camera is Turned Off",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilledTonalIconButton(onClick = { isMicOn = !isMicOn }) {
                            Icon(
                                if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Mic",
                                tint = if (isMicOn) Primary else Color.Red
                            )
                        }
                        FilledTonalIconButton(onClick = { isCameraOn = !isCameraOn }) {
                            Icon(
                                if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Camera",
                                tint = if (isCameraOn) Primary else Color.Red
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isInMeeting) {
                Button(
                    onClick = {
                        if (roomInput.isNotBlank()) isInMeeting = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Join Call")
                }
            } else {
                Button(
                    onClick = { isInMeeting = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Leave Call")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

