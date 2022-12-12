package us.epistem.jist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, "jist", null, 1) {
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }
    private var intro: String
    private var privacy: String

    init {

        intro = context.resources.getString(R.string.introduction)
        privacy = context.resources.getString(R.string.privacy_notice)
    }

    /**
     * table 'meta' contains the name of the current note at meta.id == 0, which is used as a key into table 'notes'
     * though that isn't enforced by the database.
     * the value where meta.id == 0 is re-exposed as this.id
     * the value where notes.id == this.id is re-exposed as this.value
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table meta (id Integer primary key , value VARCHAR(256) not null)")
        db?.execSQL("create table notes (id VARCHAR(256) primary key, value VARCHAR(256))")

        // insert initial helper text
        val introduction = "Introduction"
        val privacy_note = "Privacy Notice"

        db?.execSQL("insert into notes values (?, ?)", arrayOf(introduction, intro))
        db?.execSQL("insert into notes values (?, ?)", arrayOf(privacy_note, privacy))
        // set initial note to the introduction
        db?.execSQL("insert into meta values (0, ?)", arrayOf(introduction))

    }
    /**
     * return a list of all note ids
     */
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

    /**
     * delete the current note.
     */
    fun deleteNote() {
        db?.execSQL("delete from notes where id = (select value from meta where id = 0)")
    }
    // only use one connection to the database, initialize on first use.
    var db: SQLiteDatabase? = null
        get() {
            if (field !is SQLiteDatabase) { field = this.writableDatabase!! }
            return field
        }
    // id acts like a class member, but is stored persistently in the database
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
    // value acts like a class member, but is stored persistently in the database
    var value: String
        get() {
            db?.rawQuery(
                "select value from notes where id = (select value from meta where id = 0)", null).use {
                if (it!!.moveToFirst()) {
                    return it.getString(0)
                }
            }
            // insert the requested empty note if it didn't exist
            db?.execSQL("insert into notes values ((select value from meta where id = 0), '')")
            return ""
        }
        set(value) {
            val cv = ContentValues()
            cv.put("id", id)
            cv.put("value", value)
            db?.insertWithOnConflict("notes", null, cv, CONFLICT_REPLACE)
        }

}
