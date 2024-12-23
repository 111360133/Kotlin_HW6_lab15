package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 自訂建構子並繼承 SQLiteOpenHelper 類別
class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "myDatabase" // 資料庫名稱
        private const val VERSION = 1 // 資料庫版本
        private const val TABLE_NAME = "myTable" // 資料表名稱
        private const val COL_BOOK = "book" // 書名欄位名稱
        private const val COL_PRICE = "price" // 價格欄位名稱
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 建立 myTable 資料表
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COL_BOOK TEXT PRIMARY KEY,
                $COL_PRICE INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 升級資料庫版本時，刪除舊資料表，並重新執行 onCreate()，建立新資料表
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }
}
