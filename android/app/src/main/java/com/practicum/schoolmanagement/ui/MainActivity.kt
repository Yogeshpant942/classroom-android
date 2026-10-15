package com.practicum.schoolmanagement.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.schoolmanagement.ui.admin.AdminDashboardScreen
import com.practicum.schoolmanagement.ui.admin.AdminViewModel
import com.practicum.schoolmanagement.ui.auth.*
import com.practicum.schoolmanagement.ui.navigation.Screen
import com.practicum.schoolmanagement.ui.student.StudentDashboardScreen
import com.practicum.schoolmanagement.ui.student.StudentViewModel
import com.practicum.schoolmanagement.ui.teacher.TeacherDashboardScreen
import com.practicum.schoolmanagement.ui.teacher.TeacherViewModel
import com.practicum.schoolmanagement.ui.theme.SchoolManagementTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()
    private val teacherViewModel: TeacherViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SchoolNavHost(
                        authViewModel = authViewModel,
                        adminViewModel = adminViewModel,
                        teacherViewModel = teacherViewModel,
                        studentViewModel = studentViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SchoolNavHost(
    authViewModel: AuthViewModel,
    adminViewModel: AdminViewModel,
    teacherViewModel: TeacherViewModel,
    studentViewModel: StudentViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ChooseUser.route
    ) {
        composable(Screen.ChooseUser.route) {
            ChooseUserScreen(
                onNavigateToLogin = { role ->
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.createRoute(role))
                },
                onGuestLogin = { role ->
                    authViewModel.guestLogin(role)
                    when (role) {
                        "Admin" -> navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.ChooseUser.route) { inclusive = true }
                        }
                        "Teacher" -> navController.navigate(Screen.TeacherDashboard.route) {
                            popUpTo(Screen.ChooseUser.route) { inclusive = true }
                        }
                        else -> navController.navigate(Screen.StudentDashboard.route) {
                            popUpTo(Screen.ChooseUser.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Student"
            LoginScreen(
                role = role,
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate(Screen.AdminRegister.route)
                },
                onLoginSuccess = { userRole ->
                    when (userRole) {
                        "Admin" -> navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.ChooseUser.route) { inclusive = true }
                        }
                        "Teacher" -> navController.navigate(Screen.TeacherDashboard.route) {
                            popUpTo(Screen.ChooseUser.route) { inclusive = true }
                        }
                        else -> navController.navigate(Screen.StudentDashboard.route) {
                            popUpTo(Screen.ChooseUser.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.AdminRegister.route) {
            AdminRegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.ChooseUser.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onLogout = {
                    navController.navigate(Screen.ChooseUser.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TeacherDashboard.route) {
            TeacherDashboardScreen(
                viewModel = teacherViewModel,
                onLogout = {
                    navController.navigate(Screen.ChooseUser.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                viewModel = studentViewModel,
                onLogout = {
                    navController.navigate(Screen.ChooseUser.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
