package miners;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Servidor50 {
	TCPServer50 mTcpServer;
	Scanner sc;
	int countMiners = 1;
	Miners miners[] = new Miners[countMiners];
	boolean found = Global.found;
	String result;
	//int key;
	//double time;
	int zeros = Global.zeros;
	String word;
	int numberMiners = 0;
   
	public static void main(String[] args) throws InterruptedException {
		Servidor50 objser = new Servidor50();
		objser.iniciar();
	}
   
	void iniciar() throws InterruptedException{
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					mTcpServer = new TCPServer50(
						new TCPServer50.OnMessageReceived(){
							@Override
							public void messageReceived(String message){
								synchronized(this){
									ServidorRecibe(message);
								}
							}
						}
					);
					mTcpServer.run();
				}
			}
		).start();

		try{
			Thread.sleep(4000);
		} 
		catch(InterruptedException e){
			 // this part is executed when an exception (in this example InterruptedException) occurs
		}

		new Thread(
			new Runnable(){
				@Override
				public void run(){
					try{
						int idText = 0;
						FileReader f = new FileReader("list/listapalabras1.txt");
						BufferedReader b = new BufferedReader(f);
						//String text = "2JOSE695";
						while((word = b.readLine()) != null){
							String text = word.substring(0,word.indexOf(','));
							try{
								Thread.sleep(4000);
							} 
							catch(InterruptedException e){
							}

							ServidorEnvia(text+';'+idText);
							idText++;
							System.out.println("Texto: " + text);
							Global.found = false;
							while(!Global.found){
								System.out.print("");
							}
							System.out.println("Siguiente palabra!");
							/*
							StartMiners(text);
							found = false;
							while(!found){
								for(int i=0; i<countMiners; i++){
									found = miners[i].State();
									if(found){
										System.out.println("================================================");
										System.out.println("Minero "+i+ " encontro una posible key!");
										result = miners[i].getResult();
										key = miners[i].getKey();
										time = miners[i].getTime();
										System.out.println("Ceros: " + zeros);
										System.out.println("Palabra: " + text);
										System.out.println("Result: " + result);
										System.out.println("Key: " + key);
										System.out.println("Tiempo (segundos): " + time);
										StopMiners();
										System.out.println("Mineros verificando..");
										found = CheckMiners(i,key,text);
										if(found) i = countMiners;
										else System.out.println("Falsa alarma! Continua la búsqueda..");
										System.out.println("================================================");
									}
								}
							}*/
						}
						ServidorEnvia("fin");
					} catch (FileNotFoundException ex){
					} catch (IOException e){
					}
				}
			}
		).start();
		


		String salir = "n";
		sc = new Scanner(System.in);
		System.out.println("Servidor bandera 01");
		while( !salir.equals("q")){
			salir = sc.nextLine();
			ServidorEnvia(salir);
		}
		System.out.println("Servidor bandera 02"); 
   
	}

	void ServidorRecibe(String llego){
		if(llego.contains("encontre")){
			String key = PrintValue(llego);
			ServidorEnvia("parar;" + key);
			Global.stop = true;
		}else if(llego.contains("verificado")){
			String[] checking = llego.split(";");   // verificado ; check
			String check = checking[1];
			CheckMiners(check);
		}
		//System.out.println("SERVIDOR40 El mensaje:" + llego);
   }
   
	void ServidorEnvia(String envia){
		if (mTcpServer != null) {
			//miners[0] = new Miners();
			//String send = miners[0].ResultText(envia);
			mTcpServer.sendMessageTCPServer(envia);
		}
	}
	
	void CheckMiners(String check){
		if(check.equals("true")) numberMiners++;
		if(numberMiners == mTcpServer.nrcli){
			System.out.println("Todos los mineros verificaron la key!");
			System.out.println("================================================");
			Global.found = true;
			Global.stop = false;
			numberMiners = 0;
		}
	}

	String PrintValue(String llego){
		System.out.println("================================================");
		//found = true;
		String[] founded = llego.split(";");   // encontre ; key ; result ; time
		String key = founded[1];
		String result = founded[2];
		String time = founded[3];

		System.out.println("Ceros: " + zeros);
		System.out.println("Key: " + key);
		System.out.println("Result: " + result);
		System.out.println("Tiempo (milisegundos): " + time);
		return key;
	}
	/*
	void StartMiners(String text){
		for(int i=0; i<countMiners; i++){
			miners[i] = new Miners(i,text,zeros);
			miners[i].start();
		}
	}
	void StopMiners(){
		for(int j=0; j<countMiners; j++){
			miners[j].Stop();
		}
	}
	boolean CheckMiners(int id, int key, String text){
		boolean correctKey=false;
		int checking = 0;
		for(int i=0; i<countMiners; i++){
			correctKey = miners[i].CheckKey(key,text);
			//System.out.println("Minero "+(i+1)+":"+ correctKey);
			if(correctKey) checking++;
			else i = countMiners;
		}
		if(checking == countMiners){
			System.out.println("Todos los mineros verificaron la key!");
			return true;
		}
		else return false;
	}*/
}
