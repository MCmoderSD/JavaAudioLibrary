# Java Audio Library (JAL)

## Description
A simple Java audio library for recording and playing audio files.

## Usage

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>de.MCmoderSD</groupId>
        <artifactId>jal</artifactId>
        <version>1.0.4</version>
    </dependency>
</dependencies>
```

## Usage Example
```java
import de.MCmoderSD.jal.AudioFile;
import de.MCmoderSD.jal.AudioRecorder;

import java.util.Scanner;

public class Main {

    // Main method
    public static void main(String[] args) {

        // Create scanner and audio recorder
        Scanner scanner = new Scanner(System.in);
        AudioRecorder audioRecorder = new AudioRecorder();

        // Start recording
        System.out.println("Press enter to start recording.");
        scanner.nextLine();

        // Start recording
        audioRecorder.startRecording();

        // Stop recording
        System.out.println("Recording... Press enter to stop recording.");
        scanner.nextLine();

        // Stop recording
        audioRecorder.stopRecording();

        // Get audio file
        AudioFile audioFile = audioRecorder.getAudioFile();

        // Export audio file
        audioFile.exportToWav("audio.wav");

        // Play audio file
        audioFile.play();
    }
}
```