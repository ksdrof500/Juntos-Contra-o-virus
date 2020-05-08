package com.juntoscontracorona.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.juntoscontracorona.MainActivity
import com.juntoscontracorona.R
import com.juntoscontracorona.model.Help
import com.juntoscontracorona.model.Notification
import com.juntoscontracorona.util.FirebaseUtilListener
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : Fragment(), FirebaseUtilListener {

    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.myUser?.let {
            val query = if (it.help == Help.HELPER) {
                FirebaseFirestore.getInstance()
                    .collection("notificationHelping")
                    .whereEqualTo("idHelping", it.id)
                    .limit(30)
            } else {
                FirebaseFirestore.getInstance()
                    .collection("notificationHelping")
                    .whereEqualTo("idHelper", it.id)
                    .limit(30)
            }

            val options: FirestoreRecyclerOptions<Notification> = FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification::class.java)
                .build()

            FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query) { snapshot -> snapshot.toObject(Notification::class.java)!! }
                .setLifecycleOwner(this)

            adapter = NotificationAdapter(options, this)
            recycler.adapter = adapter
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun empty() {
        empty.isVisible = adapter.itemCount == 0
    }

}
