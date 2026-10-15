package com.practicum.schoolmanagement.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.practicum.schoolmanagement.data.model.HelpdeskMessage
import com.practicum.schoolmanagement.data.model.RagPolicyCitation
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import com.practicum.schoolmanagement.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CampusHelpdeskDialog(
    schoolId: String,
    role: String = "Student",
    userName: String = "Member",
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputQuery by remember { mutableStateOf("") }
    var isAsking by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            HelpdeskMessage(
                text = "Hello $userName! 👋\n\nI am your official **Campus Policy & Notice Helpdesk Bot** powered by RAG (Retrieval-Augmented Generation).\n\nAsk me about upcoming exam circulars, holiday schedules, the 75% attendance rule, grievance redressal, or laboratory safety protocols!",
                isUser = false
            )
        )
    }

    val quickQuestions = listOf(
        "📅 Exam Schedule & Holidays",
        "📝 75% Attendance Rule",
        "⚖️ Grievance Redressal",
        "🔬 Lab Safety Protocol",
        "🏫 Grading & Quiz Conduct"
    )

    fun sendQuery(queryText: String) {
        val q = queryText.trim()
        if (q.isEmpty() || isAsking) return

        messages.add(HelpdeskMessage(text = q, isUser = true))
        inputQuery = ""
        isAsking = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val result = SchoolRepository.askCampusPolicyHelpdesk(
                schoolId = schoolId,
                query = q,
                role = role,
                userName = userName
            )
            isAsking = false

            result.onSuccess { resp ->
                messages.add(
                    HelpdeskMessage(
                        text = resp.answer,
                        isUser = false,
                        citations = resp.citations
                    )
                )
            }.onFailure { err ->
                messages.add(
                    HelpdeskMessage(
                        text = "I encountered an issue retrieving official notices: ${err.message}. Please consult the School Administrative Office.",
                        isUser = false
                    )
                )
            }

            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4F46E5), Color(0xFF06B6D4))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Helpdesk",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Campus Policy & Notice Bot",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Official RAG Search • Notices & Circulars",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Quick Suggestion Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate100)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickQuestions.forEach { prompt ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            tonalElevation = 1.dp,
                            modifier = Modifier
                                .border(1.dp, Slate300, RoundedCornerShape(20.dp))
                                .clickable { sendQuery(prompt) }
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate700,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Message Thread
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(messages) { msg ->
                        HelpdeskMessageBubble(msg)
                    }

                    if (isAsking) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Scanning school notices & institutional policies...",
                                    fontSize = 12.sp,
                                    color = Slate500,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Slate200)

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask about circulars, rules, exams...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Slate300
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { sendQuery(inputQuery) },
                        enabled = inputQuery.isNotBlank() && !isAsking,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputQuery.isNotBlank() && !isAsking)
                                    Brush.linearGradient(listOf(Color(0xFF4F46E5), Color(0xFF06B6D4)))
                                else
                                    Brush.linearGradient(listOf(Slate300, Slate300))
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HelpdeskMessageBubble(msg: HelpdeskMessage) {
    if (msg.isUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp).copy(bottomEnd = androidx.compose.foundation.shape.CornerSize(2.dp)),
                color = Primary,
                tonalElevation = 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(modifier = Modifier.widthIn(max = 320.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp).copy(bottomStart = androidx.compose.foundation.shape.CornerSize(2.dp)),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = msg.text,
                            color = Slate800,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        // Citations Box
                        if (msg.citations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Slate300, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "📌 Official Verified Citations (${msg.citations.size}):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            msg.citations.forEach { citation ->
                                CitationCard(citation)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitationCard(citation: RagPolicyCitation) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = citation.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Slate800,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Secondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = citation.date,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Secondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = citation.snippet,
                fontSize = 10.sp,
                color = Slate600,
                lineHeight = 14.sp
            )
        }
    }
}
