package Interfaces;

import java.util.ArrayList;
/**
 *Основные настройки программы(различные константы)
*/
public interface SettingsGeneral {
    int DEFAULT_WIDTH = 1000; //стандартная ширина окна графика сигнала
    int DEFAULT_HEIGHT = 250; //стандартная высота окна графика сигнала
    int NUMBER_OF_SAMPLES = 0; // количество сэмплов на момент включения программы
    int LINES = 10; //количество вертикальных линий в окне графика сигнала
    String title = "no file"; //подсказка, что никакой файл не открыт
    String message = "Buffer is empty! Open file please!"; //подсказка о том, что пользователь не открыл файл
    ArrayList<Float> inputData = new ArrayList<>(); // нормализованный сигнал к амплитуде от -120 до +120 для аудио
    ArrayList<Float> defaultinputData = new ArrayList<>(); //оригинал данных сигнала из файла
}
