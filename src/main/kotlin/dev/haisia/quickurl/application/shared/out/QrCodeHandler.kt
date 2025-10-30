package dev.haisia.quickurl.application.shared.out

interface QrCodeHandler {
  fun generateQrCodeImage(url: String, width: Int = 200, height: Int = 200): ByteArray
}