package com.ma2i.water.ui.sales

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ma2i.water.R
import com.ma2i.water.data.model.Sale
import com.ma2i.water.data.repository.SaleRepository

/**
 * Example Activity demonstrating how to wire UI to the ExampleSaleViewModel and respond to SaveState.
 * Your real Activity should replace the simple input fields with the actual form used in the app.
 */
class ExampleSaleActivity : AppCompatActivity() {

    // You need to provide a real implementation of SaleRepository via a factory or DI.
    private val repository: SaleRepository by lazy {
        // TODO: Replace with actual repository e.g. AppContainer.saleRepository
        object : SaleRepository {
            override suspend fun saveSale(sale: Sale) {
                // No-op for example. Real impl writes to DB via DAO.
            }
        }
    }

    private val viewModel: ExampleSaleViewModel by lazy {
        // If you use a ViewModelFactory, supply repository properly.
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ExampleSaleViewModel(repository) as T
            }
        }).get(ExampleSaleViewModel::class.java)
    }

    private lateinit var amountInput: EditText
    private lateinit var customerIdInput: EditText
    private lateinit var noteInput: EditText
    private lateinit var saveBtn: Button
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale)

        amountInput = findViewById(R.id.inputAmount)
        customerIdInput = findViewById(R.id.inputCustomerId)
        noteInput = findViewById(R.id.inputNote)
        saveBtn = findViewById(R.id.btnSave)
        progress = findViewById(R.id.saveProgress)

        saveBtn.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val customerId = customerIdInput.text.toString().toLongOrNull() ?: 0L
            val note = noteInput.text.toString().takeIf { it.isNotBlank() }
            val sale = Sale(customerId = customerId, amount = amount, note = note)

            // Disable UI and show progress immediately
            saveBtn.isEnabled = false
            progress.visibility = View.VISIBLE

            viewModel.saveSale(sale)
        }

        viewModel.saveState.observe(this) { state ->
            when (state) {
                is ExampleSaleViewModel.SaveState.Idle -> {
                    progress.visibility = View.GONE
                    saveBtn.isEnabled = true
                }
                is ExampleSaleViewModel.SaveState.Saving -> {
                    progress.visibility = View.VISIBLE
                    saveBtn.isEnabled = false
                }
                is ExampleSaleViewModel.SaveState.Success -> {
                    progress.visibility = View.GONE
                    saveBtn.isEnabled = true
                    Toast.makeText(this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show()
                    // navigate back or clear form
                    finish()
                }
                is ExampleSaleViewModel.SaveState.Error -> {
                    progress.visibility = View.GONE
                    saveBtn.isEnabled = true
                    showSaveErrorDialog(state.message)
                }
            }
        }
    }

    private fun showSaveErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("خطأ أثناء الحفظ")
            .setMessage(message)
            .setPositiveButton("إعادة المحاولة") { _, _ ->
                // user can attempt save again - leave form values intact
            }
            .setNeutralButton("حفظ كمسودة") { _, _ ->
                // Optionally save draft locally (SharedPreferences / local file). Implement as needed.
                Toast.makeText(this, "تم حفظ المسودة محليًا", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("تصدير للنسخ الاحتياطي") { _, _ ->
                // Launch export flow so user can save current input as external backup
                // Implement export logic as needed
            }
            .show()
    }
}
