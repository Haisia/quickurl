package dev.haisia.quickurl.adapter.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import dev.haisia.quickurl.application.shared.out.QrCodeHandler
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class ZxingQrCodeHandler: QrCodeHandler {
  override fun generateQrCodeImage(url: String, width: Int, height: Int): ByteArray {
    val bitMatrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height)

    return ByteArrayOutputStream()
      .apply { MatrixToImageWriter.writeToStream(bitMatrix, "PNG", this) }
      .toByteArray()
  }
}