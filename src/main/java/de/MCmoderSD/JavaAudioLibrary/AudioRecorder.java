package de.MCmoderSD.JavaAudioLibrary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;

import java.io.ByteArrayOutputStream;

/**
 * The {@code AudioRecorder} class provides functionality to record audio using
 * a specific audio format and retrieve the recorded data as an {@code AudioFile}.
 */
@SuppressWarnings({"ALL"})
public class AudioRecorder {

    // Attributes
    private final AudioFormat format;

    // Variables
    private boolean isRecording;
    private TargetDataLine line;
    private ByteArrayOutputStream buffer;

    /**
     * Initializes a new {@code AudioRecorder} instance with a default audio format.
     * <p>
     * The default format is:
     * <ul>
     *     <li>Sample rate: 48,000 Hz</li>
     *     <li>Sample size: 16 bits</li>
     *     <li>Channels: Mono</li>
     *     <li>Signed: true</li>
     *     <li>Big-endian: false</li>
     * </ul>
     */
    public AudioRecorder() {

        // Audio format
        float sampleRate = 48000;
        int sampleSizeInBits = 16;
        int channels = 1; // Mono
        boolean signed = true;
        boolean bigEndian = false;

        // Set audio format
        format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        // Initialize variables
        isRecording = false;
    }

    /**
     * Starts the recording process in a new thread. The recorded data will be
     * stored in a buffer.
     *
     * @throws RuntimeException if the audio format is not supported by the system
     */
    public void startRecording() {

        // Check supported audio format
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) throw new RuntimeException("Audio format not supported!");

        // Open recording Thread
        new Thread(() -> {
            try {

                // Set recording flag
                isRecording = true;

                // Open audio line
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);

                // Start recording
                line.start();

                // Initialize buffer
                buffer = new ByteArrayOutputStream();
                byte[] data = new byte[line.getBufferSize()];

                // Record audio
                while (isRecording) {
                    var bytesRead = line.read(data, 0, data.length);
                    if (bytesRead == -1) break;
                    buffer.write(data, 0, bytesRead);
                }
            } catch (LineUnavailableException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Stops the recording process. If no recording is in progress, this method has no effect.
     */
    public void stopRecording() {
        if (isRecording && line != null) {
            isRecording = false;    // Set recording flag
            line.stop();            // Stop recording
            line.close();           // Close audio line
        }
    }

    /**
     * Retrieves the recorded audio data and returns it as an {@code AudioFile}.
     * <p>
     * This method also stops the recording if it is still ongoing.
     *
     * @return an {@code AudioFile} containing the recorded audio data, or {@code null}
     *         if no data was recorded
     */
    public AudioFile getAudioFile() {

        // Stop recording
        stopRecording();

        // Return audio file
        if (buffer != null) {
            byte[] audioData = buffer.toByteArray();
            return new AudioFile(audioData, format);
        }
        return null;
    }

    /**
     * Returns the audio format used for recording.
     *
     * @return the {@code AudioFormat} object representing the recording format
     */
    public AudioFormat getFormat() {
        return format;
    }

    /**
     * Checks if the recording is currently in progress.
     *
     * @return {@code true} if recording is in progress, {@code false} otherwise
     */
    public boolean isRecording() {
        return isRecording;
    }
}