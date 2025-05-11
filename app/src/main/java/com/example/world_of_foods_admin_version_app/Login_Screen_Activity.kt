package com.example.world_of_foods_admin_version_app

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.world_of_foods_admin_version_app.databinding.ActivityLoginScreenBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Login_Screen_Activity : AppCompatActivity() {

    //All Variables for Login/SignIn
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authenticaton: FirebaseAuth
    private lateinit var password : String
    private lateinit var email : String
    private lateinit var callbackManager: CallbackManager

    //Enable Binding
    private val binding: ActivityLoginScreenBinding by lazy {
        ActivityLoginScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Firebase Authentication Variable
        authenticaton = Firebase.auth


        //FaceBook Variables and FaceBook SignIn Listener
        generateKeyHash()
        callbackManager = CallbackManager.Factory.create()

        binding.FacebookBtn.setReadPermissions()
        binding.FacebookBtn.setOnClickListener {
            FacebookSignIn()
        }

        //Normal Login Button
        binding.LoginBtn.setOnClickListener {
            email = binding.LoginEmail.text.toString().trim()
            password = binding.LoginPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill both email and password , before proceeding to SignIn",Toast.LENGTH_LONG).show()
            }
            else{
                authenticate_credentials(email,password)
            }
        }

        //Going to SignUp Activity
        binding.GoToSignupBtn.setOnClickListener {
            val intent = Intent(this, SignUp_Screen_Activity::class.java)
            startActivity(intent)
        }

        //Google SignIn Variables and Google SignIn Listener
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("324490310125-5pkbo2a9damri0pkjt65rvm7cahgsd34.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.GoogleBtn.setOnClickListener {
            val SignInClient = googleSignInClient.signInIntent
            launcher.launch(SignInClient)
        }
    }


    //FaceBook SignIn Functions
    private fun FacebookSignIn() {
        binding.FacebookBtn.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                handeleFacebookAccessToken(result!!.accessToken)
            }
            override fun onError(error: FacebookException) {
            }
            override fun onCancel() {
            }
        })

    }

    private fun handeleFacebookAccessToken(accessToken: AccessToken) {
        //get the credentials
        val credentials = FacebookAuthProvider.getCredential(accessToken!!.token)
        authenticaton!!.signInWithCredential(credentials)
            .addOnSuccessListener {
                //get email
                result ->
                val email = result.user?.email
                val user = authenticaton.currentUser!!
                Toast.makeText(this,"You logged in with Facebook Account Sucessfully",Toast.LENGTH_LONG).show()
                updateUi(user)
        }
            .addOnFailureListener {e ->
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateKeyHash(){
        try {
            val info = packageManager.getPackageInfo("com.example.world_of_foods_admin_version_app",PackageManager.GET_SIGNATURES)
            for (signature in info.signatures){
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEYLASH", Base64.encodeToString(md.digest(),Base64.DEFAULT))
            }
        }catch (e:PackageManager.NameNotFoundException){

        }catch (e:NoSuchAlgorithmException){

        }
    }


    //Normal LoginIn Functions
    private fun authenticate_credentials(email: String, password: String) {
        authenticaton.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Login Successfull",Toast.LENGTH_SHORT).show()
                val user = authenticaton.currentUser!!
                updateUi(user)
            }else {
                Toast.makeText(this,"Account Doesn't exist. Kindly SignUp or use Google/Facebook Account",Toast.LENGTH_LONG).show()
            }
        }
    }

    //Google SignIn Variables/Functions
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if (task.isSuccessful) {

                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                    authenticaton.signInWithCredential(credential)
                        .addOnCompleteListener { authtask ->
                            if (authtask.isSuccessful) {
                                Toast.makeText(this, "Google SignIn Sucecssfull!", Toast.LENGTH_LONG).show()
                                updateUi(authtask.result?.user)
                                finish()
                            } else {
                                Toast.makeText(
                                    this, "Google SignIn Authentication Failed", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Google SignIn Account Data Retrival Failed", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Google SignIn Failed", Toast.LENGTH_LONG).show()
            }
        }


    //Start Up Actions/Methods
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = authenticaton.currentUser
        if (currentUser != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
