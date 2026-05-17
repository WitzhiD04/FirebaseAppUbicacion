package com.example.tallerfirebase.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tallerfirebase.modelo.AuthState
import com.example.tallerfirebase.modelo.OtroUser
import com.example.tallerfirebase.modelo.UserData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage

class MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = mutableStateOf<AuthState>(AuthState.cargando)
    val authState: State<AuthState> = _authState
    private val _userData = mutableStateOf<UserData?>(null)
    val userData: State<UserData?> = _userData

    private val _otrosUsuarios = mutableStateOf<List<OtroUser>>(emptyList())
    val otrosUsuarios: State<List<OtroUser>> = _otrosUsuarios

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var listenerOtros: ListenerRegistration? = null

    init {
        verificarAuth()
    }

    fun verificarAuth() {
        if (auth.currentUser != null) {
            val user = auth.currentUser
            user?.uid?.let { uid ->
                db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener { document ->
                        val data = document.toObject(UserData::class.java)
                        _userData.value = data
                        _authState.value = AuthState.autenticado
                        otrosGeoPoint()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al verificar auth", e)
                        _authState.value = AuthState.noAutenticado
                    }
            }
        } else {
            _authState.value = AuthState.noAutenticado
        }
    }

    fun login(mail: String, contrasena: String) {
        if (mail.isEmpty() || contrasena.isEmpty()) {
            _authState.value = AuthState.Error("Por favor, completa todos los campos")
            return
        }
        _authState.value = AuthState.cargando
        auth.signInWithEmailAndPassword(mail, contrasena)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        db.collection("usuarios").document(uid).get()
                            .addOnSuccessListener { document ->
                                val data = document.toObject(UserData::class.java)
                                _userData.value = data
                                _authState.value = AuthState.autenticado
                                otrosGeoPoint()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error Firestore tras login", e)
                                _authState.value = AuthState.Error("Error al obtener datos: ${e.message}")
                            }
                    }
                } else {
                    _authState.value =
                        AuthState.Error(tarea.exception?.message ?: "Algo salio mal😧")
                }
            }
    }

    fun registrar(nombre: String, mail: String, contrasena: String, identificacion: String, telefono: String,  fotoUri: Uri?, context: Context) {
        if (mail.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || identificacion.isEmpty() || telefono.isEmpty()) {
            _authState.value = AuthState.Error("Por favor, completa todos los campos")
            return
        }
        _authState.value = AuthState.cargando
        auth.createUserWithEmailAndPassword(mail, contrasena)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val nuevoUsuario = UserData(
                        uid = uid,
                        correo = mail,
                        nombre = nombre,
                        identificacion = identificacion,
                        telefono = telefono,
                        creacion = Timestamp.now()
                    )

                    db.collection("usuarios").document(uid).set(nuevoUsuario)
                        .addOnSuccessListener {
                            _userData.value = nuevoUsuario
                            _authState.value = AuthState.autenticado
                            otrosGeoPoint()
                            fotoUri?.let { uri -> subirFotoPerfil(uri, context) }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error al guardar usuario en Firestore", e)
                            _authState.value = AuthState.Error("Error al guardar: ${e.message}")
                        }
                } else {
                    _authState.value = AuthState.Error(tarea.exception?.message ?: "Algo salio mal😧")
                }
            }
    }

    fun subirFotoPerfil(fotoUri: Uri, context: Context) {
        val ref = storage.reference
        val nomImagen = "foto_${_userData.value?.uid}"
        val espacioRef = ref.child("imagenes/perfil/${nomImagen}.jpg")

        val byteArray = context.contentResolver.openInputStream(fotoUri)?.use {it.readBytes()}

        byteArray?.let { bytes ->
            espacioRef.putBytes(bytes).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    guardarFoto(uri.toString())
                    Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Falló la subida", Toast.LENGTH_SHORT).show()
                    _userData.value = _userData.value?.copy(fotoUrl = "")
                }
            }
        }
    }

    fun guardarFoto(fotoUrl: String) {
        _userData.value = _userData.value?.copy(fotoUrl = fotoUrl)
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).update("fotoUrl", fotoUrl)
        }
    }

    fun cerrar() {
        listenerOtros?.remove()
        listenerOtros = null
        _otrosUsuarios.value = emptyList()
        auth.signOut()
        _userData.value = null
        _authState.value = AuthState.noAutenticado
    }

    fun modificarNombre(nombre: String, uid: String) {
        if (nombre == _userData.value?.nombre) {
            return
        }
        db.collection("usuarios").document(uid).update("nombre", nombre)
            .addOnSuccessListener {
                _userData.value = _userData.value?.copy(nombre = nombre)
            }
    }

    fun modificarImagen(uri: Uri, uid: String, context: Context) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fotoRef = storageRef.child("imagenes/perfil/foto_${uid}.jpg")

        fotoRef.delete()
            .addOnSuccessListener {
                subirFotoPerfil(uri, context)
            }
            .addOnFailureListener {
                subirFotoPerfil(uri, context)
            }
    }

    fun modificarId(identificacion: String, uid: String) {
        if (identificacion == _userData.value?.identificacion) {
            return
        }
        db.collection("usuarios").document(uid).update("identificacion", identificacion)
            .addOnSuccessListener {
                _userData.value = _userData.value?.copy(identificacion = identificacion)
            }
    }

    fun modificarTel(telefono: String, uid: String) {
        if (telefono == _userData.value?.telefono) {
            return
        }
        db.collection("usuarios").document(uid).update("telefono", telefono)
            .addOnSuccessListener {
                _userData.value = _userData.value?.copy(telefono = telefono)
            }
    }

    fun modificarContra(contrasena: String) {
        auth.currentUser?.updatePassword(contrasena)
    }

    fun modificarDatos(nombre: String, uid: String, uri: Uri?, context: Context, identificacion: String, telefono: String, contrasena: String) {
        if (nombre.isNotEmpty()) {
            modificarNombre(nombre, uid)
        }

        if (identificacion.isNotEmpty()) {
            modificarId(identificacion, uid)
        }

        if (telefono.isNotEmpty()) {
            modificarTel(telefono, uid)
        }

        if (uri != null) {
            modificarImagen(uri, uid, context)
        }
        if (contrasena.isNotEmpty()) {
            modificarContra(contrasena)
        }
    }

    fun actualizarLngLat(latitud: Double, longitud: Double, uid: String, context: Context) {
        val geoPoint = GeoPoint(latitud, longitud)
        if (uid == _userData.value?.uid) {
            db.collection("usuarios").document(uid).update(
                "ubicacion", geoPoint,
                "historial", FieldValue.arrayUnion(geoPoint)
            ).addOnSuccessListener {
                val actual = _userData.value
                if (actual != null) {
                    _userData.value = actual.copy(
                        ubicacion = geoPoint,
                        historial = actual.historial + geoPoint
                    )
                }
            }
        } else {
            Toast.makeText(context, "No se pudo actualizar la ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    fun conectado() {
        val usuario = _userData.value ?: return
        val nuevo = !usuario.conectado

        val updates = mutableMapOf<String, Any>(
            "conectado" to nuevo
        )
        if (!nuevo) {
            updates["historial"] = emptyList<GeoPoint>()
        }

        db.collection("usuarios").document(usuario.uid)
            .update(updates)
            .addOnSuccessListener {
                val copy = usuario.copy(conectado = nuevo)
                _userData.value = if (!nuevo) copy.copy(historial = emptyList()) else copy
            }
    }

    fun otrosGeoPoint() {
        listenerOtros?.remove()
        val uidActual = auth.currentUser?.uid ?: return

        listenerOtros = db.collection("usuarios")
            .whereEqualTo("conectado", true)
            .limit(100)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Fallo en la escucha constante", e)
                    return@addSnapshotListener
                }

                val listaOtros = snapshot?.documents?.mapNotNull { doc ->
                    val user = doc.toObject(UserData::class.java)
                    if (user != null && user.uid != uidActual) {
                        OtroUser(
                            uid = user.uid,
                            nombre = user.nombre,
                            ubicacion = LatLng(
                                user.ubicacion.latitude,
                                user.ubicacion.longitude
                            ),
                            routePoints = user.historial.map { gp ->
                                LatLng(gp.latitude, gp.longitude)
                            }
                        )
                    } else null
                } ?: emptyList()

                _otrosUsuarios.value = listaOtros
            }
    }
}