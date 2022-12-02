package us.epistem.jist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import us.epistem.jist.R

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, "jist", null, 1) {
    val intro: String
    val priv: String
    init {
        intro = context.resources.getString(R.string.introduction)
        priv = context.resources.getString(R.string.privacy_notice)
    }
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
        val introduction = "Introduction"
        val privacy_note = "Privacy Notice"

        db?.execSQL("INSERT into notes values (?, ?)", arrayOf(introduction, intro))
        db?.execSQL("INSERT into notes values (?, ?)", arrayOf(privacy_note, priv))
        // set initial note to the introduction
        db?.execSQL("INSERT into meta values (0, ?)", arrayOf(introduction))

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
