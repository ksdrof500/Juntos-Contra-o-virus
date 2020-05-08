package com.juntoscontracorona.ui.needed

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.juntoscontracorona.MainActivity
import com.juntoscontracorona.R
import com.juntoscontracorona.model.Help
import com.juntoscontracorona.model.User
import com.juntoscontracorona.util.PreferencesDataSource
import com.juntoscontracorona.util.toastError
import com.juntoscontracorona.util.toastSuccess
import kotlinx.android.synthetic.main.fragment_needing.*

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private var urlImage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_needing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                PreferencesDataSource(context!!).set("myLocation", locationResult.lastLocation)
                MainActivity.location = locationResult.lastLocation
            }
        }

        userViewModel.getUser.observe(viewLifecycleOwner, Observer {
            urlImage = it.image

            Glide
                .with(context!!)
                .load(it.image)
                .centerCrop()
                .placeholder( CircularProgressDrawable(context!!))
                .into(profileImage)

            name.setText(it.name)
            phone.setText(it.phone)
            if (it.help == Help.HELPER) {
                buttonHelper.isSelected = true
                buttonHelping.isSelected = false
            } else {
                buttonHelper.isSelected = false
                buttonHelping.isSelected = true
            }

            store.isChecked = it.store
            talk.isChecked = it.talk
            pharmacy.isChecked = it.pharmacy

        })

        userViewModel.getFeedbackLoading.observe(viewLifecycleOwner, Observer {
            progress.isVisible = it
        })

        userViewModel.getFeedback.observe(viewLifecycleOwner, Observer {
            if (it) {
                context?.resources?.getString(R.string.save_success)?.let { message ->
                    context?.toastSuccess(message)
                }
            } else {
                context?.resources?.getString(R.string.save_error)?.let { message ->
                    context?.toastError(message)
                }
            }
        })

        buttonSave.setOnClickListener {
            if (MainActivity.location != null) {
                userViewModel.save(
                    User(
                        name = name.text.toString(),
                        phone = phone.text.toString(),
                        help = if (buttonHelper.isSelected) Help.HELPER else Help.HELPING,
                        store = store.isChecked,
                        pharmacy = pharmacy.isChecked,
                        talk = talk.isChecked
                    ), context!!
                )
            } else {
                Toast.makeText(
                    context,
                    context?.resources?.getString(R.string.save_location),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        buttonHelper.setOnClickListener {
            buttonHelper.isSelected = true
            buttonHelping.isSelected = false
        }
        buttonHelping.setOnClickListener {
            buttonHelper.isSelected = false
            buttonHelping.isSelected = true
        }

        phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

    }

}
