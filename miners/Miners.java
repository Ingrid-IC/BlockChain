package miners;

import java.util.*;

public class Miners extends Thread{

	private String result;
	private String text;
	private int key;
	private int id;
	boolean founded = false;
	int zeros;
	double time = 0;
	
	public Miners(){
		//this.id = id;
		//this.text = text;
		//this.zeros = zeros;
	}

	@Override
	public void run(){
		long start = System.currentTimeMillis();
		while(!this.founded){
			key = (int)(Math.random()*9000000);
			result = ResultText(text+key);
			this.founded = CheckZero(result);
		}
		long fin = System.currentTimeMillis();
		time = (double) ((fin - start)/1000);
	}

	public void Stop(){
		this.founded = true;
	}
	public void Restart(){
		this.founded = false;
	}

	public boolean State(){
		return this.founded;
	}
	public void setText(String text){
		this.text = text;
	}
	public void setZeros(int zeros){
		this.zeros = zeros;
	}
	public String getResult(){
		return this.result;
	}
	public int getKey(){
		return this.key;
	}
	public double getTime(){
		return time;
	}
	public String getText(){
		return text;
	}

	//public boolean CheckKey(int key, String text){
	public boolean CheckKey(int key){
		String possibleText = ResultText(text+key);
		boolean check = CheckZero(possibleText);
		return check;
	}

	private String ResultText(String text){
		SHAone sha = new SHAone();
		//byte[] byteText = text.getBytes();
		//byte[] result = new byte[key.length + byteText.length];
		//System.arraycopy(key, 0, result, 0, key.length);
		//System.arraycopy(byteText, 0, result, key.length, byteText.length);
		byte[] byteText = text.getBytes();
		return sha.Encript(byteText);
	}

	private boolean CheckZero(String text){
		//char initial = text.charAt(0);
		char charT;
		int countZeros = 0;
		for(int i=0; i<zeros; i++){
			charT = text.charAt(i);
			//System.out.println(charT);
			if(charT=='0') countZeros++;
			else i=zeros;
		}

		if(countZeros == zeros){
			//System.out.println(text);
			return true;
		}
		else return false;
		/*String initial = text.substring(0,2);
		if(initial.equals("00")){
			System.out.println(text);
			return true;	
		} 
		else return false;*/
	}
}