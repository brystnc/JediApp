package acme.appandroid.jediapp.ui.screens

import acme.appandroid.jediapp.R
import acme.appandroid.jediapp.databinding.ActivityMainBinding
import acme.appandroid.jediapp.ui.login.LoginActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init Firabase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout user
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

    }

    private fun checkUser() {
        // get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            // user not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // user logged in
            // get user info
            val email = firebaseUser.email
            //set email
            binding.tvEmail.text = email
        }
    }
}