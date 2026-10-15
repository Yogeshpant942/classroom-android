package com.practicum.schoolmanagement.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.schoolmanagement.data.model.*
import com.practicum.schoolmanagement.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TeacherDashboardData(
    val students: List<Student> = emptyList(),
    val quizzes: List<Quiz> = emptyList(),
    val assignments: List<Assignment> = emptyList(),
    val studyMaterials: List<StudyMaterial> = emptyList(),
    val aiGeneratedQuestions: List<QuizQuestion> = emptyList(),
    val isGeneratingAI: Boolean = false,
    val ragGeneratedQuestions: List<RagQuizGeneratedQuestion> = emptyList(),
    val ragMaterialTitle: String? = null,
    val isGeneratingRAGQuiz: Boolean = false,
    val assignmentSubmissions: List<AssignmentSubmission> = emptyList(),
    val isAssessingAssignment: Boolean = false,
    val currentAssessment: AssessAssignmentResponse? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class TeacherViewModel : ViewModel() {

    private val _data = MutableStateFlow(TeacherDashboardData())
    val data: StateFlow<TeacherDashboardData> = _data.asStateFlow()

    val currentTeacher get() = SchoolRepository.currentUser.value

    fun clearMessages() {
        _data.value = _data.value.copy(message = null, errorMessage = null)
    }

    fun loadAll() {
        val classId = currentTeacher?.classId ?: ""
        if (classId.isBlank()) return
        _data.value = _data.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val students = SchoolRepository.getClassStudents(classId)
                val quizzes = SchoolRepository.getQuizzes(classId)
                val assignments = SchoolRepository.getAssignments(classId)
                val materials = SchoolRepository.getStudyMaterials(classId)

                _data.value = _data.value.copy(
                    students = students,
                    quizzes = quizzes,
                    assignments = assignments,
                    studyMaterials = materials,
                    isLoading = false
                )
            } catch (e: Exception) {
                _data.value = _data.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load class data"
                )
            }
        }
    }

    fun markStudentAttendance(studentId: String, status: String) {
        val subjectId = currentTeacher?.subjectId ?: ""
        val today = java.time.LocalDate.now().toString()
        viewModelScope.launch {
            val res = SchoolRepository.markStudentAttendance(studentId, subjectId, status, today)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Attendance marked: $status")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun markBulkAttendance(status: String, studentIds: List<String>) {
        val subjectId = currentTeacher?.subjectId ?: ""
        val today = java.time.LocalDate.now().toString()
        viewModelScope.launch {
            val res = SchoolRepository.markBulkAttendance(subjectId, status, today, studentIds)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Bulk attendance marked as $status")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun updateMarks(studentId: String, marks: Double) {
        val subjectId = currentTeacher?.subjectId ?: ""
        viewModelScope.launch {
            val res = SchoolRepository.updateExamResult(studentId, subjectId, marks)
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Marks updated successfully")
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun generateQuizWithAI(topic: String, count: Int, notes: String) {
        if (topic.isBlank()) return
        _data.value = _data.value.copy(isGeneratingAI = true)
        viewModelScope.launch {
            val res = SchoolRepository.generateQuizAI(topic.trim(), count, notes.trim())
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(
                        aiGeneratedQuestions = it,
                        isGeneratingAI = false,
                        message = "Generated ${it.size} AI questions!"
                    )
                },
                onFailure = {
                    _data.value = _data.value.copy(
                        isGeneratingAI = false,
                        errorMessage = "AI generation failed: ${it.message}"
                    )
                }
            )
        }
    }

    fun createQuiz(title: String, durationMinutes: Int, questions: List<QuizQuestion>) {
        val t = currentTeacher ?: return
        if (title.isBlank() || questions.isEmpty()) return
        viewModelScope.launch {
            val res = SchoolRepository.createQuiz(
                title = title.trim(),
                classId = t.classId,
                subjectId = t.subjectId,
                teacherId = t.id,
                schoolId = t.schoolId,
                durationMinutes = durationMinutes,
                questions = questions
            )
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it, aiGeneratedQuestions = emptyList())
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun createAssignment(title: String, description: String, deadline: String) {
        val t = currentTeacher ?: return
        if (title.isBlank() || deadline.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.createAssignment(
                title = title.trim(),
                description = description.trim(),
                deadline = deadline.trim(),
                classId = t.classId,
                subjectId = t.subjectId,
                teacherId = t.id,
                schoolId = t.schoolId
            )
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun submitComplaint(complaintText: String) {
        val t = currentTeacher ?: return
        if (complaintText.isBlank()) return
        val date = java.time.LocalDate.now().toString()
        viewModelScope.launch {
            val res = SchoolRepository.createComplaint(t.id, date, complaintText.trim(), t.schoolId)
            res.fold(
                onSuccess = { _data.value = _data.value.copy(message = it) },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun uploadStudyMaterial(title: String, content: String) {
        val t = currentTeacher ?: return
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            val res = SchoolRepository.uploadStudyMaterial(
                title = title.trim(),
                content = content.trim(),
                classId = t.classId,
                subjectId = t.subjectId.ifBlank { null },
                schoolId = t.schoolId,
                uploadedBy = t.name
            )
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = it)
                    loadAll()
                },
                onFailure = { _data.value = _data.value.copy(errorMessage = it.message) }
            )
        }
    }

    fun generateQuizFromRAG(
        materialId: String?,
        topic: String?,
        numQuestions: Int,
        difficulty: String
    ) {
        val classId = currentTeacher?.classId ?: return
        _data.value = _data.value.copy(isGeneratingRAGQuiz = true)
        viewModelScope.launch {
            val res = SchoolRepository.generateQuizFromRAG(
                classId = classId,
                materialId = materialId,
                topic = topic,
                numQuestions = numQuestions,
                difficulty = difficulty
            )
            res.fold(
                onSuccess = { response ->
                    _data.value = _data.value.copy(
                        isGeneratingRAGQuiz = false,
                        ragGeneratedQuestions = response.questions,
                        ragMaterialTitle = response.materialTitle,
                        message = "Synthesized ${response.questions.size} grounded questions with verified source citations!"
                    )
                },
                onFailure = {
                    _data.value = _data.value.copy(
                        isGeneratingRAGQuiz = false,
                        errorMessage = it.message ?: "RAG Quiz Generation failed"
                    )
                }
            )
        }
    }

    fun clearRagQuestions() {
        _data.value = _data.value.copy(ragGeneratedQuestions = emptyList(), ragMaterialTitle = null)
    }

    fun removeRagQuestion(index: Int) {
        val updated = _data.value.ragGeneratedQuestions.toMutableList()
        if (index in updated.indices) {
            updated.removeAt(index)
            _data.value = _data.value.copy(ragGeneratedQuestions = updated)
        }
    }

    fun publishRagQuiz(title: String, durationMinutes: Int) {
        val questions = _data.value.ragGeneratedQuestions.map {
            QuizQuestion(
                questionText = it.questionText,
                options = it.options
            )
        }
        if (questions.isEmpty()) return
        createQuiz(title = title, durationMinutes = durationMinutes, questions = questions)
        clearRagQuestions()
    }

    // Feature 4: Automated Assignment Assessment & Rubrics
    fun loadSubmissions(assignmentId: String) {
        viewModelScope.launch {
            val subs = SchoolRepository.getAssignmentSubmissions(assignmentId)
            _data.value = _data.value.copy(assignmentSubmissions = subs)
        }
    }

    fun assessSubmission(
        submission: AssignmentSubmission,
        assignmentTitle: String,
        assignmentDesc: String,
        customRubric: String? = null
    ) {
        val classId = currentTeacher?.classId ?: ""
        _data.value = _data.value.copy(isAssessingAssignment = true, currentAssessment = null)
        viewModelScope.launch {
            val res = SchoolRepository.assessAssignment(
                AssessAssignmentRequest(
                    submissionId = submission.id,
                    assignmentTitle = assignmentTitle,
                    assignmentDescription = assignmentDesc,
                    studentContent = submission.content,
                    classId = classId,
                    customRubric = customRubric
                )
            )
            res.fold(
                onSuccess = { response ->
                    _data.value = _data.value.copy(
                        isAssessingAssignment = false,
                        currentAssessment = response,
                        message = "AI Rubric evaluated! Suggested score: ${response.overallScore}/100"
                    )
                },
                onFailure = {
                    _data.value = _data.value.copy(
                        isAssessingAssignment = false,
                        errorMessage = it.message ?: "Assessment failed"
                    )
                }
            )
        }
    }

    fun clearCurrentAssessment() {
        _data.value = _data.value.copy(currentAssessment = null, isAssessingAssignment = false)
    }

    fun saveGrade(
        submissionId: String,
        grade: Int,
        feedback: String,
        rubricBreakdown: List<RubricCriterion>,
        assignmentId: String
    ) {
        viewModelScope.launch {
            val res = SchoolRepository.saveAssignmentGrade(
                submissionId = submissionId,
                grade = grade,
                feedback = feedback,
                rubricBreakdown = rubricBreakdown
            )
            res.fold(
                onSuccess = {
                    _data.value = _data.value.copy(message = "Grade and feedback saved successfully!")
                    loadSubmissions(assignmentId)
                    clearCurrentAssessment()
                },
                onFailure = {
                    _data.value = _data.value.copy(errorMessage = it.message ?: "Failed to save grade")
                }
            )
        }
    }
}
