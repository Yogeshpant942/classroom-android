package com.practicum.schoolmanagement.ui.admin

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
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onLogout: () -> Unit
) {
    val data by viewModel.data.collectAsState()
    val currentUser by SchoolRepository.currentUser.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showServerDialog by remember { mutableStateOf(false) }

    // Dialog states
    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddNoticeDialog by remember { mutableStateOf(false) }
    var showAddLabGroupDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
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
            name = currentUser?.name ?: "Administrator",
            role = "Admin",
            email = currentUser?.email,
            schoolName = currentUser?.schoolName,
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showHelpdeskDialog) {
        CampusHelpdeskDialog(
            schoolId = currentUser?.schoolId ?: "",
            role = "Admin",
            userName = currentUser?.name ?: "Administrator",
            onDismiss = { showHelpdeskDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = currentUser?.schoolName.takeIf { !it.isNullOrBlank() } ?: "Admin Portal",
                subtitle = "Logged in as ${currentUser?.name ?: "Admin"}",
                role = "Admin",
                onProfileClick = { showProfileDialog = true },
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
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                    label = { Text("Overview") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Class, contentDescription = "Classes") },
                    label = { Text("Classes") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Teachers") },
                    label = { Text("Teachers") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Students") },
                    label = { Text("Students") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                    label = { Text("More") }
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
                LoadingIndicator("Updating Dashboard Data...")
            } else {
                when (selectedTab) {
                    0 -> AdminOverviewTab(
                        data = data,
                        onAddNotice = { showAddNoticeDialog = true },
                        onOpenHelpdesk = { showHelpdeskDialog = true },
                        onNavigateTab = { selectedTab = it }
                    )
                    1 -> AdminClassesTab(
                        classes = data.classes,
                        onAddClass = { showAddClassDialog = true },
                        onDeleteClass = { viewModel.deleteClass(it) }
                    )
                    2 -> AdminTeachersTab(
                        teachers = data.teachers,
                        onAddTeacher = { showAddTeacherDialog = true },
                        onDeleteTeacher = { viewModel.deleteTeacher(it) }
                    )
                    3 -> AdminStudentsTab(
                        students = data.students,
                        onAddStudent = { showAddStudentDialog = true },
                        onDeleteStudent = { viewModel.deleteStudent(it) }
                    )
                    4 -> AdminMoreTab(
                        data = data,
                        onAddSubject = { showAddSubjectDialog = true },
                        onDeleteSubject = { viewModel.deleteSubject(it) },
                        onAddNotice = { showAddNoticeDialog = true },
                        onDeleteNotice = { viewModel.deleteNotice(it) },
                        onAddLabGroup = { showAddLabGroupDialog = true }
                    )
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Add Class Dialog
    if (showAddClassDialog) {
        var classNameInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddClassDialog = false },
            title = { Text("Add New Class") },
            text = {
                OutlinedTextField(
                    value = classNameInput,
                    onValueChange = { classNameInput = it },
                    label = { Text("Class Name (e.g. Class 10A)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.createClass(classNameInput)
                    showAddClassDialog = false
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddClassDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 2. Add Subject Dialog
    if (showAddSubjectDialog) {
        var subName by remember { mutableStateOf("") }
        var subCode by remember { mutableStateOf("") }
        var sessions by remember { mutableStateOf("30") }
        var selectedClassId by remember { mutableStateOf(data.classes.firstOrNull()?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add New Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject Name (e.g. Physics)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subCode,
                        onValueChange = { subCode = it },
                        label = { Text("Subject Code (e.g. PHY101)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sessions,
                        onValueChange = { sessions = it },
                        label = { Text("Sessions Count") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Select Class:", style = MaterialTheme.typography.bodySmall, color = Slate500)
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(data.classes) { cls ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedClassId = cls.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedClassId == cls.id,
                                    onClick = { selectedClassId = cls.id }
                                )
                                Text(cls.sclassName)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createSubject(subName, subCode, sessions, selectedClassId)
                        showAddSubjectDialog = false
                    },
                    enabled = subName.isNotBlank() && selectedClassId.isNotBlank()
                ) {
                    Text("Add Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 3. Add Teacher Dialog
    if (showAddTeacherDialog) {
        var tName by remember { mutableStateOf("") }
        var tEmail by remember { mutableStateOf("") }
        var tPass by remember { mutableStateOf("") }
        var selSubId by remember { mutableStateOf(data.subjects.firstOrNull()?.id ?: "") }
        var selClsId by remember { mutableStateOf(data.classes.firstOrNull()?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showAddTeacherDialog = false },
            title = { Text("Register New Teacher") },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = tName,
                        onValueChange = { tName = it },
                        label = { Text("Teacher Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tEmail,
                        onValueChange = { tEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tPass,
                        onValueChange = { tPass = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Assign Class & Subject:", style = MaterialTheme.typography.bodySmall, color = Slate500)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Class:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            LazyColumn(modifier = Modifier.height(80.dp)) {
                                items(data.classes) { cls ->
                                    Row(
                                        modifier = Modifier.clickable { selClsId = cls.id },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = selClsId == cls.id, onClick = { selClsId = cls.id })
                                        Text(cls.sclassName, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Subject:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            LazyColumn(modifier = Modifier.height(80.dp)) {
                                items(data.subjects) { sub ->
                                    Row(
                                        modifier = Modifier.clickable { selSubId = sub.id },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = selSubId == sub.id, onClick = { selSubId = sub.id })
                                        Text(sub.subName, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.registerTeacher(tName, tEmail, tPass, selSubId, selClsId)
                        showAddTeacherDialog = false
                    },
                    enabled = tName.isNotBlank() && tEmail.isNotBlank() && tPass.isNotBlank()
                ) {
                    Text("Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTeacherDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 4. Add Student Dialog
    if (showAddStudentDialog) {
        var sName by remember { mutableStateOf("") }
        var sRoll by remember { mutableStateOf("") }
        var sPass by remember { mutableStateOf("") }
        var selClsId by remember { mutableStateOf(data.classes.firstOrNull()?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("Add New Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sName,
                        onValueChange = { sName = it },
                        label = { Text("Student Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sRoll,
                        onValueChange = { sRoll = it },
                        label = { Text("Roll Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sPass,
                        onValueChange = { sPass = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Assign Class:", style = MaterialTheme.typography.bodySmall, color = Slate500)
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(data.classes) { cls ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selClsId = cls.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selClsId == cls.id, onClick = { selClsId = cls.id })
                                Text(cls.sclassName)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.registerStudent(sName, sRoll, sPass, selClsId)
                        showAddStudentDialog = false
                    },
                    enabled = sName.isNotBlank() && sRoll.isNotBlank() && sPass.isNotBlank() && selClsId.isNotBlank()
                ) {
                    Text("Add Student")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 5. Add Notice Dialog
    if (showAddNoticeDialog) {
        var nTitle by remember { mutableStateOf("") }
        var nDetails by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoticeDialog = false },
            title = { Text("Create Notice") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nTitle,
                        onValueChange = { nTitle = it },
                        label = { Text("Notice Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nDetails,
                        onValueChange = { nDetails = it },
                        label = { Text("Notice Details") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createNotice(nTitle, nDetails)
                        showAddNoticeDialog = false
                    },
                    enabled = nTitle.isNotBlank() && nDetails.isNotBlank()
                ) {
                    Text("Publish Notice")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoticeDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 6. Add Lab Group Dialog
    if (showAddLabGroupDialog) {
        var gName by remember { mutableStateOf("") }
        var selClsId by remember { mutableStateOf(data.classes.firstOrNull()?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showAddLabGroupDialog = false },
            title = { Text("Create Lab Group") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = gName,
                        onValueChange = { gName = it },
                        label = { Text("Group Name (e.g. Group A)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Select Class:", style = MaterialTheme.typography.bodySmall, color = Slate500)
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(data.classes) { cls ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selClsId = cls.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selClsId == cls.id, onClick = { selClsId = cls.id })
                                Text(cls.sclassName)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createLabGroup(gName, selClsId)
                        showAddLabGroupDialog = false
                    },
                    enabled = gName.isNotBlank() && selClsId.isNotBlank()
                ) {
                    Text("Create Group")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLabGroupDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// --- SUB TABS ---

@Composable
fun AdminOverviewTab(
    data: AdminDashboardData,
    onAddNotice: () -> Unit,
    onOpenHelpdesk: () -> Unit,
    onNavigateTab: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dashboard Overview",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // 4 KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Students",
                    value = data.students.size.toString(),
                    icon = Icons.Default.School,
                    gradientColors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(3) }
                )
                StatCard(
                    title = "Total Classes",
                    value = data.classes.size.toString(),
                    icon = Icons.Default.Class,
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
                    title = "Total Teachers",
                    value = data.teachers.size.toString(),
                    icon = Icons.Default.People,
                    gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(2) }
                )
                StatCard(
                    title = "Total Subjects",
                    value = data.subjects.size.toString(),
                    icon = Icons.Default.MenuBook,
                    gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(4) }
                )
            }
        }

        // Notice Board Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notice Board",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onOpenHelpdesk,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.SupportAgent, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Helpdesk Bot", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = onAddNotice) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Notice")
                    }
                }
            }
        }

        if (data.notices.isEmpty()) {
            item {
                EmptyState(
                    title = "No notices posted",
                    message = "Post school announcements and updates here"
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
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        BadgeChip(text = notice.date.take(10), color = Secondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notice.details,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate700)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminClassesTab(
    classes: List<Sclass>,
    onAddClass: () -> Unit,
    onDeleteClass: (String) -> Unit
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
                    text = "Classes (${classes.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onAddClass,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Class")
                }
            }
        }

        if (classes.isEmpty()) {
            item {
                EmptyState(
                    title = "No classes found",
                    message = "Create classes to organize students and subjects",
                    actionText = "Create First Class",
                    onActionClick = onAddClass
                )
            }
        } else {
            items(classes) { cls ->
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
                                    .background(Primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Class, null, tint = Primary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = cls.sclassName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Class ID: ${cls.id.take(8)}...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }
                        IconButton(onClick = { onDeleteClass(cls.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Danger)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTeachersTab(
    teachers: List<Teacher>,
    onAddTeacher: () -> Unit,
    onDeleteTeacher: (String) -> Unit
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
                    text = "Teachers (${teachers.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onAddTeacher,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.PersonAdd, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Teacher")
                }
            }
        }

        if (teachers.isEmpty()) {
            item {
                EmptyState(
                    title = "No teachers registered",
                    message = "Register faculty members to teach subjects",
                    actionText = "Register Teacher",
                    onActionClick = onAddTeacher
                )
            }
        } else {
            items(teachers) { teacher ->
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
                                    .clip(CircleShape)
                                    .background(Secondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, tint = Secondary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = teacher.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = teacher.email,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }
                        IconButton(onClick = { onDeleteTeacher(teacher.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Danger)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStudentsTab(
    students: List<Student>,
    onAddStudent: () -> Unit,
    onDeleteStudent: (String) -> Unit
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
                    text = "Students (${students.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onAddStudent,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.PersonAdd, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Student")
                }
            }
        }

        if (students.isEmpty()) {
            item {
                EmptyState(
                    title = "No students enrolled",
                    message = "Add students and assign them to classes",
                    actionText = "Add First Student",
                    onActionClick = onAddStudent
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
                                    .size(44.dp)
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
                        IconButton(onClick = { onDeleteStudent(student.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Danger)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMoreTab(
    data: AdminDashboardData,
    onAddSubject: () -> Unit,
    onDeleteSubject: (String) -> Unit,
    onAddNotice: () -> Unit,
    onDeleteNotice: (String) -> Unit,
    onAddLabGroup: () -> Unit
) {
    var subSection by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = subSection) {
            Tab(selected = subSection == 0, onClick = { subSection = 0 }, text = { Text("Subjects") })
            Tab(selected = subSection == 1, onClick = { subSection = 1 }, text = { Text("Notices") })
            Tab(selected = subSection == 2, onClick = { subSection = 2 }, text = { Text("Complaints") })
            Tab(selected = subSection == 3, onClick = { subSection = 3 }, text = { Text("Lab Groups") })
        }

        when (subSection) {
            0 -> {
                // Subjects List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subjects (${data.subjects.size})", fontWeight = FontWeight.Bold)
                            Button(onClick = onAddSubject) {
                                Icon(Icons.Default.Add, null)
                                Text("Add Subject")
                            }
                        }
                    }
                    if (data.subjects.isEmpty()) {
                        item { EmptyState("No Subjects", "Create subjects for classes", actionText = "Add Subject", onActionClick = onAddSubject) }
                    } else {
                        items(data.subjects) { sub ->
                            GlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(sub.subName, fontWeight = FontWeight.Bold)
                                        Text("Code: ${sub.subCode} | Sessions: ${sub.sessions}", color = Slate500, fontSize = 12.sp)
                                    }
                                    IconButton(onClick = { onDeleteSubject(sub.id) }) {
                                        Icon(Icons.Default.Delete, null, tint = Danger)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Notices List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Notices (${data.notices.size})", fontWeight = FontWeight.Bold)
                            Button(onClick = onAddNotice) {
                                Icon(Icons.Default.Add, null)
                                Text("New Notice")
                            }
                        }
                    }
                    items(data.notices) { not ->
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(not.title, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(not.details, color = Slate700)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    BadgeChip(text = not.date.take(10), color = Secondary)
                                }
                                IconButton(onClick = { onDeleteNotice(not.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Danger)
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Complaints List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Submitted Complaints (${data.complaints.size})", fontWeight = FontWeight.Bold)
                    }
                    if (data.complaints.isEmpty()) {
                        item { EmptyState("No Complaints", "All clear! No student or teacher complaints reported.") }
                    } else {
                        items(data.complaints) { comp ->
                            GlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Date: ${comp.date.take(10)}", color = Slate500, fontSize = 12.sp)
                                    BadgeChip(text = "Complaint", color = Warning)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(comp.complaint, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            3 -> {
                // Lab Groups
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lab Groups (${data.labGroups.size})", fontWeight = FontWeight.Bold)
                            Button(onClick = onAddLabGroup) {
                                Icon(Icons.Default.Add, null)
                                Text("New Lab Group")
                            }
                        }
                    }
                    if (data.labGroups.isEmpty()) {
                        item { EmptyState("No Lab Groups", "Organize students into lab/project teams", actionText = "Add Group", onActionClick = onAddLabGroup) }
                    } else {
                        items(data.labGroups) { grp ->
                            GlassCard {
                                Text(grp.groupName, fontWeight = FontWeight.Bold)
                                Text("Members: ${grp.students?.size ?: 0} students", color = Slate500, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
