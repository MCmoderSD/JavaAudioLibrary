package de.MCmoderSD.JavaAudioLibrary;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * The {@code AudioLoader} class provides a utility for loading, caching, and managing audio files.
 * It supports loading audio files from a file system or a URL and caches them to improve performance.
 */
@SuppressWarnings("ALL")
public class AudioLoader {

    // Attributes
    private final HashMap<String, AudioFile> cache = new HashMap<>();

    /**
     * Loads an {@code AudioFile} from the specified path.
     *
     * @param path the path of the audio file.
     * @return the loaded {@code AudioFile}.
     * @throws IOException          if the file could not be loaded or is of an unsupported format.
     * @throws URISyntaxException   if the path is an invalid URI.
     */
    public AudioFile load(String path) throws IOException, URISyntaxException {
        return load(path, false);
    }

    /**
     * Loads an {@code AudioFile} from the specified path, with an option to specify whether the path is absolute.
     *
     * @param path       the path of the audio file.
     * @param isAbsolute whether the path is absolute.
     * @return the loaded {@code AudioFile}.
     * @throws IOException          if the file could not be loaded or is of an unsupported format.
     * @throws URISyntaxException   if the path is an invalid URI.
     */
    public AudioFile load(String path, boolean isAbsolute) throws IOException, URISyntaxException {

        // Check if AudioFile is already in cache
        if (cache.containsKey(path)) return cache.get(path);

        // Get file extension
        String extension = getExtension(path);

        // Load AudioFile based on extension
        AudioFile audioFile = switch (extension) {
            case "wav" -> loadAudio(path, isAbsolute);
            default -> throw new IOException("Unsupported AudioFile format: " + extension);
        };

        // Check if AudioFile is null
        if (audioFile == null) throw new IOException("AudioFile could not be loaded: " + path);

        // Put AudioFile in cache
        cache.put(path, audioFile);
        return audioFile;
    }

    /**
     * Loads an {@code AudioFile} based on the given path and its type (absolute or relative).
     *
     * @param path       the path to the audio file.
     * @param isAbsolute whether the path is absolute.
     * @return the loaded {@code AudioFile}.
     * @throws IOException          if the file path is invalid, the file does not exist, or the format is unsupported.
     * @throws URISyntaxException   if the path is an invalid URI.
     */
    public static AudioFile loadAudio(String path, boolean isAbsolute) throws IOException, URISyntaxException {

        // Validates the AudioFile path and loads accordingly
        if (path == null || path.isEmpty() || path.isBlank()) throw new IOException("AudioFile path is null or empty.");
        if (path.endsWith(".")) throw new IOException("AudioFile path is missing file extension: " + path);

        // Ensure image format is supported
        if (!Arrays.asList("wav").contains(getExtension(path)))
            throw new IOException("Unsupported AudioFile format: " + getExtension(path));

        // Load AudioFile based on path type
        if (isAbsolute) {
            File file = new File(path);
            if (!file.exists()) throw new IOException("AudioFile not found: " + path);
            return new AudioFile(Files.readAllBytes(file.toPath()));
        } else if (path.startsWith("http://") || path.startsWith("https://")) {
            return new AudioFile(Files.readAllBytes(Paths.get(new URI(path))));
        } else {
            return new AudioFile(Objects.requireNonNull(AudioLoader.class.getResourceAsStream(path)).readAllBytes());
        }
    }

    /**
     * Retrieves the file extension from the provided path.
     *
     * @param path the file path.
     * @return the file extension.
     */
    public static String getExtension(String path) {
        return path.substring(path.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Adds an {@code AudioFile} to the cache.
     *
     * @param path      the path of the audio file.
     * @param audioFile the {@code AudioFile} to cache.
     */
    public void add(String path, AudioFile audioFile) {
        cache.put(path, audioFile);
    }

    /**
     * Replaces an existing {@code AudioFile} in the cache with a new one.
     *
     * @param path      the path of the audio file.
     * @param audioFile the new {@code AudioFile} to replace the old one.
     */
    public void replace(String path, AudioFile audioFile) {
        cache.replace(path, audioFile);
    }

    /**
     * Removes an {@code AudioFile} from the cache using its path.
     *
     * @param path the path of the audio file to remove.
     */
    public void remove(String path) {
        cache.remove(path);
    }

    /**
     * Removes an {@code AudioFile} from the cache.
     *
     * @param audioFile the {@code AudioFile} to remove.
     */
    public void remove(AudioFile audioFile) {
        cache.remove(get(audioFile));
    }

    /**
     * Clears all entries in the cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Returns the current state of the cache.
     *
     * @return a {@code HashMap} containing the cached audio files.
     */
    public HashMap<String, AudioFile> get() {
        return cache;
    }

    /**
     * Retrieves an {@code AudioFile} from the cache using its path.
     *
     * @param path the path of the audio file.
     * @return the cached {@code AudioFile}, or {@code null} if it is not in the cache.
     */
    public AudioFile get(String path) {
        if (contains(path)) return cache.get(path);
        else return null;
    }

    /**
     * Retrieves the path of a cached {@code AudioFile}.
     *
     * @param audioFile the {@code AudioFile} to search for.
     * @return the path of the {@code AudioFile}, or {@code null} if it is not in the cache.
     */
    public String get(AudioFile audioFile) {
        if (contains(audioFile)) {
            for (String key : cache.keySet()) {
                if (cache.get(key).equals(audioFile)) return key;
            }
        }
        return null;
    }

    /**
     * Checks whether the cache contains an {@code AudioFile} with the specified path.
     *
     * @param path the path to check.
     * @return {@code true} if the file is in the cache, {@code false} otherwise.
     */
    public boolean contains(String path) {
        return cache.containsKey(path);
    }

    /**
     * Checks whether the cache contains the specified {@code AudioFile}.
     *
     * @param audioFile the {@code AudioFile} to check.
     * @return {@code true} if the file is in the cache, {@code false} otherwise.
     */
    public boolean contains(AudioFile audioFile) {
        return cache.containsValue(audioFile);
    }

    /**
     * Checks whether the cache is empty.
     *
     * @return {@code true} if the cache is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * Returns the number of audio files in the cache.
     *
     * @return the size of the cache.
     */
    public int size() {
        return cache.size();
    }
}