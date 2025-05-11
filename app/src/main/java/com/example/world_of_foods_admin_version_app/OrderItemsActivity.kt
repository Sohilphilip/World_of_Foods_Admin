package com.example.world_of_foods_admin_version_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.world_of_foods_admin_version_app.adapter.OrderItemsAdapter
import com.example.world_of_foods_admin_version_app.databinding.ActivityOrderItemsBinding
import com.example.world_of_foods_admin_version_app.modal.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderItemsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderItemsBinding

    private lateinit var databaseReference: DatabaseReference

    private var FoodNameList : MutableList<String> = mutableListOf()
    private var FoodImageList : MutableList<String> = mutableListOf()
    private var FoodPriceList : MutableList<String> = mutableListOf()
    private var FoodQuantityList : MutableList<Int> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate view and set content view
        binding = ActivityOrderItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val itemOrderKey : String? = intent.getStringExtra("ItemKeyValue")

        //Database Reference
        databaseReference = FirebaseDatabase.getInstance().reference

        //Retrieve Data from Database
        val orderRef = databaseReference.child("Order Details").child(itemOrderKey!!)
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderInfo = snapshot.getValue(OrderDetails::class.java)

                orderInfo?.FoodName?.let { FoodNameList.addAll(it) }
                orderInfo?.FoodPrice?.let { FoodPriceList.addAll(it) }
                orderInfo?.FoodImage?.let { FoodImageList.addAll(it) }
                orderInfo?.FoodQuantity?.let { FoodQuantityList.addAll(it) }

                setAdapter()
            }

            private fun setAdapter() {
                val adapter = OrderItemsAdapter(FoodNameList, FoodPriceList, FoodImageList, FoodQuantityList, this@OrderItemsActivity)
                binding.orderItemsRecyclerView.adapter = adapter
                binding.orderItemsRecyclerView.layoutManager = LinearLayoutManager(this@OrderItemsActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Database","Fetch Data : Failed to Retrieve Data")
            }
        })
    }
}