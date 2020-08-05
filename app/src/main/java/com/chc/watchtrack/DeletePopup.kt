package com.chc.watchtrack

import android.app.Activity
import android.app.Dialog
import android.widget.Button

// Popup to confirm deletion of selected items
class DeletePopup {
    fun showDeletePopup(activity: Activity, delete: () -> Unit) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.delete_popup)

        // Dismiss dialog on cancel
        dialog.findViewById<Button>(R.id.cancel_btn).setOnClickListener {
            dialog.dismiss()
        }

        // Performs the given delete action when delete button is pressed and dismisses dialog
        dialog.findViewById<Button>(R.id.delete_btn).setOnClickListener {
            delete()
            dialog.dismiss()
        }

        dialog.show()
    }

}