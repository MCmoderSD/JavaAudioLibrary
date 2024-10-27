# Java Audio Library (JAL)

## Description
A simple Java audio library for recording and playing audio files.

The Library currently only supports .wav files with a sample rate up to 192kHz and up to 16-bit PCM and up to 2 channels (stereo).

## Usage

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>de.MCmoderSD</groupId>
        <artifactId>jal</artifactId>
        <version>1.1.1</version>
    </dependency>
</dependencies>
```

## Usage Example

```java
import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.JavaAudioLibrary.AudioLoader;
import de.MCmoderSD.JavaAudioLibrary.AudioRecorder;

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.Objects;
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
        try {
            audioFile.export("output.wav");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void loadAudio() {

        // Initialize audio loader
        AudioLoader audioLoader = new AudioLoader();
        AudioFile absolutePath = null;
        AudioFile resourcesPath = null;
        AudioFile url = null;

        try {

            // Load audio from absolute path
            absolutePath = audioLoader.load("output.wav", true);
            System.out.println("Loaded audio from absolute path: " + absolutePath);

            // Load audio from resources path
            resourcesPath = audioLoader.load("/Eating.wav");
            System.out.println("Loaded audio from resources path: " + resourcesPath);

            // Load audio from URL
            url = audioLoader.load("https://raw.githubusercontent.com/MCmoderSD/JavaAudioLibrary/refs/heads/master/src/test/resources/Eating.wav");
            System.out.println("Loaded audio from URL: " + url);

        } catch (IOException | URISyntaxException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Debug
        System.out.println("Finished loading audio.");
        System.out.println("Press enter to listen to the audio...");
        scanner.nextLine();

        // Play audio
        Objects.requireNonNull(absolutePath).play();
        scanner.nextLine(); // Wait for input
        Objects.requireNonNull(resourcesPath).play();
        scanner.nextLine(); // Wait for input
        Objects.requireNonNull(url).play();
    }
}
```