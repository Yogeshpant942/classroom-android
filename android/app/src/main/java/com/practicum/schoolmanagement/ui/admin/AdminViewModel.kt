package com.practicum.schoolmanagement.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.schoolmanagement.data.model.*
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminDashboardData(
    val classes: List<Sclass> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val students: List<Student> = emptyList(),
    val notices: List<Notice> = emptyList(),
    val complaints: List<Complain> = emptyList(),
    val labGroups: List<LabGroup> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class AdminViewModel : ViewModel() {

    private val _data = MutableStateFlow(AdminDashboardData())
    val data: StateFlow<AdminDashboardData> = _data.asStateFlow()

    private val adminId: String
        get() = SchoolRepository.currentUser.value?.schoolId ?: ""

    fun clearMessages() {
        _data.value = _data.value.copy(message = null, errorMessage = null)
    }

    fun loadAll() {
        if (adminId.isBlank()) return
        _data.value = _data.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val classes = SchoolRepository.getClasses(adminId)
                val subjects = SchoolRepository.getAllSubjects(adminId)
                val teachers = SchoolRepository.getTeachers(adminId)
                val students = SchoolRepository.getStudents(adminId)
                val notices = SchoolRepository.getNotices(adminId)
                val complaints = SchoolRepository.getComplaints(adminId)

                _data.value = _data.value.copy(
                    classes = classes,
                    subjects = subjects,
                    teachers = teachers,
                    students = students,
                    notices = notices,
                    complaints = complaints,
                    isLoading = false
                )
            } catch (e: Exception) {
                _data.value = _data.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }

    fun createClass(className: String) {
        if (className.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.createClass(className.trim(), adminId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun deleteClass(classId: String) {
        viewModelScope.launch {
            val res = SchoolRepository.deleteClass(classId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Class deleted")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun createSubject(name: String, code: String, sessions: String, classId: String) {
        if (name.isBlank() || classId.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.createSubject(name.trim(), code.trim(), sessions.trim(), classId, adminId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun deleteSubject(subjectId: String) {
        viewModelScope.launch {
            val res = SchoolRepository.deleteSubject(subjectId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Subject deleted")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun registerTeacher(name: String, email: String, pass: String, subjectId: String, classId: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.registerTeacher(name.trim(), email.trim(), pass.trim(), subjectId, classId, adminId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun deleteTeacher(teacherId: String) {
        viewModelScope.launch {
            val res = SchoolRepository.deleteTeacher(teacherId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Teacher removed")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun registerStudent(name: String, rollNumStr: String, pass: String, classId: String) {
        val roll = rollNumStr.toIntOrNull() ?: 1
        if (name.isBlank() || pass.isBlank() || classId.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.registerStudent(name.trim(), roll, pass.trim(), classId, adminId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            val res = SchoolRepository.deleteStudent(studentId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Student removed")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun createNotice(title: String, details: String) {
        if (title.isBlank() || details.isBlank()) return
        val date = java.time.LocalDate.now().toString()
        viewModelScope.launch {
            val res = SchoolRepository.createNotice(title.trim(), details.trim(), date, adminId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun deleteNotice(noticeId: String) {
        viewModelScope.launch {
            val res = SchoolRepository.deleteNotice(noticeId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Notice deleted")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun loadLabGroups(classId: String) {
        viewModelScope.launch {
            val groups = SchoolRepository.getLabGroups(classId)
            _data.value = _data.value.copy(labGroups = groups)
        }
    }

    fun createLabGroup(name: String, classId: String) {
        if (name.isBlank() || classId.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.createLabGroup(name.trim(), classId, adminId)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadLabGroups(classId)
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }
}
