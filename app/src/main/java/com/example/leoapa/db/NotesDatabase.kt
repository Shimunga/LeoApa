package com.example.leoapa

import android.content.Context
import androidx.room.*
import androidx.room.Database
import java.io.Serializable
import java.text.BreakIterator

@Database(version = 1, entities = [NotesItem::class])
abstract class NotesDatabase: RoomDatabase(){
   abstract fun notesItemDao(): NotesItemDao
}

public class NotesItemList : MutableList<NotesItem> by mutableListOf() {

  fun findByUid(uid: Long): NotesItem? {
     this.forEach {
        if (it.uid == uid) {
           return it
        }
     }
     return null
  }
}

object Database {
   private var instance: NotesDatabase? = null
   fun getInstance(context: Context) = instance?: Room.databaseBuilder(
      context.applicationContext, NotesDatabase::class.java, "leo-notes-db").allowMainThreadQueries()
      .build()
      .also { instance = it }
}

@Entity(tableName = "notes_item")
data class NotesItem(
   var title: String,
   var text: String,

   @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
   var img: ByteArray? = null,

   @PrimaryKey(autoGenerate = true)
   var uid: Long = 0
): Serializable

@Dao
interface NotesItemDao {
   @Insert
   fun insertAll(vararg items: NotesItem): List<Long>

   @Query("SELECT * FROM notes_item")
   fun getAll(): List<NotesItem>

   @Query("SELECT * FROM notes_item WHERE uid == :itemId")
   fun getItemById(itemId: Long): NotesItem

   @Update
   fun update(item: NotesItem)

   @Delete
   fun delete(item: NotesItem)
}

enum class DataItemMode(var userString: String) {
   dimNone(""),
   dimView("View"),
   dimEdit("Edit"),
   dimInsert("Insert")
}