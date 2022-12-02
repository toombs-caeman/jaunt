package us.epistem.jist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.*
import us.epistem.jist.R

class MainActivity : AppCompatActivity() {
    lateinit var db: DatabaseHandler
    lateinit var listView: ListView
    lateinit var searchView: SearchView
    lateinit var savingMsg: Toast
    lateinit var noteBody: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(applicationContext)

        noteBody = findViewById(R.id.noteBody)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)

        savingMsg = Toast.makeText(this, "saved", Toast.LENGTH_SHORT)
        val listAdapter: ArrayAdapter<String> = ArrayAdapter( this, android.R.layout.simple_list_item_1, db.getNames())
        listView.adapter = listAdapter

        listView.setOnItemClickListener {
                _, _, position, _ ->
            val s = listAdapter.getItem(position).toString()
            openNote(s)
            searchView.setQuery(db.id, false)
        }
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

        searchView.setOnQueryTextFocusChangeListener { _, gainedFocus ->
            // toggle whether to display the note list (if searching) or a note
            if (gainedFocus) {
                // delete current note if it's empty, otherwise save it
                val body = noteBody.text.toString()
                if (body == "") { db.deleteNote() } else { db.value = body }
                // refresh list of notes
                listAdapter.clear()
                listAdapter.addAll(db.getNames())
                listAdapter.notifyDataSetChanged()
                listView.invalidate()
                listView.invalidateViews()
                listView.adapter = listAdapter
                Log.e("new list", db.getNames().toString())

                listView.visibility = VISIBLE
                noteBody.visibility = GONE
            } else {
                noteBody.visibility = VISIBLE
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
    }
}
