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
 * The {@code AudioFile} class represents an audio file in memory, allowing
 * playback and management of audio data. It provides functionality to play,
 * pause, resume, and export audio, as well as retrieve metadata like
 * duration, progress, and audio format.
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
     * Constructs an {@code AudioFile} with the specified audio data.
     *
     * @param audioFile the audio file to copy
     */
    public AudioFile(AudioFile audioFile) {
        this(audioFile.audioData);
    }

    /**
     * Constructs an {@code AudioFile} with the specified audio data.
     *
     * @param audioData the audio data as a byte array
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
     * Plays the audio data in a new thread.
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
     * Pauses the audio playback if it is currently playing.
     */
    public void pause() {
        if (audioLine != null) audioLine.stop();
    }

    /**
     * Resumes the audio playback if it was previously paused.
     */
    public void resume() {
        if (audioLine != null) audioLine.start();
    }

    /**
     * Closes the audio line, releasing system resources.
     */
    public void close() {
        if (audioLine != null) audioLine.close();
    }

    /**
     * Opens the audio line, preparing it for playback.
     *
     * @throws RuntimeException if the audio line cannot be opened
     */
    public void open() {
        try {
            if (audioLine != null) audioLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the audio is currently playing.
     *
     * @return {@code true} if the audio is playing, {@code false} otherwise
     */
    public boolean isPlaying() {
        return audioLine != null && audioLine.isRunning();
    }

    /**
     * Returns the size of the audio data in bytes.
     *
     * @return the size of the audio data
     */
    public int getSize() {
        return audioData.length;
    }

    /**
     * Calculates the duration of the audio in seconds.
     *
     * @return the duration of the audio in seconds
     */
    public float getDuration() {
        return audioData.length / audioFormat.getFrameSize() / audioFormat.getFrameRate();
    }

    /**
     * Gets the current playback position in seconds.
     *
     * @return the current playback position
     */
    public float getPosition() {
        return audioLine.getMicrosecondPosition() / 1_000_000f;
    }

    /**
     * Calculates the remaining playback time in seconds.
     *
     * @return the remaining time in seconds
     */
    public float getRemaining() {
        return getDuration() - getPosition();
    }

    /**
     * Calculates the playback progress as a percentage.
     *
     * @return the playback progress, where 1.0 is complete
     */
    public float getProgress() {
        return getPosition() / getDuration();
    }

    /**
     * Exports the audio data to a file at the specified path.
     *
     * @param filePath the path to export the audio file to
     * @return the exported {@code File} object
     * @throws IOException if an I/O error occurs during export
     */
    public File export(String filePath) throws IOException {
        return Utility.export(filePath, audioData, audioFormat);
    }

    /**
     * Creates a copy of this {@code AudioFile}.
     *
     * @return a new {@code AudioFile} with the same audio data
     */
    public AudioFile copy() {
        return new AudioFile(audioData);
    }

    /**
     * Compares this {@code AudioFile} to the specified object.
     *
     * @param obj the object to compare to
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof AudioFile)) return false;
        AudioFile audioFile = (AudioFile) obj;
        return audioFile.audioData.equals(audioData);
    }

    /**
     * Returns the audio data as a byte array.
     *
     * @return the audio data
     */
    public byte[] getAudioData() {
        return audioData;
    }

    /**
     * Returns the input stream for the audio data.
     *
     * @return the {@code ByteArrayInputStream} containing audio data
     */
    public ByteArrayInputStream getByteArrayInputStream() {
        return byteArrayInputStream;
    }

    /**
     * Returns the audio input stream used by this audio file.
     *
     * @return the {@code AudioInputStream} instance
     */
    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    /**
     * Returns the format of the audio data.
     *
     * @return the {@code AudioFormat} of the audio data
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * Returns the audio line information.
     *
     * @return the {@code DataLine.Info} for the audio line
     */
    public DataLine.Info getInfo() {
        return info;
    }

    /**
     * Returns the audio line used for playback.
     *
     * @return the {@code SourceDataLine} for the audio line
     */
    public SourceDataLine getAudioLine() {
        return audioLine;
    }
}