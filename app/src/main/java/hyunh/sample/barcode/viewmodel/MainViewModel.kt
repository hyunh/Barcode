package hyunh.sample.barcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hyunh.sample.barcode.Barcode
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val result: LiveData<String> = MutableLiveData()

    val showRefresh = Transformations.map(result) {
        !it.isNullOrEmpty()
    }

    fun resetResult() {
        (result as MutableLiveData).postValue("")
    }

    fun validate(data: ByteArray, width: Int, height: Int) {
        viewModelScope.launch {
            Barcode.decode(data, width, height)?.let {
                (result as MutableLiveData).postValue(it)
            }
        }
    }
}
