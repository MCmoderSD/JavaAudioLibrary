import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.JavaAudioLibrary.AudioLoader;
import de.MCmoderSD.JavaAudioLibrary.AudioRecorder;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Record audio
        recordAudio();

        // Load audio
        loadAudio();
    }

    public static void recordAudio() {

        // Initialize audio recorder
        AudioRecorder audioRecorder = new AudioRecorder();

        // Record audio
        System.out.println("Press enter to start recording...");
        scanner.nextLine();
        audioRecorder.startRecording();

        // Wait for input
        System.out.println("Press enter to stop recording...");
        scanner.nextLine();

        // Stop recording (optional)
        audioRecorder.stopRecording();

        // Get audio file
        AudioFile audioFile = audioRecorder.getAudioFile();

        // Export audio
        audioFile.export("output.wav");
    }

    public static void loadAudio() {

        // Audio Loader
        AudioLoader audioLoader = new AudioLoader();

        System.out.println("Loading audio from Resources...");

        // Load audio file from Absolute Path
        try {
            AudioFile audioFile = audioLoader.load("output.wav", true);
            audioFile.play();
            System.out.println("Press enter to play next");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error loading audio file: " + e.getMessage());
        }

        System.out.println("Loading audio from Resources...");

        // Load audio file from Resources
        try {
            AudioFile audioFile = audioLoader.load("/Eating.wav");
            audioFile.play();
            System.out.println("Press enter to play next");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error loading audio file: " + e.getMessage());
        }

        System.out.println("Loading audio from URL...");

        // Load audio file from URL
        try {
            AudioFile audioFile = audioLoader.load("https://raw.githubusercontent.com/MCmoderSD/ImageLoader/refs/heads/master/src/test/resources/Eating.wav");
            audioFile.play();
            System.out.println("Press enter to play next");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error loading audio file: " + e.getMessage());
        }

        System.out.println("Loaded all audio files.");
    }
}
