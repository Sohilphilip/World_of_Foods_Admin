package com.example.world_of_foods_admin_version_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.world_of_foods_admin_version_app.adapter.All_Items_Adapter
import com.example.world_of_foods_admin_version_app.databinding.ActivityAllItemMenuBinding
import com.example.world_of_foods_admin_version_app.modal.AddItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class All_Item_Menu_Activity : AppCompatActivity() {

    private lateinit var DatabaseReference : DatabaseReference
    private lateinit var Database : FirebaseDatabase
    private var menuItems : ArrayList<AddItem> = ArrayList()

    private val binding : ActivityAllItemMenuBinding by lazy {
        ActivityAllItemMenuBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DatabaseReference = FirebaseDatabase.getInstance().reference

        RetrieveMenuItem()

        binding.backBtnAllItems.setOnClickListener({
            finish()
        })
    }

    private fun RetrieveMenuItem() {

        Database = FirebaseDatabase.getInstance()
        val foodRef : DatabaseReference = Database.reference.child("menu")

        //fetch data
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear existing data before populating
                menuItems.clear()

                //loop for through each food item
                for (foodSnapshot in  snapshot.children) {
                    val menuItem = foodSnapshot.getValue(AddItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error: ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        val adapter = All_Items_Adapter(this@All_Item_Menu_Activity,menuItems,DatabaseReference)
        binding.itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemsRecyclerView.adapter = adapter
    }
}