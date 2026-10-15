package com.practicum.schoolmanagement.data.model

import com.google.gson.annotations.SerializedName

// User session / Auth state
data class SessionUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "", // "Admin", "Teacher", "Student"
    val schoolId: String = "",
    val schoolName: String = "",
    val classId: String = "",
    val className: String = "",
    val rollNum: Int = 0,
    val subjectId: String = "",
    val subjectName: String = ""
)

// Admin
data class Admin(
    @SerializedName("_id") val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Admin",
    val schoolName: String = ""
)

// Sclass (Class)
data class Sclass(
    @SerializedName("_id") val id: String = "",
    val sclassName: String = "",
    val school: Any? = null
)

// Subject
data class Subject(
    @SerializedName("_id") val id: String = "",
    val subName: String = "",
    val subCode: String = "",
    val sessions: String = "0",
    val sclassName: Any? = null,
    val teacher: Any? = null
)

// Teacher
data class Teacher(
    @SerializedName("_id") val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Teacher",
    val school: Any? = null,
    val teachSubject: Any? = null,
    val teachSclass: Any? = null,
    val attendance: List<TeacherAttendance>? = null
)

data class TeacherAttendance(
    val date: String? = null,
    val presentCount: String? = null,
    val absentCount: String? = null
)

// Student
data class Student(
    @SerializedName("_id") val id: String = "",
    val name: String = "",
    val rollNum: Int = 0,
    val role: String = "Student",
    val school: Any? = null,
    val sclassName: Any? = null,
    val labGroup: Any? = null,
    val attendance: List<AttendanceRecord>? = null,
    val examResult: List<ExamResult>? = null
)

data class AttendanceRecord(
    @SerializedName("_id") val id: String? = null,
    val date: String = "",
    val status: String = "", // "Present", "Absent"
    val subName: Any? = null
)

data class ExamResult(
    @SerializedName("_id") val id: String? = null,
    val subName: Any? = null,
    val marksObtained: Double = 0.0
)

// Notice
data class Notice(
    @SerializedName("_id") val id: String = "",
    val title: String = "",
    val details: String = "",
    val date: String = "",
    val school: Any? = null
)

// Complain
data class Complain(
    @SerializedName("_id") val id: String = "",
    val user: Any? = null,
    val date: String = "",
    val complaint: String = "",
    val school: Any? = null
)

// Lab Group
data class LabGroup(
    @SerializedName("_id") val id: String = "",
    val groupName: String = "",
    val sclassName: Any? = null,
    val students: List<Any>? = null,
    val school: Any? = null
)

// Quiz
data class Quiz(
    @SerializedName("_id") val id: String = "",
    val title: String = "",
    val sclassName: Any? = null,
    val subject: Any? = null,
    val teacher: Any? = null,
    val durationMinutes: Int = 15,
    val questions: List<QuizQuestion> = emptyList(),
    val createdAt: String? = null
)

data class QuizQuestion(
    @SerializedName("_id") val id: String? = null,
    val questionText: String = "",
    val options: List<QuizOption> = emptyList()
)

data class QuizOption(
    @SerializedName("_id") val id: String? = null,
    val optionText: String = "",
    val isCorrect: Boolean = false
)

data class QuizSubmissionAnswer(
    val questionId: String,
    val selectedOptionId: String
)

data class QuizSubmitRequest(
    val student: String,
    val quiz: String,
    val answers: List<QuizSubmissionAnswer>
)

data class QuizResultResponse(
    @SerializedName("_id") val id: String? = null,
    val student: String? = null,
    val quiz: String? = null,
    val score: Int = 0,
    val answers: List<Any>? = null,
    val submittedAt: String? = null,
    val message: String? = null
)

// Assignment
data class Assignment(
    @SerializedName("_id") val id: String = "",
    val title: String = "",
    val description: String = "",
    val deadline: String = "",
    val sclassName: Any? = null,
    val subject: Any? = null,
    val teacher: Any? = null,
    val createdAt: String? = null
)

data class AssignmentSubmission(
    @SerializedName("_id") val id: String = "",
    val assignment: Any? = null,
    val student: Any? = null,
    val content: String = "",
    val summary: String? = null,
    val plagiarizedFrom: List<Any>? = null,
    val grade: Int? = null,
    val feedback: String? = null,
    val rubricBreakdown: List<RubricCriterion>? = null,
    val strengths: List<String>? = null,
    val improvements: List<String>? = null,
    val assessedAt: String? = null,
    val fileUrl: String? = null,
    val submittedAt: String? = null,
    val createdAt: String? = null
)

// AI Quiz Generation Request / Response
data class AIQuizGenerateRequest(
    val topic: String,
    val numQuestions: Int = 5,
    val referenceNotes: String = ""
)

// Generic API message response
data class MessageResponse(
    val message: String? = null
)

// RAG (Retrieval-Augmented Generation) & Campus AI Tutor
data class StudyMaterial(
    @SerializedName("_id") val id: String = "",
    val title: String = "",
    val content: String = "",
    val uploadedBy: String = "Teacher",
    val createdAt: String = "",
    val subject: Any? = null,
    val sclass: Any? = null
)

data class RagAskRequest(
    val classId: String,
    val query: String,
    val studentName: String = "Student"
)

data class RagCitation(
    val title: String = "",
    val snippet: String = "",
    val score: Double = 0.0
)

data class RagAskResponse(
    val answer: String = "",
    val citations: List<RagCitation> = emptyList()
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val citations: List<RagCitation> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

// Feature 2: RAG-Powered Smart Quiz & Exam Generator
data class RagQuizGenerateRequest(
    val classId: String,
    val materialId: String? = null,
    val topic: String? = null,
    val numQuestions: Int = 5,
    val difficulty: String = "Medium"
)

data class RagQuizGeneratedQuestion(
    val questionText: String = "",
    val options: List<QuizOption> = emptyList(),
    val explanation: String = "",
    val sourceReference: String = ""
)

data class RagQuizGenerateResponse(
    val success: Boolean = false,
    val materialTitle: String? = null,
    val chunksUsed: Int = 0,
    val questions: List<RagQuizGeneratedQuestion> = emptyList(),
    val message: String? = null
)

// Feature 3: Campus Policy & Notice Helpdesk Bot (RAG over Notices & Policies)
data class RagPolicyAskRequest(
    val schoolId: String,
    val query: String,
    val role: String = "Student",
    val userName: String = ""
)

data class RagPolicyCitation(
    val title: String = "",
    val date: String = "",
    val snippet: String = "",
    val score: Double = 0.0
)

data class RagPolicyAskResponse(
    val success: Boolean = false,
    val answer: String = "",
    val citations: List<RagPolicyCitation> = emptyList(),
    val message: String? = null
)

data class HelpdeskMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val citations: List<RagPolicyCitation> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

// Feature 4: Automated Assignment Assessment & Rubric Matching
data class RubricCriterion(
    val criterion: String = "",
    val maxMarks: Int = 30,
    val awardedMarks: Int = 0,
    val comments: String = ""
)

data class AssessAssignmentRequest(
    val submissionId: String? = null,
    val assignmentTitle: String = "Assignment",
    val assignmentDescription: String = "",
    val studentContent: String,
    val classId: String? = null,
    val customRubric: String? = null
)

data class AssessAssignmentResponse(
    val success: Boolean = false,
    val overallScore: Int = 0,
    val rubricBreakdown: List<RubricCriterion> = emptyList(),
    val strengths: List<String> = emptyList(),
    val improvements: List<String> = emptyList(),
    val constructiveFeedback: String = "",
    val message: String? = null
)

data class SaveGradeRequest(
    val submissionId: String,
    val grade: Int,
    val feedback: String = "",
    val rubricBreakdown: List<RubricCriterion> = emptyList()
)



