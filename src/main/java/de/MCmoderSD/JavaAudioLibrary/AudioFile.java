package de.MCmoderSD.JavaAudioLibrary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * The {@code AudioFile} class provides methods to manage, play, pause, and export audio data.
 * It supports playback from a byte array and allows exporting the audio to a WAV file.
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
     * Constructs an {@code AudioFile} object with the specified byte array.
     *
     * @param audioData the byte array containing audio data
     */
    public AudioFile(byte[] audioData) {

        // Set audio data
        this.audioData = audioData;
        byteArrayInputStream = new ByteArrayInputStream(audioData);

        // Initialize audio
        initializeAudio();
    }

    /**
     * Constructs an {@code AudioFile} object with the specified byte array and audio format.
     *
     * @param audioData the byte array containing audio data
     * @param format the format of the audio data
     */
    public AudioFile(byte[] audioData, AudioFormat format) {

        // Set audio data
        this.audioData = audioData;
        this.audioFormat = format;
        byteArrayInputStream = new ByteArrayInputStream(audioData);

        // Initialize audio
        audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
        initializeAudio();
    }

    /**
     * Initializes the audio components, such as the {@link AudioInputStream} and the {@link SourceDataLine}.
     */
    private void initializeAudio() {
        try {
            // Check if audio format is null
            if (audioFormat == null) {
                audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);
                audioFormat = audioInputStream.getFormat();
            }

            // Check if the audio format is supported
            info = new DataLine.Info(SourceDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(info)) throw new UnsupportedAudioFileException("Audio format not supported!");

            // Open audio line
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(audioFormat);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Error initializing audio: " + e.getMessage());
            audioLine = null;
            audioInputStream = null;
            audioFormat = null;
            info = null;
        }
    }

    /**
     * Plays the audio in a separate thread.
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
     * Pauses the audio playback by stopping the audio line.
     */
    public void pause() {
        if (audioLine != null) audioLine.stop();
    }

    /**
     * Resumes the audio playback by starting the audio line.
     */
    public void resume() {
        if (audioLine != null) audioLine.start();
    }

    /**
     * Closes the audio line, releasing any system resources.
     */
    public void close() {
        if (audioLine != null) audioLine.close();
    }

    /**
     * Resets the audio input stream to the beginning of the audio data.
     */
    public void reset() {
        try {
            if (audioInputStream != null) audioInputStream.reset();
        } catch (IOException e) {
            System.err.println("Error resetting audio: " + e.getMessage());
        }
    }

    /**
     * Returns the raw audio data as a byte array.
     *
     * @return the audio data
     */
    public byte[] getAudioData() {
        return audioData;
    }

    /**
     * Returns the {@link ByteArrayInputStream} used for the audio data.
     *
     * @return the byte array input stream
     */
    public ByteArrayInputStream getByteArrayInputStream() {
        return byteArrayInputStream;
    }

    /**
     * Returns the {@link AudioInputStream} for the audio data.
     *
     * @return the audio input stream
     */
    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    /**
     * Returns the {@link AudioFormat} for the audio data.
     *
     * @return the audio format
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * Returns the {@link DataLine.Info} for the audio line.
     *
     * @return the audio line info
     */
    public DataLine.Info getInfo() {
        return info;
    }

    /**
     * Returns the {@link SourceDataLine} used to play the audio.
     *
     * @return the source data line
     */
    public SourceDataLine getAudioLine() {
        return audioLine;
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
     * Returns the duration of the audio in seconds.
     *
     * @return the duration of the audio
     */
    public float getDuration() {
        return audioData.length / audioFormat.getFrameSize() / audioFormat.getFrameRate();
    }

    /**
     * Exports the audio data to a WAV file at the specified file path.
     *
     * @param filePath the path to export the audio file to
     * @return the exported WAV file
     * @throws IOException if an I/O error occurs
     */
    public File export(String filePath) throws IOException {

        // Check path
        if (filePath == null || filePath.isEmpty() || filePath.isBlank()) throw new IOException("File path is invalid!");

        // Check extension
        if (!filePath.endsWith(".wav")) throw new IOException("File extension is not supported: " + filePath);

        // Check format
        if (audioFormat == null) throw new IOException("Audio format is null!");

        // Check if audio data is empty
        if (audioData == null || audioData.length == 0) throw new IOException("Audio data is empty!");

        // Create WAV file
        File wavFile = new File(filePath);

        // Export audio
        ByteArrayInputStream exportStream = new ByteArrayInputStream(audioData);
        AudioInputStream exportAudioStream = new AudioInputStream(exportStream, audioFormat, audioData.length / audioFormat.getFrameSize());
        AudioSystem.write(exportAudioStream, AudioFileFormat.Type.WAVE, wavFile);
        exportAudioStream.close();
        return wavFile;
    }
}