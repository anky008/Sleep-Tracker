package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

        private var viewModelJob= Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope= CoroutineScope(Dispatchers.Main + viewModelJob)
    private var tonight=MutableLiveData<SleepNight?> ()
    private val allNights=database.getAllNights()

    private var _navigateSleepQuality=MutableLiveData<SleepNight?> ()
    val navigateSleepQuality:LiveData<SleepNight?>
            get()=_navigateSleepQuality

    val allNightsString = Transformations.map(allNights){allNights ->
        formatNights(allNights,application.resources)
    }

    val startButtonVisible=Transformations.map(tonight){
     null==it
    }

    val stopButtonVisible=Transformations.map(tonight){
        null!=it
    }

    val clearButtonVisible=Transformations.map(allNights){
        it?.isNotEmpty()
    }

    private var _showSnackBarEvent=MutableLiveData<Boolean>()
    val showSnackbar:LiveData<Boolean>
        get()=_showSnackBarEvent


    init{
        _showSnackBarEvent.value=false
        initializeTonight()
    }

    fun doneNavigating(){
        _navigateSleepQuality.value=null
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value=getNightFromDatabase()

        }
    }

    private suspend fun getNightFromDatabase() : SleepNight? {
        return withContext(Dispatchers.IO) {
            var night=database.getTonight()
            if (night?.startTimeMilli != night?.endTimeMilli){
            night=null
        }

            night
        }
    }

    fun onStartTracking(){
        Log.i("SleepTrackerViewModel","Button Clicked!")
        uiScope.launch {
            val newNight=SleepNight()
            insert(newNight)

            tonight.value=getNightFromDatabase()
        }
    }

    suspend fun insert(newNight : SleepNight){
        withContext(Dispatchers.IO){
            database.insert(newNight)
        }
    }

    fun onStopTracking(){
        uiScope.launch {
            val oldNight=tonight.value ?: return@launch
            oldNight.endTimeMilli=System.currentTimeMillis()
            update(oldNight)

            _navigateSleepQuality.value=oldNight
        }
    }

    private suspend fun update(oldNight : SleepNight){
        withContext(Dispatchers.IO) {
          database.update(oldNight)
        }
    }

    fun onClear(){
        uiScope.launch {
            clear()
            tonight.value=null
            _showSnackBarEvent.value=true
        }
    }

    fun doneShowingSnackBar(){
        _showSnackBarEvent.value=false
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }


}

