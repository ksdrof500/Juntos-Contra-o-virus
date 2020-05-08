package com.juntoscontracorona.ui.helpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.juntoscontracorona.R
import com.juntoscontracorona.model.Help
import com.juntoscontracorona.model.User
import com.juntoscontracorona.util.FirebaseUtilListener
import kotlinx.android.synthetic.main.fragment_helping.*

class HelpersFragment : Fragment(), FirebaseUtilListener {

    private lateinit var adapter: HelpersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return  inflater.inflate(R.layout.fragment_helper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = FirebaseFirestore.getInstance()
            .collection("user")
            .whereEqualTo("help",  Help.HELPING.name)

        val options: FirestoreRecyclerOptions<User> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query) { snapshot -> snapshot.toObject(User::class.java)!! }
            .setLifecycleOwner(this)

        adapter = HelpersAdapter(options, this)
        recycler.adapter = adapter
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
