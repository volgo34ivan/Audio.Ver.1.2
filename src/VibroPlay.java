import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VibroPlay extends JFrame {
    	/**
	 * Kravtsov Iva Alexandrovich 2018г. c.Volgograd
	 */
   private static String TAG = "DEBUG";
   private static final long serialVersionUID = 1L;
   protected static  int SAMPLE_RATE = 0;
   private static int NUMBER_OF_SAMPLES = 0;
   private static float max, min;
   private static double RMS;
   private static String title = "no file";
   private static final int LINES = 10;
   static private JLabel labelRMS = new JLabel();
   static private JSlider zoomer = new JSlider();
   static private JButton btnZoom= new JButton("ZOOM");
   static private JButton btnOpen = new JButton("OPEN FILE");
   static private JButton btnPlay = new JButton("PLAY");
   static private JButton btnStop = new JButton("STOP");
   static private JButton btnSpeed = new JButton("Change speed");
   static private JLabel labelSamples = new JLabel(); //число сэмплов в файле (не редактируется, для информации)
   static private JLabel labelTime = new JLabel(); //время воспроизведения при текущих настройках (не редактируется, для информации)
   static private JLabel labelMaximum = new JLabel(); //максимум (не редактируется, для информации)
   static private JLabel labelMinimum = new JLabel(); //минимум (не редактируется, для информации)
   static private JLabel labelFileName = new JLabel(); //имя открытого файла
   static private JTextField textFieldSamplesRate = new JTextField(); //поле ввода частоты сэмплирование (n точек в секунду)
   static private JTextField textFieldSamplesSpeed = new JTextField(); //скорость воспроизведения в %
   static private DrawComponent Canvas;
   private static File file;
   private static ArrayList<Float> inputData = new ArrayList<>(); // нормализованный сигнал к амплитуде от -120 до +120 для аудио
   private static ArrayList<Float> defaultinputData = new ArrayList<>(); //оригинал данных сигнала из файла
   private static String lineContents;
   private static String message = "Buffer is empty! Open file please!";
   private static VibroPlay app;
   private static int i = 0;
   private static String s = "";
   private static  int NORM = 1000;
   private static MyThread myThread = new MyThread();
   private static Thread thread;
   
class DrawComponent extends JComponent{
       
	   	 private static final long serialVersionUID = 1L;
		 private final static  int DEFAULT_WIDTH = 1000;
		 private static final int DEFAULT_HEIGHT = 250;
		 private int N;
		 private int count = 0;
		 private int normal = 10;
		 private static final int H = DEFAULT_WIDTH/LINES;
		 private static final int V = DEFAULT_HEIGHT/LINES;
	
		 public void paintComponent(Graphics g){
		     		RMS = 0;
		     		float aPoints = 0;
		     		float bNorm =0;
			 	if(inputData.isEmpty()){
			 		N = 1000;
			 		}else{
			 		   N = inputData.size();
			 		   float  points = Float.parseFloat(labelSamples.getText());
			 		   float  norm = (float) NORM; 
			 		   aPoints = points;
			 		   bNorm = norm;
			 		   if(points < norm){
			 		      normal = varUp(norm/points);  
			 		   }else{
			 		      normal = varUp(points/norm);
			 		   }
			 	}
			 	Graphics2D g2 = (Graphics2D) g;
			 	for(int x = 0; x < N-1; x++){
			 			if(!inputData.isEmpty()){
			 			    	g2.setColor(new Color(10, 100, 250));
			 			    	if(aPoints > bNorm){
			 			    	g2.draw(new Line2D.Double(count, 
									(DEFAULT_HEIGHT/2)-inputData.get(x), 
									count + 1, 
									(DEFAULT_HEIGHT/2)-inputData.get(x+1)));
			 			    	}else{
			 			    	g2.draw(new Line2D.Double(count, 
									(DEFAULT_HEIGHT/2)-inputData.get(x), 
									count + normal, 
									(DEFAULT_HEIGHT/2)-inputData.get(x+1)));
			 			    	}
			 				if(max < defaultinputData.get(x)){
			 					max = defaultinputData.get(x);
			 				}
			 				if(min > defaultinputData.get(x)){
			 					min = defaultinputData.get(x);
			 				}
			 				RMS = RMS + defaultinputData.get(x)* defaultinputData.get(x);
			 			}
			 				if(aPoints < bNorm){
			 				   count = count + normal;
			 				}else if(x%normal == 0){
			 				   count = count + 1;
			 				}
			 	}
			 		if(RMS != 0){
			 		    	RMS = Math.sqrt(RMS / defaultinputData.size());  
			 		}			 	
			 	
			 	for(int x = 0; x < LINES + 1; x++){
			 	      g2.setColor(new Color(48,185,192));		
			 	      		//Вертикальная сетка    
			 	      		g2.draw(new Line2D.Double(DEFAULT_WIDTH - H * x, 
			 	      								DEFAULT_HEIGHT/2 - DEFAULT_HEIGHT/2, 
			 	      								DEFAULT_WIDTH-H*x, 
			 	      								DEFAULT_HEIGHT/2 +  DEFAULT_HEIGHT/2));
			 	     //Горизонтальная сетка
			 	     g2.draw(new Line2D.Double(	0, 
			 		     						DEFAULT_HEIGHT-V*x, 
			 		     						DEFAULT_WIDTH, 
			 		     						DEFAULT_HEIGHT-V*x));
			 	}
			 	
				 labelMaximum.setText(Float.toString(max));
				 labelMinimum.setText(Float.toString(min));
				 labelRMS.setText(Double.toString(RMS));
			 	count = 0;
			 	NORM = 1000;
	 }

		 public Dimension getPreferredSize(){ return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT); }
	}
	 	
   public VibroPlay() {
	    super("Vibroplayer V1.1 Kravtsov I.A.");
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setResizable(false);
	    labelSamples.setText(Integer.toString(NUMBER_OF_SAMPLES));
	    textFieldSamplesRate.setText(Integer.toString(SAMPLE_RATE));
	    labelFileName.setText(title);
	    textFieldSamplesSpeed.setText("100");
	    max = 0f;
	    min = 0f;
	    labelRMS.setText(Double.toString(RMS));
	    if(Integer.parseInt(textFieldSamplesRate.getText()) != 0){
		i = 1000 * Integer.parseInt(labelSamples.getText())/Integer.parseInt(textFieldSamplesRate.getText());
		s =Integer.toString(i);
		s = s + " мс";
	    	labelTime.setText(s);
	    }else{
	    	labelTime.setText("0 мс");
	    }
	    
	    
	    Canvas = new DrawComponent();
	    
	    /**
	     * Настройка разметки по умолчанию
	     * По умолчанию натуральная высота и максимальная ширина
	     */
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.weightx = 0.5;
	    constraints.gridy = 0; //нулевя ячейка таблицы по вертикали

	    JPanel container1 = new JPanel ();//контейнер для панели управления
	    container1.setLayout(new GridBagLayout());
	    container1.setBackground(new Color(48,185,192));
	      
	    JPanel container2 = new JPanel (); //контейнер для графика
	    container2.setLayout(new GridLayout(1,1));
	    container2.setBackground(new Color(0,0,0));
	    
	    /*
	     * Заполнение контейнеров
	     */
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    container1.add(new JLabel("Sample rate: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 0;
	    container1.add(textFieldSamplesRate, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    container1.add(new JLabel("Samples: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 1;
	    container1.add(labelSamples, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 2;
	    container1.add(new JLabel("Opened: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 2;
	    container1.add(labelFileName, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 3;
	    container1.add(btnOpen, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 3;
	    container1.add(btnPlay, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 2;
	    constraints.gridy = 3;
	    container1.add(btnStop, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 4;
	    container1.add(new JLabel("MAX: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 4;
	    container1.add(labelMaximum, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 5;
	    container1.add(new JLabel("MIN: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 5;
	    container1.add(labelMinimum, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 6;
	    container1.add(new JLabel("RMS: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 6;
	    container1.add(labelRMS, constraints);

	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 9;
	    container1.add(btnSpeed, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 10;
	    container1.add(textFieldSamplesSpeed, constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 10;
	    container1.add(new JLabel("%"), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 11;
	    container1.add(new JLabel("Time play: ", SwingConstants.RIGHT), constraints);
	    
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 1;
	    constraints.gridy = 11;
	    container1.add(labelTime, constraints);
	    /*
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridwidth = 2;
	    constraints.gridy = 12;
	    container1.add(btnZoom, constraints);
	    */

	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridx = 0;
	    constraints.gridy = 13;
	    constraints.gridwidth = 3;
	    zoomer.setValue(100);
	    container1.add(zoomer, constraints);

	    container2.add(Canvas);
	    
	    btnOpen.addActionListener(new ButtonEventListener());
	    btnPlay.addActionListener(new ButtonEventListener());
	    btnStop.addActionListener(new ButtonEventListener());
	    btnSpeed.addActionListener(new ButtonEventListener());
	    btnZoom.addActionListener(new ButtonEventListener());
	    zoomer.addChangeListener(new ChangeListener(){
		public void stateChanged(ChangeEvent e){
		    	float n = Float.parseFloat(labelSamples.getText());
		       	float k = 1000/n;
			NORM = zoomer.getValue()/10*NORM;
			if(NORM == 0){NORM = 1000;}
			Canvas.repaint();
		}
		
	    });
	    
	    add(container1, BorderLayout.NORTH);
	    add(container2, BorderLayout.SOUTH);
	    this.pack();
	}
   
   class ButtonEventListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()){
			case "PLAY":
				myThread.setStop(false);
				checkBuffer(inputData);
			break;
			case "STOP":
				myThread.setStop(true);
				myThread.getLine().stop();

			break;
			case "OPEN FILE":
				OpenFile();
				timeSet();
			break;
			case "Change speed":
				timeSet();
			break;
			case "ZOOM":
			       	float n = Float.parseFloat(labelSamples.getText());
			       	float k = 1000/n;
				NORM = zoomer.getValue()/10*NORM;
				if(NORM == 0){NORM = 1000;}
				Canvas.repaint();
			break;
			}
		}
			
		private void timeSet(){
			textFieldSamplesRate.setText(Integer.toString(Math.abs(Integer.parseInt(labelSamples.getText()) * Integer.parseInt(textFieldSamplesSpeed.getText())/100)));
			if(Integer.parseInt(textFieldSamplesRate.getText()) != 0){
			    i = 1000 * Integer.parseInt(labelSamples.getText())/Integer.parseInt(textFieldSamplesRate.getText());
			    s =Integer.toString(i) + " ms";
		    	labelTime.setText(s);	
		    }else{
		    	labelTime.setText("0 ms");
		    }
		}
	}

   public static int varUp(double d){ //метод округления числа Вверх. Example: 3,1 = 4;  3,9 = 4; 
    int ans = 0;
    int in = (int) d;
    double d2 = 0;
    double d3 =0;
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

   public static byte[] createWaveBuffer() {
       float[] inputDataOut = new float[inputData.size()];
       for(int d = 0; d < inputData.size()-1; d++){inputDataOut[d] = inputData.get(d);}
       return FloatArray2ByteArray(inputDataOut);
   }
   
   public static void OpenFile(){
	   JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.showOpenDialog(app);
		file = chooser.getSelectedFile();
		inputData.clear();
		defaultinputData.clear();
	    labelSamples.setText(Integer.toString(NUMBER_OF_SAMPLES));
		textFieldSamplesRate.setText(Integer.toString(SAMPLE_RATE));
		if(file != null){
			loadBuffer();
			Canvas.repaint();
			reAmp();
			labelSamples.setText(Integer.toString(inputData.size()));
			textFieldSamplesRate.setText(Integer.toString(inputData.size()));
			labelFileName.setText(file.getName());
		}
   }
   
   public static void getScreenSize(){
	   Toolkit kit = Toolkit.getDefaultToolkit();
	   Dimension screenSize = kit.getScreenSize();
	   int screenWidth = screenSize.width;
	   int screenHeight = screenSize.height;
   }
   
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
				SAMPLE_RATE = Integer.parseInt(textFieldSamplesRate.getText());
				try {start();} catch (LineUnavailableException e1) {e1.printStackTrace();}
	   		break;
	   		}
   }
   
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
				//if(lineContents.split("\t")[0].equals("мсек")){enter = true;}
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
   
    public static byte[] FloatArray2ByteArray(float[] values){
      byte[] out = new byte[values.length];
      
      for(int x=0; x<out.length-1; x++){
    	  out[x] = (byte) values[x];
      }
   
      return out;
   }
    
    public static void start() throws LineUnavailableException{
    	thread = new Thread(myThread);
    	thread.start();
	}
	
   
   public static void clearBuffer(){
	   inputData.clear();
       labelSamples.setText(Integer.toString(0));
	   textFieldSamplesRate.setText(Integer.toString(0));
   }
   
   public static void main(String[] args) {
	   EventQueue.invokeLater(new Runnable()
	   {
	   public void run()
	   {
		   app = new VibroPlay();
	       app.setVisible(true);
	   }
    });
   }
}
