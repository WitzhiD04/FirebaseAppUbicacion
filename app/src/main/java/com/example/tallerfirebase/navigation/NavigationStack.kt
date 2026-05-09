package com.example.tallerfirebase.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tallerfirebase.ui.screens.profile.EditProfileScreen
import com.example.tallerfirebase.ui.screens.login.LoginScreen
import com.example.tallerfirebase.ui.screens.map.MapScreen
import com.example.tallerfirebase.ui.screens.login.RegisterScreen

/**
 * Grafo de navegación principal de la aplicación.
 * Define todas las rutas y las transiciones animadas entre pantallas.
 *
 * @param controladorNav El controlador de navegación de Jetpack Compose.
 * @param pantallaInicial La pantalla que se muestra al iniciar la app.
 */
@Composable
fun NavigationStack(
    controladorNav: NavHostController,
    pantallaInicial: String = Screen.InicioSesion.ruta
) {
    NavHost(
        navController = controladorNav,
        startDestination = pantallaInicial,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        }
    ) {
        // Pantalla de Inicio de Sesión
        composable(Screen.InicioSesion.ruta) {
            LoginScreen(
                alIniciarSesion = { correo, contrasena ->
                    // TODO: Conectar con ViewModel para autenticación
                    controladorNav.navigate(Screen.Mapa.ruta) {
                        popUpTo(Screen.InicioSesion.ruta) { inclusive = true }
                    }
                },
                alIrARegistro = {
                    controladorNav.navigate(Screen.Registro.ruta)
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Registro.ruta) {
            RegisterScreen(
                alRegistrarse = { nombre, identificacion, correo, contrasena, telefono ->
                    // TODO: Conectar con ViewModel para registro
                    controladorNav.navigate(Screen.Mapa.ruta) {
                        popUpTo(Screen.InicioSesion.ruta) { inclusive = true }
                    }
                },
                alVolverAtras = {
                    controladorNav.popBackStack()
                }
            )
        }

        // Pantalla del Mapa
        composable(Screen.Mapa.ruta) {
            MapScreen(
                alEditarPerfil = {
                    controladorNav.navigate(Screen.EditarPerfil.ruta)
                },
                alCerrarSesion = {
                    // TODO: Conectar con ViewModel para cerrar sesión
                    controladorNav.navigate(Screen.InicioSesion.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                alCambiarConexion = { conectado ->
                    // TODO: Conectar con ViewModel para actualizar estado
                }
            )
        }

        // Pantalla de Editar Perfil
        composable(Screen.EditarPerfil.ruta) {
            EditProfileScreen(
                alGuardarCambios = { nombre, identificacion, telefono, contrasena ->
                    // TODO: Conectar con ViewModel para actualizar datos
                    controladorNav.popBackStack()
                },
                alVolverAtras = {
                    controladorNav.popBackStack()
                }
            )
        }
    }
}
