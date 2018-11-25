import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This class will play back a stream.
 * Data is taken from the array byte[] out
 */

public class MyThread implements Runnable {
	
	 	private Boolean stop = false;
		private SourceDataLine line;

	@Override
	public void run() {
		while(!stop){
			final AudioFormat af = new AudioFormat(VibroPlay.SAMPLE_RATE,
					8,
					1,
					true,
					true);
	    	   byte [] toneBuffer = FetchData.createWaveBuffer();
	           try {
	        	line = AudioSystem.getSourceDataLine(af);
				line.open(af, VibroPlay.SAMPLE_RATE);
				line.start();
		        line.write(toneBuffer, 0, toneBuffer.length);
		        line.close();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
        }
    }

	public SourceDataLine getLine() {
		return line;
	}

    public void setStop(Boolean stop) {
        this.stop = stop;
    }       
}
