package com.example.jauntnote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

const val initial_note = "Introduction"
const val helptext = "Welcome!\n\n" +
        "Use the search bar to find or create notes.\n" +
        "Everything is saved automatically.\n" +
        "Empty notes will be deleted!"
class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, "jaunt", null, 1) {
    var db: SQLiteDatabase? = null
        get() {
            if (field !is SQLiteDatabase) { field = this.writableDatabase!! }
            return field
        }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE notes (id VARCHAR(256) PRIMARY KEY, value VARCHAR(256))")
        db?.execSQL("CREATE TABLE meta (id Integer primary key , value VARCHAR(256) not null)")

        // insert initial helper text
        db?.execSQL("INSERT into meta values (0, ?)", arrayOf(initial_note))
        db?.execSQL("INSERT into notes values (?, ?)", arrayOf(initial_note, helptext))
    }
    fun getNames(): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        val result = db?.rawQuery("select id from notes order by id", null)
        if (result!!.moveToFirst()) {
            do { list.add(result.getString(0)) } while (result.moveToNext())
        } else {
            Log.e("getnames", "no result?")
        }
        result.close()
        return list
    }
    var id: String
        get() {
            db?.rawQuery("select value from meta where id = 0", null).use {
                if (it!!.moveToFirst()) {
                    return it.getString(0)
                }
            }
            return ""
        }
        set(value) {
            val cv = ContentValues()
            cv.put("id", 0)
            cv.put("value", value)
            db?.insertWithOnConflict("meta", null, cv, CONFLICT_REPLACE)
    }
    var value: String
        get() {
            db?.rawQuery(
                "select value from notes where id = (select value from meta where id = 0)", null).use {
                if (it!!.moveToFirst()) {
                    return it.getString(0)
                }
            }
            return ""
        }
        set(value) {
            val cv = ContentValues()
            cv.put("id", id)
            cv.put("value", value)
            db?.insertWithOnConflict("notes", null, cv, CONFLICT_REPLACE)
        }
    fun deleteNote() {
        db?.execSQL("delete from notes where id = (select value from meta where id = 0)")
    }
}