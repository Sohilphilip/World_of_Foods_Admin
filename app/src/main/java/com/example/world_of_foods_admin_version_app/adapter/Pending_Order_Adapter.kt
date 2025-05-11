package com.example.world_of_foods_admin_version_app.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.world_of_foods_admin_version_app.OrderItemsActivity
import com.example.world_of_foods_admin_version_app.R
import com.example.world_of_foods_admin_version_app.databinding.PendingOrderAdapterBinding
import com.example.world_of_foods_admin_version_app.modal.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class  Pending_Order_Adapter(private val PendingCustumerNameList : MutableList<String> ,
                             private val PendingCustumerAddressList  : MutableList<String> ,
                             private val PendingCustumerPhoneList  : MutableList<String> ,
                             private val PendingCustumerTotalPriceList  : MutableList<String> ,
                             private val ItemOrderKeyList : MutableList<String>,
                             private val OrderAcceptedList : MutableList<Boolean>,
                             private val PaymentRecievedList : MutableList<Boolean>,
                             private val context : Context)
    : RecyclerView.Adapter<Pending_Order_Adapter.Pending_Order_ViewHolder>() {

    private var isAccepted = false
    private val light_red : Int = R.color.light_red
    private val light_yellow : Int = R.color.light_yellow
    private val light_green : Int = R.color.light_green

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pending_Order_ViewHolder {
        val binding = PendingOrderAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Pending_Order_ViewHolder(binding)
    }

    override fun getItemCount(): Int  = PendingCustumerNameList.size

    override fun onBindViewHolder(holder: Pending_Order_ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class Pending_Order_ViewHolder(private val binding: PendingOrderAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.PendingOrderCard.setOnClickListener {
                val intent = Intent(context, OrderItemsActivity::class.java).apply {
                    putExtra("ItemKeyValue", ItemOrderKeyList[adapterPosition])
                }
                context.startActivity(intent)
            }
        }

        fun bind(position: Int) {
            binding.apply {

                binding.PendingCustumerName.text = PendingCustumerNameList[position]
                binding.PendingCustumerAddress.text = PendingCustumerAddressList[position]
                binding.PendingCustumerPhone.text = PendingCustumerPhoneList[position]
                binding.PendingTotalPrice.text = PendingCustumerTotalPriceList[position]

                AcceptBtn.apply {

                    if (!isAccepted){ //changes status btw Accepted and Dispatched
                        text = "Accept"
                        binding.PendingOrderCard.setCardBackgroundColor(context.getColor(light_red))
                    }else{
                        text = "Delivered"
                        binding.PendingOrderCard.setCardBackgroundColor(context.getColor(light_yellow))
                    }

                    setOnClickListener {
                        if (!isAccepted){
                            text = "Delivered"
                            isAccepted = true
                            binding.PendingOrderCard.setCardBackgroundColor(context.getColor(light_yellow))
                            OrderAcceptedList[position] = true
                            updateOrderStatusInDatabase(ItemOrderKeyList[position], true)
                            Toast.makeText(context,"Order is Accepted", Toast.LENGTH_SHORT).show()

                        }else{
                            isAccepted = false
                            PaymentRecievedList[position] = true
                            updatePaymentStatusInDatabase(ItemOrderKeyList[position],true)
                            transferOrderInfoToHistoryAndDispatch(ItemOrderKeyList[position])
                            removeItemFromOrderDetailsAndAdapter(position)
                            Toast.makeText(context,"Order is Delivered", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        private fun transferOrderInfoToHistoryAndDispatch(orderKey: String) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            val orderRef = databaseReference.child("Order Details").child(orderKey)
            orderRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderInfo = snapshot.getValue(OrderDetails::class.java)
                    if (orderInfo != null){
                        val userId = orderInfo?.UserId
                        val orderKey = orderInfo?.itemPushKey

                        databaseReference.child("user").child(userId!!)
                            .child("Order History").child(orderKey!!).setValue(orderInfo)

                    }
                    else{
                        Log.e("TransferOrderInfo", "orderInfo is null for orderKey: $orderKey")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("Database","Fetch Data : Failed to Retrieve Data")
                }
            })
        }

        private fun removeItemFromOrderDetailsAndAdapter(position: Int) {
            if (PaymentRecievedList[position]) {
                // Remove item from order details
                val orderKey = ItemOrderKeyList[position]
                val databaseReference = FirebaseDatabase.getInstance().reference
                val orderDetailsRef = databaseReference.child("Order Details").child(orderKey)
                orderDetailsRef.removeValue()

                // Remove adapter associated with the item
                PendingCustumerNameList.removeAt(position)
                PendingCustumerAddressList.removeAt(position)
                PendingCustumerPhoneList.removeAt(position)
                PendingCustumerTotalPriceList.removeAt(position)
                ItemOrderKeyList.removeAt(position)
                OrderAcceptedList.removeAt(position)
                PaymentRecievedList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,PendingCustumerNameList.size)

                // Check if all items are removed
                if (PendingCustumerNameList.isEmpty()) {
                    // Finish the activity if all items are removed
                    (context as Activity).finish()
                }
            }
        }

        private fun updatePaymentStatusInDatabase(orderKey: String, accepted: Boolean) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child("Order Details").child(orderKey).child("paymentRecieved")
                .setValue(accepted)
        }

        private fun updateOrderStatusInDatabase(orderKey: String, accepted: Boolean) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child("Order Details").child(orderKey).child("orderAccepted")
                .setValue(accepted)
        }
    }
}