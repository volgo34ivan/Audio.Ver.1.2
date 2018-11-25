package Interfaces;

import javax.swing.*;

/**
 * Basic user interface settings.
 */

public interface SettingsGUI {
    JLabel labelRMS = new JLabel();
    JSlider zoomer = new JSlider();
    JButton btnZoom= new JButton("ZOOM");
    JButton btnOpen = new JButton("OPEN FILE");
    JButton btnPlay = new JButton("PLAY");
    JButton btnStop = new JButton("STOP");
    JButton btnSpeed = new JButton("Change speed");
    JLabel labelSamples = new JLabel(); // number of samples in the file (not editable, for information)
    JLabel labelTime = new JLabel(); // playback time at current settings (not editable, for information)
    JLabel labelMaximum = new JLabel(); // maximum (not editable, for information)
    JLabel labelMinimum = new JLabel(); // minimum (not editable, for information)
    JLabel labelFileName = new JLabel(); // name of the open file
    JTextField textFieldSamplesRate = new JTextField(); // sampling frequency input field (n points per second)
    JTextField textFieldSamplesSpeed = new JTextField(); // playback speed in%
}
