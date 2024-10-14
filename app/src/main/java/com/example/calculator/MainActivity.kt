package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), OnClickListener {
    lateinit var buttonNumbers: IntArray
    lateinit var buttonOperators: IntArray
    lateinit var textview: TextView
    var op1: Long = 0
    var op2: Long? = null
    var op: Char = 'N' // 1: + || 2: - || 3: x || 4: /
    var flag: Int = 1 // 1 ~ op1 || 2 ~ op2
    var equalBefore: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        textview= findViewById(R.id.textView)
        buttonNumbers = intArrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9

        )
        buttonOperators = intArrayOf(
            R.id.buttonDivision, R.id.buttonMultiplication, R.id.buttonSubtraction, R.id.buttonPlus
        )
        val buttonFunctions = arrayOf(
            R.id.buttonCE, R.id.buttonC, R.id.buttonBS,
            R.id.buttonEqual, R.id.buttonDot, R.id.buttonTrans, R.id.buttonDot
        )

        for (id in buttonNumbers) {
            findViewById<Button>(id).setOnClickListener(this)
        }
        for (id in buttonFunctions) {
            findViewById<Button>(id).setOnClickListener(this)
        }
        for (id in buttonOperators) {
            findViewById<Button>(id).setOnClickListener(this)
        }
    }

    override fun onClick(p0: View?) {
        if(p0 != null){
            // Khi ấn CE
            if(p0.id == R.id.buttonCE)
                handleClearElement()
            // Khi ấn C
            if(p0.id == R.id.buttonC)
                handleClear()
            // khi ấn BS
            if(p0.id == R.id.buttonBS)
                handleBackspace()
            // khi ấn đổi dấu
            if(p0.id == R.id.buttonTrans)
                handleTrans()
            // Khi ấn toán tử
            if(buttonOperators.contains(p0.id))
                handleOperator(p0)
            // Khi ấn số
            if(buttonNumbers.contains(p0.id))
                handleNumber(p0)
            equalBefore = false
            // Khi ấn dấu =
            if(p0.id == R.id.buttonEqual)
                handleEqual()

        }
    }

    fun handleClearElement() {
        var text: String = ""
        if(flag == 1) {
            op1 = 0
            op2 = null
            text = "0"
        } else if(flag == 2) {
            op2 = null
            text = op1.toString() + op
        }
        textview.text = text
    }
    fun handleClear() {
        flag = 1
        op1 = 0
        op2 = null
        op = 'N'
        textview.text = "0"
    }
    fun handleBackspace() {
        var text: String = ""
        if(flag == 1) {
            op1 /= 10
            text = op1.toString()
        }else if(flag == 2){
            if(op2 == null) {
                flag = 1
                op = 'N'
                text = op1.toString()
            } else {
                op2 = op2!! / 10
                if(op2 == 0.toLong()) {
                    op2 = null
                    text = op1.toString() + op
                } else
                    text = op1.toString() + op + op2.toString()
            }
        }
        textview.text = text
    }
    fun handleTrans() {
        var text: String = ""
        if(flag == 1){
            op1 = -op1
            text = op1.toString()
        } else {
            if(op2 == null) return
            else {
                if(op == '-') op = '+'
                else if(op == '+') op = '-'
                else op2 = -op2!!
                text = op1.toString() + op + op2.toString()
            }
        }
        textview.text = text
    }
    fun handleOperator(operation: View?) {
        var text: String = ""
        val newOp: Char = checkOperator(operation)
        if(flag == 1) {
            flag = 2
            op = newOp
            text = op1.toString() + op.toString()
        } else if(flag == 2) {
            if(op2 == null) {
                op = newOp
                text = op1.toString() + op
            } else {
                if(op == '/' && op2 == 0.toLong()) {
                    flag = 1
                    op1 = 0
                    op2 = null
                    op = 'N'
                    text = "Không thể chia cho 0"
                } else {
                    val result =  calculator(op1, op2!!, op)
                    op1 = result
                    op2 = null
                    flag = 2
                    op = newOp
                    text = op1.toString() + op
                }
            }
        }
        textview.text = text
    }
    fun handleNumber(number: View?) {
        var Number= resources.getResourceEntryName(number!!.id).replace("button", "").toLong()
        // Log.d("ButtonClick", "Button ID: $buttonName pressed")
        var text: String = ""
        if(flag == 1) {
            if(equalBefore) {
                op1 = 0
                equalBefore = false
            }
            op1 = op1*10 + Number
            text = op1.toString()
        } else if(flag == 2){
            if(op2 == null) op2 = 0
            op2 = op2!! *10 + Number
            text = op1.toString() + op + op2.toString()
        }
        textview.text = text
    }
    fun handleEqual() {
        var text: String = ""
        if(flag == 1)
            return
        else {
            if(op2 == null)
                return
            else if(op == '/' && op2 == 0.toLong()) {
                flag = 1
                op1 = 0
                op2 = null
                op = 'N'
                text = "Không thể chia cho 0"
            } else {
                val result = calculator(op1, op2!!, op)
                // Tạo chuỗi biểu thức đầy đủ
                text = "$op1$op$op2=$result"
                op1 = result
                op2 = null
                flag = 1
                op = 'N'
                equalBefore = true
            }
        }
        textview.text = text
    }

    fun checkOperator(operation: View?): Char {
        val Op = resources.getResourceEntryName(operation!!.id).toString()
        if(Op == "buttonDivision")
            return '/'
        else if(Op == "buttonMultiplication")
            return 'x'
        else if(Op == "buttonSubtraction")
            return '-'
        return '+'
    }
    fun calculator(a: Long, b: Long, OP: Char): Long {
        if(OP == '+')
            return a + b
        else if(OP == '-')
            return a - b
        else if(OP == 'x')
            return a*b
        else return a/b
    }
}