package us.epistem.jist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.*

class MainActivity : AppCompatActivity() {
    lateinit var db: DatabaseHandler
    lateinit var listView: ListView
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var searchView: SearchView
    lateinit var savingMsg: Toast
    lateinit var noteBody: EditText
    lateinit var dummy: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(applicationContext)

        noteBody = findViewById(R.id.noteBody)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        dummy = findViewById(R.id.dummy)

        savingMsg = Toast.makeText(this, "saved", Toast.LENGTH_SHORT)
        listAdapter = ArrayAdapter( this, android.R.layout.simple_list_item_1, db.getNames())
        listView.adapter = listAdapter

        // when you click on a drop-down item, open that note
        listView.setOnItemClickListener { _, _, position, _ -> openNote(listAdapter.getItem(position).toString()) }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                openNote(query!!)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.filter.filter(newText)
                return false
            }
        })
        dummy.setOnClickListener {openNote(searchView.query.toString())}

        searchView.setOnQueryTextFocusChangeListener { _, gainedFocus ->
            // toggle whether to display the note list (if searching) or a note
            if (gainedFocus) {
                // delete current note if it's empty, otherwise save it
                val body = noteBody.text.toString()
                if (body == "") { db.deleteNote() } else { db.value = body }

                listView.visibility = VISIBLE
                dummy.visibility = VISIBLE
                noteBody.visibility = GONE
            } else {
                noteBody.visibility = VISIBLE
                dummy.visibility = GONE
                listView.visibility = GONE
            }
        }
        openNote(db.id)
    }

    override fun onPause() {
        super.onPause()
        // save note and reassure us that it's been done
        db.value = noteBody.text.toString()
        savingMsg.show()
    }

    fun openNote(name: String) {
        searchView.setQuery(name, false) // for if we clicked an autocomplete option
        db.id = name
        noteBody.setText(db.value)
        noteBody.setSelection(noteBody.text.length) // move cursor to the end
        searchView.clearFocus()
        noteBody.requestFocus()

        // update list
        Log.e("new list", db.getNames().toString())
        listAdapter.clear()
        listAdapter.addAll(db.getNames())
        listAdapter.notifyDataSetChanged()
        listView.invalidate()
    }
}
