package com.example.healthsystem.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.healthsystem.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseUrl = "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
    val userRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("user")

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val safeEmail = email.replace(".", "_")

                        userRef.child(safeEmail).get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val user = User(
                                        email = snapshot.child("email").getValue(String::class.java) ?: "",
                                        name = snapshot.child("name").getValue(String::class.java) ?: "",
                                        phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java) ?: "",
                                        isMale = snapshot.child("male").getValue(Boolean::class.java) != false,
                                        dateOfBirth = snapshot.child("dateOfBirth").getValue(String::class.java) ?: ""
                                    )
                                    onSuccess(user)
                                } else {
                                    onError("Không tìm thấy dữ liệu người dùng trong database.")
                                }
                            }
                            .addOnFailureListener {
                                onError("Lỗi truy cập dữ liệu: ${it.message}")
                            }
                    } else {
                        onError("Không tìm thấy người dùng đã đăng nhập.")
                    }
                } else {
                    onError(task.exception?.message ?: "Lỗi không xác định khi đăng nhập.")
                }
            }
    }

    fun registerWithEmail(
        name: String,
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val user = User(
                            name = name,
                            email = email,
                        )
                        val emailKey = email.replace(".", "_") // Firebase không cho phép dấu chấm
                        val userInfo = User(name = name)
                        userRef.child(emailKey).setValue(userInfo)
                            .addOnSuccessListener {
                                onSuccess(user)
                            }
                            .addOnFailureListener {
                                onError("Lưu thông tin người dùng thất bại: ${it.message}")
                            }

                    } else {
                        onError("Không tìm thấy tài khoản")
                    }
                } else {
                    onError(task.exception?.message ?: "Lỗi không xác định")
                }
            }
    }

}
