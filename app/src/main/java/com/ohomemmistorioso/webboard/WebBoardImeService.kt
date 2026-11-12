package com.ohomemmistorioso.webboard

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class WebBoardImeService : InputMethodService() {
    private var browserMode = false
    private var webView: WebView? = null
    private var address: EditText? = null
    private var browserPanel: LinearLayout? = null
    private var keyboardPanel: LinearLayout? = null

    private val dark = Color.rgb(22, 29, 38)
    private val key = Color.rgb(49, 61, 75)
    private val accent = Color.rgb(35, 155, 210)

    override fun onCreateInputView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(dark)
            setPadding(dp(5), dp(5), dp(5), dp(7))
        }

        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        toolbar.addView(actionButton("🌐") { showBrowser() }, LinearLayout.LayoutParams(dp(48), dp(42)))
        val hint = TextView(this).apply {
            text = "WebBoard"
            textSize = 15f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
        }
        toolbar.addView(hint, LinearLayout.LayoutParams(0, dp(42), 1f))
        toolbar.addView(actionButton("⌨") { showKeyboard() }, LinearLayout.LayoutParams(dp(48), dp(42)))
        root.addView(toolbar)

        browserPanel = buildBrowserPanel()
        keyboardPanel = buildKeyboardPanel()
        root.addView(browserPanel, LinearLayout.LayoutParams(-1, dp(218)))
        root.addView(keyboardPanel, LinearLayout.LayoutParams(-1, dp(218)))
        showKeyboard()
        return root
    }

    private fun buildBrowserPanel(): LinearLayout {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }
        val addressRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        address = EditText(this).apply {
            singleLine = true
            textSize = 14f
            hint = "Digite um site ou pesquise"
            setTextColor(Color.WHITE)
            setHintTextColor(0xFFB7C2CE.toInt())
            setBackground(roundRect(key, 10))
            setPadding(dp(10), 0, dp(10), 0)
        }
        addressRow.addView(address, LinearLayout.LayoutParams(0, dp(42), 1f))
        addressRow.addView(actionButton("Ir") { navigate() }, LinearLayout.LayoutParams(dp(54), dp(42)))
        panel.addView(addressRow)

        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        controls.addView(actionButton("‹") { if (webView?.canGoBack() == true) webView?.goBack() })
        controls.addView(actionButton("›") { if (webView?.canGoForward() == true) webView?.goForward() })
        controls.addView(actionButton("↻") { webView?.reload() })
        controls.addView(actionButton("⌂") { loadPage("https://www.google.com") })
        panel.addView(controls)

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    address?.setText(url)
                }
            }
            webChromeClient = WebChromeClient()
            loadUrl("https://www.google.com")
        }
        panel.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        return panel
    }

    private fun buildKeyboardPanel(): LinearLayout {
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val rows = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
        rows.forEachIndexed { index, letters ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            if (index == 2) row.addView(keyButton("⇧") { })
            letters.forEach { char ->
                row.addView(keyButton(char.toString()) {
                    currentInputConnection?.commitText(char.toString(), 1)
                }, LinearLayout.LayoutParams(0, dp(48), 1f))
            }
            if (index == 2) row.addView(keyButton("⌫") {
                currentInputConnection?.deleteSurroundingText(1, 0)
            })
            panel.addView(row, LinearLayout.LayoutParams(-1, dp(52)))
        }
        val bottom = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        bottom.addView(keyButton("?123") { })
        bottom.addView(keyButton(",") { currentInputConnection?.commitText(",", 1) })
        bottom.addView(keyButton("☻") { currentInputConnection?.commitText("🙂", 1) })
        bottom.addView(keyButton("Espaço") { currentInputConnection?.commitText(" ", 1) },
            LinearLayout.LayoutParams(0, dp(46), 1f))
        bottom.addView(keyButton(".") { currentInputConnection?.commitText(".", 1) })
        bottom.addView(keyButton("↵") {
            val info = currentInputEditorInfo
            if (info != null) currentInputConnection?.performEditorAction(info.imeOptions and EditorInfo.IME_MASK_ACTION)
            else currentInputConnection?.commitText("\n", 1)
        })
        panel.addView(bottom, LinearLayout.LayoutParams(-1, dp(48)))
        return panel
    }

    private fun showBrowser() {
        browserMode = true
        browserPanel?.visibility = View.VISIBLE
        keyboardPanel?.visibility = View.GONE
    }

    private fun showKeyboard() {
        browserMode = false
        browserPanel?.visibility = View.GONE
        keyboardPanel?.visibility = View.VISIBLE
    }

    private fun navigate() {
        val raw = address?.text?.toString()?.trim().orEmpty()
        if (raw.isBlank()) return
        val url = when {
            raw.startsWith("https://") || raw.startsWith("http://") -> raw
            raw.contains(".") && !raw.contains(" ") -> "https://$raw"
            else -> "https://www.google.com/search?q=" + android.net.Uri.encode(raw)
        }
        loadPage(url)
    }

    private fun loadPage(url: String) {
        webView?.loadUrl(url)
        address?.setText(url)
    }

    private fun actionButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            textSize = 13f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = roundRect(accent, 10)
            setPadding(dp(3), 0, dp(3), 0)
            setOnClickListener { action() }
        }

    private fun keyButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            textSize = if (label.length > 1) 12f else 18f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = roundRect(key, 7)
            setPadding(dp(1), 0, dp(1), 0)
            setOnClickListener { action() }
            layoutParams = LinearLayout.LayoutParams(0, dp(46), 1f).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            }
        }

    private fun roundRect(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (!browserMode) showKeyboard()
    }

    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}
