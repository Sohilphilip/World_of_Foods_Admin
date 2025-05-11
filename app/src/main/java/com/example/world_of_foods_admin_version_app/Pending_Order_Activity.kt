package com.example.world_of_foods_admin_version_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.world_of_foods_admin_version_app.adapter.Pending_Order_Adapter
import com.example.world_of_foods_admin_version_app.databinding.ActivityPendingOrderBinding
import com.example.world_of_foods_admin_version_app.modal.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Pending_Order_Activity : AppCompatActivity() {

    private var customerNameList : MutableList<String> = mutableListOf()
    private var customerAddressList : MutableList<String> = mutableListOf()
    private var customerPhoneList : MutableList<String> = mutableListOf()
    private var customerTotalPriceList : MutableList<String> = mutableListOf()
    private var ItemOrderKeyList : MutableList<String> = mutableListOf()
    private var orderedAcceptedList : MutableList<Boolean> = mutableListOf()
    private var paymentAcceptedList : MutableList<Boolean> = mutableListOf()

    private lateinit var authentication: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private val binding : ActivityPendingOrderBinding by lazy {
        ActivityPendingOrderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("Order Details")

        getCustumerDetails()

        binding.bachBtnPendingOrder.setOnClickListener({
            finish()
        })

    }

    private fun getCustumerDetails() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (customerSnapshot in snapshot.children){
                    val orderInfo = customerSnapshot.getValue(OrderDetails::class.java)

                    orderInfo?.UserName?.let{ customerNameList.add(it) }
                    orderInfo?.UserAddress?.let{ customerAddressList.add(it) }
                    orderInfo?.UserPhoneNo?.let{ customerPhoneList.add(it) }
                    orderInfo?.totalPrice?.let{ customerTotalPriceList.add(it) }
                    orderInfo?.itemPushKey?.let{ ItemOrderKeyList.add(it) }
                    orderInfo?.orderAccepted?.let{ orderedAcceptedList.add(it) }
                    orderInfo?.paymentRecieved?.let{ paymentAcceptedList.add(it) }
                }
                setAdpater()
            }

            private fun setAdpater() {
                val adapter = Pending_Order_Adapter(
                    customerNameList,
                    customerAddressList,
                    customerPhoneList,
                    customerTotalPriceList,
                    ItemOrderKeyList,
                    orderedAcceptedList,
                    paymentAcceptedList,
                    this@Pending_Order_Activity
                )
                binding.PendingOrderRecyclerView.layoutManager = LinearLayoutManager(this@Pending_Order_Activity)
                binding.PendingOrderRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Database","Fetch Data : Failed to Retrieve Data")
            }
        })
    }
}
