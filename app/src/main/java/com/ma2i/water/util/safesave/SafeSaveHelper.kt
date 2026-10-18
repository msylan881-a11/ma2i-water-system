package com.ma2i.water.util.safesave

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

/**
 * Wrapper for performing save operations safely.
 * Usage (in a ViewModel or Activity with lifecycleScope):
 *
 * safeSave(
 *   scope = lifecycleScope,
 *   dbSave = { dao.insert(sale) },
 *   fileSave = { backupManager.maybeAutoBackup() },
 *   onSuccess = { showToast("تم الحفظ بنجاح"); navigateBack() },
 *   onError = { e -> showErrorDialog(e) }
 * )
 */
object SafeSaveHelper {
    /**
     * Perform dbSave and optional fileSave on IO dispatcher.
     * Ensures any exception is propagated to onError (so UI won't show success prematurely).
     */
    fun safeSave(
        scope: CoroutineScope,
        dbSave: suspend () -> Unit,
        fileSave: (suspend () -> Unit)? = null,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // dbSave should handle Room transactions itself if needed
                    dbSave()
                    // optional additional IO step (e.g., auto-backup)
                    fileSave?.invoke()
                }
                // back to Main thread
                onSuccess()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }
}
