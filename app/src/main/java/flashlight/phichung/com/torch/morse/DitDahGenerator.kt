package flashlight.phichung.com.torch.morse

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

enum class SoundTypes {
    DIT, DAH, LETTER_SPACE, WORD_SPACE, UNDEFINED
}

// Given a string, convert it to a sequence of SoundTypes representing the morse code
fun StringToSoundSequence(s: String): List<SoundTypes> {
    if (s.isEmpty()) {
        return listOf()
    }

    val first = when (s[0]) {
        ' ' -> listOf(SoundTypes.WORD_SPACE)
        'A' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'B' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'C' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'D' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'E' -> listOf(
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'F' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'G' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'H' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'I' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'J' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'K' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'L' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'M' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'N' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'O' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'P' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'Q' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'R' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'S' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        'T' -> listOf(
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'U' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'V' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'W' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'X' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'Y' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        'Z' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '0' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '1' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '2' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '3' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '4' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '5' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '6' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '7' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '8' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '9' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '.' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        ',' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        ':' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '?' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '\'' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '-' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '/' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '(' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        ')' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '"' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '=' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '+' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        '×' -> listOf(
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.LETTER_SPACE
        )

        '@' -> listOf(
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.DAH,
            SoundTypes.DIT,
            SoundTypes.LETTER_SPACE
        )

        else -> {
            listOf(
                SoundTypes.UNDEFINED, SoundTypes.LETTER_SPACE
            )
        }
    }

    return first + StringToSoundSequence(s.substring(1))
}


data class DitDahGeneratorSettings(var context: Context? = null) {
    var toneFrequency = 650
    var wordsPerMinute = 10
    var farnsworthWordsPerMinute = 10
}

class DitDahSoundStream(config: DitDahGeneratorSettings) {
    interface StreamNotificationListener {
        // We ran out of symbols to add to the stream
        fun symbolsExhausted(stream: DitDahSoundStream)
    }

    fun enqueue(symbols: List<SoundTypes>) {
        mSymbolQueue.clear()
        for (sym in symbols) {
            mSymbolQueue.put(sym)
        }
    }

    // Returns the duration of the sequence, in seconds
    fun durationOf(symbols: List<SoundTypes>): Float {
        var numSamples = 0
        for (sym in symbols) {
            numSamples += when (sym) {
                SoundTypes.DIT -> mDitSound
                SoundTypes.DAH -> mDahSound
                SoundTypes.LETTER_SPACE -> mCharacterSpacingSound
                SoundTypes.WORD_SPACE -> mWordSpacingSound
                SoundTypes.UNDEFINED -> mCharacterSpacingSound

            }.size
        }

        return numSamples.toFloat() / mAudioSampleRate.toFloat()
    }

    fun quit() {
        mShouldQuit = true

        // Pause and flush the audio player, which has the effect of stopping
        // playback immediately.
        mSoundPlayer.pause()
        mSoundPlayer.flush()

        // Add a symbol to the queue to ensure the worker thread is released to see the quit signal
        mSymbolQueue.put(SoundTypes.WORD_SPACE)
    }

    fun setSoundState(isOn: Boolean) {
        stateSound = isOn
    }

    fun setFlashState(isOn: Boolean) {
        stateFlash = isOn
    }

    suspend fun makeSoundsWorkerThreadFunc(
        onDone: (Unit) -> Unit,
        onCharacter: MutableStateFlow<Int>,
        camManager: CameraManager?,
        cameraId: String?
    ) {
        mShouldQuit = false
        var characterIndex = -1
        Timber.i("HAOHAO makeSoundsWorkerThreadFunc")

        while (true) {
            if (mSymbolQueue.size == 0) {
                onDone(Unit)
                return
            }
            // Try to get a symbol from the queue:
            var didWait = false
            var sym: SoundTypes? = mSymbolQueue.poll(1, TimeUnit.MILLISECONDS)

            if (sym == null) {
                // We didn't get a symbol quickly enough; this happens during the
                // interactive sounder mode, where the user is pressing keys or
                // when we have hit the end of a sequence.
                // Tell any listener, give them the opportunity to restart or quit:
                streamNotificationListener?.symbolsExhausted(this)
                // Now we can wait indefinitely:
                didWait = true
                sym = mSymbolQueue.take()
            }

            if (mShouldQuit)
                return

            characterIndex++

//            if (sym != SoundTypes.WORD_SPACE) {
//                characterIndex++
//            } else {
//                characterIndex += 2
            //   }
            onCharacter.value = characterIndex //view text


            if (stateSound) {
                val startTime = System.currentTimeMillis()

                val soundToWrite: ShortArray = when (sym!!) {
                    SoundTypes.DIT -> mDitSound
                    SoundTypes.DAH -> mDahSound
                    SoundTypes.LETTER_SPACE -> mCharacterSpacingSound
                    SoundTypes.WORD_SPACE -> mWordSpacingSound
                    SoundTypes.UNDEFINED -> mCharacterSpacingSound

                }

                if (didWait) {
                    // If we had to wait for a character, that means we are
                    // in interactive mode. Sometimes, a pop can be heard at
                    // the start, so add an extra bit of silence, to fill up
                    // the audio buffers. If the buffer is smaller than this,
                    // it should be OK, since we just want leading silence:
                    mSoundPlayer.write(mCharacterSpacingSound, 0, mCharacterSpacingSound.size)
                }

                // Now attempt to write the real sound:
                val numWritten = mSoundPlayer.write(soundToWrite, 0, soundToWrite.size)
                // And start the audio playing:
                mSoundPlayer.play()
                if (sym != SoundTypes.UNDEFINED && sym != SoundTypes.WORD_SPACE && sym != SoundTypes.LETTER_SPACE && stateFlash) {
                    cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                }

                if (numWritten != soundToWrite.size) {
                    mSoundPlayer.write(
                        soundToWrite,
                        numWritten,
                        soundToWrite.size - numWritten,
                        AudioTrack.WRITE_BLOCKING
                    )
                }


                val endTime = System.currentTimeMillis()
                val timeDelay = endTime - startTime
                Timber.i("Time delay $sym: $timeDelay milliseconds")
                // Now we can stop the audio player; it won't stop the audio
                // immediately, instead once the last write() has been output
                mSoundPlayer.stop()
                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

            } else {
                if (stateFlash) {
                    cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON
                }
                delay(
                    when (sym) {
                        SoundTypes.DIT -> 80
                        SoundTypes.DAH -> 450
                        SoundTypes.LETTER_SPACE -> 285
                        SoundTypes.WORD_SPACE -> 1010
                        SoundTypes.UNDEFINED -> 285
                        else -> {
                            1
                        }
                    }
                )
                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF


            }
        }
    }

    // Optional listener for stream finished event
    var streamNotificationListener: StreamNotificationListener? = null

    // Used to notify our audio-track-writing thread that it should quit
    private var mShouldQuit = false

    // on off sound morse
    private var stateSound = true  // false = off, true = on


    // on off flash morse
    private var stateFlash = true  // false = off, true = on

    // A queue of sounds we want to play
    private var mSymbolQueue = ArrayBlockingQueue<SoundTypes>(1000)

    private val mAudioSampleRate = 44100

    // Buffers containing the sounds we want to play
    private val mDitSound: ShortArray
    private val mDahSound: ShortArray
    private val mWordSpacingSound: ShortArray
    private val mCharacterSpacingSound: ShortArray

    // Our actual audio track
    private val mSoundPlayer: AudioTrack =
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(mAudioSampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(mAudioSampleRate)
            .build()

    init {
        val t_dit = 60.0f / (50.0f * config.wordsPerMinute)
        val t_fdit = ((60.0f / min(
            config.farnsworthWordsPerMinute,
            config.wordsPerMinute
        )) - 31.0f * t_dit) / 19.0f
        val ditLengthInSamples = (t_dit * mAudioSampleRate).toInt()
        val dahLengthInSamples = (t_dit * 3 * mAudioSampleRate).toInt()
        val intraCharacterSpacingInSamples = (t_dit * mAudioSampleRate).toInt()
        val interCharacterSpacingInSamples = (3 * t_fdit * mAudioSampleRate).toInt()
        val wordSpacingInSamples = (7 * t_fdit * mAudioSampleRate).toInt()
        mDitSound = ShortArray(ditLengthInSamples + intraCharacterSpacingInSamples)
        mDahSound = ShortArray(dahLengthInSamples + intraCharacterSpacingInSamples)
        mWordSpacingSound = ShortArray(wordSpacingInSamples - intraCharacterSpacingInSamples)
        mCharacterSpacingSound =
            ShortArray(interCharacterSpacingInSamples - intraCharacterSpacingInSamples)
        val invSampleRate = 1.0 / mAudioSampleRate.toFloat()
        for (i in 0 until ditLengthInSamples) {
            mDitSound[i] =
                (0x7fff.toFloat() * sin(2.0f * PI * i * config.toneFrequency * invSampleRate)).toInt()
                    .toShort()
        }
        for (i in 0 until dahLengthInSamples) {
            mDahSound[i] =
                (0x7fff.toFloat() * sin(2.0f * PI * i * config.toneFrequency * invSampleRate)).toInt()
                    .toShort()
        }
        val attackReleaseDuration = 0.008f
        val attackReleaseTimeInSamples = (attackReleaseDuration * mAudioSampleRate).toInt()
        for (i in 0 until attackReleaseTimeInSamples) {
            val frac = i.toFloat() / attackReleaseTimeInSamples.toFloat()
            mDitSound[i] = (frac * mDitSound[i]).toInt().toShort()
            mDahSound[i] = (frac * mDahSound[i]).toInt().toShort()

            mDitSound[ditLengthInSamples - i - 1] =
                (frac * mDitSound[ditLengthInSamples - i - 1]).toInt().toShort()
            mDahSound[dahLengthInSamples - i - 1] =
                (frac * mDahSound[dahLengthInSamples - i - 1]).toInt().toShort()
        }
    }
}
