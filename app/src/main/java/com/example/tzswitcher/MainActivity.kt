package com.example.tzswitcher

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import java.util.TimeZone

class MainActivity : Activity() {

    private lateinit var current: TextView
    private val all: List<String> = TimeZone.getAvailableIDs()
        .filter { it.contains("/") && !it.startsWith("Etc/") }
        .sorted()
    private val shown = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 24)
        }

        current = TextView(this).apply { textSize = 18f }
        refreshCurrent()

        val toggle = Button(this).apply {
            text = "立即切换"
            setOnClickListener { Thread { TzSwitcher.toggle(context) }.start() }
        }

        val search = EditText(this).apply {
            hint = "搜索城市，如 new york"
            isSingleLine = true
        }

        val list = ListView(this)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shown)
        list.adapter = adapter
        list.setOnItemClickListener { _, _, pos, _ ->
            TzSwitcher.setTarget(this, shown[pos])
            refreshCurrent()
            Toast.makeText(this, "已设为 ${shown[pos]}", Toast.LENGTH_SHORT).show()
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filter(s.toString())
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })
        filter("")

        box.addView(current, MATCH_PARENT, WRAP_CONTENT)
        box.addView(toggle, MATCH_PARENT, WRAP_CONTENT)
        box.addView(search, MATCH_PARENT, WRAP_CONTENT)
        box.addView(list, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
        setContentView(box)
    }

    private fun refreshCurrent() {
        current.text = "目标时区：${TzSwitcher.target(this)}"
    }

    private fun filter(q: String) {
        val key = q.trim().replace(" ", "_").lowercase()
        shown.clear()
        shown.addAll(all.filter { it.lowercase().contains(key) })
        adapter.notifyDataSetChanged()
    }
}
