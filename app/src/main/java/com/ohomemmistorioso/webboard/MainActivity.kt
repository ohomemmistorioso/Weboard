package com.ohomemmistorioso.webboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 36, 28, 28)
            setBackgroundColor(0xFF101820.toInt())
        }
        val title = TextView(this).apply {
            text = "WebBoard"
            textSize = 30f
            setTextColor(0xFFFFFFFF.toInt())
        }
        val description = TextView(this).apply {
            text = "Seu teclado com uma mini janela de navegação integrada.\n\nVersão 0.1 — protótipo inicial."
            textSize = 16f
            setTextColor(0xFFD5DEE7.toInt())
            setPadding(0, 12, 0, 28)
        }
        val enable = Button(this).apply {
            text = "1. Ativar WebBoard"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        }
        val choose = Button(this).apply {
            text = "2. Escolher WebBoard"
            setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }
        root.addView(title)
        root.addView(description)
        root.addView(enable)
        root.addView(choose)
        setContentView(root)
    }
}
