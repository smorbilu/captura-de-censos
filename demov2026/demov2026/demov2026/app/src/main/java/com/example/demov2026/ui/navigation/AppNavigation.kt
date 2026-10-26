package com.example.demov2026.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.demov2026.data.SessionManager
import com.example.demov2026.model.Roles
import com.example.demov2026.model.SesionUsuario
import com.example.demov2026.ui.components.map.MapScreen
import com.example.demov2026.ui.theme.Censo.CensoScreen
import com.example.demov2026.ui.theme.Login.LoginScreen
import com.example.demov2026.ui.theme.Login.LoginViewModel
import com.example.demov2026.ui.theme.Profile.ProfileScreen
import com.example.demov2026.ui.theme.Register.RegisterScreen

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile/{nombre}/{apellido}/{usuario}?telefono={telefono}&rol={rol}"
    const val MAP = "map"
    const val CENSO = "censo"

    fun profileRoute(sesion: SesionUsuario): String {
        return "profile/${Uri.encode(sesion.nombre)}/${Uri.encode(sesion.apellido)}/${Uri.encode(sesion.usuario)}" +
            "?telefono=${Uri.encode(sesion.telefono)}&rol=${Uri.encode(sesion.rol)}"
    }
}

@Composable
fun AppNavigator(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        modifier = modifier
    ) {
        composable(AppRoutes.LOGIN) {
            val viewModel: LoginViewModel = viewModel()
            LoginScreen(
                loginViewModel = viewModel,
                onLoginExitoso = { sesion ->
                    navController.navigate(AppRoutes.profileRoute(sesion)) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegistroClic = {
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onVolverLogin = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.REGISTER) { inclusive = true }
                    }
                },
                onRegistroExitoso = { sesion ->
                    navController.navigate(AppRoutes.profileRoute(sesion)) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.PROFILE) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val apellido = backStackEntry.arguments?.getString("apellido") ?: ""
            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            val telefono = backStackEntry.arguments?.getString("telefono") ?: ""
            val rol = backStackEntry.arguments?.getString("rol").orEmpty().ifBlank { Roles.CENSISTA }

            ProfileScreen(
                nombre = nombre,
                apellido = apellido,
                usuario = usuario,
                telefono = telefono,
                rol = rol,
                onLogoutClick = {
                    SessionManager.cerrar()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onMapaClick = {
                    navController.navigate(AppRoutes.MAP)
                },
                onCensoClick = {
                    navController.navigate(AppRoutes.CENSO)
                }
            )
        }

        composable(AppRoutes.MAP) {
            MapScreen(onVolver = { navController.popBackStack() })
        }

        composable(AppRoutes.CENSO) {
            CensoScreen(onVolver = { navController.popBackStack() })
        }
    }
}
