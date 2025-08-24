package com.example.myapplication2

import android.os.Parcel
import android.os.Parcelable

class MaliciousParcelable() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MaliciousParcelable> {
        override fun createFromParcel(parcel: Parcel): MaliciousParcelable {
            return MaliciousParcelable(parcel)
        }

        override fun newArray(size: Int): Array<MaliciousParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
