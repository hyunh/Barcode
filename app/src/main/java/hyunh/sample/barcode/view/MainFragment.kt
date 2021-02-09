package hyunh.sample.barcode.view

import android.Manifest
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import hyunh.sample.barcode.R
import hyunh.sample.barcode.databinding.FragmentMainBinding
import hyunh.sample.barcode.logd
import hyunh.sample.barcode.loge
import hyunh.sample.barcode.viewmodel.MainViewModel
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainFragment : Fragment() {

    companion object {
        private const val TAG = "MainFragment"

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private val model: MainViewModel by viewModels()
    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val analyzer: (ImageProxy) -> Unit = { imageProxy ->
        fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            return ByteArray(remaining()).also {
                get(it)
            }
        }
        imageProxy.use {
            logd(TAG, "analyze() width: ${it.width}, height: ${it.height}")
            model.validate(it.planes[0].buffer.toByteArray(), it.width, it.height)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = model
            preview.post {
                setupCamera(preview)
            }
        }

        setHasOptionsMenu(true)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
            Navigation.findNavController(requireActivity(), R.id.nav_host)
                    .navigate(MainFragmentDirections.actionMainToPermission())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        model.showRefresh.observe(viewLifecycleOwner) {
            menu.findItem(R.id.menu_refresh).isVisible = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                model.resetResult()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        executor.shutdown()
    }

    private fun setupCamera(view: PreviewView) {
        ProcessCameraProvider.getInstance(requireContext()).apply {
            addListener({
                bindUseCase(view, get())
            }, ContextCompat.getMainExecutor(requireContext()))
        }
    }

    private fun bindUseCase(view: PreviewView, cameraProvider: ProcessCameraProvider) {
        val metrics = DisplayMetrics().apply {
            view.display?.getRealMetrics(this)
        }

        val ratio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        logd(TAG, "bindUseCase() width: ${metrics.widthPixels}, height: ${metrics.heightPixels}")

        val facing = when {
            cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.LENS_FACING_BACK
            cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.LENS_FACING_FRONT
            else -> throw IllegalStateException("Back and front camera are unavilable")
        }

        val selector = CameraSelector.Builder().requireLensFacing(facing).build()

        val preview =  Preview.Builder()
                .setTargetAspectRatio(ratio)
                .setTargetRotation(view.display.rotation)
                .build()

        val analyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(ratio)
                .setTargetRotation(view.display.rotation)
                .build()
                .apply {
                    setAnalyzer(executor, analyzer)
                }

        cameraProvider.unbindAll()
        try {
            cameraProvider.bindToLifecycle(viewLifecycleOwner, selector, preview, analyzer)
            preview.setSurfaceProvider(view.surfaceProvider)
        } catch (e: Exception) {
            loge(TAG, "bindUseCase() $e")
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }
}
