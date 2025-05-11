package com.example.world_of_foods_admin_version_app.modal

import android.os.Parcel
import android.os.Parcelable

class OrderDetails() : Parcelable {
    var UserId: String? = null
    var UserName: String? = null
    var UserAddress: String? = null
    var UserPhoneNo: String? = null
    var totalPrice :String? =null

    var FoodName: MutableList<String>? = null
    var FoodPrice: MutableList<String>? = null
    var FoodImage: MutableList<String>?  = null
    var FoodQuantity: MutableList<Int>?  = null

    var orderAccepted : Boolean = false
    var paymentRecieved : Boolean = false
    var itemPushKey : String? = null
    var currentTime : Long = 0

    constructor(parcel: Parcel) : this() {
        UserId = parcel.readString()
        UserName = parcel.readString()
        UserAddress = parcel.readString()
        UserPhoneNo = parcel.readString()
        totalPrice = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentRecieved = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(UserId)
        parcel.writeString(UserName)
        parcel.writeString(UserAddress)
        parcel.writeString(UserPhoneNo)
        parcel.writeString(totalPrice)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentRecieved) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}
