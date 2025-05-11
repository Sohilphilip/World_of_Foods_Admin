package com.example.world_of_foods_admin_version_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.world_of_foods_admin_version_app.databinding.ActivityCreateNewUserBinding
import com.example.world_of_foods_admin_version_app.modal.UserModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Create_New_User_Activity : AppCompatActivity() {

    private lateinit var NewName : String
    private lateinit var NewEmail : String
    private lateinit var NewPassword : String
    private lateinit var Authentication : FirebaseAuth
    private lateinit var Database : DatabaseReference

    private val binding : ActivityCreateNewUserBinding by lazy {
        ActivityCreateNewUserBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Authentication = Firebase.auth
        Database = Firebase.database.reference


        binding.backBtnCreateNewUser.setOnClickListener({
            finish()
        })

        binding.CreateNewUserBtn.setOnClickListener {
            NewName = binding.NameData.text.toString().trim()
            NewEmail = binding.EmailPhoneData.text.toString().trim()
            NewPassword = binding.PasswordData.text.toString().trim()

            if (NewName.isBlank() || NewEmail.isBlank() || NewPassword.isBlank())
            {
                Toast.makeText(this,"Kindly Fill all the details before Proceeding",Toast.LENGTH_SHORT).show()
            }else{
                CreateUserAccount(NewName,NewEmail,NewPassword)
            }
        }
    }

    private fun CreateUserAccount(newName: String,newEmail: String, newPassword: String) {
        Authentication.createUserWithEmailAndPassword(newEmail,newPassword).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                val user = Authentication.currentUser!!
                UpdateUi(user)
                SaveDataToFirebase(newName,newEmail,newPassword)
            }else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
                Log.d("Account","Account Creation : Failure",task.exception)
            }
        }
    }

    private fun UpdateUi(user: FirebaseUser?) {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun SaveDataToFirebase(newName: String, newEmail: String, newPassword: String) {

        NewName = binding.NameData.text.toString().trim()
        NewEmail = binding.EmailPhoneData.text.toString().trim()
        NewPassword = binding.PasswordData.text.toString().trim()

        val User = UserModal(NewName,null,NewEmail,NewPassword)
        val UserId = FirebaseAuth.getInstance().currentUser!!.uid

        //Save Data to Firebase DataBase
        Database.child("User").child(UserId).setValue(User)
    }
}