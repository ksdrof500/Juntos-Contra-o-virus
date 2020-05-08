package com.juntoscontracorona.ui.notification

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.juntoscontracorona.MainActivity
import com.juntoscontracorona.R
import com.juntoscontracorona.model.Help
import com.juntoscontracorona.model.Notification
import com.juntoscontracorona.util.FirebaseUtilListener
import com.juntoscontracorona.util.inflate
import com.juntoscontracorona.util.toastError
import com.juntoscontracorona.util.toastSuccess
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationAdapter(
    options: FirestoreRecyclerOptions<Notification>,
    private val firebaseUtilListener: FirebaseUtilListener
) :
    FirestoreRecyclerAdapter<Notification, NotificationAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onDataChanged() {
        super.onDataChanged()
        firebaseUtilListener.empty()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: Notification) {
        holder.bind(item, snapshots.getSnapshot(position).id)
    }

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_notification)) {
        fun bind(notification: Notification, idDocument: String) = with(itemView) {
            name.text = context.resources.getString(R.string.name_item, notification.name)
            phone.text = context.resources.getString(R.string.phone_item, notification.phone)

            MainActivity.myUser?.let { myUser ->
                if (notification.idHelping == myUser.id) {
                    cardView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                    helping.isVisible = false
                    confirmHelp.isVisible = true
                    notConfirmHelp.isVisible = true
                    if (myUser.help == Help.HELPER) {
                        confirmHelp.text = context.resources.getString(R.string.button_notification_helper_confirm)
                        notConfirmHelp.text = context.resources.getString(R.string.button_notification_helper_not_confirm)
                        notification.helpConfirm?.let {
                            confirmHelp.isSelected = it
                            notConfirmHelp.isSelected = !it
                        }
                    } else {
                        confirmHelp.text = context.resources.getString(R.string.button_notification_confirm)
                        notConfirmHelp.text = context.resources.getString(R.string.button_notification_not_confirm)
                        notification.helpConfirm?.let {
                            confirmHelp.isSelected = it
                            notConfirmHelp.isSelected = !it
                        }
                    }
                } else {
                    cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSalmon))
                    helping.isVisible = true
                    confirmHelp.isVisible = false
                    notConfirmHelp.isVisible = false
                    helping.text = notification.helpConfirm?.let {
                        helping.isSelected = it
                        if (it) {
                            context.resources.getString(R.string.button_notification_helping)
                        } else {
                            context.resources.getString(R.string.button_notification_not_helping)
                        }
                    } ?: context.resources.getString(R.string.button_notification_loading_helping)
                }
            }

            confirmHelp.setOnClickListener {
                save(true, context, idDocument)
            }

            notConfirmHelp.setOnClickListener {
                save(false, context, idDocument)
            }
        }

        private fun save(confirm: Boolean, context: Context, idDocument: String) {
            MainActivity.myUser?.let {
                FirebaseFirestore.getInstance().collection("notificationHelping")
                    .document(idDocument)
                    .update("helpConfirm", confirm)
                    .addOnSuccessListener {
                        context.toastSuccess(context.resources.getString(R.string.save_success_item))
                    }
                    .addOnFailureListener { context.toastError(context.resources.getString(R.string.save_error)) }
            }
        }

    }
}