package hyunh.sample.barcode.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import hyunh.sample.barcode.R

fun hasPermission(context: Context, permission: String) : Boolean {
    return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
}

class PermissionFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (hasPermission(requireContext(), Manifest.permission.CAMERA)) {
            actionToMain()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSION)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                actionToMain()
            } else {
                Toast.makeText(requireContext(), "Pemission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun actionToMain() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(PermissionFragmentDirections.actionPermissionToMain())
    }
}
