package com.jk.share

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jk.share.databinding.ActivitySignInBinding
import java.io.Serializable

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.canonicalName
    private lateinit var binding: ActivitySignInBinding
    private lateinit var prefs: SharedPreferences
    // declare firebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)

        // initialize firebaseAuth
        this.firebaseAuth = FirebaseAuth.getInstance()

        prefs = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)

        if (prefs.contains("USER_EMAIL")) {
            binding.editEmail.setText(this.prefs.getString("USER_EMAIL", ""))
        }
        if (prefs.contains("USER_PASSWORD")) {
            binding.editPassword.setText(this.prefs.getString("USER_PASSWORD", ""))
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.btn_sign_in -> {
                    Log.d(TAG, "onClick: Sign In Button Clicked")
                    this.validateData()
                }
                R.id.btn_sign_up -> {
                    Log.d(TAG, "onClick: Sign Up Button Clicked")
                    val signUpIntent = Intent(this, SignUpActivity::class.java)
                    startActivity(signUpIntent)
                }
            }
        }
    }

    private fun validateData() {
        var validData = true
        var email = ""
        var password = ""
        if (binding.editEmail.text.toString().isEmpty()) {
            binding.editEmail.error = "Email Cannot be Empty"
            validData = false
        } else {
            email = binding.editEmail.text.toString()
        }
        if (binding.editPassword.text.toString().isEmpty()) {
            binding.editPassword.error = "Password Cannot be Empty"
            validData = false
        } else {
            password = binding.editPassword.text.toString()
        }
        if (validData) {
            signIn(email, password)
        } else {
            Toast.makeText(this, "Please provide correct inputs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn(email: String, password: String) {
        //signIn using FirebaseAuth
        this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "SignIn: Login successful")
                    saveToPrefs(email, password)
                    goToMain()
                } else {
                    Log.d(TAG, "SignIn: Login failed : ${task.exception}")
                    Toast.makeText(this@SignInActivity,
                        "Authentication failed. Check the credentials",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveToPrefs(email: String, password: String) {
        if (binding.swtRemember.isChecked) {
            prefs.edit().putString("USER_EMAIL", email).apply()
            prefs.edit().putString("USER_PASSWORD", password).apply()
        } else {
            if (prefs.contains("USER_EMAIL")) {
                prefs.edit().remove("USER_EMAIL").apply()
            }
            if (prefs.contains("USER_PASSWORD")) {
                prefs.edit().remove("USER_PASSWORD").apply()
            }
        }
    }

    private fun goToMain() {
//        val mainIntent = Intent(this, MainActivity::class.java)
        val mainIntent = Intent(this, HomeActivity::class.java)
        startActivity(mainIntent)
    }
}