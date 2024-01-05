package com.jk.share

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.play.core.integrity.p
import com.google.firebase.auth.FirebaseAuth
import com.jk.share.databinding.ActivityProfileBinding
import com.jk.share.models.Expense
import com.jk.share.models.User
import com.jk.share.repositories.UserRepository

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var userRepository: UserRepository
    private lateinit var sharedPrefs: SharedPreferences

    private var TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize
        sharedPrefs = this.getSharedPreferences(packageName, Context.MODE_PRIVATE)
        this.userRepository = UserRepository(applicationContext)

        if (sharedPrefs.contains("USER_EMAIL")){
            userRepository.getUser(sharedPrefs.getString("USER_EMAIL", "NA").toString())
        }

        // handle btn clicks
        binding.btnProfileUpdate.setOnClickListener {

            Log.d("TAG", "onProfileUpdate: Profile is updated")
            // getting user info
            val userEmail = binding.tvProfileEmail.text.toString()
            val userName = binding.etProfileName.text.toString()
            val userPwd = binding.etProfilePassword.text.toString()
            val userPhone = binding.etProfilePhone.text.toString()

            // update info
            val userToUpdate = User(
                email = userEmail,
                name = userName,
                password = userPwd,
                phoneNumber = userPhone
            )

            Log.d(TAG, "User update data: $userToUpdate")

            this.userRepository.updateUserProfile(userToUpdate)

            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        // getting user info
        userRepository.currentUser.observe(this) { user ->
            if (user != null) {
                this.binding.tvProfileEmail.text = user.email
                this.binding.etProfileName.setText(user.name)
                this.binding.etProfilePassword.setText(user.password)
                this.binding.etProfilePhone.setText(user.phoneNumber)
            }
        }
    }


}