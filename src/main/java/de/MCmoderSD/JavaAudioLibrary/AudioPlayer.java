package de.MCmoderSD.JavaAudioLibrary;

import java.util.HashMap;

/**
 * The {@code AudioPlayer} class provides methods for managing and playing multiple
 * {@link AudioFile} instances. It supports playback, pausing, resuming, and stopping
 * of audio files, as well as tracking audio files by unique IDs.
 */
@SuppressWarnings("ALL")
public class AudioPlayer {

    // Attributes
    private final HashMap<Integer, AudioFile> pool = new HashMap<>();

    // Variables
    private boolean isPaused = false;

    /**
     * Plays audio data from a byte array by creating an {@link AudioFile} instance.
     *
     * @param audioData the audio data as a byte array
     */
    public void play(byte[] audioData) {
        play(new AudioFile(audioData));
    }

    /**
     * Plays the specified {@link AudioFile} and adds it to the internal pool
     * with a unique identifier.
     *
     * @param audioFile the {@code AudioFile} to play
     */
    public void play(AudioFile audioFile) {
        if (audioFile == null) return;

        // Generate a unique ID
        var id = pool.size();
        while (pool.containsKey(id)) id++;

        // Add audioFile to pool and start playback
        pool.put(id, audioFile);
        audioFile.play(this, id);
    }

    /**
     * Removes the {@link AudioFile} with the specified ID from the pool,
     * closing it if it is currently playing.
     *
     * @param id the unique identifier of the {@code AudioFile} to remove
     */
    public void remove(int id) {
        if (pool.containsKey(id)) {
            pool.get(id).close();
            pool.remove(id);
        } else System.err.println("AudioFile with ID " + id + " not found.");
    }

    /**
     * Pauses playback of all {@link AudioFile} instances in the pool.
     */
    public void pause() {
        isPaused = true;
        for (AudioFile audioFile : pool.values()) audioFile.pause();
    }

    /**
     * Resumes playback of all {@link AudioFile} instances in the pool.
     */
    public void resume() {
        for (AudioFile audioFile : pool.values()) audioFile.resume();
        isPaused = false;
    }

    /**
     * Stops playback of all {@link AudioFile} instances in the pool and clears
     * the pool.
     */
    public void stop() {
        for (AudioFile audioFile : pool.values()) audioFile.close();
        pool.clear();
    }

    /**
     * Checks if the audio player is currently paused.
     *
     * @return {@code true} if the player is paused, {@code false} otherwise
     */
    public boolean isPaused() {
        return isPaused;
    }
}