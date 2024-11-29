package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_1 = findViewById<FrameLayout>(R.id.btn_search)

        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку ПОИСК", Toast.LENGTH_SHORT).show()
            }
        }

        btn_1.setOnClickListener(imageClickListener)

        val btn_2 = findViewById<FrameLayout>(R.id.btn_media)

        btn_2.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку МЕДИАТЕКА", Toast.LENGTH_SHORT).show()
        }

        val btn_3 = findViewById<FrameLayout>(R.id.btn_settings)

        btn_3.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку НАСТРОЙКИ", Toast.LENGTH_SHORT).show()
        }
    }
}