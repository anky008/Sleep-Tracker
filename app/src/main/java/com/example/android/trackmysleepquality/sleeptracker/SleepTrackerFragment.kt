package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.example.android.trackmysleepquality.sleepquality.SleepQualityViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        val sleepTrackerViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(SleepTrackerViewModel::class.java)



        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.setLifecycleOwner(this)

        sleepTrackerViewModel.navigateSleepQuality.observe(viewLifecycleOwner, Observer { sleepNight ->
            sleepNight?.let {
                this.findNavController().navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepQualityFragment(sleepNight.nightId))
                sleepTrackerViewModel.doneNavigating()
            }

        })

        sleepTrackerViewModel.showSnackbar.observe(viewLifecycleOwner, Observer { showSnackbar ->
            if (showSnackbar==true){
                Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.cleared_message),
                        Snackbar.LENGTH_SHORT
                ).show()

                sleepTrackerViewModel.doneShowingSnackBar()
            }
        })



        return binding.root
    }
}
