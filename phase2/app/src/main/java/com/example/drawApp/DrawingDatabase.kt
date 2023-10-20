package com.example.drawApp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.internal.synchronized
import java.util.Date

@Database(entities = [DrawData::class], version = 1, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class DrawingDatabase : RoomDatabase() {
    abstract fun drawDao(): DrawDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): DrawingDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = DrawingDatabase::class.java,
                    name = "draw_database"
                ).build()
                INSTANCE=instance
                //return instance
                instance
            }

        }
    }
}



class DateConverters{
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

//Defines a sqlite table, set up the table schema
@Entity(tableName = "drawings")
data class DrawData(var timestamp: Date?, var filename:String, var filepath:String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface DrawDao {
    @Insert
    suspend fun addDrawData(data: DrawData)

    @Query("SELECT * FROM drawings WHERE filename = :fileName")
    fun getDrawingByName(fileName: String) : DrawData?

    @Query("SELECT * FROM drawings ORDER BY timestamp DESC")
    fun getAllDrawings(): Flow<List<DrawData>>
}
