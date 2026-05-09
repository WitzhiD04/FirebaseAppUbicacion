package com.example.tallerfirebase.navigation

/**
 * Rutas de navegación de la aplicación.
 * Define todas las pantallas disponibles como constantes selladas.
 */
sealed class Screen(val ruta: String) {
    /** Pantalla de inicio de sesión */
    data object InicioSesion : Screen("inicio_sesion")

    /** Pantalla de registro de nuevo usuario */
    data object Registro : Screen("registro")

    /** Pantalla principal con el mapa y switch de conexión */
    data object Mapa : Screen("mapa")

    /** Pantalla para editar el perfil del usuario */
    data object EditarPerfil : Screen("editar_perfil")
}
