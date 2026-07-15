package dev.wceng.seaofvoice.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Singleton
class VisualizerManager @Inject constructor() {
    
    private val _magnitudes = MutableStateFlow(FloatArray(48) { 0.1f })
    val magnitudes = _magnitudes.asStateFlow()

    private val fftSize = 1024
    private val real = FloatArray(fftSize)
    private val imag = FloatArray(fftSize)

    fun processPcm(buffer: ByteBuffer) {
        val remaining = buffer.remaining()
        if (remaining < fftSize * 2) return

        // 1. Read PCM 16-bit and normalize to -1..1
        for (i in 0 until fftSize) {
            if (buffer.remaining() >= 2) {
                val low = buffer.get().toInt() and 0xFF
                val high = buffer.get().toInt()
                val sample = (high shl 8) or low
                real[i] = sample.toFloat() / 32768f
                imag[i] = 0f
            } else {
                real[i] = 0f
                imag[i] = 0f
            }
        }

        // 2. Execute FFT
        fft(real, imag, fftSize)

        // 3. Process into buckets with better scaling
        val n = fftSize / 2
        val outMagnitudes = FloatArray(48)
        
        for (i in 0 until 48) {
            val startSample = (n * (Math.pow(i.toDouble() / 48, 2.0))).toInt()
            val endSample = (n * (Math.pow((i + 1).toDouble() / 48, 2.0))).toInt().coerceAtLeast(startSample + 1)
            
            var sum = 0f
            var count = 0
            for (j in startSample until endSample.coerceAtMost(n)) {
                sum += hypot(real[j], imag[j])
                count++
            }

            if (count > 0) {
                // Better normalization: Average magnitude / (N/2)
                val avgMagnitude = (sum / count) / (fftSize / 2f)
                
                // Frequency boost: higher frequencies are physically weaker in most audio
                val boost = 1f + (i.toFloat() / 48f) * 5f 
                
                // Apply Square Root scaling for better visual dynamic range (compression)
                val raw = Math.sqrt(avgMagnitude.toDouble()).toFloat() * boost * 1.5f
                
                // Smooth transition: 80% history, 20% new data
                val smoothed = _magnitudes.value[i] * 0.8f + raw.coerceIn(0.05f, 1f) * 0.2f
                outMagnitudes[i] = smoothed
            } else {
                outMagnitudes[i] = _magnitudes.value[i] * 0.85f // Fast decay
            }
        }
        _magnitudes.value = outMagnitudes
    }

    private fun fft(re: FloatArray, im: FloatArray, n: Int) {
        var i = 0
        var j = 0
        while (i < n) {
            if (i < j) {
                val tempR = re[i]; re[i] = re[j]; re[j] = tempR
                val tempI = im[i]; im[i] = im[j]; im[j] = tempI
            }
            var m = n shr 1
            while (m >= 1 && j >= m) {
                j -= m
                m = m shr 1
            }
            j += m
            i++
        }

        var len = 2
        while (len <= n) {
            val ang = -2.0 * Math.PI / len // Note the negative sign for forward FFT
            val wlenR = cos(ang).toFloat()
            val wlenI = sin(ang).toFloat()
            i = 0
            while (i < n) {
                var wR = 1f
                var wI = 0f
                for (k in 0 until len / 2) {
                    val uR = re[i + k]
                    val uI = im[i + k]
                    // Fixed complex multiplication: (re+im*i) * (wR+wI*i)
                    val vR = re[i + k + len / 2] * wR - im[i + k + len / 2] * wI
                    val vI = re[i + k + len / 2] * wI + im[i + k + len / 2] * wR
                    re[i + k] = uR + vR
                    im[i + k] = uI + vI
                    re[i + k + len / 2] = uR - vR
                    im[i + k + len / 2] = uI - vI
                    val nextWR = wR * wlenR - wI * wlenI
                    wI = wR * wlenI + wI * wlenR
                    wR = nextWR
                }
                i += len
            }
            len = len shl 1
        }
    }

    fun start(audioSessionId: Int) {}
    fun stop() {
        _magnitudes.value = FloatArray(48) { 0.1f }
    }
}
