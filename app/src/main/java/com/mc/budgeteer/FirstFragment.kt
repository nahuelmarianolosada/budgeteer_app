package com.mc.budgeteer

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.mc.budgeteer.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private val budgetItems = mutableListOf<BudgetItem>()
    private lateinit var tableLayout: TableLayout
    private lateinit var totalAmountTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableLayout = binding.tableLayout
        totalAmountTextView = binding.totalAmount

        //populateTable()

        binding.addButton.setOnClickListener {
            addItemRow("", "", "", "")
        }

        updateTotalAmount()
    }

    private fun addItemRow(description: String, quantity: String, unitPrice: String, totalPrice: String) {
        val row = TableRow(context)

        val descriptionEditText = EditText(context).apply {
            setText(description)
            inputType = InputType.TYPE_CLASS_TEXT
        }
        val quantityEditText = EditText(context).apply {
            setText(quantity)
            inputType = InputType.TYPE_CLASS_NUMBER
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    recalculateRowTotal(row)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
        val unitPriceEditText = EditText(context).apply {
            setText(unitPrice)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    recalculateRowTotal(row)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
        val totalPriceEditText = EditText(context).apply {
            setText(totalPrice)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            isEnabled = false
        }
        val deleteButton = Button(context).apply {
            text = "ELIMINAR"
            setOnClickListener {
                tableLayout.removeView(row)
                updateTotalAmount()
            }
        }

        row.addView(descriptionEditText)
        row.addView(quantityEditText)
        row.addView(unitPriceEditText)
        row.addView(totalPriceEditText)
        row.addView(deleteButton)

        tableLayout.addView(row)
    }


    private fun recalculateRowTotal(row: TableRow) {
        val quantityEditText = row.getChildAt(1) as EditText
        val unitPriceEditText = row.getChildAt(2) as EditText
        val totalPriceEditText = row.getChildAt(3) as EditText

        val quantity = quantityEditText.text.toString().replace("$", "").replace(".", "").toDoubleOrNull() ?: 0.0
        val unitPrice = unitPriceEditText.text.toString().replace("$", "").replace(".", "").toDoubleOrNull() ?: 0.0

        val totalPrice = quantity * unitPrice
        totalPriceEditText.setText("$%.2f".format(totalPrice))

        updateTotalAmount()
    }

    private fun updateTotalAmount() {
        var totalAmount = 0.0
        for (i in 1 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            val totalPriceEditText = row.getChildAt(3) as EditText
            val totalPrice = totalPriceEditText.text.toString().replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
            totalAmount += totalPrice
        }
        totalAmountTextView.text = "TOTAL: $${"%,.2f".format(totalAmount)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateTable() {
        val items = listOf(
            BudgetItem("Bolsas de Masilla Nivel Zero", "3", "$5.700", "$17.100"),
            BudgetItem("Latas de pintura epoxy", "2", "$16.500", "$33.000"),
            BudgetItem("Set de Resina", "4", "$62.000", "$248.000"),
            BudgetItem("Mano de obra x 15M2", "1", "$125.000", "$175.000")
        )

        items.forEach { addItemRow(it.description, it.quantity, it.unitPrice, it.totalPrice) }
    }

//    private fun addItemRow(description: String, quantity: String, unitPrice: String, totalPrice: String) {
//        val tableRow = TableRow(requireContext())
//
//        val totalPriceEditText = EditText(requireContext())
//        totalPriceEditText.setText(totalPrice)
//        totalPriceEditText.inputType = InputType.TYPE_NULL
//        totalPriceEditText.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
//        totalPriceEditText.isEnabled = false
//        totalPriceEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                updateTotalAmount()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//
//        val descriptionEditText = EditText(requireContext())
//        descriptionEditText.setText(description)
//        descriptionEditText.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
//        tableRow.addView(descriptionEditText)
//
//        val unitPriceEditText = EditText(requireContext())
//        unitPriceEditText.setText(unitPrice)
//        unitPriceEditText.inputType = InputType.TYPE_CLASS_NUMBER
//        unitPriceEditText.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
//        unitPriceEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                if (p0.toString() != "") {
//                    var unitPriceFloat = p0.toString().replace("$", "").replace(".", "").toFloatOrNull()
//                    var quantityFloat = p0.toString().toFloatOrNull()
//                    val totalPrice = unitPriceFloat!! * quantityFloat!!
//                    totalPriceEditText.setText(totalPrice.toString())
//                    updateTotalAmount()
//                    Toast.makeText(
//                        requireContext(),
//                        "Total actualizado",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//
//        val cantidadEditText = EditText(requireContext())
//        cantidadEditText.setText(quantity)
//        cantidadEditText.inputType = InputType.TYPE_CLASS_NUMBER
//        cantidadEditText.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
//        cantidadEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                if (p0.toString() != "") {
//                    var unitPriceFloat = unitPrice.replace("$", "").replace(".", "").toFloatOrNull()
//
//                    var quantityFloat = p0.toString().toFloatOrNull()
//                    val totalPrice = unitPriceFloat!! * quantityFloat!!
//                    totalPriceEditText.setText(totalPrice.toString())
//                    updateTotalAmount()
//                    Toast.makeText(
//                        requireContext(),
//                        "Total actualizado",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//
//        tableRow.addView(cantidadEditText)
//        tableRow.addView(unitPriceEditText)
//        tableRow.addView(totalPriceEditText)
//
//        val deleteButton = Button(requireContext())
//        deleteButton.text = "Eliminar"
//        deleteButton.setOnClickListener {
//            binding.tableLayout.removeView(tableRow)
//            updateTotalAmount()
//        }
//        tableRow.addView(deleteButton)
//
//        binding.tableLayout.addView(tableRow)
//    }
}

data class BudgetItem(val description: String, val quantity: String, val unitPrice: String, val totalPrice: String)

