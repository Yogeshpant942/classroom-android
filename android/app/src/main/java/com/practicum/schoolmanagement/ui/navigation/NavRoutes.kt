package com.practicum.schoolmanagement.ui.navigation

sealed class Screen(val route: String) {
    object ChooseUser : Screen("choose_user")
    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object AdminRegister : Screen("admin_register")
    object AdminDashboard : Screen("admin_dashboard")
    object TeacherDashboard : Screen("teacher_dashboard")
    object StudentDashboard : Screen("student_dashboard")
}
