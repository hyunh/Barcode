package hyunh.sample.barcode.view

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

internal fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    context?.let {
        if (it is AppCompatActivity) {
            it.setSupportActionBar(toolbar)
        }
    }
}

internal val Fragment.supportActionBar: ActionBar?
    get() {
        context?.let {
            if (it is AppCompatActivity) {
                return it.supportActionBar
            }
        }
        return null
    }
