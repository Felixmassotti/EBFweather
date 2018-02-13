import java.awt.*;
import java.net.URL;

import javax.swing.*;

public class Grafica extends JFrame {
	//Graphic 
	private JPanel pannelloSup=new JPanel();
	private JPanel pannelloInf=new JPanel();
	private JPanel pannelloInfSup=new JPanel();
	private JPanel pannelloInfInf=new JPanel();
	private JLabel timeS=new JLabel(" Time: ");
	private JLabel descriptionS=new JLabel("Weather: ");
	private JLabel cloudsS=new JLabel(" Clouds: ");
	private JLabel humidityS=new JLabel("Humidity");
	private JLabel pressureS=new JLabel(" Pressure: ");
	private JLabel tempS=new JLabel("Temp: ");
	private JLabel windS=new JLabel(" Wind's speed: ");
	
	//Inputs
	private JButton b_on =new JButton("On");
	private JButton b_share =new JButton("Share");
	private JButton b_off =new JButton("Off");
	private JButton b_succ =new JButton(">>");
	private JButton b_prec =new JButton("<<");
	
	//Outputs
	private JLabel out =new JLabel();
	private JLabel time=new JLabel("          ");
	private JLabel description=new JLabel("          ");
	private JLabel clouds=new JLabel("          ");
	private JLabel humidity=new JLabel("          ");
	private JLabel pressure=new JLabel("          ");
	private JLabel temp=new JLabel("          ");
	private JLabel wind=new JLabel("          ");
	private JLabel space=new JLabel("          ");
	
	//Constructor
	public Grafica(){
		avvio();
		this.setOut("http://www.solidbackgrounds.com/images/640x480/640x480-white-solid-color-background.jpg");
	}
	
	//Graphic options
	private void avvio(){
		//PannelloSup
		pannelloSup.setLayout(new FlowLayout());
		
		out.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		Dimension dim=new Dimension(640,480);
		out.setMaximumSize(dim);
		out.setMinimumSize(dim);
		pannelloSup.add(out);
		
		//PannelloInf
		pannelloInf.setLayout(new BorderLayout());
		
		pannelloInfSup.setLayout(new GridLayout(4,5));
		
		pannelloInfSup.add(timeS);
		pannelloInfSup.add(time);
		pannelloInfSup.add(descriptionS);
		pannelloInfSup.add(description);
		pannelloInfSup.add(cloudsS);
		pannelloInfSup.add(clouds);	
		
		pannelloInfSup.add(humidityS);
		pannelloInfSup.add(humidity);
		
		pannelloInfSup.add(pressureS);
		pannelloInfSup.add(pressure);
		
		pannelloInfSup.add(tempS);
		pannelloInfSup.add(temp);
			
		pannelloInfSup.add(windS);
		pannelloInfSup.add(wind);
		
		pannelloInfSup.add(space);
		pannelloInfSup.add(space);
		
		pannelloInfSup.add(space);
		pannelloInfSup.add(space);
		
		pannelloInfInf.setLayout(new FlowLayout());
		
		b_prec.setEnabled(false);
		b_off.setEnabled(false);
		b_share.setEnabled(false);
		b_succ.setEnabled(false);
		
		b_share.setBackground(Color.decode("#3B5998"));
		b_share.setSize(10,30);
	
		pannelloInfInf.add(b_prec);
		pannelloInfInf.add(b_on);
		pannelloInfInf.add(b_share);
		pannelloInfInf.add(b_off);
		pannelloInfInf.add(b_succ);
		
		pannelloInf.add(pannelloInfSup, BorderLayout.NORTH);
		pannelloInf.add(pannelloInfInf, BorderLayout.SOUTH);
		
		//This
		this.setTitle("Client");
		this.getContentPane().add(pannelloSup, BorderLayout.NORTH);
		this.getContentPane().add(pannelloInf, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800,650);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	//Setting methods
	public void setOut(String s){
		try{
			URL url =new URL(s);
			ImageIcon img=new ImageIcon(url);
			out.setIcon(img);
		}catch(Exception e){JOptionPane.showMessageDialog(null,"Errore nella visualizzazione dell'immagine");}
	}
	
	public void setTime(String s){
		time.setText(s);
	}
	
	public void setClouds(String s){
		clouds.setText(s+" %");
	}
	
	public void setDescription(String s){
		description.setText(s);
	}
	
	public void setHumidity(String s){
		humidity.setText(s+" %");
	}
	
	public void setPressure(String s){
		pressure.setText(s+" hPa");
	}
	
	public void setTemp(String s){
		temp.setText(s +" °C");
	}
	
	public void setWind(String s){
		wind.setText(s+ " m/s");
	}
	
	public void setOn(Boolean b){
		b_on.setEnabled(b);
	}
	
	public void setOFF(Boolean b){
		b_off.setEnabled(b);
	}
	
	public void setPrec(Boolean b){
		b_prec.setEnabled(b);
	}
	
	public void setSucc(Boolean b){
		b_succ.setEnabled(b);
	}
	
	public void setShare(Boolean b){
		b_share.setEnabled(b);
	}
	
	public void addListener(Listener listener) {
		b_prec.addActionListener(listener);
		b_prec.setActionCommand(Listener.PRECEDENTE);
		b_on.addActionListener(listener);
		b_on.setActionCommand(Listener.ON);
		b_off.addActionListener(listener);
		b_off.setActionCommand(Listener.OFF);
		b_succ.addActionListener(listener);
		b_succ.setActionCommand(Listener.SUCCESSIVO);
		b_share.addActionListener(listener);
		b_share.setActionCommand(Listener.SHARE);
	}
}
