package de.MCmoderSD.JavaAudioLibrary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * The {@code AudioFile} class provides an abstraction for handling and playing audio data.
 * This class allows you to play, pause, resume, and export audio stored in byte arrays.
 * It also provides methods to retrieve audio information such as duration and size.
 */
@SuppressWarnings({"ALL"})
public class AudioFile {

    // Audio Data
    private final byte[] audioData;
    private final ByteArrayInputStream byteArrayInputStream;

    // Audio Components
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private DataLine.Info info;
    private SourceDataLine audioLine;

    /**
     * Constructs an {@code AudioFile} instance with the specified audio data.
     *
     * @param audioData the audio data as a byte array
     * @throws UnsupportedAudioFileException if the audio format is not supported
     * @throws LineUnavailableException if a line cannot be opened due to resource restrictions
     * @throws IOException if an I/O error occurs
     */
    public AudioFile(byte[] audioData) {

        // Set audio data
        this.audioData = audioData;
        byteArrayInputStream = new ByteArrayInputStream(audioData);

        // Initialize audio
        try {

            // Initialize audio input stream
            audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);
            audioFormat = audioInputStream.getFormat();

            // Check if the audio format is supported
            info = new DataLine.Info(SourceDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(info)) throw new UnsupportedAudioFileException("Audio format not supported!");

            // Open audio line
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(audioFormat);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Error initializing audio: " + e.getMessage());
        }
    }

    /**
     * Plays the audio file. This method starts a new thread to play the audio data
     * asynchronously.
     */
    public void play() {
        if (audioLine == null) return;
        new Thread(() -> {
            try {
                // Start audio line
                audioLine.start();

                // Play audio
                byte[] allData = audioInputStream.readAllBytes();
                audioLine.write(allData, 0, allData.length);

                // Reset audio stream
                audioInputStream.reset();

                // Stop audio
                audioLine.drain();
                audioLine.stop();

            } catch (IOException e) {
                System.err.println("Error playing audio: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Pauses the audio playback. If the audio is not currently playing,
     * this method has no effect.
     */
    public void pause() {
        if (audioLine != null) audioLine.stop();
    }

    /**
     * Resumes the audio playback. If the audio is not currently paused,
     * this method has no effect.
     */
    public void resume() {
        if (audioLine != null) audioLine.start();
    }

    /**
     * Closes the audio line, releasing any resources associated with it.
     * After calling this method, the audio line cannot be used until it is reopened.
     */
    public void close() {
        if (audioLine != null) audioLine.close();
    }

    /**
     * Opens the audio line for playback. If the line is already open,
     * this method has no effect.
     *
     * @throws RuntimeException if the audio line is unavailable
     */
    public void open() {
        try {
            if (audioLine != null) audioLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns {@code true} if the audio is currently playing; otherwise {@code false}.
     *
     * @return {@code true} if the audio is currently playing; otherwise {@code false}
     */
    public boolean isPlaying() {
        return audioLine != null && audioLine.isRunning();
    }

    /**
     * Returns the size of the audio data in bytes.
     *
     * @return the size of the audio data in bytes
     */
    public int getSize() {
        return audioData.length;
    }

    /**
     * Returns the duration of the audio file in seconds.
     *
     * @return the duration of the audio file in seconds
     */
    public float getDuration() {
        return audioData.length / audioFormat.getFrameSize() / audioFormat.getFrameRate();
    }

    /**
     * Exports the audio data to a file at the specified path.
     *
     * @param filePath the path of the file to export
     * @return the exported file
     * @throws IOException if an I/O error occurs
     */
    public File export(String filePath) throws IOException {
        return Utility.export(filePath, audioData, audioFormat);
    }

    /**
     * Creates a copy of the audio file.
     *
     * @return a copy of the audio file
     */
    public AudioFile copy() {
        return new AudioFile(audioData);
    }

    // Getters
    public byte[] getAudioData() {
        return audioData;
    }

    public ByteArrayInputStream getByteArrayInputStream() {
        return byteArrayInputStream;
    }

    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public DataLine.Info getInfo() {
        return info;
    }

    public SourceDataLine getAudioLine() {
        return audioLine;
    }
}