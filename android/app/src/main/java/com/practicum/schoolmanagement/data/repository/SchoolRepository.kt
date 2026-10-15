package com.practicum.schoolmanagement.data.repository

import com.google.gson.JsonObject
import com.practicum.schoolmanagement.data.api.RetrofitClient
import com.practicum.schoolmanagement.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SchoolRepository {

    private val api get() = RetrofitClient.getClient()

    private val _currentUser = MutableStateFlow<SessionUser?>(null)
    val currentUser: StateFlow<SessionUser?> = _currentUser.asStateFlow()

    fun setCurrentUser(user: SessionUser?) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Authentication ---
    suspend fun adminLogin(email: String, pass: String): Result<SessionUser> {
        return try {
            val res = api.adminLogin(mapOf("email" to email, "password" to pass))
            if (res.isSuccessful && res.body() != null) {
                val json = res.body()!!
                if (json.has("message")) {
                    Result.failure(Exception(json.get("message").asString))
                } else {
                    val user = SessionUser(
                        id = json.get("_id")?.asString ?: "",
                        name = json.get("name")?.asString ?: "",
                        email = json.get("email")?.asString ?: "",
                        role = "Admin",
                        schoolId = json.get("_id")?.asString ?: "",
                        schoolName = json.get("schoolName")?.asString ?: ""
                    )
                    setCurrentUser(user)
                    Result.success(user)
                }
            } else {
                Result.failure(Exception("Login failed: ${res.code()} ${res.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun adminRegister(name: String, email: String, pass: String, schoolName: String): Result<SessionUser> {
        return try {
            val res = api.adminRegister(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to pass,
                    "schoolName" to schoolName,
                    "role" to "Admin"
                )
            )
            if (res.isSuccessful && res.body() != null) {
                val json = res.body()!!
                if (json.has("message")) {
                    Result.failure(Exception(json.get("message").asString))
                } else {
                    val user = SessionUser(
                        id = json.get("_id")?.asString ?: "",
                        name = json.get("name")?.asString ?: "",
                        email = json.get("email")?.asString ?: "",
                        role = "Admin",
                        schoolId = json.get("_id")?.asString ?: "",
                        schoolName = json.get("schoolName")?.asString ?: ""
                    )
                    setCurrentUser(user)
                    Result.success(user)
                }
            } else {
                Result.failure(Exception("Registration failed: ${res.code()} ${res.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun teacherLogin(email: String, pass: String): Result<SessionUser> {
        return try {
            val res = api.teacherLogin(mapOf("email" to email, "password" to pass))
            if (res.isSuccessful && res.body() != null) {
                val json = res.body()!!
                if (json.has("message")) {
                    Result.failure(Exception(json.get("message").asString))
                } else {
                    val schoolObj = json.getAsJsonObject("school")
                    val subjectObj = json.getAsJsonObject("teachSubject")
                    val classObj = json.getAsJsonObject("teachSclass")
                    val user = SessionUser(
                        id = json.get("_id")?.asString ?: "",
                        name = json.get("name")?.asString ?: "",
                        email = json.get("email")?.asString ?: "",
                        role = "Teacher",
                        schoolId = schoolObj?.get("_id")?.asString ?: "",
                        schoolName = schoolObj?.get("schoolName")?.asString ?: "",
                        classId = classObj?.get("_id")?.asString ?: "",
                        className = classObj?.get("sclassName")?.asString ?: "",
                        subjectId = subjectObj?.get("_id")?.asString ?: "",
                        subjectName = subjectObj?.get("subName")?.asString ?: ""
                    )
                    setCurrentUser(user)
                    Result.success(user)
                }
            } else {
                Result.failure(Exception("Teacher login failed: ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun studentLogin(studentName: String, rollNum: Int, pass: String): Result<SessionUser> {
        return try {
            val res = api.studentLogin(
                mapOf(
                    "studentName" to studentName,
                    "rollNum" to rollNum,
                    "password" to pass
                )
            )
            if (res.isSuccessful && res.body() != null) {
                val json = res.body()!!
                if (json.has("message")) {
                    Result.failure(Exception(json.get("message").asString))
                } else {
                    val schoolObj = json.getAsJsonObject("school")
                    val classObj = json.getAsJsonObject("sclassName")
                    val user = SessionUser(
                        id = json.get("_id")?.asString ?: "",
                        name = json.get("name")?.asString ?: "",
                        role = "Student",
                        rollNum = json.get("rollNum")?.asInt ?: rollNum,
                        schoolId = schoolObj?.get("_id")?.asString ?: "",
                        schoolName = schoolObj?.get("schoolName")?.asString ?: "",
                        classId = classObj?.get("_id")?.asString ?: "",
                        className = classObj?.get("sclassName")?.asString ?: ""
                    )
                    setCurrentUser(user)
                    Result.success(user)
                }
            } else {
                Result.failure(Exception("Student login failed: ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Classes ---
    suspend fun getClasses(adminId: String): List<Sclass> {
        val res = api.getSclassList(adminId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun createClass(name: String, adminId: String): Result<String> {
        return try {
            val res = api.createSclass(mapOf("sclassName" to name, "adminID" to adminId))
            if (res.isSuccessful) {
                val msg = res.body()?.get("message")?.asString
                if (msg != null && msg.contains("already exists", ignoreCase = true)) {
                    Result.failure(Exception(msg))
                } else {
                    Result.success("Class created successfully")
                }
            } else Result.failure(Exception("Failed: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteClass(classId: String): Result<Unit> {
        return try {
            val res = api.deleteSclass(classId)
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Delete failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClassStudents(classId: String): List<Student> {
        val res = api.getSclassStudents(classId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    // --- Subjects ---
    suspend fun getAllSubjects(adminId: String): List<Subject> {
        val res = api.getAllSubjects(adminId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun getClassSubjects(classId: String): List<Subject> {
        val res = api.getClassSubjects(classId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun createSubject(subName: String, subCode: String, sessions: String, classId: String, adminId: String): Result<String> {
        return try {
            val res = api.createSubject(
                mapOf(
                    "subjects" to listOf(
                        mapOf(
                            "subName" to subName,
                            "subCode" to subCode,
                            "sessions" to sessions
                        )
                    ),
                    "sclassName" to classId,
                    "adminID" to adminId
                )
            )
            if (res.isSuccessful) Result.success("Subject created successfully")
            else Result.failure(Exception("Failed: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSubject(subjectId: String): Result<Unit> {
        return try {
            val res = api.deleteSubject(subjectId)
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Teachers ---
    suspend fun getTeachers(adminId: String): List<Teacher> {
        val res = api.getTeachers(adminId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun registerTeacher(
        name: String,
        email: String,
        pass: String,
        teachSubject: String,
        teachSclass: String,
        schoolId: String
    ): Result<String> {
        return try {
            val res = api.teacherRegister(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to pass,
                    "role" to "Teacher",
                    "school" to schoolId,
                    "teachSubject" to teachSubject,
                    "teachSclass" to teachSclass
                )
            )
            if (res.isSuccessful && res.body() != null) {
                val json = res.body()!!
                if (json.has("message")) Result.failure(Exception(json.get("message").asString))
                else Result.success("Teacher registered successfully")
            } else Result.failure(Exception("Failed: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTeacher(teacherId: String): Result<Unit> {
        return try {
            val res = api.deleteTeacher(teacherId)
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Students ---
    suspend fun getStudents(adminId: String): List<Student> {
        val res = api.getStudents(adminId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun getStudentDetail(studentId: String): JsonObject? {
        val res = api.getStudentDetail(studentId)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun registerStudent(
        name: String,
        rollNum: Int,
        pass: String,
        sclassName: String,
        adminId: String
    ): Result<String> {
        return try {
            val res = api.studentRegister(
                mapOf(
                    "name" to name,
                    "rollNum" to rollNum,
                    "password" to pass,
                    "sclassName" to sclassName,
                    "adminID" to adminId,
                    "role" to "Student"
                )
            )
            if (res.isSuccessful && res.body() != null) {
                val json = res.body()!!
                if (json.has("message")) Result.failure(Exception(json.get("message").asString))
                else Result.success("Student added successfully")
            } else Result.failure(Exception("Failed: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(studentId: String): Result<Unit> {
        return try {
            val res = api.deleteStudent(studentId)
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markStudentAttendance(
        studentId: String,
        subId: String,
        status: String,
        date: String
    ): Result<Unit> {
        return try {
            val res = api.studentAttendance(
                studentId,
                mapOf("subName" to subId, "status" to status, "date" to date)
            )
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markBulkAttendance(
        subId: String,
        status: String,
        date: String,
        students: List<String>
    ): Result<Unit> {
        return try {
            val res = api.bulkStudentAttendance(
                mapOf(
                    "subName" to subId,
                    "status" to status,
                    "date" to date,
                    "students" to students
                )
            )
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExamResult(studentId: String, subId: String, marks: Double): Result<Unit> {
        return try {
            val res = api.updateExamResult(
                studentId,
                mapOf("subName" to subId, "marksObtained" to marks)
            )
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Notices ---
    suspend fun getNotices(adminId: String): List<Notice> {
        val res = api.getNoticeList(adminId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun createNotice(title: String, details: String, date: String, adminId: String): Result<String> {
        return try {
            val res = api.createNotice(
                mapOf(
                    "title" to title,
                    "details" to details,
                    "date" to date,
                    "adminID" to adminId
                )
            )
            if (res.isSuccessful) Result.success("Notice published successfully")
            else Result.failure(Exception("Failed: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotice(noticeId: String): Result<Unit> {
        return try {
            val res = api.deleteNotice(noticeId)
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Complaints ---
    suspend fun getComplaints(adminId: String): List<Complain> {
        val res = api.getComplainList(adminId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun createComplaint(user: String, date: String, complaint: String, schoolId: String): Result<String> {
        return try {
            val res = api.createComplain(
                mapOf(
                    "user" to user,
                    "date" to date,
                    "complaint" to complaint,
                    "school" to schoolId
                )
            )
            if (res.isSuccessful) Result.success("Complaint submitted successfully")
            else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Lab Groups ---
    suspend fun getLabGroups(classId: String): List<LabGroup> {
        val res = api.getLabGroupsByClass(classId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun createLabGroup(groupName: String, classId: String, adminId: String): Result<String> {
        return try {
            val res = api.createLabGroup(
                mapOf(
                    "groupName" to groupName,
                    "sclassName" to classId,
                    "adminID" to adminId
                )
            )
            if (res.isSuccessful) Result.success("Lab Group created")
            else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignStudentToLabGroup(groupId: String, studentId: String): Result<Unit> {
        return try {
            val res = api.assignStudentToLabGroup(
                mapOf("groupId" to groupId, "studentId" to studentId)
            )
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Quizzes ---
    suspend fun getQuizzes(classId: String): List<Quiz> {
        val res = api.getQuizzesByClass(classId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun getQuizDetails(quizId: String): Quiz? {
        val res = api.getQuizDetails(quizId)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun createQuiz(
        title: String,
        classId: String,
        subjectId: String,
        teacherId: String,
        schoolId: String,
        durationMinutes: Int,
        questions: List<QuizQuestion>
    ): Result<String> {
        return try {
            val res = api.createQuiz(
                mapOf(
                    "title" to title,
                    "sclassName" to classId,
                    "subject" to subjectId,
                    "teacher" to teacherId,
                    "school" to schoolId,
                    "durationMinutes" to durationMinutes,
                    "questions" to questions
                )
            )
            if (res.isSuccessful) Result.success("Quiz created successfully")
            else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQuiz(req: QuizSubmitRequest): Result<QuizResultResponse> {
        return try {
            val res = api.submitQuiz(req)
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(Exception("Failed to submit quiz"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateQuizAI(topic: String, count: Int, notes: String): Result<List<QuizQuestion>> {
        return try {
            val res = api.generateQuizAI(
                AIQuizGenerateRequest(topic = topic, numQuestions = count, referenceNotes = notes)
            )
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(Exception("AI Generation failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Assignments ---
    suspend fun getAssignments(classId: String): List<Assignment> {
        val res = api.getAssignmentsByClass(classId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun createAssignment(
        title: String,
        description: String,
        deadline: String,
        classId: String,
        subjectId: String,
        teacherId: String,
        schoolId: String
    ): Result<String> {
        return try {
            val res = api.createAssignment(
                mapOf(
                    "title" to title,
                    "description" to description,
                    "deadline" to deadline,
                    "sclassName" to classId,
                    "subject" to subjectId,
                    "teacher" to teacherId,
                    "school" to schoolId
                )
            )
            if (res.isSuccessful) Result.success("Assignment published")
            else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitAssignment(assignmentId: String, studentId: String, content: String): Result<String> {
        return try {
            val res = api.submitAssignment(
                mapOf(
                    "assignment" to assignmentId,
                    "student" to studentId,
                    "content" to content
                )
            )
            if (res.isSuccessful) Result.success("Assignment submitted successfully")
            else Result.failure(Exception("Failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAssignmentSubmissions(assignmentId: String): List<AssignmentSubmission> {
        val res = api.getSubmissionsByAssignment(assignmentId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    // --- RAG (Retrieval-Augmented Generation) & AI Campus Tutor ---
    suspend fun uploadStudyMaterial(
        title: String,
        content: String,
        classId: String,
        subjectId: String?,
        schoolId: String,
        uploadedBy: String
    ): Result<String> {
        return try {
            val body = mutableMapOf<String, Any>(
                "title" to title,
                "content" to content,
                "sclass" to classId,
                "school" to schoolId,
                "uploadedBy" to uploadedBy
            )
            if (!subjectId.isNullOrBlank()) body["subject"] = subjectId
            val res = api.uploadStudyMaterial(body)
            if (res.isSuccessful) Result.success("Study material indexed into RAG Knowledge Base")
            else Result.failure(Exception("Failed to upload study material"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudyMaterials(classId: String): List<StudyMaterial> {
        return try {
            val res = api.getStudyMaterials(classId)
            if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun askCampusTutor(classId: String, query: String, studentName: String): Result<RagAskResponse> {
        return try {
            val res = api.askCampusTutor(RagAskRequest(classId = classId, query = query, studentName = studentName))
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Tutor query failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateQuizFromRAG(
        classId: String,
        materialId: String? = null,
        topic: String? = null,
        numQuestions: Int = 5,
        difficulty: String = "Medium"
    ): Result<RagQuizGenerateResponse> {
        return try {
            val req = RagQuizGenerateRequest(
                classId = classId,
                materialId = if (materialId.isNullOrBlank()) null else materialId,
                topic = if (topic.isNullOrBlank()) null else topic,
                numQuestions = numQuestions,
                difficulty = difficulty
            )
            val res = api.generateQuizFromRAG(req)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Failed to generate quiz from RAG: ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun askCampusPolicyHelpdesk(
        schoolId: String,
        query: String,
        role: String = "Student",
        userName: String = ""
    ): Result<RagPolicyAskResponse> {
        return try {
            val req = RagPolicyAskRequest(
                schoolId = schoolId,
                query = query,
                role = role,
                userName = userName
            )
            val res = api.askCampusPolicyHelpdesk(req)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Helpdesk query failed: ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Feature 4: Automated Assignment Assessment & Rubrics
    suspend fun assessAssignment(req: AssessAssignmentRequest): Result<AssessAssignmentResponse> {
        return try {
            val res = api.assessAssignment(req)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Assessment failed: ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveAssignmentGrade(
        submissionId: String,
        grade: Int,
        feedback: String = "",
        rubricBreakdown: List<RubricCriterion> = emptyList()
    ): Result<Boolean> {
        return try {
            val res = api.saveAssignmentGrade(SaveGradeRequest(submissionId, grade, feedback, rubricBreakdown))
            if (res.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to save grade: ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


