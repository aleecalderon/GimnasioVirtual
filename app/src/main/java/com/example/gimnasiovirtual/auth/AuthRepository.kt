package com.example.gimnasiovirtual.auth

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun currentUser(): FirebaseUser? = auth.currentUser

    fun signIn(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) onResult(Result.success(user))
                else onResult(Result.failure(IllegalStateException("No se pudo identificar al usuario.")))
            }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    onResult(Result.failure(IllegalStateException("No se pudo crear el usuario.")))
                    return@addOnSuccessListener
                }

                val profile = hashMapOf(
                    "uid" to user.uid,
                    "name" to name.trim(),
                    "email" to (user.email ?: email.trim()),
                    "createdAt" to Timestamp.now()
                )

                firestore.collection("users").document(user.uid).set(profile)
                    .addOnSuccessListener { onResult(Result.success(user)) }
                    .addOnFailureListener { error ->
                        // Auth ya creó la cuenta. Informamos que el perfil no se guardó.
                        onResult(Result.failure(ProfileSaveException(
                            "La cuenta se creó, pero no se pudo guardar el perfil en Firestore. Revisa la conexión y las reglas de Firestore.",
                            error
                        )))
                    }
            }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun getUserProfile(onResult: (Result<Map<String, Any>>) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(Result.failure(IllegalStateException("No hay una sesión iniciada.")))
            return
        }

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(Result.success(document.data ?: emptyMap()))
                } else {
                    onResult(Result.failure(NoSuchElementException("El perfil del usuario todavía no existe en Firestore.")))
                }
            }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun signOut() {
        auth.signOut()
    }
}

class ProfileSaveException(message: String, cause: Throwable) : Exception(message, cause)
