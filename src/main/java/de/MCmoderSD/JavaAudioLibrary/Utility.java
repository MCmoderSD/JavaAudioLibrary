package de.MCmoderSD.JavaAudioLibrary;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * The {@code Utility} class provides static methods for handling audio data,
 * including exporting audio files and processing audio data to fix issues like crackling.
 */
@SuppressWarnings({"ALL"})
public class Utility {

    /**
     * Exports the given audio data to a specified file path in WAV format.
     *
     * @param filePath   the path to the file where the audio data will be exported
     * @param audioData  the audio data as a byte array
     * @param audioFormat the format of the audio data
     * @return the exported audio file
     * @throws IOException if the file path is invalid, the audio data is empty, or
     *                     if the audio format is null or an error occurs during export
     */
    public static File export(String filePath, byte[] audioData, AudioFormat audioFormat) throws IOException {

        // Check path
        if (filePath == null || filePath.isEmpty() || filePath.isBlank())
            throw new IOException("File path is invalid!");

        // Export audio file
        File wavFile = new File(filePath);
        return export(wavFile, audioData, audioFormat);
    }

    /**
     * Exports the given audio data to the specified WAV file.
     *
     * @param wavFile    the file to which the audio data will be exported
     * @param audioData  the audio data as a byte array
     * @param audioFormat the format of the audio data
     * @return the exported audio file
     * @throws IOException if the file extension is not .wav, the audio data is empty,
     *                     or if the audio format is null or an error occurs during export
     */
    public static File export(File wavFile, byte[] audioData, AudioFormat audioFormat) throws IOException {

        // Get file path
        String filePath = wavFile.getPath();

        // Check extension
        if (!filePath.endsWith(".wav")) throw new IOException("File extension is not supported: " + filePath);

        // Check if audio data is empty
        if (audioData == null || audioData.length == 0) throw new IOException("Audio data is empty!");

        // Check format
        if (audioFormat == null) throw new IOException("Audio format is null!");

        // Fix crackle
        audioData = fixCrackling(audioData, audioFormat);

        // Export audio file
        ByteArrayInputStream exportStream = new ByteArrayInputStream(audioData);
        AudioInputStream exportAudioStream = new AudioInputStream(exportStream, audioFormat, audioData.length / audioFormat.getFrameSize());
        AudioSystem.write(exportAudioStream, AudioFileFormat.Type.WAVE, wavFile);
        exportAudioStream.close();

        return wavFile;
    }

    /**
     * Fixes crackling issues in the provided audio data by removing an offset
     * from the beginning of the audio data.
     *
     * @param audioData  the audio data as a byte array
     * @param audioFormat the format of the audio data
     * @return modified audio data with crackling issues fixed
     * @throws IllegalArgumentException if the calculated offset is out of bounds
     */
    public static byte[] fixCrackling(byte[] audioData, AudioFormat audioFormat) {

        // Calculate offset
        var offsetInBytes = (int) Math.ceil((1_000_000_000 / audioFormat.getSampleRate()) / audioFormat.getSampleSizeInBits() / 2);
        while (offsetInBytes % audioFormat.getFrameSize() != 0) offsetInBytes++;
        var length = audioData.length - offsetInBytes;

        // Check bounds
        if (offsetInBytes < 0 || offsetInBytes >= audioData.length) throw new IllegalArgumentException("Offset is out of bounds!");

        // Skip audio data
        byte[] modifiedAudioData = new byte[length];
        System.arraycopy(audioData, offsetInBytes, modifiedAudioData, 0, length);

        // Return modified audio data
        return modifiedAudioData;
    }
}