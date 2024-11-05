package de.MCmoderSD.JavaAudioLibrary;

import de.MCmoderSD.executor.NanoLoop;

import java.util.HashMap;
/**
 * A utility class for managing and controlling the playback of audio files.
 * Provides methods to play, pause, resume, and stop audio files, as well as to manage
 * a pool of active and paused audio files. Supports automatic cleanup of finished audio.
 */
@SuppressWarnings("ALL")
public class AudioPlayer {

    // Attributes
    private final HashMap<Integer, AudioFile> pool;
    private final HashMap<Integer, AudioFile> pausedPool;
    private final NanoLoop cleanupTask;

    // Variables
    private boolean isPaused;

    /**
     * Constructs a new AudioPlayer instance, initializing the audio pools and
     * starting a cleanup task to automatically remove finished audio files.
     */
    public AudioPlayer() {

        // Initialize pools
        pool = new HashMap<>();
        pausedPool = new HashMap<>();

        // Initialize variables
        isPaused = false;

        // Start cleanup task
        cleanupTask = new NanoLoop(this::cleanUp, 100);
    }
    /**
     * Logs an error message when an audio file with the given ID does not exist.
     *
     * @param id the ID of the audio file
     */
    private static void idDoesNotExist(int id) {
        System.err.println("AudioFile with ID " + id + " not found.");
    }

    /**
     * Removes finished audio files from the active pool and adjusts the cleanup interval.
     */
    private void cleanUp() {

        // Check if pool is empty or player is paused
        if (pool.isEmpty() || isPaused) return;

        // Remove finished audio files
        pool.entrySet().removeIf(entry -> !entry.getValue().isPlaying());

        // Update cleanup task
        cleanupTask.setModifier(1f / pool.size());
    }

    /**
     * Plays an audio file from the given byte array data.
     *
     * @param audioData the byte array containing audio data
     * @return the unique ID assigned to the audio file in the pool
     */
    public Integer play(byte[] audioData) {
        return play(new AudioFile(audioData));
    }

    /**
     * Plays the specified AudioFile instance.
     *
     * @param audioFile the AudioFile to be played
     * @return the unique ID assigned to the audio file in the pool, or null if the audioFile is null
     */
    public Integer play(AudioFile audioFile) {
        if (audioFile == null) return null;

        // Resume player if paused
        if (isPaused) resume();

        // Generate a unique ID
        var id = pool.size();
        while (pool.containsKey(id)) id++;

        // Copy the audio file
        AudioFile instance = audioFile.copy();

        // Add the audio file to the pool
        pool.put(id, instance);
        instance.play();

        // Update the cleanup task
        cleanupTask.setModifier(1f / pool.size());

        // Return the ID
        return id;
    }

    /**
     * Resumes playback of an audio file with the specified ID.
     *
     * @param id the ID of the audio file
     */
    public void play(int id) {
        if (!pool.containsKey(id)) idDoesNotExist(id);
        else pool.get(id).play();
    }

    /**
     * Pauses the audio file with the specified ID.
     *
     * @param id the ID of the audio file
     */
    public void pause(int id) {
        if (!pool.containsKey(id)) idDoesNotExist(id);
        else {
            AudioFile audioFile = pool.get(id);
            audioFile.pause();
            pausedPool.put(id, audioFile);
            pool.remove(id);
        }
    }

    /**
     * Resumes the audio file with the specified ID.
     *
     * @param id the ID of the paused audio file
     */
    public void resume(int id) {
        if (!pausedPool.containsKey(id)) idDoesNotExist(id);
        else {
            AudioFile audioFile = pausedPool.get(id);
            audioFile.resume();
            pool.put(id, audioFile);
            pausedPool.remove(id);
        }
    }

    /**
     * Removes the audio file with the specified ID from the pool and closes it.
     *
     * @param id the ID of the audio file
     */
    public void remove(int id) {
        if (!pool.containsKey(id)) idDoesNotExist(id);
        else {
            pool.get(id).close();
            pool.remove(id);
        }
    }

    /**
     * Pauses all active audio files and stops the cleanup task.
     */
    public void pause() {
        cleanupTask.stop();
        isPaused = true;
        for (AudioFile audioFile : pool.values()) audioFile.pause();
    }

    /**
     * Resumes all paused audio files and restarts the cleanup task.
     */
    public void resume() {
        for (AudioFile audioFile : pool.values()) audioFile.resume();
        isPaused = false;
        cleanupTask.start();
    }

    /**
     * Stops all audio playback, clears the pool, and stops the cleanup task.
     */
    public void stop() {
        for (AudioFile audioFile : pool.values()) audioFile.close();
        pool.clear();
        cleanupTask.stop();
    }

    /**
     * Checks if an audio file with the specified ID exists in the pool or paused pool.
     *
     * @param id the ID of the audio file
     * @return true if the audio file exists, otherwise false
     */
    public boolean contains(int id) {
        return pool.containsKey(id) || pausedPool.containsKey(id);
    }

    /**
     * Checks if the audio player is currently paused.
     *
     * @return true if paused, otherwise false
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Checks if the audio file with the specified ID is currently paused.
     *
     * @param id the ID of the audio file
     * @return true if the audio file is paused, otherwise false
     */
    public boolean isPaused(int id) {
        if (!pausedPool.containsKey(id)) idDoesNotExist(id);
        else return pausedPool.get(id).isPlaying();
        return false;
    }

    /**
     * Checks if the audio file with the specified ID is currently playing.
     *
     * @param id the ID of the audio file
     * @return true if the audio file is playing, otherwise false
     */
    public boolean isPlaying(int id) {
        if (!pool.containsKey(id)) idDoesNotExist(id);
        else return pool.get(id).isPlaying();
        return false;
    }

    /**
     * Retrieves the audio file with the specified ID from the pool.
     *
     * @param id the ID of the audio file
     * @return the AudioFile instance, or null if not found
     */
    public AudioFile get(int id) {
        if (!pool.containsKey(id)) idDoesNotExist(id);
        else return pool.get(id);
        return null;
    }

    /**
     * Retrieves the paused audio file with the specified ID.
     *
     * @param id the ID of the paused audio file
     * @return the paused AudioFile instance, or null if not found
     */
    public AudioFile getPaused(int id) {
        if (!pausedPool.containsKey(id)) idDoesNotExist(id);
        else return pausedPool.get(id);
        return null;
    }

    /**
     * Gets the current pool of active audio files.
     *
     * @return the pool of active audio files
     */
    public HashMap<Integer, AudioFile> getPool() {
        return pool;
    }

    /**
     * Gets the current pool of paused audio files.
     *
     * @return the pool of paused audio files
     */
    public HashMap<Integer, AudioFile> getPausedPool() {
        return pausedPool;
    }

    /**
     * Gets the size of the active audio pool.
     *
     * @return the number of active audio files
     */
    public int getPoolSize() {
        return pool.size();
    }

    /**
     * Gets the size of the paused audio pool.
     *
     * @return the number of paused audio files
     */
    public int getPausedSize() {
        return pausedPool.size();
    }

    /**
     * Gets the total number of audio files, both active and paused.
     *
     * @return the total count of audio files
     */
    public int getSize() {
        return getPoolSize() + getPausedSize();
    }
}