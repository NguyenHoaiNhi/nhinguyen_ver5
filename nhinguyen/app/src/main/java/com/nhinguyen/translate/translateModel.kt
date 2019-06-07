package com.nhinguyen.translate


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class translateModel{
    @Parcelize
    data class Results(
        val text: Array<String>?
    ):Parcelable{
        constructor(): this(null)
    }

}