package com.md.skrivaci

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.Locale
import kotlin.math.ceil

class MainActivity : Activity() {
    private lateinit var boardWidth: EditText
    private lateinit var boardHeight: EditText
    private lateinit var boardFace: EditText
    private lateinit var roomWidth: EditText
    private lateinit var roomLength: EditText
    private lateinit var roomPerimeter: EditText
    private lateinit var boardInfo: TextView
    private lateinit var totalGypsum: TextView
    private lateinit var totalUd: TextView
    private lateinit var roomResults: TextView

    private var roomCount = 0
    private var gypsumTotal = 0
    private var udTotal = 0
    private val entries = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardWidth = findViewById(R.id.boardWidth)
        boardHeight = findViewById(R.id.boardHeight)
        boardFace = findViewById(R.id.boardFace)
        roomWidth = findViewById(R.id.roomWidth)
        roomLength = findViewById(R.id.roomLength)
        roomPerimeter = findViewById(R.id.roomPerimeter)
        boardInfo = findViewById(R.id.boardInfo)
        totalGypsum = findViewById(R.id.totalGypsum)
        totalUd = findViewById(R.id.totalUd)
        roomResults = findViewById(R.id.roomResults)

        val prefs = getSharedPreferences("skrivaci", MODE_PRIVATE)
        roomCount = prefs.getInt("roomCount", 0)
        gypsumTotal = prefs.getInt("gypsumTotal", 0)
        udTotal = prefs.getInt("udTotal", 0)
        val savedEntries = prefs.getString("entries", "").orEmpty()
        if (savedEntries.isNotBlank()) entries.addAll(savedEntries.split("\n"))
        updateTotals()

        findViewById<Button>(R.id.calcDimensions).setOnClickListener { calculateByDimensions() }
        findViewById<Button>(R.id.calcPerimeter).setOnClickListener { calculateByPerimeter() }
        findViewById<Button>(R.id.reset).setOnClickListener { resetAll() }
    }

    private fun number(field: EditText): Double? = field.text.toString().trim()
        .replace(',', '.').toDoubleOrNull()

    private fun boardRatio(): Double? {
        val w = number(boardWidth)
        val h = number(boardHeight)
        val f = number(boardFace)
        if (w == null || h == null || f == null || w <= 0 || h <= 0 || f < 0) return null
        val sum = w + h + f
        var ratio = 120.0 / sum
        if (ratio < 2.0) ratio = 1.0
        return ratio
    }

    private fun calculateByDimensions() {
        val ratio = boardRatio()
        val w = number(roomWidth)
        val l = number(roomLength)
        if (ratio == null) { toast("Prvo unesi ispravne dimenzije škrivača."); return }
        if (w == null || l == null || w <= 0 || l <= 0) { toast("Unesi širinu i dužinu sobe u metrima."); return }
        val perimeter = ceil((w + l) * 2.0)
        val ud = ceil(perimeter * 4.0).toInt()
        val gypsum = ceil((perimeter / ratio) / 2.0).toInt()
        addRoom(ud, gypsum, "Po dimenzijama (${fmt(w)} × ${fmt(l)} m)")
        roomWidth.text.clear(); roomLength.text.clear()
    }

    private fun calculateByPerimeter() {
        val ratio = boardRatio()
        val p = number(roomPerimeter)
        if (ratio == null) { toast("Prvo unesi ispravne dimenzije škrivača."); return }
        if (p == null || p <= 0) { toast("Unesi ispravan obim sobe u metrima."); return }
        val perimeter = ceil(p)
        val udBeforeDivision = ceil(perimeter * 4.0)
        val ud = ceil(udBeforeDivision / 3.0).toInt()
        val gypsum = ceil((perimeter / ratio) / 2.0).toInt()
        addRoom(ud, gypsum, "Po obimu (${fmt(p)} m)")
        roomPerimeter.text.clear()
    }

    private fun addRoom(ud: Int, gypsum: Int, description: String) {
        roomCount++
        udTotal += ud
        gypsumTotal += gypsum
        entries.add("SOBA $roomCount — $description: $gypsum gips, $ud UD")
        saveAndUpdate()
    }

    private fun resetAll() {
        roomCount = 0; gypsumTotal = 0; udTotal = 0; entries.clear()
        getSharedPreferences("skrivaci", MODE_PRIVATE).edit().clear().apply()
        updateTotals()
        toast("Obračun je obrisan.")
    }

    private fun saveAndUpdate() {
        getSharedPreferences("skrivaci", MODE_PRIVATE).edit()
            .putInt("roomCount", roomCount)
            .putInt("gypsumTotal", gypsumTotal)
            .putInt("udTotal", udTotal)
            .putString("entries", entries.joinToString("\n"))
            .apply()
        updateTotals()
    }

    private fun updateTotals() {
        totalGypsum.text = gypsumTotal.toString()
        totalUd.text = udTotal.toString()
        roomResults.text = if (entries.isEmpty()) "Još nema obračunatih soba." else entries.joinToString("\n\n")
        boardInfo.text = boardRatio()?.let { "Odnos za obračun gipsa: 120 ÷ (širina + visina + čelo) = ${fmt(it)}" }
            ?: "Unesi dimenzije škrivača da bi se izračunao broj gipsanih ploča."
    }

    private fun fmt(value: Double): String = String.format(Locale.getDefault(), "%.2f", value)
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
