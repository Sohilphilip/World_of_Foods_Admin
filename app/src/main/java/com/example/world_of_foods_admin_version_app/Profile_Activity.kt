package com.example.world_of_foods_admin_version_app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.world_of_foods_admin_version_app.databinding.ActivityProfileBinding
import com.example.world_of_foods_admin_version_app.modal.ProfileInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile_Activity : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var database  : FirebaseDatabase
    private lateinit var UserId : String

    private lateinit var Name : String
    private lateinit var Address : String
    private lateinit var Email : String
    private lateinit var PhoneNo : String
    private lateinit var Password : String

    private val binding : ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.backBtnProfile.setOnClickListener({
            finish()
        })

        binding.apply {
            ProfileName.isEnabled = false
            ProfileAddress.isEnabled = false
            ProfileEmail.isEnabled = false
            ProfilePhoneNo.isEnabled = false
            ProfilePassword.isEnabled = false
            SaveInfoBtn.isEnabled = false
        }

        binding.EditBtn.setOnClickListener {
            binding.apply {
                ProfileName.isEnabled = true
                ProfileAddress.isEnabled = true
                ProfileEmail.isEnabled = true
                ProfilePhoneNo.isEnabled = true
                ProfilePassword.isEnabled = true
                SaveInfoBtn.isEnabled = true
            }
        }

        val profile_present : Boolean = displayInfo()

        binding.SaveInfoBtn.setOnClickListener {
            Name = binding.ProfileName.text.toString().trim()
            Address = binding.ProfileAddress.text.toString().trim()
            Email = binding.ProfileEmail.text.toString().trim()
            PhoneNo = binding.ProfilePhoneNo.text.toString().trim()
            Password = binding.ProfilePassword.text.toString().trim()

            if ((Name.isNotBlank() || Address.isNotBlank() || Email.isNotBlank() || PhoneNo.isNotBlank() || Password.isNotBlank()) && profile_present == false){
                saveProfileInfo(Name,Address,Email,PhoneNo,Password)
                Toast.makeText(this , "Profile Saved Succesfully", Toast.LENGTH_SHORT).show()
            }
            else if ((Name.isNotBlank() || Address.isNotBlank() || Email.isNotBlank() || PhoneNo.isNotBlank() || Password.isNotBlank()) && profile_present == true){
                updateProfileInfo(Name,Address,Email,PhoneNo,Password)
                Toast.makeText(this , "Profile Updated Succesfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this , "Kindly Fill All The Fields", Toast.LENGTH_SHORT).show()
            }

            binding.apply {
                ProfileName.isEnabled = false
                ProfileAddress.isEnabled = false
                ProfileEmail.isEnabled = false
                ProfilePhoneNo.isEnabled = false
                ProfilePassword.isEnabled = false
                SaveInfoBtn.isEnabled = false
            }
        }
    }

    private fun saveProfileInfo(
        name: String,
        address: String,
        email: String,
        phoneNo: String,
        password: String
    ) {
        UserId = authentication.currentUser?.uid?:""

        val profileRef = database.reference.child("User").child(UserId).child("Profile")
        val profileInfo = ProfileInfo(name,address,email,phoneNo,password)
        profileRef.setValue(profileInfo)
    }

    private fun updateProfileInfo(
        name: String,
        address: String,
        email: String,
        phoneNo: String,
        password: String
    ) {
        UserId = authentication.currentUser?.uid?:""

        val profileRef = database.reference.child("User").child(UserId)
        profileRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (profileSnapshot in snapshot.children){
                    val profileInfo = profileSnapshot.getValue(ProfileInfo::class.java)

                    if (profileInfo?.ProfileName != name){
                        val ref = database.reference.child("User").child(UserId).child("Profile").child("ProfileName")
                        ref.setValue(name)
                    }
                    if (profileInfo?.ProfileAddress != address){
                        val ref = database.reference.child("User").child(UserId).child("Profile").child("ProfileAddress")
                        ref.setValue(address)
                    }
                    if (profileInfo?.ProfileEmail != email){
                        val ref = database.reference.child("User").child(UserId).child("Profile").child("ProfileEmail")
                        ref.setValue(email)
                    }
                    if (profileInfo?.ProfileName != phoneNo){
                        val ref = database.reference.child("User").child(UserId).child("Profile").child("ProfilePhone")
                        ref.setValue(phoneNo)
                    }
                    if (profileInfo?.ProfileName != password){
                        val ref = database.reference.child("User").child(UserId).child("Profile").child("ProfilePassword")
                        ref.setValue(password)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Database","Fetch Data : Failed to Retrieve Data")
            }
        })
    }

    private fun displayInfo(): Boolean {
        var profile_is_present = false
        UserId = authentication.currentUser?.uid?:""

        val profileRef = database.getReference("User").child(UserId).child("Profile")
        profileRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    // Profile exists, retrieve its contents
                    val profileInfo = snapshot.getValue(ProfileInfo::class.java)
                    if (profileInfo != null) {
                        // Display profile contents
                        binding.ProfileName.setText(profileInfo?.ProfileName)
                        binding.ProfileAddress.setText(profileInfo?.ProfileAddress)
                        binding.ProfileEmail.setText(profileInfo?.ProfileEmail)
                        binding.ProfilePhoneNo.setText(profileInfo?.ProfilePhone)
                        binding.ProfilePassword.setText(profileInfo?.ProfilePassword)
                    }
                    profile_is_present = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Database","Fetch Data : Failed to Retrieve Data")
            }
        })

        return profile_is_present
    }
}