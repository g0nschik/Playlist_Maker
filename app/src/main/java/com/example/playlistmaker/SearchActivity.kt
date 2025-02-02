package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var searchTextValue: CharSequence? = TEXT_DEF

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackList: ArrayList<Track>
    private lateinit var placeholder: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<FrameLayout>(R.id.btn_back)
        val searchLine = findViewById<EditText>(R.id.inputSearchText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val updateButton = findViewById<ImageView>(R.id.updateButton)

        placeholder = findViewById<LinearLayout>(R.id.placeholderLayout)

        searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideInput()
                showMessage("loading")
                performSearch(searchLine.text.toString())
                true
            } else {
                false
            }
        }

        backButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(displayIntent)
        }

        clearButton.setOnClickListener {
            searchLine.setText(TEXT_DEF)
            hideInput()
            searchLine.clearFocus()
            trackList.clear()
            recyclerView.visibility = View.GONE
            showMessage("hide")
        }

        updateButton.setOnClickListener {
            showMessage("loading")
            performSearch(searchLine.text.toString())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTextValue = s?.takeIf { it.isNotEmpty() }
                clearButton.visibility = clearButtonVisibility(s)
                if (s.isNullOrEmpty()) {
                    trackList.clear()
                    recyclerView.visibility = View.GONE
                    showMessage("hide")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchTextValue = s
            }
        }
        searchLine.addTextChangedListener(simpleTextWatcher)

        recyclerView = findViewById(R.id.itemsList)

        trackList = ArrayList()
        trackAdapter = TrackAdapter(trackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
    }

    private fun hideInput() {
        val searchLine = findViewById<EditText>(R.id.inputSearchText)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchLine.windowToken, 0)
    }

    private fun performSearch(query: String) {
        RetrofitClient.trackService.search(query).enqueue(object : Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                placeholder.visibility = View.GONE
                trackAdapter.notifyDataSetChanged()
                if (response.isSuccessful) {
                    trackList.clear()
                    response.body()?.results?.let {
                        if (it.isNotEmpty()) {
                            trackList.addAll(it)
                            recyclerView.visibility = View.VISIBLE
                            showMessage("hide")
                        } else {
                            recyclerView.visibility = View.GONE // Скрываем RecyclerView
                            showMessage("empty")
                        }
                    } ?: run {
                        recyclerView.visibility = View.GONE // Скрываем RecyclerView
                        showMessage("empty")
                    }
                } else {
                    recyclerView.visibility = View.GONE // Скрываем RecyclerView
                    showMessage("error")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                trackAdapter.notifyDataSetChanged()
                recyclerView.visibility = View.GONE // Скрываем RecyclerView
                showMessage("error")
            }
        })
    }

    private fun showMessage(type: String) {
        val messageImage: ImageView = placeholder.findViewById(R.id.placeholderImage)
        val messageText: TextView = placeholder.findViewById(R.id.placeholderText)
        val updateButton: ImageView = placeholder.findViewById(R.id.updateButton)

        placeholder.visibility = View.GONE
        updateButton.visibility = View.GONE

        when (type) {
            "loading" -> {
                messageImage.visibility = View.GONE
                messageText.text = getString(R.string.loading)
                placeholder.visibility = View.VISIBLE
            }
            "empty" -> {
                messageImage.setImageResource(R.drawable.no_results)
                messageImage.visibility = View.VISIBLE
                messageText.text = getString(R.string.no_results)
                placeholder.visibility = View.VISIBLE
            }
            "error" -> {
                messageImage.setImageResource(R.drawable.net_error)
                messageImage.visibility = View.VISIBLE
                messageText.text = getString(R.string.net_error)
                updateButton.visibility = View.VISIBLE
                placeholder.visibility = View.VISIBLE
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT_VALUE, searchTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getCharSequence(SEARCH_TEXT_VALUE, TEXT_DEF)
    }

    companion object {
        const val SEARCH_TEXT_VALUE = "SEARCH_TEXT_VALUE"
        const val TEXT_DEF = ""
    }
}