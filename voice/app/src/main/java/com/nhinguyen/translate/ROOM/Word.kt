package com.nhinguyen.translate.ROOM

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var language1: String,
    var language2: String,
    var content_language1: String,
    var content_language2: String
): Parcelable {
    constructor(): this(null, "","", "", "")
}
