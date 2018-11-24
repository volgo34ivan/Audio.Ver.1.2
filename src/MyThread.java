import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MyThread implements Runnable {
	
	 private Boolean stop = false;

	private SourceDataLine line;

	@Override
	public void run() {
		while(!stop){
			final AudioFormat af = new AudioFormat(VibroPlay.SAMPLE_RATE, 8, 1, true, true);
	    	   byte [] toneBuffer = VibroPlay.createWaveBuffer();
	           try {
	        	line = AudioSystem.getSourceDataLine(af);
				line.open(af, VibroPlay.SAMPLE_RATE);
				line.start();
		        line.write(toneBuffer, 0, toneBuffer.length);
		        line.close();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	public SourceDataLine getLine() {
		return line;
	}

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }       
}
