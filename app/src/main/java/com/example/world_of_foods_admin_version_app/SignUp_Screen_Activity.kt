package com.example.world_of_foods_admin_version_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.world_of_foods_admin_version_app.databinding.ActivitySignUpScreenBinding
import com.example.world_of_foods_admin_version_app.modal.UserModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUp_Screen_Activity : AppCompatActivity() {

    private lateinit var authenticate : FirebaseAuth
    private lateinit var Owner_Name : String
    private lateinit var Restaurant_Name : String
    private lateinit var SignupEmail : String
    private lateinit var signupPassword : String
    private lateinit var database : DatabaseReference

    private val binding : ActivitySignUpScreenBinding by lazy {
        ActivitySignUpScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authenticate = Firebase.auth
        database = Firebase.database.reference

        binding.GoToLoginBtn.setOnClickListener({
            val intent = Intent(this,Login_Screen_Activity::class.java)
            startActivity(intent)
        })

        binding.CreateAccountBtn.setOnClickListener({

            Owner_Name = binding.SignupName.text.toString().trim()
            Restaurant_Name = binding.SignupRestraurantName.text.toString().trim()
            SignupEmail = binding.SignupEmailPhone.text.toString().trim()
            signupPassword = binding.SignupPassword.text.toString().trim()

            if (Owner_Name.isBlank() || Restaurant_Name.isBlank() || SignupEmail.isBlank() || signupPassword.isBlank()){
                Toast.makeText(this,"Please Fill All The Details", Toast.LENGTH_SHORT).show()
            }else{
                CreateAccount(SignupEmail,signupPassword)
            }
        })

        val locationlist = arrayOf("Delhi","Mumbai","Chennai","Kolkata","Jaipur","Banglore")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,locationlist)

        val autoCompleteEditText = binding.listOfLocation
        autoCompleteEditText.setAdapter(adapter)
    }

    private fun CreateAccount(signupEmail: String, signupPassword: String) {
        authenticate.createUserWithEmailAndPassword(signupEmail,signupPassword).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Account Created Sucessfully",Toast.LENGTH_SHORT).show()
                SaveDataToFirebase()
                val intent = Intent(this,Login_Screen_Activity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
                Log.d("Account","Account Creation : Failure",task.exception)
            }
        }
    }

    private fun SaveDataToFirebase() {

        //Save Data to Database
        Owner_Name = binding.SignupName.text.toString().trim()
        Restaurant_Name = binding.SignupRestraurantName.text.toString().trim()
        SignupEmail = binding.SignupEmailPhone.text.toString().trim()
        signupPassword = binding.SignupPassword.text.toString().trim()

        val User = UserModal(Owner_Name,Restaurant_Name,SignupEmail,signupPassword)
        val UserId = FirebaseAuth.getInstance().currentUser!!.uid

        //Save Data to Firebase DataBase
        database.child("User").child(UserId).setValue(User)
    }
}