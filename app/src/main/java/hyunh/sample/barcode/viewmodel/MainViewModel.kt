package hyunh.sample.barcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val result: LiveData<String> = MutableLiveData()

    val showRefresh = Transformations.map(result) {
        !it.isNullOrEmpty()
    }

    fun validate() {
    }
}
