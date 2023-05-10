package com.example.sanabi.Room

import android.content.Context
import androidx.room.Room

object DatabaseRoom {

    fun getDatabase(context: Context): RoomServices {
        return Room.databaseBuilder(
            context,
            RoomServices::class.java, "basket"
        ).build()
    }
    fun getSearchDatabase(context: Context): PasteSearchRoomServices {
        return Room.databaseBuilder(
            context,
            PasteSearchRoomServices::class.java, "pasteSearch"
        ).fallbackToDestructiveMigration().build()
    }
}