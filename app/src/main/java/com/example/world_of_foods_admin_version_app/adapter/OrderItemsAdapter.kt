package com.example.world_of_foods_admin_version_app.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.world_of_foods_admin_version_app.databinding.OrderItemsAdapterBinding

class OrderItemsAdapter(private val OrderNameList : MutableList<String>,
                        private val OrderPriceList : MutableList<String>,
                        private val OrderImageList : MutableList<String>,
                        private val OrderQuantityList : MutableList<Int>,
                        private val context: Context) :
    RecyclerView.Adapter<OrderItemsAdapter.PendingOrderItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderItemsViewHolder {
        val binding  = OrderItemsAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingOrderItemsViewHolder(binding)
    }

    override fun getItemCount(): Int = OrderNameList.size

    override fun onBindViewHolder(holder: PendingOrderItemsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class PendingOrderItemsViewHolder(private val binding: OrderItemsAdapterBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                OrderFoodName.text = OrderNameList[position]
                OrderFoodPrice.text = OrderPriceList[position]
                OrderFoodQuantity.text = OrderQuantityList[position].toString()

                val uri = Uri.parse(OrderImageList[position])
                Glide.with(context).load(uri).into(OrderFoodImage)
            }
        }
    }
}