package com.example.android.trackmysleepquality.sleepquality

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel(
        private val sleepNightKey: Long=0L,
        private val databaseDao: SleepDatabaseDao): ViewModel(){

    private val viewModelJob= Job()

    private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _navigateBackTracker=MutableLiveData<Boolean?> ()
    val navigateBackTracker : LiveData<Boolean?>
    get()=_navigateBackTracker

    init {
        _navigateBackTracker.value=false
    }

    fun doneNavigating(){
        _navigateBackTracker.value=null
    }

    fun onSetSleepQuality(quality:Int){
        uiScope.launch {
            updateDatabase(quality)
        }


    }

    private suspend fun updateDatabase(quality: Int) {
        withContext(Dispatchers.IO){
            val tonight=databaseDao.get(sleepNightKey)?: return@withContext
            tonight.sleepQuality = quality
            databaseDao.update(tonight)
        }
        _navigateBackTracker.value=true
    }
}