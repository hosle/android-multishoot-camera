package com.sfexpress.multishootcamera

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.widget.Toast

/**
 * Created by tanjiahao on 2018/1/8
 * Original Project MultiShootCamera
 */
class ConfirmationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity)

                .setMessage(arguments.getInt(ARG_MESSAGE))

                .setPositiveButton(android.R.string.ok, { _: DialogInterface, _: Int ->
                    val permissions = arguments.getStringArray(ARG_PERMISSIONS) ?: throw IllegalArgumentException()
                    ActivityCompat.requestPermissions(activity, permissions, arguments.getInt(ARG_REQUEST_CODE))
                })

                .setNegativeButton(android.R.string.cancel, { _: DialogInterface, _: Int ->
                    Toast.makeText(activity, arguments.getInt(ARG_NOT_GRANTED_MESSAGE), Toast.LENGTH_SHORT).show()
                })
                .create()
    }

    companion object {
        const val ARG_MESSAGE = "message"
        const val ARG_PERMISSIONS = "permissions"
        const val ARG_REQUEST_CODE = "request_code"
        const val ARG_NOT_GRANTED_MESSAGE = "not_granted_message"

        fun newInstance(message:Int,permissions: Array<String>,requestCode:Int,notGrantedMsg:Int) : ConfirmationDialogFragment{

            val args = Bundle().apply{
                putInt(ARG_MESSAGE,message)
                putStringArray(ARG_PERMISSIONS,permissions)
                putInt(ARG_REQUEST_CODE,requestCode)
                putInt(ARG_NOT_GRANTED_MESSAGE,notGrantedMsg)
            }

            return ConfirmationDialogFragment().apply { arguments = args }
        }

    }
}