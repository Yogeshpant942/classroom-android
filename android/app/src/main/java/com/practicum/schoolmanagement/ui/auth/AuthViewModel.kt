package com.practicum.schoolmanagement.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.schoolmanagement.data.model.SessionUser
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: SessionUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun adminLogin(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = SchoolRepository.adminLogin(email.trim(), pass.trim())
            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Admin login failed") }
            )
        }
    }

    fun adminRegister(name: String, email: String, pass: String, schoolName: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || schoolName.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = SchoolRepository.adminRegister(name.trim(), email.trim(), pass.trim(), schoolName.trim())
            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Admin register failed") }
            )
        }
    }

    fun teacherLogin(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = SchoolRepository.teacherLogin(email.trim(), pass.trim())
            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Teacher login failed") }
            )
        }
    }

    fun studentLogin(name: String, rollNumStr: String, pass: String) {
        val rollNum = rollNumStr.toIntOrNull()
        if (name.isBlank() || rollNum == null || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Valid Name, Roll Number, and Password are required")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = SchoolRepository.studentLogin(name.trim(), rollNum, pass.trim())
            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Student login failed") }
            )
        }
    }

    fun guestLogin(role: String) {
        // Quick demo login helper
        val guest = when (role) {
            "Admin" -> SessionUser(
                id = "guest_admin",
                name = "Demo Admin",
                email = "admin@guest.com",
                role = "Admin",
                schoolId = "guest_admin",
                schoolName = "Springfield Academy (Demo)"
            )
            "Teacher" -> SessionUser(
                id = "guest_teacher",
                name = "Demo Teacher",
                email = "teacher@guest.com",
                role = "Teacher",
                schoolId = "guest_admin",
                schoolName = "Springfield Academy (Demo)",
                classId = "guest_class",
                className = "Class 10A",
                subjectId = "guest_sub",
                subjectName = "Computer Science"
            )
            else -> SessionUser(
                id = "guest_student",
                name = "Demo Student",
                role = "Student",
                rollNum = 101,
                schoolId = "guest_admin",
                schoolName = "Springfield Academy (Demo)",
                classId = "guest_class",
                className = "Class 10A"
            )
        }
        SchoolRepository.setCurrentUser(guest)
        _uiState.value = AuthUiState.Success(guest)
    }
}
