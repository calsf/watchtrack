package com.chc.watchtrack.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShowEntity::class, MovieEntity::class], version = 4, exportSchema = false)
abstract class WatchDatabase: RoomDatabase() {
    // Data access objects
    abstract val showDatabaseDao: ShowDatabaseDao
    abstract val movieDatabaseDao: MovieDatabaseDao

    companion object {
        /*
        Store reference to database to avoid reopening connections to database
        Make variable volatile so it will never be cached, read/write via main memory
        Will help keep value updated and consistent on all execution threads
         */
        @Volatile
        private var DB_INSTANCE: WatchDatabase? = null

        fun getInstance(context: Context): WatchDatabase {
            /*
            Only one execution thread should run code at any time
            Ensures database will only get initialized once
             */
            synchronized(this) {
                var instance = DB_INSTANCE
                if (instance == null)
                {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WatchDatabase::class.java,
                        "watch_database").fallbackToDestructiveMigration().build()
                    DB_INSTANCE = instance
                }
                return instance
            }
        }
    }
}