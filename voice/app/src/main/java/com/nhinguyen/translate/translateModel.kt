package com.nhinguyen.translate
import kotlinx.serialization.*


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class translateModel{
//    data class Results(
//       val code: Int?,
//       val lang: String?
//    )

}
@Serializable
data class Mymodel(val code : Int, val lang: String)