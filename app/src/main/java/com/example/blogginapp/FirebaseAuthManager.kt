import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Get the current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Sign out the current user
    fun signOut() {
        auth.signOut()
    }

    // Register a new user
    fun registerUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    onFailure.invoke("User registration failed: ${task.exception?.message}")
                    Log.e("FirebaseAuthManager", "User registration failed", task.exception)
                }
            }
    }

    // Login an existing user
    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    onFailure.invoke("Login failed: ${task.exception?.message}")
                    Log.e("FirebaseAuthManager", "Login failed", task.exception)
                }
            }
    }
}
