package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackAdapterHistory: TrackAdapter
    private lateinit var trackList: ArrayList<Track>
    private var trackListHistory: ArrayList<Track> = ArrayList()
    private lateinit var content: FrameLayout
    private lateinit var searchLine: EditText

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_search)

        val sharedPrefs = getSharedPreferences(APP_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs, this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        content = findViewById(R.id.contentLayout)
        searchLine = findViewById(R.id.inputSearchText)

        val historyView = LayoutInflater.from(this).inflate(R.layout.history_block, content, false)

        val backButton = findViewById<FrameLayout>(R.id.btn_back)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideInput()
                performSearch(searchLine.text.toString())
                true
            } else {
                false
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            hideInput()
            searchLine.clearFocus()
            showMessage("clear")
            searchLine.setText(TEXT_DEF)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTextValue = s ?: ""
                clearButton.visibility = clearButtonVisibility(s)

                if (s.isNullOrEmpty()) {
                    showMessage("clear")
                    if (trackListHistory.isNotEmpty()) {
                        if (searchLine.hasFocus()) {
                            content.addView(historyView)
                        }
                        else {
                            content.removeAllViews()
                        }
                    }
                }
                else {
                    content.removeAllViews()
                }
            }
            override fun afterTextChanged(s: Editable?) {
                searchTextValue = s
            }
        }
        searchLine.addTextChangedListener(simpleTextWatcher)

        recyclerView = findViewById(R.id.itemsList)
        recyclerView.visibility = View.GONE
        trackList = ArrayList()
        trackAdapter = TrackAdapter(trackList, searchHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
        recyclerViewHistory = historyView.findViewById(R.id.itemsHistoryList)

        trackListHistory.addAll(searchHistory.getHistory())

        trackAdapterHistory = TrackAdapter(trackListHistory, searchHistory)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = trackAdapterHistory

        trackAdapterHistory.notifyDataSetChanged()

        val clearHistoryButton = historyView.findViewById<Button>(R.id.clearHistoryButton)
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            content.removeAllViews()
        }

        searchLine.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && searchLine.text.isEmpty() && trackListHistory.isNotEmpty()) {
                content.addView(historyView)
            } else {
                content.removeView(historyView)
            }
        }
    }

    private fun hideInput() {
        val searchLine = findViewById<EditText>(R.id.inputSearchText)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchLine.windowToken, 0)
    }

    private fun performSearch(query: String) {
        showMessage("loading")
        RetrofitClient.trackService.search(query).enqueue(object : Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let {
                        if (it.isNotEmpty()) {
                            showMessage("hide")
                            trackList.addAll(it)
                            trackAdapter.notifyDataSetChanged()
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            showMessage("empty")
                        }
                    } ?: run {
                        showMessage("empty")
                    }
                } else {
                    showMessage("error")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showMessage("error")
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(type: String) {
        content.removeAllViews()

        if (type == "hide") return

        trackList.clear()
        recyclerView.visibility = View.GONE
        trackAdapter.notifyDataSetChanged()

        if (type == "clear") return

        val messageView = LayoutInflater.from(this).inflate(R.layout.message_block, content, false)

        val messageImage: ImageView = messageView.findViewById(R.id.placeholderImage)
        val messageText: TextView = messageView.findViewById(R.id.placeholderText)
        val updateButton: TextView = messageView.findViewById(R.id.updateButton)

        updateButton.setOnClickListener {
            performSearch(searchLine.text.toString())
        }

        updateButton.visibility = View.GONE

        when (type) {
            "loading" -> {
                messageImage.visibility = View.GONE
                messageText.text = getString(R.string.loading)
            }
            "empty" -> {
                messageImage.setImageResource(R.drawable.no_results)
                messageImage.visibility = View.VISIBLE
                messageText.text = getString(R.string.no_results)
            }
            "error" -> {
                messageImage.setImageResource(R.drawable.net_error)
                messageImage.visibility = View.VISIBLE
                messageText.text = getString(R.string.net_error)
                updateButton.visibility = View.VISIBLE
            }
        }

        content.addView(messageView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reloadHistory() {
        if (trackListHistory.isNotEmpty()) trackListHistory.clear()
        trackListHistory.addAll(searchHistory.getHistory())
        trackAdapterHistory.notifyDataSetChanged()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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
        const val APP_HISTORY = "search_history"
    }
}