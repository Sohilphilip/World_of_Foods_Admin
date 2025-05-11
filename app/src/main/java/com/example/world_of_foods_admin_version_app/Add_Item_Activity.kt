package com.example.world_of_foods_admin_version_app

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.world_of_foods_admin_version_app.databinding.ActivityAddItemBinding
import com.example.world_of_foods_admin_version_app.modal.AddItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class Add_Item_Activity : AppCompatActivity() {

    private lateinit var AddFoodNameString : String
    private lateinit var AddFoodPriceString : String
    private lateinit var AddFoodDescriptionString : String
    private lateinit var AddFoodIngredientsString : String
    private var AddFoodImageUri : Uri? = null


    //Firebase variables
    private lateinit var Authentication: FirebaseAuth
    private lateinit var Database: FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Firebase instance
        Authentication = FirebaseAuth.getInstance()
        Database = FirebaseDatabase.getInstance()

        binding.AddItemBackBtn.setOnClickListener({
            finish()
        })

        binding.AddItemBtn.setOnClickListener {
            AddFoodNameString = binding.ItemName.text.toString().trim()
            AddFoodPriceString = binding.ItemPrice.text.toString().trim()
            AddFoodDescriptionString = binding.ItemDescription.text.toString().trim()
            AddFoodIngredientsString = binding.ItemIngredients.text.toString().trim()

            if (AddFoodNameString.isBlank() || AddFoodPriceString.isBlank() || AddFoodDescriptionString.isBlank() || AddFoodIngredientsString.isBlank()){
                Toast.makeText(this,"Kindly fill all the feilds.", Toast.LENGTH_SHORT).show()
            }else{
                UploadData(AddFoodNameString,AddFoodPriceString,AddFoodDescriptionString,AddFoodIngredientsString,AddFoodImageUri)
                Toast.makeText(this,"Item Added Successfully!!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.AddImageBtn.setOnClickListener {
            pickimage.launch("image/*")
        }
    }

    val pickimage = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri ->
        if (uri != null){
            binding.ItemImage.setImageURI(uri)
            AddFoodImageUri = uri
        }
    }

    private fun UploadData(addFoodNameString: String, addFoodPriceString: String, addFoodDescriptionString: String, addFoodIngredientsString: String, addFoodImageString: Uri?) {
        //get reference to the menu "node"to the database.
        val MenuRes = Database.getReference("menu")

        //initialise unique itemkey id
        val itemKey = MenuRes.push().key

        if (AddFoodImageUri != null){
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${itemKey}.jpg")
            val uploadTask = imageRef.putFile(AddFoodImageUri!!)

            uploadTask.addOnCompleteListener{
                imageRef.downloadUrl.addOnSuccessListener {
                    downloadUrl ->

                    val newItem = AddItem(
                        AddFoodName = AddFoodNameString ,
                        AddFoodPrice = AddFoodPriceString,
                        AddShortDescription = AddFoodDescriptionString,
                        AddIngredients = AddFoodIngredientsString,
                        AddFoodImage = downloadUrl.toString()
                    )

                    itemKey?.let {
                        key ->
                        MenuRes.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this,"Data Uploaded Sucessfully",Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this,"Failure to Upload Data",Toast.LENGTH_SHORT).show()
                    }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,"Failure to Download Image",Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this,"Please Select Image",Toast.LENGTH_SHORT).show()
        }
    }
}