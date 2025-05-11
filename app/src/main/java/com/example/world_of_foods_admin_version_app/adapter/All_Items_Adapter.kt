package com.example.world_of_foods_admin_version_app.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.world_of_foods_admin_version_app.databinding.AllItemsAdapterBinding
import com.example.world_of_foods_admin_version_app.modal.AddItem
import com.google.firebase.database.DatabaseReference

class All_Items_Adapter(
    private val context: Context,
    private val MenuList: ArrayList<AddItem>,
    DatabaseReference: DatabaseReference
) :
    RecyclerView.Adapter<All_Items_Adapter.All_Items_ViewHolder>() {

    private val ItemQuantity = IntArray(MenuList.size){1}



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): All_Items_ViewHolder {
        val binding  = AllItemsAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return All_Items_ViewHolder(binding)
    }

    override fun getItemCount(): Int = MenuList.size

    override fun onBindViewHolder(holder: All_Items_ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class All_Items_ViewHolder(private val binding : AllItemsAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                var quantity = ItemQuantity[position]
                val menuItem = MenuList[position]

                FoodName.text = menuItem.AddFoodName
                FoodPrice.text = menuItem.AddFoodPrice

                val uriString : String? = menuItem.AddFoodImage
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(FoodImage)

                binding.Quantity.text = quantity.toString()

                binding.plusBtn.setOnClickListener {
                    IncreaseCount(position)
                }

                binding.minusBtn.setOnClickListener {
                    DecreseCount(position)
                }

                binding.DeleteBtn.setOnClickListener{
                    TrashItem(position)
                }
            }
        }

        private fun TrashItem(position: Int) {
            MenuList.removeAt(position)
            MenuList.removeAt(position)
            MenuList.removeAt(position)
            MenuList.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(position,MenuList.size)
        }

        private fun DecreseCount(position: Int) {
            if (ItemQuantity[position] > 1){
                ItemQuantity[position]--
                binding.Quantity.text = ItemQuantity[position].toString()
            }
        }

        private fun IncreaseCount(position: Int) {
            ItemQuantity[position]++
            binding.Quantity.text = ItemQuantity[position].toString()
        }
    }
}