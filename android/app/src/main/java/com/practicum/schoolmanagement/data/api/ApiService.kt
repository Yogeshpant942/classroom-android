package com.practicum.schoolmanagement.data.api

import com.google.gson.JsonObject
import com.practicum.schoolmanagement.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Admin ---
    @POST("/AdminReg")
    suspend fun adminRegister(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @POST("/AdminLogin")
    suspend fun adminLogin(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/Admin/{id}")
    suspend fun getAdminDetail(@Path("id") id: String): Response<JsonObject>

    // --- Student ---
    @POST("/StudentReg")
    suspend fun studentRegister(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @POST("/StudentLogin")
    suspend fun studentLogin(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/Students/{id}")
    suspend fun getStudents(@Path("id") adminId: String): Response<List<Student>>

    @GET("/Student/{id}")
    suspend fun getStudentDetail(@Path("id") id: String): Response<JsonObject>

    @DELETE("/Student/{id}")
    suspend fun deleteStudent(@Path("id") id: String): Response<JsonObject>

    @PUT("/StudentAttendance/{id}")
    suspend fun studentAttendance(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<JsonObject>

    @POST("/BulkStudentAttendance")
    suspend fun bulkStudentAttendance(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @PUT("/UpdateExamResult/{id}")
    suspend fun updateExamResult(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<JsonObject>

    // --- Teacher ---
    @POST("/TeacherReg")
    suspend fun teacherRegister(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @POST("/TeacherLogin")
    suspend fun teacherLogin(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/Teachers/{id}")
    suspend fun getTeachers(@Path("id") adminId: String): Response<List<Teacher>>

    @GET("/Teacher/{id}")
    suspend fun getTeacherDetail(@Path("id") id: String): Response<JsonObject>

    @DELETE("/Teacher/{id}")
    suspend fun deleteTeacher(@Path("id") id: String): Response<JsonObject>

    @PUT("/TeacherSubject")
    suspend fun updateTeacherSubject(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    // --- Class (Sclass) ---
    @POST("/SclassCreate")
    suspend fun createSclass(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/SclassList/{id}")
    suspend fun getSclassList(@Path("id") adminId: String): Response<List<Sclass>>

    @GET("/Sclass/{id}")
    suspend fun getSclassDetail(@Path("id") id: String): Response<JsonObject>

    @GET("/Sclass/Students/{id}")
    suspend fun getSclassStudents(@Path("id") classId: String): Response<List<Student>>

    @DELETE("/Sclass/{id}")
    suspend fun deleteSclass(@Path("id") id: String): Response<JsonObject>

    // --- Subject ---
    @POST("/SubjectCreate")
    suspend fun createSubject(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/AllSubjects/{id}")
    suspend fun getAllSubjects(@Path("id") adminId: String): Response<List<Subject>>

    @GET("/ClassSubjects/{id}")
    suspend fun getClassSubjects(@Path("id") classId: String): Response<List<Subject>>

    @GET("/FreeSubjectList/{id}")
    suspend fun getFreeSubjectList(@Path("id") adminId: String): Response<List<Subject>>

    @GET("/Subject/{id}")
    suspend fun getSubjectDetail(@Path("id") id: String): Response<JsonObject>

    @DELETE("/Subject/{id}")
    suspend fun deleteSubject(@Path("id") id: String): Response<JsonObject>

    // --- Notice ---
    @POST("/NoticeCreate")
    suspend fun createNotice(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/NoticeList/{id}")
    suspend fun getNoticeList(@Path("id") adminId: String): Response<List<Notice>>

    @DELETE("/Notice/{id}")
    suspend fun deleteNotice(@Path("id") id: String): Response<JsonObject>

    // --- Complain ---
    @POST("/ComplainCreate")
    suspend fun createComplain(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/ComplainList/{id}")
    suspend fun getComplainList(@Path("id") adminId: String): Response<List<Complain>>

    // --- Lab Group ---
    @POST("/LabGroupCreate")
    suspend fun createLabGroup(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/LabGroupsClass/{id}")
    suspend fun getLabGroupsByClass(@Path("id") classId: String): Response<List<LabGroup>>

    @POST("/LabGroupAssign")
    suspend fun assignStudentToLabGroup(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    // --- Quiz ---
    @POST("/QuizCreate")
    suspend fun createQuiz(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/QuizzesClass/{id}")
    suspend fun getQuizzesByClass(@Path("id") classId: String): Response<List<Quiz>>

    @GET("/Quiz/{id}")
    suspend fun getQuizDetails(@Path("id") id: String): Response<Quiz>

    @POST("/QuizSubmit")
    suspend fun submitQuiz(@Body body: QuizSubmitRequest): Response<QuizResultResponse>

    @POST("/QuizGenerateAI")
    suspend fun generateQuizAI(@Body body: AIQuizGenerateRequest): Response<List<QuizQuestion>>

    // --- Assignment ---
    @POST("/AssignmentCreate")
    suspend fun createAssignment(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/AssignmentsClass/{id}")
    suspend fun getAssignmentsByClass(@Path("id") classId: String): Response<List<Assignment>>

    @POST("/AssignmentSubmit")
    suspend fun submitAssignment(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/AssignmentSubmissions/{id}")
    suspend fun getSubmissionsByAssignment(@Path("id") assignmentId: String): Response<List<AssignmentSubmission>>

    // --- RAG (Retrieval-Augmented Generation) & AI Campus Tutor ---
    @POST("/api/rag/upload-material")
    suspend fun uploadStudyMaterial(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<JsonObject>

    @GET("/api/rag/materials/{classId}")
    suspend fun getStudyMaterials(@Path("classId") classId: String): Response<List<StudyMaterial>>

    @DELETE("/api/rag/material/{id}")
    suspend fun deleteStudyMaterial(@Path("id") id: String): Response<JsonObject>

    @POST("/api/rag/ask-tutor")
    suspend fun askCampusTutor(@Body body: RagAskRequest): Response<RagAskResponse>

    @POST("/api/rag/generate-quiz")
    suspend fun generateQuizFromRAG(@Body req: RagQuizGenerateRequest): Response<RagQuizGenerateResponse>

    // Feature 3: Campus Policy & Notice Helpdesk Bot
    @POST("/api/rag/ask-policy")
    suspend fun askCampusPolicyHelpdesk(@Body req: RagPolicyAskRequest): Response<RagPolicyAskResponse>

    // Feature 4: Automated Assignment Assessment & Rubric Matching
    @POST("/api/rag/assess-assignment")
    suspend fun assessAssignment(@Body req: AssessAssignmentRequest): Response<AssessAssignmentResponse>

    @POST("/api/assignment/save-grade")
    suspend fun saveAssignmentGrade(@Body req: SaveGradeRequest): Response<JsonObject>
}


