package org.hyperskill.calculator.tip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit_text.addTextChangedListener { displayTip() }
        slider.addOnChangeListener { _, _, _ -> displayTip() }
    }

    private fun displayTip() {
        val billText = edit_text.text

        if (billText.isNotBlank()) {
            val billValue: Double = edit_text.text.toString().toDouble()
            val tipPercentage: Int = slider.value.toInt()
            val tipValue = BigDecimal(billValue * tipPercentage / 100.0)
            val tip: String = tipValue.setScale(2, RoundingMode.HALF_EVEN).toString()

            text_view.text = "Tip amount: $tip"
        } else {
            text_view.text = ""
        }
    }
}
