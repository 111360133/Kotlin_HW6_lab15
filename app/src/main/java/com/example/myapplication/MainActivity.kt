package com.example.myapplication

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化資料庫和Adapter
        dbrw = MyDBHelper(this).writableDatabase
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        // 設定按鈕監聽
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close() // 關閉資料庫
    }

    private fun setListeners() {
        val edBook = findViewById<EditText>(R.id.edBook)
        val edPrice = findViewById<EditText>(R.id.edPrice)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            if (validateInput(edBook, edPrice)) {
                try {
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?, ?)",
                        arrayOf(edBook.text.toString(), edPrice.text.toString().toInt())
                    )
                    showToast("新增成功: ${edBook.text}, 價格: ${edPrice.text}")
                    cleanEditText(edBook, edPrice)
                } catch (e: Exception) {
                    showToast("新增失敗: $e")
                }
            }
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            if (validateInput(edBook, edPrice)) {
                try {
                    dbrw.execSQL(
                        "UPDATE myTable SET price = ? WHERE book = ?",
                        arrayOf(edPrice.text.toString().toInt(), edBook.text.toString())
                    )
                    showToast("更新成功: ${edBook.text}, 價格: ${edPrice.text}")
                    cleanEditText(edBook, edPrice)
                } catch (e: Exception) {
                    showToast("更新失敗: $e")
                }
            }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            if (edBook.text.isNotBlank()) {
                try {
                    dbrw.execSQL(
                        "DELETE FROM myTable WHERE book = ?",
                        arrayOf(edBook.text.toString())
                    )
                    showToast("刪除成功: ${edBook.text}")
                    cleanEditText(edBook, edPrice)
                } catch (e: Exception) {
                    showToast("刪除失敗: $e")
                }
            } else {
                showToast("請輸入書名")
            }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            val queryString = if (edBook.text.isBlank()) {
                "SELECT * FROM myTable"
            } else {
                "SELECT * FROM myTable WHERE book = ?"
            }
            val args = if (edBook.text.isNotBlank()) arrayOf(edBook.text.toString()) else null

            val c = dbrw.rawQuery(queryString, args)
            c.use {
                items.clear()
                if (it.moveToFirst()) {
                    do {
                        items.add("書名: ${it.getString(0)}, 價格: ${it.getInt(1)}")
                    } while (it.moveToNext())
                }
                adapter.notifyDataSetChanged()
                showToast("查詢完成，共有 ${it.count} 筆資料")
            }
        }
    }

    private fun validateInput(edBook: EditText, edPrice: EditText): Boolean {
        return when {
            edBook.text.isBlank() -> {
                showToast("書名請勿留空")
                false
            }
            edPrice.text.isBlank() -> {
                showToast("價格請勿留空")
                false
            }
            edPrice.text.toString().toIntOrNull() == null -> {
                showToast("價格請輸入有效數字")
                false
            }
            else -> true
        }
    }

    private fun cleanEditText(vararg edits: EditText) {
        edits.forEach { it.setText("") }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
