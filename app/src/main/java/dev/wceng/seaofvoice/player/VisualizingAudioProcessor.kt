package dev.wceng.seaofvoice.player

import androidx.annotation.OptIn
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer

@OptIn(UnstableApi::class)
class VisualizingAudioProcessor(
    private val visualizerManager: VisualizerManager
) : BaseAudioProcessor() {

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        if (inputAudioFormat.encoding != androidx.media3.common.C.ENCODING_PCM_16BIT) {
            return AudioFormat.NOT_SET
        }
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        
        val duplicate = inputBuffer.duplicate()
        visualizerManager.processPcm(duplicate)
        
        val remaining = inputBuffer.remaining()
        val buffer = replaceOutputBuffer(remaining)
        buffer.put(inputBuffer)
        buffer.flip()
    }
}
