const router = require('express').Router();

// const { adminRegister, adminLogIn, deleteAdmin, getAdminDetail, updateAdmin } = require('../controllers/admin-controller.js');

const { adminRegister, adminLogIn, getAdminDetail} = require('../controllers/admin-controller.js');

const { sclassCreate, sclassList, deleteSclass, deleteSclasses, getSclassDetail, getSclassStudents } = require('../controllers/class-controller.js');
const { complainCreate, complainList } = require('../controllers/complain-controller.js');
const { noticeCreate, noticeList, deleteNotices, deleteNotice, updateNotice } = require('../controllers/notice-controller.js');
const {
    studentRegister,
    studentLogIn,
    getStudents,
    getStudentDetail,
    deleteStudents,
    deleteStudent,
    updateStudent,
    studentAttendance,
    deleteStudentsByClass,
    updateExamResult,
    clearAllStudentsAttendanceBySubject,
    clearAllStudentsAttendance,
    removeStudentAttendanceBySubject,
    removeStudentAttendance,
    bulkStudentAttendance } = require('../controllers/student_controller.js');
const { subjectCreate, classSubjects, deleteSubjectsByClass, getSubjectDetail, deleteSubject, freeSubjectList, allSubjects, deleteSubjects } = require('../controllers/subject-controller.js');
const { teacherRegister, teacherLogIn, getTeachers, getTeacherDetail, deleteTeachers, deleteTeachersByClass, deleteTeacher, updateTeacherSubject, teacherAttendance } = require('../controllers/teacher-controller.js');

// Admin
router.post('/AdminReg', adminRegister);
router.post('/AdminLogin', adminLogIn);

router.get("/Admin/:id", getAdminDetail)
// router.delete("/Admin/:id", deleteAdmin)

// router.put("/Admin/:id", updateAdmin)

// Student

router.post('/StudentReg', studentRegister);
router.post('/StudentLogin', studentLogIn)

router.get("/Students/:id", getStudents)
router.get("/Student/:id", getStudentDetail)

router.delete("/Students/:id", deleteStudents)
router.delete("/StudentsClass/:id", deleteStudentsByClass)
router.delete("/Student/:id", deleteStudent)

router.put("/Student/:id", updateStudent)

router.put('/UpdateExamResult/:id', updateExamResult)

router.put('/StudentAttendance/:id', studentAttendance)

router.put('/RemoveAllStudentsSubAtten/:id', clearAllStudentsAttendanceBySubject);
router.put('/RemoveAllStudentsAtten/:id', clearAllStudentsAttendance);

router.put('/RemoveStudentSubAtten/:id', removeStudentAttendanceBySubject);
router.put('/RemoveStudentAtten/:id', removeStudentAttendance)

// Teacher

router.post('/TeacherReg', teacherRegister);
router.post('/TeacherLogin', teacherLogIn)

router.get("/Teachers/:id", getTeachers)
router.get("/Teacher/:id", getTeacherDetail)

router.delete("/Teachers/:id", deleteTeachers)
router.delete("/TeachersClass/:id", deleteTeachersByClass)
router.delete("/Teacher/:id", deleteTeacher)

router.put("/TeacherSubject", updateTeacherSubject)

router.post('/TeacherAttendance/:id', teacherAttendance)

// Notice

router.post('/NoticeCreate', noticeCreate);

router.get('/NoticeList/:id', noticeList);

router.delete("/Notices/:id", deleteNotices)
router.delete("/Notice/:id", deleteNotice)

router.put("/Notice/:id", updateNotice)

// Complain

router.post('/ComplainCreate', complainCreate);

router.get('/ComplainList/:id', complainList);

// Sclass

router.post('/SclassCreate', sclassCreate);

router.get('/SclassList/:id', sclassList);
router.get("/Sclass/:id", getSclassDetail)

router.get("/Sclass/Students/:id", getSclassStudents)

router.delete("/Sclasses/:id", deleteSclasses)
router.delete("/Sclass/:id", deleteSclass)

// Subject

router.post('/SubjectCreate', subjectCreate);

router.get('/AllSubjects/:id', allSubjects);
router.get('/ClassSubjects/:id', classSubjects);
router.get('/FreeSubjectList/:id', freeSubjectList);
router.get("/Subject/:id", getSubjectDetail)

router.delete("/Subject/:id", deleteSubject)
router.delete("/Subjects/:id", deleteSubjects)
router.delete("/SubjectsClass/:id", deleteSubjectsByClass)

// Bulk Attendance
router.post('/BulkStudentAttendance', bulkStudentAttendance)

// Quiz
const { createQuiz, getQuizzesByClass, getQuizDetails, submitQuiz, generateQuestionsWithAI } = require('../controllers/quiz-controller.js');
router.post('/QuizCreate', createQuiz);
router.get('/QuizzesClass/:id', getQuizzesByClass);
router.get('/Quiz/:id', getQuizDetails);
router.post('/QuizSubmit', submitQuiz);
router.post('/QuizGenerateAI', generateQuestionsWithAI);

// Lab Group
const { createLabGroup, getLabGroupsByClass, assignStudentToLabGroup } = require('../controllers/labGroup-controller.js');
router.post('/LabGroupCreate', createLabGroup);
router.get('/LabGroupsClass/:id', getLabGroupsByClass);
router.post('/LabGroupAssign', assignStudentToLabGroup);

// Assignment
const { createAssignment, getAssignmentsByClass, submitAssignment, getSubmissionsByAssignment } = require('../controllers/assignment-controller.js');
router.post('/AssignmentCreate', createAssignment);
router.get('/AssignmentsClass/:id', getAssignmentsByClass);
router.post('/AssignmentSubmit', submitAssignment);
router.get('/AssignmentSubmissions/:id', getSubmissionsByAssignment);

// RAG - AI Campus Tutor & Knowledge Base
const {
    uploadStudyMaterial,
    getStudyMaterialsByClass,
    deleteStudyMaterial,
    askCampusTutor,
    generateQuizFromRAG,
    askCampusPolicyHelpdesk,
    assessAssignmentSubmission,
    saveAssignmentGrade
} = require('../controllers/rag-controller.js');
router.post('/api/rag/upload-material', uploadStudyMaterial);
router.get('/api/rag/materials/:classId', getStudyMaterialsByClass);
router.delete('/api/rag/material/:id', deleteStudyMaterial);
router.post('/api/rag/ask-tutor', askCampusTutor);
router.post('/api/rag/generate-quiz', generateQuizFromRAG);
router.post('/api/rag/ask-policy', askCampusPolicyHelpdesk);
router.post('/api/rag/assess-assignment', assessAssignmentSubmission);
router.post('/api/assignment/save-grade', saveAssignmentGrade);

module.exports = router;