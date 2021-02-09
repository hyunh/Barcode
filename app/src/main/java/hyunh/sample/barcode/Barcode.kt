package hyunh.sample.barcode

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.common.HybridBinarizer

object Barcode {
    fun decode(data: ByteArray, width: Int, height: Int): String? {
        val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)

        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val result = try {
            MultiFormatReader().decode(bitmap)
        } catch (e: ReaderException) {
            null
        }
        return result?.toString()
    }
}
