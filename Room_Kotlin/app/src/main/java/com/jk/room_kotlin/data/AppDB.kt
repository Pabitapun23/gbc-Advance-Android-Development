package com.jk.room_kotlin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

// first we inherit from the db

//if we have more than one entities in our class
//@Database(entities = [Note::class, User::class], version = 1, exportSchema = false)
// version no. is Int - it needs to be always incremented if we make any changes in our entities

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {

    // instance of NoteDAO
    abstract fun noteDAO() : NoteDAO

    // companion object - we can create instance without create an actual object of the class,
    //                    which we can shared among other activities

    // singleton pattern
    // ensures that only one object of the AppDB is going to be available throughout the app
    // helps maintain the single point of access to the db
    // ensures data consistency
    // reduces data redundancy
    // reduces the resource consumption
    // increases performance
    companion object {

        private var db : AppDB? = null

        private const val NUMBER_OF_THREADS = 4
        val databaseQueryExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        // this function will be used to obtain the instance of the database where needed
        fun getDB(context: Context) : AppDB? {
            // check if the object is null
            if (db == null) {
                // if yes, create a new object,

                // supply context, class and name(recommended - package name)
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "com.jk.room_kotlin.db"
                )
                    // any change to existing table will delete it and create new tables with empty data.
                    .fallbackToDestructiveMigration()
                    .build()

                // otherwise, do nothing
            }

            // return the same database object anywhere it is requested
            return db
        }
    }

}