package com.example.leoapa

import android.content.Context
import androidx.room.*
import androidx.room.Database
import java.io.Serializable

/**
 * Notes database abstraction class
 */
@Database(version = 1, entities = [NotesItem::class])
abstract class NotesDatabase: RoomDatabase(){
   abstract fun notesItemDao(): NotesItemDao
}

/**
 * Class contains all notes items with manipulation functions
 */
public class NotesItemList : MutableList<NotesItem> by mutableListOf() {

   /**
    * Function searches the item by primary key field
    * @param uid primmary key
    * @return null if not found, item if found the note by pk
    */
   fun findByUid(uid: Long): NotesItem? {
     this.forEach {
        if (it.uid == uid) {
           return it
        }
     }
     return null
  }
}

/**
 * Database singleton class
 */
object Database {
   private var instance: NotesDatabase? = null
   fun getInstance(context: Context) = instance?: Room.databaseBuilder(
      context.applicationContext, NotesDatabase::class.java, "leo-notes-db").allowMainThreadQueries()
         /* allowMainThreadQueries must be called to allow long operations because all works in the
         main (UI) thread. Otherwise IllegalStateException exception could occur - kinda "Cannot
         access database on the main thread since it may potentially lock the UI for a long period of time."*/
      .build()
      .also { instance = it }
}

/**
 * Notes item entity class. Serializable in order to pu in intent extra
 */
@Entity(tableName = "notes_item")
data class NotesItem(
   var title: String,
   var text: String,

   @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
   var img: ByteArray? = null,

   @PrimaryKey(autoGenerate = true)
   var uid: Long = 0
): Serializable

/**
 * Note's data access object class
 */
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

/**
 * Data form states enumeration
 */
enum class DataItemMode(var userString: String) {
   dimNone(""),
   dimView("View"),
   dimEdit("Edit"),
   dimInsert("Insert")
}