package Interfaces;

import java.util.ArrayList;
/**
 *  Basic program settings (various constants)
*/
public interface SettingsGeneral {
    int DEFAULT_WIDTH = 1000; // standard width of the signal graph window
    int DEFAULT_HEIGHT = 250; // standard height of the signal graph window
    int NUMBER_OF_SAMPLES = 0; // number of samples at the moment the program was turned on
    int LINES = 10; // number of vertical lines in the signal graph window
    String title = "no file"; // hint that no file is open
    String message = "Buffer is empty! Open file please!"; // hint that the user has not opened the file
    ArrayList<Float> inputData = new ArrayList<>(); // normalized signal to amplitude from -120 to +120 for audio
    ArrayList<Float> defaultinputData = new ArrayList<>(); // original signal data from file
}
