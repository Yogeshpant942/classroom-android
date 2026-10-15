package com.practicum.schoolmanagement.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.practicum.schoolmanagement.data.model.*
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentDashboardData(
    val studentDetails: JsonObject? = null,
    val subjects: List<Subject> = emptyList(),
    val notices: List<Notice> = emptyList(),
    val quizzes: List<Quiz> = emptyList(),
    val assignments: List<Assignment> = emptyList(),
    val examResults: List<ExamResult> = emptyList(),
    val studyMaterials: List<StudyMaterial> = emptyList(),
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hello! I am your AI Campus Tutor powered by RAG (Retrieval-Augmented Generation). Ask me any questions about your class notes, syllabus, or exam topics, and I'll retrieve answers with exact citations from your course materials!",
            isUser = false
        )
    ),
    val isTutorTyping: Boolean = false,
    val attendancePercentage: Double = 0.0,
    val totalPresent: Int = 0,
    val totalAbsent: Int = 0,
    val quizScoreResult: QuizResultResponse? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class StudentViewModel : ViewModel() {

    private val _data = MutableStateFlow(StudentDashboardData())
    val data: StateFlow<StudentDashboardData> = _data.asStateFlow()

    val currentStudent get() = SchoolRepository.currentUser.value

    fun clearMessages() {
        _data.value = _data.value.copy(message = null, errorMessage = null, quizScoreResult = null)
    }

    fun loadAll() {
        val student = currentStudent ?: return
        _data.value = _data.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val details = SchoolRepository.getStudentDetail(student.id)
                val subjects = SchoolRepository.getClassSubjects(student.classId)
                val notices = SchoolRepository.getNotices(student.schoolId)
                val quizzes = SchoolRepository.getQuizzes(student.classId)
                val assignments = SchoolRepository.getAssignments(student.classId)
                val materials = SchoolRepository.getStudyMaterials(student.classId)

                // Parse attendance from studentDetails JSON
                var present = 0
                var absent = 0
                if (details != null && details.has("attendance")) {
                    val arr = details.getAsJsonArray("attendance")
                    arr.forEach { el ->
                        val obj = el.asJsonObject
                        val status = obj.get("status")?.asString ?: ""
                        if (status.equals("Present", ignoreCase = true)) present++
                        else if (status.equals("Absent", ignoreCase = true)) absent++
                    }
                }
                val total = present + absent
                val pct = if (total > 0) (present.toDouble() / total) * 100 else 0.0

                // Parse exam results
                val examResultsList = mutableListOf<ExamResult>()
                if (details != null && details.has("examResult")) {
                    val arr = details.getAsJsonArray("examResult")
                    arr.forEach { el ->
                        try {
                            val obj = el.asJsonObject
                            val marks = obj.get("marksObtained")?.asDouble ?: 0.0
                            val subId = obj.get("subName")?.asString ?: ""
                            examResultsList.add(ExamResult(marksObtained = marks, subName = subId))
                        } catch (_: Exception) {}
                    }
                }

                _data.value = _data.value.copy(
                    studentDetails = details,
                    subjects = subjects,
                    notices = notices,
                    quizzes = quizzes,
                    assignments = assignments,
                    studyMaterials = materials,
                    examResults = examResultsList,
                    attendancePercentage = pct,
                    totalPresent = present,
                    totalAbsent = absent,
                    isLoading = false
                )
            } catch (e: Exception) {
                _data.value = _data.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load student data"
                )
            }
        }
    }

    fun sendTutorQuestion(query: String) {
        val student = currentStudent ?: return
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return

        val userMsg = ChatMessage(text = cleanQuery, isUser = true)
        val currentList = _data.value.chatMessages
        _data.value = _data.value.copy(
            chatMessages = currentList + userMsg,
            isTutorTyping = true
        )

        viewModelScope.launch {
            val result = SchoolRepository.askCampusTutor(
                classId = student.classId,
                query = cleanQuery,
                studentName = student.name
            )
            result.fold(
                onSuccess = { res ->
                    val aiMsg = ChatMessage(
                        text = res.answer,
                        isUser = false,
                        citations = res.citations
                    )
                    _data.value = _data.value.copy(
                        chatMessages = _data.value.chatMessages + aiMsg,
                        isTutorTyping = false
                    )
                },
                onFailure = { err ->
                    val errMsg = ChatMessage(
                        text = "I encountered an issue retrieving course notes: ${err.message ?: "Unknown error"}. Please make sure study materials are uploaded for your class.",
                        isUser = false
                    )
                    _data.value = _data.value.copy(
                        chatMessages = _data.value.chatMessages + errMsg,
                        isTutorTyping = false
                    )
                }
            )
        }
    }

    fun submitQuiz(quizId: String, answers: Map<String, String>) {
        val s = currentStudent ?: return
        val list = answers.map { (qId, optId) ->
            QuizSubmissionAnswer(questionId = qId, selectedOptionId = optId)
        }
        val req = QuizSubmitRequest(student = s.id, quiz = quizId, answers = list)
        viewModelScope.launch {
            val res = SchoolRepository.submitQuiz(req)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(
                        quizScoreResult = it,
                        message = "Quiz submitted! Your Score: ${it.score}"
                    )
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun submitAssignment(assignmentId: String, content: String) {
        val s = currentStudent ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.submitAssignment(assignmentId, s.id, content.trim())
            res.fold(
                onSuccess = { _data.value = _data.value.copy(message = it) },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun submitComplaint(text: String) {
        val s = currentStudent ?: return
        if (text.isBlank()) return
        val date = java.time.LocalDate.now().toString()
        viewModelScope.launch {
            val res = SchoolRepository.createComplaint(s.id, date, text.trim(), s.schoolId)
            res.fold(
                onSuccess = { _data.value = _data.value.copy(message = it) },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }
}
