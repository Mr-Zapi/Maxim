package ru.ok.android.model.links

import android.os.Parcel
import android.os.Parcelable

// This is a fake class with the same name as the target's Parcelable.
// It will be used to trigger a deserialization error in the target app.
class LinkResult() : Parcelable {

    constructor(parcel: Parcel) : this() {
        // The target app will crash when it tries to read this parcel,
        // because it expects different data.
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // Write malicious or unexpected data to the parcel.
        // For example, write an integer where a string is expected.
        parcel.writeInt(12345)
        parcel.writeString("this_is_not_what_the_app_expects")
        parcel.writeInt(67890)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LinkResult> {
        override fun createFromParcel(parcel: Parcel): LinkResult {
            return LinkResult(parcel)
        }

        override fun newArray(size: Int): Array<LinkResult?> {
            return arrayOfNulls(size)
        }
    }
}