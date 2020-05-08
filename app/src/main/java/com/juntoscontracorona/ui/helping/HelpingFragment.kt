package com.juntoscontracorona.ui.helping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.juntoscontracorona.MainActivity
import com.juntoscontracorona.R
import com.juntoscontracorona.model.Help
import com.juntoscontracorona.model.User
import com.juntoscontracorona.util.FirebaseUtilListener
import com.juntoscontracorona.util.PreferencesDataSource
import kotlinx.android.synthetic.main.fragment_helping.*


class HelpingFragment : Fragment(), FirebaseUtilListener {

    private var adapter: HelpingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_helping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                PreferencesDataSource(context!!).set("myLocation", locationResult.lastLocation)
                MainActivity.location = locationResult.lastLocation
                change()
            }
        }

        change()
    }

    private fun change() {
        MainActivity.location?.let {
            val lat = 0.0144927536231884
            val lon = 0.0181818181818182

            val lowerLat = it.latitude - (lat * 10)
            val lowerLon = it.longitude - (lon * 10)

            val greaterLat = it.latitude + (lat * 10)
            val greaterLon = it.longitude + (lon * 10)

            val lesserGeopoint = GeoPoint(lowerLat, lowerLon)
            val greaterGeopoint = GeoPoint(greaterLat, greaterLon)

            val query = FirebaseFirestore.getInstance()
                .collection("user")
                .whereEqualTo("help", Help.HELPER.name)
                .whereGreaterThan("latLong", lesserGeopoint)
                .whereLessThan("latLong", greaterGeopoint)
                .limit(30)

            val options: FirestoreRecyclerOptions<User> =
                FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(query, User::class.java)
                    .build()

            FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query) { snapshot -> snapshot.toObject(User::class.java)!! }
                .setLifecycleOwner(this@HelpingFragment)

            adapter = HelpingAdapter(options, this@HelpingFragment)
            recycler.adapter = adapter
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    override fun empty() {
        empty.isVisible = adapter?.itemCount == 0
    }
}
