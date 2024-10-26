package de.MCmoderSD.JavaAudioLibrary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.HexFormat;

/**
 * The {@code AudioRecorder} class provides functionality for recording audio
 * from the microphone. It captures audio data in real-time and stores it
 * in memory, allowing the user to export the recorded audio as a file.
 */
@SuppressWarnings({"ALL"})
public class AudioRecorder {

    // Attributes
    private final AudioFormat format;
    private final DataLine.Info info;
    private final TargetDataLine line;

    // Variables
    private ByteArrayOutputStream buffer;
    private boolean isRecording;

    /**
     * Constructs an {@code AudioRecorder} instance and initializes the audio format,
     * checks if the format is supported, and prepares the audio line for recording.
     *
     * @throws RuntimeException if the audio format is not supported or if an error occurs
     *                          while initializing the audio line.
     */
    public AudioRecorder() {

        // Initialize audio format
        var sampleRate = 192000f;       // 192 kHz
        var sampleSizeInBits = 16;      // 16 bit
        var channels = 2;               // Stereo
        boolean signed = true;          // Signed
        boolean bigEndian = false;      // Little-endian

        // Set audio format
        format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        // Check supported audio format
        info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) throw new RuntimeException("Audio format not supported!");

        // Get audio line
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Error initializing audio line: " + e.getMessage());
        }

        // Initialize variables
        isRecording = false;
    }

    /**
     * Starts recording audio in a separate thread. The recorded audio data
     * is stored in a byte array output stream.
     */
    public void startRecording() {
        new Thread(() -> {
            try {

                // Set recording flag
                isRecording = true;

                // Open audio line
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
     * Stops the audio recording. This method sets the recording flag to false,
     * stops the audio line, and closes it to free up resources.
     */
    public void stopRecording() {
        if (isRecording && line != null) {
            isRecording = false;    // Set recording flag
            line.stop();            // Stop recording
            line.close();           // Close audio line
        }
    }

    /**
     * Retrieves the recorded audio data as an {@code AudioFile} object.
     * This method stops the recording, exports the audio data to a temporary
     * file, loads the audio file, and then deletes the temporary file.
     *
     * @return an {@code AudioFile} containing the recorded audio data, or {@code null}
     *         if an error occurs during the process.
     */
    public AudioFile getAudioFile() {

        // Stop recording
        stopRecording();

        try {

            // Return audio file
            if (buffer != null) {

                // Get audio data
                byte[] audioData = buffer.toByteArray();

                // Get temporary file
                String tempPath = System.getProperty("java.io.tmpdir");
                while (tempPath.endsWith("/") || tempPath.endsWith("\\")) tempPath = tempPath.substring(0, tempPath.length() - 1);
                File tempFile = new File(tempPath + "/" + HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(audioData)) + ".wav");

                // Export audio file
                Utility.export(tempFile, audioData, format);

                // Load audio file
                AudioFile audioFile = AudioLoader.loadAudio(tempFile.getAbsolutePath(), true);

                // Delete temporary file
                tempFile.delete();

                // Return audio file
                return audioFile;
            }
        } catch (IOException | URISyntaxException | NoSuchAlgorithmException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if the recorder is currently in the process of recording audio.
     *
     * @return {@code true} if the recorder is recording, {@code false} otherwise
     */
    public boolean isRecording() {
        return isRecording;
    }

    // Getters
    public AudioFormat getFormat() {
        return format;
    }

    public DataLine.Info getInfo() {
        return info;
    }

    public TargetDataLine getLine() {
        return line;
    }

    public ByteArrayOutputStream getBuffer() {
        return buffer;
    }
}