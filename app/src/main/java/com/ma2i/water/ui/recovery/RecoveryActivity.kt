package com.ma2i.water.ui.recovery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ma2i.water.R

/**
 * A simple Recovery screen that explains the problem and offers actions:
 * - Restore from backup (launch file picker)
 * - Try another password (open password prompt)
 * - Export current file for analysis (share)
 *
 * Integrate the actual import/restore logic where noted.
 */
class RecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a simple layout or reuse existing layout: res/layout/activity_recovery.xml
        setContentView(R.layout.activity_recovery)

        val titleTv = findViewById<TextView>(R.id.recoveryTitle)
        val descTv = findViewById<TextView>(R.id.recoveryDesc)
        val restoreBtn = findViewById<Button>(R.id.recoveryRestoreBtn)
        val tryPwdBtn = findViewById<Button>(R.id.recoveryTryPwdBtn)
        val exportBtn = findViewById<Button>(R.id.recoveryExportBtn)

        titleTv.text = "تعذّر قراءة بيانات التطبيق"
        descTv.text = "قد تكون النسخة المحلية تالفة أو كلمة السر غير صحيحة. يمكنك استعادة نسخة احتياطية أو تجربة كلمة سر مختلفة."

        restoreBtn.setOnClickListener {
            // Launch a file picker for the user to select backup file
            // Example: use ACTION_OPEN_DOCUMENT with mime type application/json or your custom extension
            launchBackupPicker()
        }

        tryPwdBtn.setOnClickListener {
            // Show a password entry dialog (implement your dialog)
            showPasswordPrompt()
        }

        exportBtn.setOnClickListener {
            // Export current damaged file for analysis (share as attachment)
            exportCurrentFileForAnalysis()
        }
    }

    private fun launchBackupPicker() {
        // Implement actual file picker. Example (pseudo):
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_BACKUP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_BACKUP && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            // Read the selected file, validate header (version, salt), then attempt decrypt/import
            // If decrypt fails -> show informative dialog and do not overwrite current DB
            AlertDialog.Builder(this)
                .setTitle("استيراد النسخة الاحتياطية")
                .setMessage("تم اختيار ملف النسخة الاحتياطية. الرجاء إدخال كلمة السر لفك التشفير (إن وُجد).")
                .setPositiveButton("موافق") { _, _ ->
                    // call your import flow
                }
                .setNegativeButton("إلغاء", null)
                .show()
        }
    }

    private fun showPasswordPrompt() {
        // Implement password dialog, then try importing
    }

    private fun exportCurrentFileForAnalysis() {
        // Implement share intent logic to allow user to send the current encrypted file to support
    }

    companion object {
        const val REQUEST_CODE_PICK_BACKUP = 5021
    }
}
