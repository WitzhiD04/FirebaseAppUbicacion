package com.example.tallerfirebase.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tallerfirebase.modelo.AuthState
import com.example.tallerfirebase.ui.MainViewModel
import com.example.tallerfirebase.ui.screens.profile.EditProfileScreen
import com.example.tallerfirebase.ui.screens.profile.ProfileScreen
import com.example.tallerfirebase.ui.screens.login.LoginScreen
import com.example.tallerfirebase.ui.screens.map.MapScreen
import com.example.tallerfirebase.ui.screens.login.RegisterScreen

@Composable
fun NavigationStack(
    controladorNav: NavHostController,
    pantallaInicial: String = Screen.InicioSesion.ruta
) {

    val mainViewModel: MainViewModel = viewModel()
    val userData by mainViewModel.userData
    var ruta by remember{mutableStateOf(pantallaInicial)}

    LaunchedEffect(mainViewModel.authState.value) {
        val auth = mainViewModel.authState.value
        if (auth is AuthState.autenticado) {
            ruta = Screen.Mapa.ruta
            controladorNav.navigate(Screen.Mapa.ruta) {
                popUpTo(0)
            }
        } else if (auth is AuthState.noAutenticado) {
            ruta = Screen.InicioSesion.ruta
            val destinoActual = controladorNav.currentDestination?.route
            if (destinoActual != Screen.InicioSesion.ruta &&
                destinoActual != Screen.Registro.ruta) {
                controladorNav.navigate(Screen.InicioSesion.ruta) {
                    popUpTo(0)
                }
            }
        }
    }

    NavHost(
        navController = controladorNav,
        startDestination = ruta,
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
        composable(Screen.InicioSesion.ruta) {
            LoginScreen(
                onClickLogin = { mail, contra -> mainViewModel.login(mail, contra) },
                alIrARegistro = {
                    controladorNav.navigate(Screen.Registro.ruta)
                },
                authState = mainViewModel.authState.value,
            )
        }

        composable(Screen.Registro.ruta) {
            RegisterScreen(
                onClickRegister = { nombre, mail, contra, identificacion, telefono, fotoUri, context  ->
                    mainViewModel.registrar(nombre, mail, contra, identificacion, telefono, fotoUri, context)
                },
                alVolverAtras = {
                    controladorNav.popBackStack()
                },
                authState = mainViewModel.authState.value
            )
        }

        // Pantalla del Mapa
        composable(Screen.Mapa.ruta) {
            MapScreen(
                alEditarPerfil = {
                    controladorNav.navigate(Screen.Perfil.ruta)
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

        composable(Screen.Perfil.ruta) {
            ProfileScreen(
                alEditarPerfil = {
                    controladorNav.navigate(Screen.EditarPerfil.ruta)
                },
                alVolverAtras = {
                    controladorNav.popBackStack()
                }
            )
        }

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
