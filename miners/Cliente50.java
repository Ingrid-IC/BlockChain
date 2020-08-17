package miners;

import java.util.Scanner;
import miners.TCPClient50;

class Cliente50{
	TCPClient50 mTcpClient;
	Scanner sc;
	double rpta;
	int zeros = Global.zeros;
	int state = 0; // 0 = buscando, 1 = encontre, 2 = verificando , 3 = fin
	boolean nextword = false;
	boolean wait = true;
	Miners miners = new Miners();

	
	public static void main(String[] args)  {
		Cliente50 objcli = new Cliente50();
		objcli.iniciar();
	}
	void iniciar(){
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					mTcpClient = new TCPClient50("192.168.0.106",
						new TCPClient50.OnMessageReceived(){
							@Override
							public void messageReceived(String message){
								ClienteRecibe(message);
							}
						}
					);
					mTcpClient.run();                   
				}
			}
		).start();
		//---------------------------
		
		try{
			Thread.sleep(1000);
		} 
		catch(InterruptedException e){
		}

		while(state < 3){
			if(!miners.State()) state = 0;
			if(miners.State() && state != 2) state = 1;
			if(state == 0) System.out.print("");
			if(Global.stop) state = 2;
			if(state == 1 && !Global.stop){ 
				state = 2;
				System.out.println("Minero encontró una posible key!");
				int key = miners.getKey();
				String result = miners.getResult();
				double time = miners.getTime();
				System.out.println("Texto: " + miners.getText());
				String founded = ""+';'+ key +';'+ result +';'+ time;
				ClienteEnvia("encontre" + founded); //minero encontró key
				Global.stop = true;
			}
		}


		String salir = "quit";
		sc = new Scanner(System.in);
		System.out.println("Cliente bandera 01");
		while( !salir.equals("q")){
			salir = sc.nextLine();
			ClienteEnvia(salir);
		}
		System.out.println("Cliente bandera 02");
	
	}

	void ClienteRecibe(String llego){
		if(llego != null){
			if(llego.contains("parar")){
				miners.Stop();
				state = 2;
				System.out.println("Estado: Stop");
				String[] founded = llego.split(";");   // parar ; key
				String key = founded[1];
				System.out.println("Estado: Verificando key");
				Global.stop = true;
				boolean check = miners.CheckKey(Integer.parseInt(key));
				ClienteEnvia("verificado;" + check);
			}
			else if(llego.contains("fin")){
				state = 3;
			}
			else{
				System.out.println("CLINTE50 El mensaje::" + llego);
				String[] idtext = llego.split(";");
				String text = idtext[0];
				String id = idtext[1];
				if( id.equals("0") ){
					miners.setText(text);
					miners.setZeros(zeros);
					miners.start();
					state = 0;
				}else {
					miners = new Miners();
					miners.setText(text);
					miners.setZeros(zeros);
					miners.start();
					state = 0;
					Global.stop = false;
				}
			}
		}
	}

	void ClienteEnvia(String envia){
		if (mTcpClient != null)
			mTcpClient.sendMessage(envia);
	}

}
