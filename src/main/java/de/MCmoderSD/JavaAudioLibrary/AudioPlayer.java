package de.MCmoderSD.JavaAudioLibrary;

import java.util.HashMap;

/**
 * AudioPlayer is a utility class for managing and controlling audio playback.
 * It allows for playing, pausing, resuming, and stopping audio files.
 * Each audio file is assigned a unique ID for identification and control.
 */
@SuppressWarnings("ALL")
public class AudioPlayer {

    // Attributes
    private final HashMap<Integer, AudioFile> pool = new HashMap<>();

    // Variables
    private boolean isPaused = false;

    /**
     * Plays an audio file with the specified ID if it exists in the pool.
     *
     * @param id the unique ID of the audio file to play
     */
    public void play(int id) {
        if (pool.containsKey(id)) pool.get(id).play(this, id);
    }

    /**
     * Plays audio data from a byte array, creating a new audio file instance.
     *
     * @param audioData the audio data as a byte array
     * @return the unique ID assigned to the new audio file, or {@code null} if creation fails
     */
    public Integer play(byte[] audioData) {
        return play(new AudioFile(audioData));
    }

    /**
     * Plays the specified AudioFile instance.
     *
     * @param audioFile the AudioFile instance to play
     * @return the unique ID assigned to the audio file, or {@code null} if the audio file is {@code null}
     */
    public Integer play(AudioFile audioFile) {
        if (audioFile == null) return null;

        // Generate a unique ID
        var id = pool.size();
        while (pool.containsKey(id)) id++;

        // Copy the audio file
        AudioFile instance = audioFile.copy();

        // Add the audio file to the pool
        pool.put(id, instance);
        instance.play(this, id);

        // Return the ID
        return id;
    }

    /**
     * Removes an audio file from the pool using the specified ID and closes the file if it exists.
     *
     * @param id the unique ID of the audio file to remove
     */
    public void remove(int id) {
        if (pool.containsKey(id)) {
            pool.get(id).close();
            pool.remove(id);
        } else System.err.println("AudioFile with ID " + id + " not found.");
    }

    /**
     * Pauses the audio file with the specified ID if it exists in the pool.
     *
     * @param id the unique ID of the audio file to pause
     */
    public void pause(int id) {
        if (pool.containsKey(id)) pool.get(id).pause();
    }

    /**
     * Pauses all audio files in the pool and sets the player to a paused state.
     */
    public void pause() {
        isPaused = true;
        for (AudioFile audioFile : pool.values()) audioFile.pause();
    }

    /**
     * Resumes playback of the audio file with the specified ID if it exists in the pool.
     *
     * @param id the unique ID of the audio file to resume
     */
    public void resume(int id) {
        if (pool.containsKey(id)) pool.get(id).resume();
    }

    /**
     * Resumes playback of all audio files in the pool and sets the player to an unpaused state.
     */
    public void resume() {
        for (AudioFile audioFile : pool.values()) audioFile.resume();
        isPaused = false;
    }

    /**
     * Stops all audio playback, closes each audio file, and clears the pool.
     */
    public void stop() {
        for (AudioFile audioFile : pool.values()) audioFile.close();
        pool.clear();
    }

    /**
     * Retrieves the audio file associated with the specified ID.
     *
     * @param id the unique ID of the audio file
     * @return the AudioFile instance, or {@code null} if no audio file is found for the given ID
     */
    public AudioFile get(int id) {
        return pool.get(id);
    }

    /**
     * Checks if the player is currently paused.
     *
     * @return {@code true} if the player is paused; {@code false} otherwise
     */
    public boolean isPaused() {
        return isPaused;
    }
}