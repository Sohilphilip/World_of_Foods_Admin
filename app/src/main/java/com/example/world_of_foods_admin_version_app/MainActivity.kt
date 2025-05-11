package com.example.world_of_foods_admin_version_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.world_of_foods_admin_version_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var authentication : FirebaseAuth

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.AddMenu.setOnClickListener({
            val intent = Intent(this,Add_Item_Activity::class.java)
            startActivity(intent)
        })

        binding.Profile.setOnClickListener({
            val intent = Intent(this,Profile_Activity::class.java)
            startActivity(intent)
        })

        binding.AllItemMenu.setOnClickListener({
            val intent = Intent(this,All_Item_Menu_Activity::class.java)
            startActivity(intent)
        })

        binding.CreateNewUser.setOnClickListener({
            val intent = Intent(this,Create_New_User_Activity::class.java)
            startActivity(intent)
        })


        binding.LogOut.setOnClickListener({
            Firebase.auth.signOut()
            Toast.makeText(this,"Logout Sucessfull!!", Toast.LENGTH_LONG).show()
            val intent = Intent(this,Login_Screen_Activity::class.java)
            startActivity(intent)

        })

        binding.PendingOrderBtn.setOnClickListener({
            val intent = Intent(this,Pending_Order_Activity::class.java)
            startActivity(intent)
        })

        setContentView(binding.root)
    }
}