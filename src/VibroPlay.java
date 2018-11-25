import Interfaces.SettingsGUI;
import Interfaces.SettingsGeneral;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Kravtsov Ivan Alexandrovich 2018г. c.Volgograd
 */

public class VibroPlay extends JFrame  implements SettingsGeneral, SettingsGUI {

	static DrawComponent Canvas;
	static VibroPlay app;
	static MyThread myThread = new MyThread();
	static Thread thread;
	static String s = "";
	static int i = 0;
	static int NORM = 1000; //Число точек графика сигнала по оси Х
	static int SAMPLE_RATE = 0; //Частота сэмплирования
	static double RMS; //средеквадратичное значение сигнала

	/** DrawComponent
	 *  Этот метод предназначен для вывода графика сигнала
	 */
   
class DrawComponent extends JComponent{
       
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
			 		      normal = FetchData.varUp(norm/points);
			 		   }else{
			 		      normal = FetchData.varUp(points/norm);
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
			 				if(FetchData.getMax() < defaultinputData.get(x)){
			 					FetchData.setMax(defaultinputData.get(x));
			 				}
			 				if(FetchData.getMin() > defaultinputData.get(x)){
			 					FetchData.setMin(defaultinputData.get(x));
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
			 	
				 labelMaximum.setText(String.valueOf(FetchData.getMax()));
				 labelMinimum.setText(String.valueOf(FetchData.getMin()));
				 labelRMS.setText(Double.toString(RMS));
			 	count = 0;
			 	NORM = 1000;
	 }

		 public Dimension getPreferredSize(){ return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT); }
	}

	/** VibroPlay
	 *  Запускается при создании программного интерфейса.
	 *  Это конструктор главного класса.
	 */
	 	
   public VibroPlay() {
	    super("Vibroplayer V1.1 Kravtsov I.A.");
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setResizable(false);
	    labelSamples.setText(Integer.toString(NUMBER_OF_SAMPLES));
	    textFieldSamplesRate.setText(Integer.toString(SAMPLE_RATE));
	    labelFileName.setText(title);
	    textFieldSamplesSpeed.setText("10");
	    FetchData.setMax(0f);
	    FetchData.setMin(0f);
	    labelRMS.setText(Double.toString(RMS));

	    	if(Integer.parseInt(textFieldSamplesRate.getText()) != 0){
				i = 1000 * Integer.parseInt(labelSamples.getText())/Integer.parseInt(textFieldSamplesRate.getText());
				s =Integer.toString(i);
				s = s + " ms";
	    		labelTime.setText(s);
	    	}else{
	    			labelTime.setText("0 ms");
	    	}
	    
	    
	    Canvas = new DrawComponent();
	    
	    /** Здесь происходит компоновка элементов пользовательского интерфейса
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
	    zoomer.addChangeListener(e -> {
				float n = Float.parseFloat(labelSamples.getText());
				   float k = 1000/n;
			NORM = zoomer.getValue()/10*NORM;
			if(NORM == 0){NORM = 1000;}
			Canvas.repaint();
		});
	    
	    add(container1, BorderLayout.NORTH);
	    add(container2, BorderLayout.SOUTH);
	    this.pack();
	}

	/**
	 * Действия, выполняемые при взаимодействии с пользовательским интерфейсом
	 */
   
   class ButtonEventListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()){
			case "PLAY":
				myThread.setStop(false);
				FetchData.checkBuffer(inputData);
			break;
			case "STOP":
				myThread.setStop(true);
				myThread.getLine().stop();

			break;
			case "OPEN FILE":
				FetchData.OpenFile();
				GuiMethods.timeSet();
			break;
			case "Change speed":
				GuiMethods.timeSet();
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
	}

	/** start
	 * Этот метод запускает воспроизведение сигнала.
	 */

    public static void start(){
    	thread = new Thread(myThread);
    	thread.start();
	}

	/** main
	 *  Старт программы начинается здесь.
	 *  Эту подробность можно было опустить, но она намеренно оставлена разработчиком. :)
	 */
   
   public static void main(String[] args) {
	   EventQueue.invokeLater(() -> {
		   app = new VibroPlay();
	       app.setVisible(true);
	   });
   }
}
