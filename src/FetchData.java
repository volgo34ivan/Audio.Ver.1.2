import Interfaces.SettingsGUI;
import Interfaces.SettingsGeneral;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

    /**
    *   This class is designed to get data from files.
    */
public class FetchData implements SettingsGUI, SettingsGeneral {

    private static File file;
    private static String lineContents;
    private static float max, min;

    public static void setMax(float max) {
        FetchData.max = max;
    }

    public static void setMin(float min) {
        FetchData.min = min;
    }

    public static float getMax() {
        return max;
    }

    public static float getMin() {
        return min;
    }

    /** OpenFile
     *  The standard method for displaying a java explorer window to select a file with signal data.
     */

    public static void OpenFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.showOpenDialog(VibroPlay.app);
        file = chooser.getSelectedFile();
        inputData.clear();
        defaultinputData.clear();
        labelSamples.setText(Integer.toString(NUMBER_OF_SAMPLES));
        textFieldSamplesRate.setText(Integer.toString(VibroPlay.SAMPLE_RATE));
        if(file != null){
            loadBuffer();
            reAmp();
            labelSamples.setText(Integer.toString(inputData.size()));
            textFieldSamplesRate.setText(Integer.toString(inputData.size()));
            labelFileName.setText(file.getName());
            VibroPlay.Canvas.repaint();
        }
    }

    /**loadBuffer
     * this method takes data from the * .txt file
     * currently implemented as a bootloader from the 19th line in the file
     * later will be redone as a universal loader
     * data is taken from a file in this form:
     * column 1 (time) (tabulation) column 2 (instantaneous value)
     * 0.00000  1.12345 << it is important that this line is number 19 !
     * 0.00100  -1.23452
     * 0.00200  4.32123
     */

    public static void loadBuffer(){
        boolean enter = false;
        boolean subEnter = false;
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(file));
            try {
                int c = 0;
                int row = 1;
                while((lineContents = bReader.readLine()) != null){
                    row++;
                    if(row == 19){enter = true;}
                    if(enter){
                        if(subEnter){
                            inputData.add(Float.parseFloat(lineContents.split("\t")[1]));
                            defaultinputData.add(Float.parseFloat(lineContents.split("\t")[1]));
                            c++;
                        }
                        subEnter = true;
                    }
                }
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**reAmp
     * this method converts the signal to a level from -120 to +120
     * if the maximum in the signal is 200, then 200 will become equal to 120
     * other numbers will change in proportion to the maximum
     */

    public static void reAmp (){
        max = inputData.get(0);
        min = inputData.get(0);
        for(int x = 1; x < inputData.size() - 1; x++){
            if(max < inputData.get(x)){max = inputData.get(x);}
            if(min > inputData.get(x)){min = inputData.get(x);}
        }
        for(int y=0; y<inputData.size()-1; y++){
            float k1 = Math.abs(max/120);
            float k2 = Math.abs(min/120);
            if(inputData.get(y)<0f){
                inputData.set(y, inputData.get(y)/k2);
            }else{
                inputData.set(y, inputData.get(y)/k1);
            }
        }
    }

    /**createWaveBuffer
     * This method creates a data buffer for playback.
     * returns the data buffer as an array of numbers in the byte format.
     */

    public static byte[] createWaveBuffer() {
        float[] inputDataOut = new float[inputData.size()];
        for(int d = 0; d < inputData.size()-1; d++){inputDataOut[d] = inputData.get(d);}
        return FloatArray2ByteArray(inputDataOut);
    }

    /**checkBuffer
     * this method will check the contents of the buffer
     * if the buffer is not empty, the signal will start playing
     */

    public static void checkBuffer(ArrayList<Float> list){
        int buffer  = list.size();
        if(buffer != 0){buffer = 1;}
        switch(buffer){
            case 0:
                JOptionPane.showMessageDialog(null,
                        message,
                        "Warning!",
                        JOptionPane.PLAIN_MESSAGE);
                break;
            case 1:
                VibroPlay.SAMPLE_RATE = Integer.parseInt(textFieldSamplesRate.getText());
                VibroPlay.start();
                break;
        }
    }

    /**FloatArray2ByteArray
     * this method converts an array of float values
     * to array of Byte format values
     */

    public static byte[] FloatArray2ByteArray(float[] values){
        byte[] out = new byte[values.length];

        for(int x=0; x<out.length-1; x++){
            out[x] = (byte) values[x];
        }

        return out;
    }

    /**varUp
     * This method rounds double to integer.
     * Rounding is always in a big way.
     * For example, 3.05 or 3.1 will be equal to 4, 3.9 will also be 4.
     */

    public static int varUp(double d){
        int ans = 0;
        int in = (int) d;
        double d2 = 0;
        double d3 = 0;
        double d4 = 0;
        if(d - in == 0){
            ans = (int) d;
        }else{
            d = d + 0.5;
            d2 = (int)d;
            d3 = d2;
            if(d - d2 < 0.5 & d - d2 != 0){
                d = d + 1;
                d2 = (int) d;
                d4 = d2;
                if(d3 < d4){
                    ans = (int) d3;
                }else{
                    ans = (int) d4;
                }
            }else{
                d = d + 0.5;
                d2 = (int) d;
                ans = (int) d;
            }
        }
        return ans;
    }
}
