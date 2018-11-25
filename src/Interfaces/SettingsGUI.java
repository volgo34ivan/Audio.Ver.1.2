package Interfaces;

import javax.swing.*;

/**
 * Основные настройки элементов пользовательского интерфейса.
 */

public interface SettingsGUI {
    JLabel labelRMS = new JLabel();
    JSlider zoomer = new JSlider();
    JButton btnZoom= new JButton("ZOOM");
    JButton btnOpen = new JButton("OPEN FILE");
    JButton btnPlay = new JButton("PLAY");
    JButton btnStop = new JButton("STOP");
    JButton btnSpeed = new JButton("Change speed");
    JLabel labelSamples = new JLabel(); //число сэмплов в файле (не редактируется, для информации)
    JLabel labelTime = new JLabel(); //время воспроизведения при текущих настройках (не редактируется, для информации)
    JLabel labelMaximum = new JLabel(); //максимум (не редактируется, для информации)
    JLabel labelMinimum = new JLabel(); //минимум (не редактируется, для информации)
    JLabel labelFileName = new JLabel(); //имя открытого файла
    JTextField textFieldSamplesRate = new JTextField(); //поле ввода частоты сэмплирование (n точек в секунду)
    JTextField textFieldSamplesSpeed = new JTextField(); //скорость воспроизведения в %
}
