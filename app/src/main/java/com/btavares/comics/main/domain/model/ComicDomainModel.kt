package com.btavares.comics.main.domain.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


internal data class ComicDomainModel(
    val month : Int,
    val number : Int,
    val link : String?,
    val year : Int,
    val news : String?,
    val safeTitle : String?,
    val description : String?,
    val alt : String?,
    val imageUrl : String?,
    val title : String?,
    val day : Int,
    var favorite : Boolean,
    var comicBitmap: Bitmap?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(month)
        parcel.writeInt(number)
        parcel.writeString(link)
        parcel.writeInt(year)
        parcel.writeString(news)
        parcel.writeString(safeTitle)
        parcel.writeString(description)
        parcel.writeString(alt)
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeInt(day)
        parcel.writeByte(if (favorite) 1 else 0)
        parcel.writeParcelable(comicBitmap, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ComicDomainModel> {
        override fun createFromParcel(parcel: Parcel): ComicDomainModel {
            return ComicDomainModel(parcel)
        }

        override fun newArray(size: Int): Array<ComicDomainModel?> {
            return arrayOfNulls(size)
        }
    }
}


