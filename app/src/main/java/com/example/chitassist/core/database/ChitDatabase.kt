package com.example.chitassist.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.chitassist.core.database.dao.ChitDao
import com.example.chitassist.core.database.dao.PaymentDao
import com.example.chitassist.core.database.dao.UserDao
import com.example.chitassist.core.database.entity.ChitEntity
import com.example.chitassist.core.database.entity.PaymentEntity
import com.example.chitassist.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ChitEntity::class,
        PaymentEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class ChitDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chitDao(): ChitDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        const val DATABASE_NAME = "chit_assist_db"

        val CALLBACK = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys = ON;")
            }
        }
    }
}
