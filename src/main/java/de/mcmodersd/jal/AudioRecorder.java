package de.MCmoderSD.jal;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

@SuppressWarnings({"ALL"})
public class AudioRecorder {

    // Attributes
    private final AudioFormat format;

    // Variables
    private boolean isRecording;
    private TargetDataLine line;
    private ByteArrayOutputStream buffer;

    // Constructor
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

    // Stop recording
    public void stopRecording() {
        if (isRecording && line != null) {
            isRecording = false;    // Set recording flag
            line.stop();            // Stop recording
            line.close();           // Close audio line
        }
    }

    // Get the recorded audio as an AudioFile
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

    // Getters
    public AudioFormat getFormat() {
        return format;
    }

    public boolean isRecording() {
        return isRecording;
    }
}