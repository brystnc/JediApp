package acme.appandroid.jediapp.ui.login

import acme.appandroid.jediapp.R
import acme.appandroid.jediapp.ui.screens.MainActivity
import acme.appandroid.jediapp.databinding.ActivityLoginBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    /* Constants */
    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Configure the Google SignIn */
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        /* Init Firebase Auth */
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        /* Google SignIn Button, Click to begin Google SignIn*/
        binding.btnLoginGoogle.setOnClickListener {
            Log.d(TAG,"onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null) {
            //user id already logged in
            //start profile activity
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

    }

    @Deprecated("Hols")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* Result returned from launching the Intent from GoogleSign*/
        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                /* Google SignIn success, now auth with Firebase */
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin Firebase auth with Google account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                authResult ->

                // Login success
                Log.d(TAG, "firebaseAuthWithGoogleAcccount: LoggedIn")

                //get loggedIn user
                val firebaseUser = firebaseAuth.currentUser

                //get user info
                val uid = firebaseUser.uid
                val email = firebaseUser.email

                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                //check if user is new or existing
                if (authResult.additionalUserInfo!!.isNewUser) {
                    // user is new -- account created
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created .. \n$email")
                    Toast.makeText(this@LoginActivity, "Account created .. \n$email", Toast.LENGTH_SHORT).show()
                } else {
                    //existing user --LoggedIn
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user ... \n$email")
                    Toast.makeText(this@LoginActivity,"LoggedIn.. \n$email", Toast.LENGTH_SHORT).show()
                }

                //start profile activity
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener {
                e ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Loggin Failed due to ${e.message} ")
                Toast.makeText(this@LoginActivity," Loggin Failed due to ${e.message} ", Toast.LENGTH_SHORT).show()

            }

    }
}