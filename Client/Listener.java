import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Listener implements ActionListener {
	public static String PRECEDENTE="Precedente";
	public static String ON="On";
	public static String OFF="Off";
	public static String SUCCESSIVO="Successivo";
	public static String SHARE="Share";
	public final Grafica g;
	public final Client client;
	private int i=0; //Counts the hour where I am
	private int max;
	
	public Listener(Client client, Grafica g){
		this.client = client;
		this.g=g;
	}
	
	//Events Listener
	public void actionPerformed(ActionEvent e) {
		//Prec Case
		if(e.getActionCommand()==PRECEDENTE){
			i--;
			if(i==0){
				g.setPrec(false);
			}
			g.setSucc(true);		
			client.precedente();
		}
		//On Case
		else if(e.getActionCommand()==ON){
			//Button setting
			g.setOn(false);
			g.setOFF(true);
			g.setSucc(false);
			g.setPrec(false);
			//WebSocket command
			client.on();
		}
		//Off Case
		else if(e.getActionCommand()==OFF){
			//Button setting
			g.setOn(true);
			g.setOFF(false);
			g.setSucc(false);
			g.setPrec(false);
			//WebSocket command
			client.off();
		}
		//Succ Case
		else if(e.getActionCommand()==SUCCESSIVO){
			i++;
			if(i==max-1){
				g.setSucc(false);
			}
			g.setPrec(true);
			client.successivo();
		}
		else if(e.getActionCommand()==SHARE){
			client.share();
		}
	}

	public void setMax(int size) {
		this.max=size;
	}

	public int getMax() {
		return max;
	}
}
