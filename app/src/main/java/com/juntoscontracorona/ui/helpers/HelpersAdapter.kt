package com.juntoscontracorona.ui.helpers

import android.location.Location
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.juntoscontracorona.MainActivity
import com.juntoscontracorona.R
import com.juntoscontracorona.model.Notification
import com.juntoscontracorona.model.User
import com.juntoscontracorona.util.*
import kotlinx.android.synthetic.main.item_helpers.view.*

class HelpersAdapter(
    options: FirestoreRecyclerOptions<User>,
    private val firebaseUtilListener: FirebaseUtilListener
) :
    FirestoreRecyclerAdapter<User, HelpersAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onDataChanged() {
        super.onDataChanged()
        firebaseUtilListener.empty()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: User) {
        holder.bind(item)
    }

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_helpers)) {
        fun bind(user: User) = with(itemView) {
            Glide
                .with(context)
                .load(user.image)
                .centerCrop()
                .placeholder(CircularProgressDrawable(context))
                .into(profileImage)

            if (!user.beingHelped.isNullOrBlank()) {
                buttonHelp.isSelected = true
                buttonHelp.setTextColor(ContextCompat.getColor(context, R.color.gray5))
                buttonHelp.text =
                    context.resources.getString(R.string.being_helper_item, user.helpingCount)
                distance.isVisible = false
            } else {
                buttonHelp.isSelected = false
                buttonHelp.text = context.resources.getString(R.string.button_get_help)
                distance.isVisible = true
                MainActivity.location?.let { myLocation ->
                    if (user.latLong != null) {
                        val helperLocation = Location("")
                        helperLocation.latitude = user.latLong?.latitude!!
                        helperLocation.longitude = user.latLong?.latitude!!
                        distance.text = context.resources.getString(
                            R.string.distance_item,
                            dec.format(myLocation.distanceTo(helperLocation).div(1000))
                        )
                    }
                }

            }

            name.text = context.resources.getString(R.string.name_item, user.name)

            listNeed.removeAllViews()
            if (user.store) {
                val store = TextView(context)
                store.text = context.resources.getString(R.string.store)
                store.setPadding(0, 4, 20, 4)
                store.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                listNeed.addView(store)
            }
            if (user.talk) {
                val talk = TextView(context)
                talk.text = context.resources.getString(R.string.talk)
                talk.setPadding(0, 4, 20, 4)
                talk.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                listNeed.addView(talk)
            }
            if (user.pharmacy) {
                val pharmacy = TextView(context)
                pharmacy.text = context.resources.getString(R.string.pharmacy)
                pharmacy.setPadding(0, 4, 20, 4)
                pharmacy.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                listNeed.addView(pharmacy)
            }

            buttonHelp.setOnClickListener {
                if (FirebaseAuth.getInstance().currentUser?.uid != user.id) {
                    FirebaseFirestore.getInstance().collection("user")
                        .document(user.id)
                        .set(user)
                        .addOnSuccessListener { context.toastSuccess(context.resources.getString(R.string.save_success_item)) }
                        .addOnFailureListener { context.toastError(context.resources.getString(R.string.save_error)) }

                    MainActivity.myUser?.let {
                        FirebaseFirestore.getInstance().collection("notificationHelping")
                            .document()
                            .set(Notification(it.id, it.name, it.phone, it.token, user.id, user.token))
                    }
                }
            }
        }
    }

}