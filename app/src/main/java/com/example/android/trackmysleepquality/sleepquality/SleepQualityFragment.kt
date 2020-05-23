package com.example.android.trackmysleepquality.sleepquality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

class SleepQualityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container,
                false)

        val application= requireNotNull(this.activity).application

        val dataSource=SleepDatabase.getInstance(application).sleepDatabaseDao

        val arguments = SleepQualityFragmentArgs.fromBundle(arguments!!)

        val sleepQualityViewModelFactory=SleepQualityViewModelFactory(arguments.sleepNightKey,dataSource = dataSource)

        val sleepQualityViewModel=ViewModelProvider(this,sleepQualityViewModelFactory)
                                                      .get(SleepQualityViewModel::class.java)

        binding.sleepQualityViewModel=sleepQualityViewModel

        sleepQualityViewModel.navigateBackTracker.observe(viewLifecycleOwner, Observer {  shouldNavigate ->
            if (shouldNavigate==true){
                this.findNavController().navigate(
                        SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                sleepQualityViewModel.doneNavigating()
            }
        })

        return binding.root
    }
}
