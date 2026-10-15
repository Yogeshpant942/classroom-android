package com.practicum.schoolmanagement.ui.student

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.schoolmanagement.data.model.*
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import com.practicum.schoolmanagement.ui.components.*
import com.practicum.schoolmanagement.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    viewModel: StudentViewModel,
    onLogout: () -> Unit
) {
    val data by viewModel.data.collectAsState()
    val student = viewModel.currentStudent

    var selectedTab by remember { mutableIntStateOf(0) }
    var showServerDialog by remember { mutableStateOf(false) }

    // Dialogs
    var takingQuiz by remember { mutableStateOf<Quiz?>(null) }
    var submittingAssignment by remember { mutableStateOf<Assignment?>(null) }
    var showComplainDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLiveClassDialog by remember { mutableStateOf(false) }
    var showHelpdeskDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }

    LaunchedEffect(data.message, data.errorMessage) {
        data.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        data.errorMessage?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearMessages()
        }
    }

    if (showServerDialog) {
        ServerConfigDialog(onDismiss = { showServerDialog = false })
    }

    if (showProfileDialog) {
        UserProfileDialog(
            name = student?.name ?: "Student",
            role = "Student",
            schoolName = student?.schoolName,
            className = student?.className,
            rollNum = student?.rollNum,
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showLiveClassDialog) {
        LiveClassDialog(
            userRole = "Student",
            userName = student?.name ?: "Student",
            onDismiss = { showLiveClassDialog = false }
        )
    }

    if (showHelpdeskDialog) {
        CampusHelpdeskDialog(
            schoolId = student?.schoolId ?: "",
            role = "Student",
            userName = student?.name ?: "Student",
            onDismiss = { showHelpdeskDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = student?.name.takeIf { !it.isNullOrBlank() } ?: "Student Portal",
                subtitle = "Roll: ${student?.rollNum} • ${student?.className ?: "Class"}",
                role = "Student",
                onProfileClick = { showProfileDialog = true },
                onVideoCallClick = { showLiveClassDialog = true },
                onHelpdeskClick = { showHelpdeskDialog = true },
                onSettingsClick = { showServerDialog = true },
                onLogoutClick = {
                    SchoolRepository.logout()
                    onLogout()
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.MenuBook, "Subjects") },
                    label = { Text("Subjects") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.FactCheck, "Attendance") },
                    label = { Text("Attendance") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Quiz, "Quizzes") },
                    label = { Text("Quizzes") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Assignment, "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.AutoAwesome, "AI Tutor") },
                    label = { Text("AI Tutor") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Slate50)
        ) {
            if (data.isLoading) {
                LoadingIndicator("Fetching your academic records...")
            } else {
                when (selectedTab) {
                    0 -> StudentHomeTab(
                        student = student,
                        data = data,
                        onNavigateTab = { selectedTab = it },
                        onFileComplain = { showComplainDialog = true },
                        onOpenHelpdesk = { showHelpdeskDialog = true }
                    )
                    1 -> StudentSubjectsTab(
                        subjects = data.subjects,
                        examResults = data.examResults
                    )
                    2 -> StudentAttendanceTab(
                        data = data,
                        attendancePct = data.attendancePercentage,
                        present = data.totalPresent,
                        absent = data.totalAbsent
                    )
                    3 -> StudentQuizzesTab(
                        quizzes = data.quizzes,
                        onTakeQuiz = { takingQuiz = it }
                    )
                    4 -> StudentTasksTab(
                        assignments = data.assignments,
                        onSubmitAssignment = { submittingAssignment = it },
                        onFileComplain = { showComplainDialog = true }
                    )
                    5 -> StudentAITutorTab(
                        data = data,
                        onSendMessage = { viewModel.sendTutorQuestion(it) }
                    )
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Take Quiz Interactive Dialog
    if (takingQuiz != null) {
        val quiz = takingQuiz!!
        val answers = remember { mutableStateMapOf<String, String>() }

        AlertDialog(
            onDismissRequest = { takingQuiz = null },
            title = {
                Column {
                    Text(quiz.title, fontWeight = FontWeight.Bold)
                    Text(
                        "${quiz.questions.size} Questions • Time: ${quiz.durationMinutes} mins",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(quiz.questions) { q ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, Slate300, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(q.questionText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            q.options.forEach { opt ->
                                val optId = opt.id ?: opt.optionText
                                val isSelected = answers[q.id ?: q.questionText] == optId
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { answers[q.id ?: q.questionText] = optId }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { answers[q.id ?: q.questionText] = optId }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(opt.optionText, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitQuiz(quiz.id, answers)
                        takingQuiz = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Submit Quiz")
                }
            },
            dismissButton = {
                TextButton(onClick = { takingQuiz = null }) { Text("Cancel") }
            }
        )
    }

    // 2. Submit Assignment Dialog
    if (submittingAssignment != null) {
        val assignment = submittingAssignment!!
        var submissionText by remember { mutableStateOf("") }
        var isCheckingRubric by remember { mutableStateOf(false) }
        var rubricCheckResult by remember { mutableStateOf<AssessAssignmentResponse?>(null) }
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = { submittingAssignment = null },
            title = { Text("Submit: ${assignment.title}") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "Instructions: ${assignment.description}",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = submissionText,
                            onValueChange = { submissionText = it },
                            label = { Text("Your Submission Content / Solution") },
                            minLines = 4,
                            maxLines = 8,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                if (submissionText.isNotBlank()) {
                                    isCheckingRubric = true
                                    scope.launch {
                                        val res = SchoolRepository.assessAssignment(
                                            AssessAssignmentRequest(
                                                assignmentTitle = assignment.title,
                                                assignmentDescription = assignment.description,
                                                studentContent = submissionText,
                                                classId = student?.classId
                                            )
                                        )
                                        isCheckingRubric = false
                                        res.onSuccess { rubricCheckResult = it }
                                    }
                                }
                            },
                            enabled = submissionText.isNotBlank() && !isCheckingRubric,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isCheckingRubric) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Checking with AI Rubric...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pre-Check with AI Rubric (Self-Check)", fontSize = 12.sp)
                            }
                        }
                    }

                    if (rubricCheckResult != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Slate100,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Estimated Score: ${rubricCheckResult!!.overallScore}/100",
                                            fontWeight = FontWeight.Bold,
                                            color = Primary,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        rubricCheckResult!!.constructiveFeedback,
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitAssignment(assignment.id, submissionText)
                        submittingAssignment = null
                    },
                    enabled = submissionText.isNotBlank()
                ) {
                    Text("Turn In")
                }
            },
            dismissButton = {
                TextButton(onClick = { submittingAssignment = null }) { Text("Cancel") }
            }
        )
    }

    // 3. File Complain Dialog
    if (showComplainDialog) {
        var compText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showComplainDialog = false },
            title = { Text("Submit Complaint to Administration") },
            text = {
                OutlinedTextField(
                    value = compText,
                    onValueChange = { compText = it },
                    label = { Text("Describe your feedback or grievance") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitComplaint(compText)
                        showComplainDialog = false
                    },
                    enabled = compText.isNotBlank()
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showComplainDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// --- STUDENT TABS ---

@Composable
fun StudentHomeTab(
    student: SessionUser?,
    data: StudentDashboardData,
    onNavigateTab: (Int) -> Unit,
    onFileComplain: () -> Unit,
    onOpenHelpdesk: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hello, ${student?.name ?: "Student"}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Roll #${student?.rollNum} • Enrolled in ${student?.className ?: "Class"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }

        // Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Attendance",
                    value = "${String.format("%.1f", data.attendancePercentage)}%",
                    icon = Icons.Default.FactCheck,
                    gradientColors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(2) }
                )
                StatCard(
                    title = "Subjects",
                    value = data.subjects.size.toString(),
                    icon = Icons.Default.MenuBook,
                    gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Quizzes",
                    value = data.quizzes.size.toString(),
                    icon = Icons.Default.Quiz,
                    gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(3) }
                )
                StatCard(
                    title = "Assignments",
                    value = data.assignments.size.toString(),
                    icon = Icons.Default.Assignment,
                    gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(4) }
                )
            }
        }

        // AI Campus Tutor (RAG) Promo Banner
        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Campus Study Tutor (RAG)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "${data.studyMaterials.size} course notes indexed • Ask questions & get cited answers",
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { onNavigateTab(5) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ask Tutor", fontSize = 12.sp)
                    }
                }
            }
        }

        // Campus Policy & Notice Helpdesk Bot (RAG Feature 3)
        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF4F46E5), Color(0xFF06B6D4)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SupportAgent, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Policy & Notice Helpdesk Bot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Instant AI answers on exam circulars, 75% attendance rule & campus policies",
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onOpenHelpdesk,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ask Bot", fontSize = 12.sp)
                    }
                }
            }
        }

        // Notices Board
        item {
            Text(
                text = "Campus Notices",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (data.notices.isEmpty()) {
            item {
                EmptyState(
                    title = "No notices right now",
                    message = "Important school announcements will appear here"
                )
            }
        } else {
            items(data.notices) { notice ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(notice.title, fontWeight = FontWeight.Bold)
                        BadgeChip(text = notice.date.take(10), color = Secondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(notice.details, color = Slate700)
                }
            }
        }
    }
}

@Composable
fun StudentSubjectsTab(
    subjects: List<Subject>,
    examResults: List<ExamResult>? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Enrolled Subjects (${subjects.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (subjects.isEmpty()) {
            item {
                EmptyState(
                    title = "No subjects assigned",
                    message = "Admin has not yet mapped subjects to your class"
                )
            }
        } else {
            items(subjects) { sub ->
                val result = examResults?.find {
                    when (val sn = it.subName) {
                        is String -> sn == sub.id || sn == sub.subName
                        is Map<*, *> -> sn["_id"] == sub.id || sn["subName"] == sub.subName
                        else -> false
                    }
                }
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MenuBook, null, tint = Primary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(sub.subName, fontWeight = FontWeight.Bold)
                                Text("Code: ${sub.subCode}", color = Slate500, fontSize = 12.sp)
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (result != null) {
                                BadgeChip(text = "Score: ${result.marksObtained}", color = Primary)
                            }
                            BadgeChip(text = "${sub.sessions} Sessions", color = Secondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceTab(
    data: StudentDashboardData,
    attendancePct: Double,
    present: Int,
    absent: Int
) {
    var targetPctInput by remember { mutableStateOf("75") }

    val targetPct = targetPctInput.toDoubleOrNull() ?: 75.0
    val total = present + absent

    // Calculation: How many more classes needed to reach target percentage?
    // Formula: (present + x) / (total + x) >= target/100
    // => present + x >= (target/100)*total + (target/100)*x
    // => x * (1 - target/100) >= (target/100)*total - present
    val classesNeeded = if (targetPct < 100 && total > 0) {
        val targetRatio = targetPct / 100.0
        val numerator = targetRatio * total - present
        val denominator = 1.0 - targetRatio
        if (numerator > 0 && denominator > 0) Math.ceil(numerator / denominator).toInt() else 0
    } else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Attendance Overview",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Circular Gauge Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${String.format("%.1f", attendancePct)}%",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = if (attendancePct >= 75) Success else Danger,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text("Overall Attendance", color = Slate300, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$present", fontWeight = FontWeight.Bold, color = Success, fontSize = 18.sp)
                            Text("Present", color = Slate300, fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$absent", fontWeight = FontWeight.Bold, color = Danger, fontSize = 18.sp)
                            Text("Absent", color = Slate300, fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$total", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Text("Total", color = Slate300, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Attendance Goal Calculator
        item {
            GlassCard {
                Text(
                    text = "Attendance Calculator",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Calculate how many consecutive classes you must attend to achieve your target percentage.",
                    color = Slate500,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = targetPctInput,
                        onValueChange = { targetPctInput = it },
                        label = { Text("Target %") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (classesNeeded == 0) Success.copy(alpha = 0.15f) else Warning.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (classesNeeded == 0) "Goal Achieved!" else "Need $classesNeeded classes",
                                fontWeight = FontWeight.Bold,
                                color = if (classesNeeded == 0) Success else Warning,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentQuizzesTab(
    quizzes: List<Quiz>,
    onTakeQuiz: (Quiz) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Available Quizzes (${quizzes.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (quizzes.isEmpty()) {
            item {
                EmptyState(
                    title = "No active quizzes",
                    message = "When your teachers publish quizzes, you can take them here"
                )
            }
        } else {
            items(quizzes) { quiz ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(quiz.title, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "${quiz.questions.size} Questions • ${quiz.durationMinutes} mins",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                        Button(
                            onClick = { onTakeQuiz(quiz) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Take Quiz")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentTasksTab(
    assignments: List<Assignment>,
    onSubmitAssignment: (Assignment) -> Unit,
    onFileComplain: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assignments (${assignments.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                OutlinedButton(onClick = onFileComplain) {
                    Icon(Icons.Default.Feedback, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("File Complaint")
                }
            }
        }

        if (assignments.isEmpty()) {
            item {
                EmptyState(
                    title = "No assignments pending",
                    message = "All caught up! No homework or lab tasks assigned."
                )
            }
        } else {
            items(assignments) { item ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.title, fontWeight = FontWeight.Bold)
                        BadgeChip(text = "Due: ${item.deadline.take(10)}", color = Warning)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.description, color = Slate700)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onSubmitAssignment(item) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Solution")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAITutorTab(
    data: StudentDashboardData,
    onSendMessage: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(data.chatMessages.size, data.isTutorTyping) {
        if (data.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(data.chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // RAG Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Campus AI Study Tutor",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "RAG Verified: Grounded in your class notes",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                    BadgeChip(
                        text = "${data.studyMaterials.size} Notes Indexed",
                        color = if (data.studyMaterials.isNotEmpty()) Success else Warning
                    )
                }

                // Quick Prompt Chips
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = { onSendMessage("Summarize the key concepts from our course notes") },
                        label = { Text("Summarize Notes", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    SuggestionChip(
                        onClick = { onSendMessage("What are the most important exam topics?") },
                        label = { Text("Exam Topics", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Chat Messages Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(data.chatMessages, key = { it.id }) { msg ->
                ChatMessageItem(msg = msg)
            }

            if (data.isTutorTyping) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tutor is searching your class notes & formulating answer...",
                            fontSize = 12.sp,
                            color = Slate500,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }

        // Bottom Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask anything about your syllabus or notes...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            val q = textInput
                            textInput = ""
                            onSendMessage(q)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Primary)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(msg: ChatMessage) {
    var expandedCitations by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            if (!msg.isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f, fill = false),
                horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                        bottomEnd = if (msg.isUser) 4.dp else 16.dp
                    ),
                    color = if (msg.isUser) Primary else Color.White,
                    tonalElevation = if (msg.isUser) 0.dp else 2.dp,
                    shadowElevation = if (msg.isUser) 0.dp else 1.dp
                ) {
                    Text(
                        text = msg.text,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = if (msg.isUser) Color.White else Slate900,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                // If message has RAG citations
                if (msg.citations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Primary.copy(alpha = 0.08f),
                        modifier = Modifier
                            .clickable { expandedCitations = !expandedCitations }
                            .padding(top = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MenuBook, null, tint = Primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "📚 ${msg.citations.size} Verified Source Citation(s)",
                                color = Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (expandedCitations) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, start = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            msg.citations.forEachIndexed { idx, cit ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Slate200),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${idx + 1}. ${cit.title}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Primary
                                            )
                                            if (cit.score > 0.0) {
                                                Text(
                                                    text = "${(cit.score * 100).toInt()}% match",
                                                    fontSize = 10.sp,
                                                    color = Success,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "\"${cit.snippet}\"",
                                            fontSize = 11.sp,
                                            color = Slate600,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

