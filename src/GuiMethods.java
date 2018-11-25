public class GuiMethods {
    /** timeSet
     *  This method sets the signal playback time in ms.
     */
    public static void timeSet(){
        VibroPlay.textFieldSamplesRate.setText(
                Integer.toString(
                        Math.abs(
                                Integer.parseInt(
                                        VibroPlay.labelSamples.getText())
                                        * Integer.parseInt(VibroPlay.textFieldSamplesSpeed.getText())/100)));

        if(Integer.parseInt(VibroPlay.textFieldSamplesRate.getText()) != 0){
            VibroPlay.i = 1000 * Integer.parseInt(VibroPlay.labelSamples.
                    getText())/Integer.parseInt(VibroPlay.textFieldSamplesRate.getText());
            VibroPlay.s =Integer.toString(VibroPlay.i) + " ms";
            VibroPlay.labelTime.setText(VibroPlay.s);
        }else{
            VibroPlay.labelTime.setText("0 ms");
        }
    }
}
