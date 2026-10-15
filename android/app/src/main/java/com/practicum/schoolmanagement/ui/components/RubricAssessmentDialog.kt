package com.practicum.schoolmanagement.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.practicum.schoolmanagement.data.model.*
import com.practicum.schoolmanagement.ui.theme.*

@Composable
fun AssignmentSubmissionsDialog(
    assignment: Assignment,
    submissions: List<AssignmentSubmission>,
    isAssessing: Boolean,
    currentAssessment: AssessAssignmentResponse?,
    onAssessSubmission: (AssignmentSubmission) -> Unit,
    onSaveGrade: (String, Int, String, List<RubricCriterion>) -> Unit,
    onClearAssessment: () -> Unit,
    onDismiss: () -> Unit
) {
    var assessingSubmission by remember { mutableStateOf<AssignmentSubmission?>(null) }
    var editedGrade by remember { mutableStateOf("") }
    var editedFeedback by remember { mutableStateOf("") }

    LaunchedEffect(currentAssessment) {
        if (currentAssessment != null) {
            editedGrade = currentAssessment.overallScore.toString()
            editedFeedback = currentAssessment.constructiveFeedback
        }
    }

    Dialog(
        onDismissRequest = {
            onClearAssessment()
            onDismiss()
        },
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
                                listOf(Color(0xFF0D9488), Color(0xFF0284C7))
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
                                    imageVector = Icons.Default.Grading,
                                    contentDescription = "Grading",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = assignment.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "AI Rubric Assessment & Submissions (${submissions.size})",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onClearAssessment()
                                onDismiss()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // If assessment is currently open for a student
                if (currentAssessment != null && assessingSubmission != null) {
                    val sub = assessingSubmission!!
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI Rubric Evaluation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                TextButton(onClick = {
                                    onClearAssessment()
                                    assessingSubmission = null
                                }) {
                                    Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Back to Submissions", fontSize = 12.sp)
                                }
                            }
                        }

                        // Score Gauge Card
                        item {
                            GlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Overall Rubric Score", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            "Evaluated across 4 academic criteria grounded in class context",
                                            fontSize = 11.sp,
                                            color = Slate500
                                        )
                                    }

                                    val scoreInt = editedGrade.toIntOrNull() ?: currentAssessment.overallScore
                                    val badgeColor = when {
                                        scoreInt >= 75 -> Success
                                        scoreInt >= 50 -> Warning
                                        else -> Danger
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = badgeColor.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeColor),
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "$scoreInt / 100",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 20.sp,
                                                color = badgeColor
                                            )
                                            Text(
                                                text = when {
                                                    scoreInt >= 90 -> "Grade A"
                                                    scoreInt >= 75 -> "Grade B"
                                                    scoreInt >= 60 -> "Grade C"
                                                    else -> "Needs Revision"
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Criterion Breakdown
                        item {
                            Text(
                                text = "Criterion Breakdown",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate700
                            )
                        }

                        items(currentAssessment.rubricBreakdown) { criterion ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Slate100,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = criterion.criterion,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Slate800,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Primary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${criterion.awardedMarks} / ${criterion.maxMarks} pts",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = criterion.comments,
                                        fontSize = 11.sp,
                                        color = Slate600
                                    )
                                }
                            }
                        }

                        // Strengths & Improvements
                        if (currentAssessment.strengths.isNotEmpty()) {
                            item {
                                Text("Key Strengths", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Success)
                                Spacer(modifier = Modifier.height(4.dp))
                                currentAssessment.strengths.forEach { s ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(s, fontSize = 11.sp, color = Slate700)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }

                        if (currentAssessment.improvements.isNotEmpty()) {
                            item {
                                Text("Areas for Improvement", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Warning)
                                Spacer(modifier = Modifier.height(4.dp))
                                currentAssessment.improvements.forEach { imp ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.ArrowForward, null, tint = Warning, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(imp, fontSize = 11.sp, color = Slate700)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }

                        // Editable Grade & Feedback Form
                        item {
                            HorizontalDivider(color = Slate300)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Finalize Grade & Constructive Feedback",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = editedGrade,
                                onValueChange = { editedGrade = it },
                                label = { Text("Final Score (0 - 100)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = editedFeedback,
                                onValueChange = { editedFeedback = it },
                                label = { Text("Teacher Feedback for Student") },
                                minLines = 3,
                                maxLines = 6,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    val finalScore = editedGrade.toIntOrNull() ?: currentAssessment.overallScore
                                    onSaveGrade(
                                        sub.id,
                                        finalScore,
                                        editedFeedback,
                                        currentAssessment.rubricBreakdown
                                    )
                                    assessingSubmission = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Success)
                            ) {
                                Icon(Icons.Default.Check, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Grade & Send Feedback to Student")
                            }
                        }
                    }
                } else {
                    // Submissions List
                    if (submissions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Inbox, null, tint = Slate500, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No submissions yet", fontWeight = FontWeight.Bold, color = Slate600)
                                Text("Student homework submissions will appear here", fontSize = 12.sp, color = Slate500)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Student Submissions",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${submissions.size} received",
                                        color = Slate500,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            items(submissions) { sub ->
                                SubmissionCard(
                                    submission = sub,
                                    isAssessing = isAssessing && assessingSubmission?.id == sub.id,
                                    onAssess = {
                                        assessingSubmission = sub
                                        onAssessSubmission(sub)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionCard(
    submission: AssignmentSubmission,
    isAssessing: Boolean,
    onAssess: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Slate50,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Primary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        val studentObj = submission.student as? Map<*, *>
                        val studentName = studentObj?.get("name")?.toString() ?: "Student"
                        val rollNum = studentObj?.get("rollNum")?.toString() ?: ""

                        Text(
                            text = studentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (rollNum.isNotBlank()) {
                            Text(
                                text = "Roll #$rollNum",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                }

                if (submission.grade != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Success.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Graded: ${submission.grade}/100",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Success,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Warning.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Pending Assessment",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = Warning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, Slate200, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = submission.content,
                    fontSize = 12.sp,
                    color = Slate700,
                    modifier = Modifier.padding(10.dp),
                    maxLines = 4
                )
            }

            if (!submission.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Summary: ${submission.summary}",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Slate500
                )
            }

            // Plagiarism alert if present
            if (!submission.plagiarizedFrom.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Danger.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "High similarity detected with peer submission",
                            fontSize = 10.sp,
                            color = Danger,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Existing Feedback if already graded
            if (!submission.feedback.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Feedback: ${submission.feedback}",
                    fontSize = 11.sp,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onAssess,
                enabled = !isAssessing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (submission.grade != null) Primary else Color(0xFF0D9488)
                )
            ) {
                if (isAssessing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Evaluating with AI Rubric...", fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (submission.grade != null) "Re-Evaluate / Edit Grade (Rubric)" else "Assess with AI Rubric",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
