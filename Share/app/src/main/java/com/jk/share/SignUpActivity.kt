package com.jk.share

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jk.share.databinding.ActivitySignUpBinding
import com.jk.share.models.User
import com.jk.share.repositories.UserRepository

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = this.javaClass.canonicalName
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        binding.btnCreateAccount.setOnClickListener(this)

        // initialize firebase auth
        this.firebaseAuth = FirebaseAuth.getInstance()

        this.userRepository = UserRepository(applicationContext)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.btn_create_account -> {
                    Log.d(TAG, "onClick: Create Account button Clicked")
                    validateData()
                }
            }
        }
    }

    private fun validateData() {
        var validData = true
        var name = ""
        var email = ""
        var password = ""
        var phoneNumber = ""

        if (binding.editName.getText().toString().isEmpty()) {
            binding.editName.setError("Name Cannot be Empty")
            validData = false
        } else {
            name = binding.editName.getText().toString()
        }
        if (binding.editEmail.getText().toString().isEmpty()) {
            binding.editEmail.setError("Email Cannot be Empty")
            validData = false
        } else {
            email = binding.editEmail.getText().toString()
        }
        if (binding.editPassword.getText().toString().isEmpty()) {
            binding.editPassword.setError("Password Cannot be Empty")
            validData = false
        } else {
            if (binding.editConfirmPassword.getText().toString().isEmpty()) {
                binding.editConfirmPassword.setError("Confirm Password Cannot be Empty")
                validData = false
            } else {
                if (!binding.editPassword.getText().toString()
                        .equals(binding.editConfirmPassword.getText().toString())
                ) {
                    binding.editConfirmPassword.setError("Both passwords must be same")
                    validData = false
                } else {
                    password = binding.editPassword.getText().toString()
                }
            }
        }
        if (binding.editPhone.getText().toString().isEmpty()) {
            binding.editPhone.setError("Name Cannot be Empty")
            validData = false
        } else {
            phoneNumber = binding.editPhone.getText().toString()
        }
        if (validData) {
            createAccount(name, email, password, phoneNumber)
        } else {
            Toast.makeText(this, "Please provide correct inputs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(name: String, email: String, password: String, phoneNumber: String) {
//        SignUp using FirebaseAuth

        this.firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->

                if (task.isSuccessful) {
                    // create user document with default profile info
                    val userToAdd = User(
                        id = email,
                        email = email,
                        password = password,
                        name = name,
//                      phoneNumber =  "XXX-XXX-",
                        phoneNumber = phoneNumber
                        )
                    userRepository.addUserToDB(userToAdd)

                    Log.d(TAG, "createAccount: User account successfully create with email $email")
                    saveToPrefs(email, password)
                    goToMain()
                } else {
                    Log.d(TAG, "createAccount: Unable to create user account")
                    Toast.makeText(this@SignUpActivity, "Account creation failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // saving email and password to the preferences
    private fun saveToPrefs(email: String, password: String) {
        val prefs = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)
        prefs.edit().putString("USER_EMAIL", email).apply()
        prefs.edit().putString("USER_PASSWORD", password).apply()
    }

    // going to the Main Activity
    private fun goToMain() {
//        val mainIntent = Intent(this, MainActivity::class.java)
        val mainIntent = Intent(this, HomeActivity::class.java)
        startActivity(mainIntent)
    }
}