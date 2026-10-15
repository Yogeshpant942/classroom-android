package com.practicum.schoolmanagement.ui.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.schoolmanagement.data.model.*
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import com.practicum.schoolmanagement.ui.components.*
import com.practicum.schoolmanagement.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    viewModel: TeacherViewModel,
    onLogout: () -> Unit
) {
    val data by viewModel.data.collectAsState()
    val teacher = viewModel.currentTeacher

    var selectedTab by remember { mutableIntStateOf(0) }
    var showServerDialog by remember { mutableStateOf(false) }

    // Dialogs
    var showAIQuizDialog by remember { mutableStateOf(false) }
    var showManualQuizDialog by remember { mutableStateOf(false) }
    var showRAGQuizDialog by remember { mutableStateOf(false) }
    var showAssignmentDialog by remember { mutableStateOf(false) }
    var showComplainDialog by remember { mutableStateOf(false) }
    var showUpdateMarksDialog by remember { mutableStateOf<Student?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLiveClassDialog by remember { mutableStateOf(false) }
    var showUploadMaterialDialog by remember { mutableStateOf(false) }
    var showHelpdeskDialog by remember { mutableStateOf(false) }
    var viewingAssignmentSubmissions by remember { mutableStateOf<Assignment?>(null) }

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
            name = teacher?.name ?: "Teacher",
            role = "Teacher",
            email = teacher?.email,
            schoolName = teacher?.schoolName,
            className = teacher?.className,
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showLiveClassDialog) {
        LiveClassDialog(
            userRole = "Teacher",
            userName = teacher?.name ?: "Teacher",
            onDismiss = { showLiveClassDialog = false }
        )
    }

    if (showHelpdeskDialog) {
        CampusHelpdeskDialog(
            schoolId = teacher?.schoolId ?: "",
            role = "Teacher",
            userName = teacher?.name ?: "Teacher",
            onDismiss = { showHelpdeskDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = teacher?.name.takeIf { !it.isNullOrBlank() } ?: "Teacher Portal",
                subtitle = "${teacher?.className ?: "Class"} • ${teacher?.subjectName ?: "Subject"}",
                role = "Teacher",
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
                    icon = { Icon(Icons.Default.Groups, "Class") },
                    label = { Text("Class") }
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
                    icon = { Icon(Icons.Default.Assignment, "Assignments") },
                    label = { Text("Tasks") }
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
                LoadingIndicator("Loading class details...")
            } else {
                when (selectedTab) {
                    0 -> TeacherHomeTab(
                        teacher = teacher,
                        data = data,
                        onNavigateTab = { selectedTab = it },
                        onFileComplain = { showComplainDialog = true },
                        onUploadMaterial = { showUploadMaterialDialog = true },
                        onOpenHelpdesk = { showHelpdeskDialog = true }
                    )
                    1 -> TeacherClassRosterTab(
                        students = data.students,
                        onMarkAttendance = { sId, status -> viewModel.markStudentAttendance(sId, status) },
                        onUpdateMarks = { student -> showUpdateMarksDialog = student }
                    )
                    2 -> TeacherBulkAttendanceTab(
                        students = data.students,
                        onSaveBulk = { status, sIds -> viewModel.markBulkAttendance(status, sIds) }
                    )
                    3 -> TeacherQuizzesTab(
                        quizzes = data.quizzes,
                        aiQuestions = data.aiGeneratedQuestions,
                        isGeneratingAI = data.isGeneratingAI,
                        ragQuestions = data.ragGeneratedQuestions,
                        ragMaterialTitle = data.ragMaterialTitle,
                        isGeneratingRAG = data.isGeneratingRAGQuiz,
                        onOpenRAGDialog = { showRAGQuizDialog = true },
                        onOpenAIDialog = { showAIQuizDialog = true },
                        onOpenManualDialog = { showManualQuizDialog = true },
                        onRemoveRagQuestion = { viewModel.removeRagQuestion(it) },
                        onPublishRagQuiz = { title, duration ->
                            viewModel.publishRagQuiz(title, duration)
                        },
                        onClearRagQuiz = { viewModel.clearRagQuestions() },
                        onSaveAIQuiz = { title, duration, questions ->
                            viewModel.createQuiz(title, duration, questions)
                        }
                    )
                    4 -> TeacherAssignmentsTab(
                        assignments = data.assignments,
                        onAddAssignment = { showAssignmentDialog = true },
                        onViewSubmissions = { assignment ->
                            viewingAssignmentSubmissions = assignment
                            viewModel.loadSubmissions(assignment.id)
                        }
                    )
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. AI Quiz Generator Dialog
    if (showAIQuizDialog) {
        var topicInput by remember { mutableStateOf("") }
        var countInput by remember { mutableStateOf("5") }
        var notesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAIQuizDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Quiz Generator (Gemini)")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Generates multiple choice questions with correct answer options automatically using Gemini API.",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("Topic (e.g. Data Structures)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = countInput,
                        onValueChange = { countInput = it },
                        label = { Text("Number of Questions") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Reference Notes / Text (Optional)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = countInput.toIntOrNull() ?: 5
                        viewModel.generateQuizWithAI(topicInput, count, notesInput)
                        showAIQuizDialog = false
                    },
                    enabled = topicInput.isNotBlank() && !data.isGeneratingAI
                ) {
                    Text("Generate with AI")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAIQuizDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 2. Manual Quiz Dialog
    if (showManualQuizDialog) {
        var qTitle by remember { mutableStateOf("") }
        var qDuration by remember { mutableStateOf("15") }
        var qText by remember { mutableStateOf("") }
        var opt1 by remember { mutableStateOf("") }
        var opt2 by remember { mutableStateOf("") }
        var opt3 by remember { mutableStateOf("") }
        var opt4 by remember { mutableStateOf("") }
        var correctIndex by remember { mutableIntStateOf(0) }

        AlertDialog(
            onDismissRequest = { showManualQuizDialog = false },
            title = { Text("Create Quiz") },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = qTitle,
                        onValueChange = { qTitle = it },
                        label = { Text("Quiz Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = qDuration,
                        onValueChange = { qDuration = it },
                        label = { Text("Duration (minutes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider()
                    Text("Add Question 1:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = qText,
                        onValueChange = { qText = it },
                        label = { Text("Question Text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(value = opt1, onValueChange = { opt1 = it }, label = { Text("Option 1") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = opt2, onValueChange = { opt2 = it }, label = { Text("Option 2") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = opt3, onValueChange = { opt3 = it }, label = { Text("Option 3") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = opt4, onValueChange = { opt4 = it }, label = { Text("Option 4") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Correct Option: ", fontSize = 12.sp)
                        (0..3).forEach { idx ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = correctIndex == idx, onClick = { correctIndex = idx })
                                Text("${idx + 1}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = qDuration.toIntOrNull() ?: 15
                        val options = listOf(
                            QuizOption(optionText = opt1, isCorrect = correctIndex == 0),
                            QuizOption(optionText = opt2, isCorrect = correctIndex == 1),
                            QuizOption(optionText = opt3, isCorrect = correctIndex == 2),
                            QuizOption(optionText = opt4, isCorrect = correctIndex == 3)
                        )
                        val question = QuizQuestion(questionText = qText, options = options)
                        viewModel.createQuiz(qTitle, duration, listOf(question))
                        showManualQuizDialog = false
                    },
                    enabled = qTitle.isNotBlank() && qText.isNotBlank() && opt1.isNotBlank() && opt2.isNotBlank()
                ) {
                    Text("Create Quiz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualQuizDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 3. Create Assignment Dialog
    if (showAssignmentDialog) {
        var aTitle by remember { mutableStateOf("") }
        var aDesc by remember { mutableStateOf("") }
        var aDeadline by remember { mutableStateOf(java.time.LocalDate.now().plusDays(7).toString()) }

        AlertDialog(
            onDismissRequest = { showAssignmentDialog = false },
            title = { Text("New Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = aTitle,
                        onValueChange = { aTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = aDesc,
                        onValueChange = { aDesc = it },
                        label = { Text("Description / Instructions") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = aDeadline,
                        onValueChange = { aDeadline = it },
                        label = { Text("Deadline (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createAssignment(aTitle, aDesc, aDeadline)
                        showAssignmentDialog = false
                    },
                    enabled = aTitle.isNotBlank() && aDeadline.isNotBlank()
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignmentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 4. Update Marks Dialog
    if (showUpdateMarksDialog != null) {
        val student = showUpdateMarksDialog!!
        var marksInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUpdateMarksDialog = null },
            title = { Text("Update Marks for ${student.name}") },
            text = {
                Column {
                    Text("Roll Number: ${student.rollNum}", color = Slate500, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = marksInput,
                        onValueChange = { marksInput = it },
                        label = { Text("Marks Obtained") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = marksInput.toDoubleOrNull() ?: 0.0
                        viewModel.updateMarks(student.id, marks)
                        showUpdateMarksDialog = null
                    }
                ) {
                    Text("Save Marks")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateMarksDialog = null }) { Text("Cancel") }
            }
        )
    }

    // 5. Submit Complaint Dialog
    if (showComplainDialog) {
        var compText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showComplainDialog = false },
            title = { Text("Submit Complaint to Admin") },
            text = {
                OutlinedTextField(
                    value = compText,
                    onValueChange = { compText = it },
                    label = { Text("Explain your issue or request") },
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

    // 6. Upload Study Material (RAG) Dialog
    if (showUploadMaterialDialog) {
        var matTitle by remember { mutableStateOf("") }
        var matContent by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showUploadMaterialDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Index Course Notes (RAG)")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Upload course syllabus, textbook chapters, or lecture notes. Our AI will automatically chunk & embed this content into the vector store so students can get cited answers from the AI Tutor.",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    OutlinedTextField(
                        value = matTitle,
                        onValueChange = { matTitle = it },
                        label = { Text("Chapter / Topic Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = matContent,
                        onValueChange = { matContent = it },
                        label = { Text("Study Material Text / Notes") },
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (matTitle.isNotBlank() && matContent.isNotBlank()) {
                            viewModel.uploadStudyMaterial(matTitle, matContent)
                            showUploadMaterialDialog = false
                        }
                    },
                    enabled = matTitle.isNotBlank() && matContent.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Index into RAG")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadMaterialDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 7. RAG Smart Quiz & Exam Generator Dialog
    if (showRAGQuizDialog) {
        RAGQuizGeneratorDialog(
            materials = data.studyMaterials,
            onDismiss = { showRAGQuizDialog = false },
            onGenerate = { matId, topic, count, diff ->
                viewModel.generateQuizFromRAG(matId, topic, count, diff)
                showRAGQuizDialog = false
            }
        )
    }

    // 8. Automated Assignment Assessment & Rubrics Dialog
    if (viewingAssignmentSubmissions != null) {
        val currentAssignment = viewingAssignmentSubmissions!!
        AssignmentSubmissionsDialog(
            assignment = currentAssignment,
            submissions = data.assignmentSubmissions,
            isAssessing = data.isAssessingAssignment,
            currentAssessment = data.currentAssessment,
            onAssessSubmission = { sub ->
                viewModel.assessSubmission(
                    submission = sub,
                    assignmentTitle = currentAssignment.title,
                    assignmentDesc = currentAssignment.description
                )
            },
            onSaveGrade = { subId, grade, feedback, rubrics ->
                viewModel.saveGrade(
                    submissionId = subId,
                    grade = grade,
                    feedback = feedback,
                    rubricBreakdown = rubrics,
                    assignmentId = currentAssignment.id
                )
            },
            onClearAssessment = { viewModel.clearCurrentAssessment() },
            onDismiss = { viewingAssignmentSubmissions = null }
        )
    }
}

// --- TEACHER TABS ---

@Composable
fun TeacherHomeTab(
    teacher: SessionUser?,
    data: TeacherDashboardData,
    onNavigateTab: (Int) -> Unit,
    onFileComplain: () -> Unit,
    onUploadMaterial: () -> Unit,
    onOpenHelpdesk: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hello, ${teacher?.name ?: "Teacher"}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Assigned to ${teacher?.className ?: "Class"} • ${teacher?.subjectName ?: "Subject"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }

        // RAG Course Knowledge Base Uploader Card
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
                        Text("Class Knowledge Base (RAG)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "${data.studyMaterials.size} chapter(s) indexed • Feed notes for student AI tutor",
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onUploadMaterial,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Notes", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Class Students",
                    value = data.students.size.toString(),
                    icon = Icons.Default.Groups,
                    gradientColors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) }
                )
                StatCard(
                    title = "Quizzes",
                    value = data.quizzes.size.toString(),
                    icon = Icons.Default.Quiz,
                    gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(3) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Assignments",
                    value = data.assignments.size.toString(),
                    icon = Icons.Default.Assignment,
                    gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(4) }
                )
                StatCard(
                    title = "File Complain",
                    value = "Help",
                    icon = Icons.Default.Feedback,
                    gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                    modifier = Modifier.weight(1f),
                    onClick = onFileComplain
                )
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
                        Text("Campus Policy & Circulars Bot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Instant AI answers on official notices, leave policies & academic rules",
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
    }
}

@Composable
fun TeacherClassRosterTab(
    students: List<Student>,
    onMarkAttendance: (String, String) -> Unit,
    onUpdateMarks: (Student) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Class Students (${students.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (students.isEmpty()) {
            item {
                EmptyState(
                    title = "No students in this class",
                    message = "Admin has not added any students to this class yet"
                )
            }
        } else {
            items(students) { student ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.rollNum.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Roll: ${student.rollNum}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onMarkAttendance(student.id, "Present") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Success)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Present", color = Success)
                        }
                        OutlinedButton(
                            onClick = { onMarkAttendance(student.id, "Absent") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp), tint = Danger)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Absent", color = Danger)
                        }
                        FilledTonalButton(
                            onClick = { onUpdateMarks(student) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Marks", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherBulkAttendanceTab(
    students: List<Student>,
    onSaveBulk: (status: String, studentIds: List<String>) -> Unit
) {
    val selectedIds = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(students) {
        students.forEach { s ->
            if (!selectedIds.containsKey(s.id)) {
                selectedIds[s.id] = true // default all selected for bulk
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Bulk Attendance Marking",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Select students and mark status for today in one click",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val ids = selectedIds.filter { it.value }.keys.toList()
                            onSaveBulk("Present", ids)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Success),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark All Present")
                    }
                    Button(
                        onClick = {
                            val ids = selectedIds.filter { it.value }.keys.toList()
                            onSaveBulk("Absent", ids)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Danger),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark All Absent")
                    }
                }
            }
        }

        items(students) { student ->
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedIds[student.id] ?: false,
                            onCheckedChange = { selectedIds[student.id] = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(student.name, fontWeight = FontWeight.Bold)
                            Text("Roll: ${student.rollNum}", fontSize = 12.sp, color = Slate500)
                        }
                    }
                    BadgeChip(
                        text = if (selectedIds[student.id] == true) "Selected" else "Skipped",
                        color = if (selectedIds[student.id] == true) Primary else Slate500
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RAGQuizGeneratorDialog(
    materials: List<StudyMaterial>,
    onDismiss: () -> Unit,
    onGenerate: (materialId: String?, topic: String?, numQuestions: Int, difficulty: String) -> Unit
) {
    var selectedMaterialId by remember { mutableStateOf<String?>(null) }
    var topicQuery by remember { mutableStateOf("") }
    var selectedCount by remember { mutableIntStateOf(5) }
    var selectedDifficulty by remember { mutableStateOf("Medium") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF6366F1), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("RAG Smart Exam Generator", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Grounded in Class Study Materials", fontSize = 11.sp, color = Slate500)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        "1. Select Course Material / Chapter Source:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Slate700
                    )
                }

                // Option: Entire Class Knowledge Base
                item {
                    val isSelected = selectedMaterialId == null
                    Surface(
                        onClick = { selectedMaterialId = null },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF6366F1).copy(alpha = 0.12f) else Slate100,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF6366F1) else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LibraryBooks,
                                null,
                                tint = if (isSelected) Color(0xFF6366F1) else Slate500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "All Indexed Course Notes",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color(0xFF4338CA) else Slate700
                                )
                                Text(
                                    "Semantic retrieval across all uploaded chapters",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (materials.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate100)
                        ) {
                            Text(
                                "💡 Tip: No chapters uploaded yet. You can still specify a topic query below, or upload lecture notes from the Home tab.",
                                fontSize = 11.sp,
                                color = Slate600,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                } else {
                    items(materials) { mat ->
                        val isSelected = selectedMaterialId == mat.id
                        Surface(
                            onClick = { selectedMaterialId = mat.id },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF6366F1).copy(alpha = 0.12f) else Slate100,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF6366F1) else Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    null,
                                    tint = if (isSelected) Color(0xFF6366F1) else Slate500,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        mat.title,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color(0xFF4338CA) else Slate700
                                    )
                                    Text(
                                        "Indexed Chapter by ${mat.uploadedBy}",
                                        fontSize = 10.sp,
                                        color = Slate500
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "2. Focus Topic / Concept (Optional):",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = topicQuery,
                        onValueChange = { topicQuery = it },
                        placeholder = { Text("e.g., Mitosis vs Meiosis, Newton's 2nd Law", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        "3. Question Count:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 5, 10).forEach { count ->
                            FilterChip(
                                selected = selectedCount == count,
                                onClick = { selectedCount = count },
                                label = { Text("$count MCQs", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    Text(
                        "4. Difficulty Level:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Easy", "Medium", "Hard").forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = diff },
                                label = { Text(diff, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGenerate(selectedMaterialId, topicQuery.ifBlank { null }, selectedCount, selectedDifficulty)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Synthesize with RAG")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TeacherQuizzesTab(
    quizzes: List<Quiz>,
    aiQuestions: List<QuizQuestion>,
    isGeneratingAI: Boolean,
    ragQuestions: List<RagQuizGeneratedQuestion>,
    ragMaterialTitle: String?,
    isGeneratingRAG: Boolean,
    onOpenRAGDialog: () -> Unit,
    onOpenAIDialog: () -> Unit,
    onOpenManualDialog: () -> Unit,
    onRemoveRagQuestion: (Int) -> Unit,
    onPublishRagQuiz: (String, Int) -> Unit,
    onClearRagQuiz: () -> Unit,
    onSaveAIQuiz: (String, Int, List<QuizQuestion>) -> Unit
) {
    var aiQuizTitle by remember { mutableStateOf("AI Generated Quiz") }
    var ragQuizTitle by remember(ragMaterialTitle) {
        mutableStateOf(if (ragMaterialTitle != null) "Exam: $ragMaterialTitle" else "Class RAG Quiz")
    }
    var ragDurationMinutes by remember { mutableStateOf("15") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero RAG Quiz Generator Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.12f),
                                    Color(0xFF06B6D4).copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF6366F1), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("RAG Smart Quiz & Exam Generator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Grounded in Course Notes & Lecture Materials", color = Slate500, fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Extracts factual assertions and concepts from your indexed course materials to formulate citation-backed multiple choice exams with verified correct answers.",
                            color = Slate600,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenRAGDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate from Notes (RAG)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(onClick = onOpenManualDialog) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Manual Quiz", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Active RAG Generation Progress Card
        if (isGeneratingRAG) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6366F1).copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF6366F1), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("RAG Vector Engine Active", fontWeight = FontWeight.Bold, color = Color(0xFF4338CA))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Retrieving semantic course chunks and formulating grounded MCQs with verified citations...",
                            fontSize = 12.sp,
                            color = Slate600,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // RAG Synthesized Questions Interactive Review Card
        if (ragQuestions.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Color(0xFF6366F1).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Synthesized Exam (${ragQuestions.size} MCQs)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF4338CA)
                                    )
                                }
                                if (ragMaterialTitle != null) {
                                    Text(
                                        "Source: $ragMaterialTitle",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }
                            TextButton(onClick = onClearRagQuiz) {
                                Text("Discard", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = ragQuizTitle,
                            onValueChange = { ragQuizTitle = it },
                            label = { Text("Quiz / Exam Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ragDurationMinutes,
                            onValueChange = { ragDurationMinutes = it },
                            label = { Text("Duration (Minutes)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "Review Questions & Verified Citations:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ragQuestions.forEachIndexed { idx, q ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${idx + 1}. ${q.questionText}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Slate800,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { onRemoveRagQuestion(idx) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, "Remove", tint = Slate500, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    q.options.forEach { opt ->
                                        val isCorrect = opt.isCorrect
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isCorrect) Color(0xFFECFDF5) else Color.White,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isCorrect) Color(0xFF10B981) else Slate200
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (isCorrect) {
                                                    Icon(
                                                        Icons.Default.CheckCircle,
                                                        null,
                                                        tint = Color(0xFF059669),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    opt.optionText,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isCorrect) FontWeight.SemiBold else FontWeight.Normal,
                                                    color = if (isCorrect) Color(0xFF065F46) else Slate700
                                                )
                                                if (isCorrect) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    Text(
                                                        "Correct",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF059669)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (q.sourceReference.isNotBlank() || q.explanation.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF6366F1).copy(alpha = 0.08f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    Icons.Default.MenuBook,
                                                    null,
                                                    tint = Color(0xFF6366F1),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    if (q.sourceReference.isNotBlank()) {
                                                        Text(
                                                            "Source: ${q.sourceReference}",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = Color(0xFF4338CA)
                                                        )
                                                    }
                                                    if (q.explanation.isNotBlank()) {
                                                        Text(
                                                            q.explanation,
                                                            fontSize = 11.sp,
                                                            color = Slate700
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                val dur = ragDurationMinutes.toIntOrNull() ?: 15
                                onPublishRagQuiz(ragQuizTitle, dur)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish Directly to Class Quizzes (${ragQuestions.size} MCQs)")
                        }
                    }
                }
            }
        }

        // Legacy AI Generated preview section
        if (isGeneratingAI) {
            item {
                LoadingIndicator("Gemini AI is generating quiz questions...")
            }
        } else if (aiQuestions.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Primary, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Generated Questions (${aiQuestions.size})",
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Button(
                                onClick = { onSaveAIQuiz(aiQuizTitle, 15, aiQuestions) },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text("Save & Publish")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = aiQuizTitle,
                            onValueChange = { aiQuizTitle = it },
                            label = { Text("Quiz Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        aiQuestions.forEachIndexed { i, q ->
                            Text("${i + 1}. ${q.questionText}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            q.options.forEach { opt ->
                                Text(
                                    "  • ${opt.optionText} ${if (opt.isCorrect) "✓ (Correct)" else ""}",
                                    fontSize = 12.sp,
                                    color = if (opt.isCorrect) Success else Slate700
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Section Title: Published Quizzes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Class Quizzes (${quizzes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Ready for Students",
                    color = Slate500,
                    fontSize = 11.sp
                )
            }
        }

        if (quizzes.isEmpty() && ragQuestions.isEmpty() && aiQuestions.isEmpty()) {
            item {
                EmptyState(
                    title = "No quizzes published yet",
                    message = "Synthesize an exam using RAG from course notes or create one manually",
                    actionText = "Synthesize First Quiz (RAG)",
                    onActionClick = onOpenRAGDialog
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
                        Column {
                            Text(quiz.title, fontWeight = FontWeight.Bold)
                            Text(
                                "${quiz.questions.size} Questions • ${quiz.durationMinutes} mins",
                                color = Slate500,
                                fontSize = 12.sp
                            )
                        }
                        BadgeChip(text = "Published", color = Success)
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherAssignmentsTab(
    assignments: List<Assignment>,
    onAddAssignment: () -> Unit,
    onViewSubmissions: (Assignment) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                Button(onClick = onAddAssignment) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task")
                }
            }
        }

        if (assignments.isEmpty()) {
            item {
                EmptyState(
                    title = "No assignments assigned",
                    message = "Assign homework, lab tasks or projects to your class",
                    actionText = "Create Assignment",
                    onActionClick = onAddAssignment
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
                        onClick = { onViewSubmissions(item) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                    ) {
                        Icon(Icons.Default.Grading, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Submissions & Assess (AI Rubric)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
