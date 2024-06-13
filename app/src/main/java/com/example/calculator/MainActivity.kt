package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var calculationDisplay: TextView
    private lateinit var display: TextView
    private var operand1: Double? = null
    private var operand2: Double? = null
    private var operator: String? = null
    private var isNewOp: Boolean = false
    private var lastResult: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculationDisplay = findViewById(R.id.calculationDisplay)
        display = findViewById(R.id.display)

        val buttons = intArrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide,
            R.id.buttonEquals, R.id.buttonClear, R.id.buttonSign, R.id.buttonPercent,
            R.id.buttonDot
        )

        buttons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener { onButtonClick(it as Button) }
        }
    }

    private fun onButtonClick(button: Button) {
        when (button.id) {
            R.id.buttonClear -> clear()
            R.id.buttonSign -> toggleSign()
            R.id.buttonPercent -> applyPercentage()
            R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide -> setOperator(button.text.toString())
            R.id.buttonEquals -> calculateResult()
            R.id.buttonDot -> appendDot()
            else -> appendNumber(button.text.toString())
        }
    }

    private fun appendNumber(number: String) {
        if (display.text.length >= 9) return

        display.text = if (isNewOp) {
            isNewOp = false
            number
        } else {
            if (display.text == "0") number else display.text.toString() + number
        }

        if (operator == null) {
            operand1 = display.text.toString().toDoubleOrNull()
        } else {
            operand2 = display.text.toString().toDoubleOrNull()
        }

        updateCalculationDisplay(false)
    }

    private fun appendDot() {
        if (display.text.contains(".") || display.text.length >= 9) return
        display.append(".")
        updateCalculationDisplay(false)
    }

    private fun setOperator(op: String) {
        if (operand1 != null) {
            operator = op
            isNewOp = true
        }
        updateCalculationDisplay(false)
    }

    private fun calculateResult() {
        operand2 = display.text.toString().toDoubleOrNull()
        if (operand1 != null && operand2 != null && operator != null) {
            val result = when (operator) {
                "+" -> operand1!! + operand2!!
                "-" -> operand1!! - operand2!!
                "×" -> operand1!! * operand2!!
                "÷" -> {
                    if (operand2 == 0.0) {
                        display.text = "Ошибка"
                        updateCalculationDisplay(true)
                        return
                    } else {
                        operand1!! / operand2!!
                    }
                }
                else -> null
            }

            val formattedResult = result?.let { formatNumber(it) } ?: "Ошибка"
            val calculationText = buildCalculationText()

            display.text = formattedResult
            lastResult = result
            operand1 = result
            operand2 = null
            operator = null
            isNewOp = true

            updateCalculationDisplay(true)
        }
    }

    private fun clear() {
        display.text = "0"
        operand1 = null
        operand2 = null
        operator = null
        isNewOp = true
        lastResult = null
        calculationDisplay.text = ""
    }

    private fun toggleSign() {
        val value = display.text.toString().toDoubleOrNull()
        if (value != null && value != 0.0) {
            display.text = formatNumber(-value)
            if (operator == null) {
                operand1 = display.text.toString().toDoubleOrNull()
            } else {
                operand2 = display.text.toString().toDoubleOrNull()
            }
        }
        updateCalculationDisplay(false)
    }

    private fun applyPercentage() {
        val value = display.text.toString().toDoubleOrNull()
        if (value != null) {
            display.text = formatNumber(value / 100)
            if (operator == null) {
                operand1 = display.text.toString().toDoubleOrNull()
            } else {
                operand2 = display.text.toString().toDoubleOrNull()
            }
        }
        updateCalculationDisplay(false)
    }

    private fun updateCalculationDisplay(isResult: Boolean) {
        val displayText = StringBuilder()
        operand1?.let { displayText.append(formatNumber(it)) }
        operator?.let { displayText.append(" $it ") }
        if (!isResult) {
            operand2?.let { displayText.append(formatNumber(it)) }
        }
        calculationDisplay.text = displayText.toString()
    }

    private fun buildCalculationText(): String {
        val displayText = StringBuilder()
        operand1?.let { displayText.append(formatNumber(it)) }
        operator?.let { displayText.append(" $it ") }
        operand2?.let { displayText.append(formatNumber(it)) }
        return displayText.toString()
    }

    private fun formatNumber(number: Double): String {
        val df = DecimalFormat("0.#########")
        return df.format(number)
    }
}