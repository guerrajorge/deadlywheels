package groupa.deadlywheels.carserver;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import groupa.deadlywheels.core.CarDroiDuinoCore;
import groupa.deadlywheels.utils.SystemProperties;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br>Classe que implenta o resolvedor de comandos Internos para o Dispositivo Android Server
 * 
 * <p>
 * <b>Data da Criacao:</b> 30/07/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class InternalCommandResolverServer implements Runnable {

	/**
	 * Core do Sistema - Prov� as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;
	
	/**
	 * Camera do Dispositivo Server - Para acessar fun��es como Ligar/Desligar o Flash
	 */
	private Camera mCamera;
	
	/**
	 * Cria uma nova inst�ncia do Resolver que processar� os comandos recebidos do core
	 * e executar� sobre o dispositivo Server
	 * @param systemCore Core do Sistema 
	 * @param msgPromptHandler Manipulador para repassar Msgs de Status a Interface grafica
	 */
	public InternalCommandResolverServer(CarDroiDuinoCore systemCore, Handler msgPromptHandler){
		//**********************************************
		this.systemCore = systemCore;
		this.msgPromptHandler = msgPromptHandler;
		//**********************************************
		this.isOn = true;
		this.sendMessageToPrompt("Inicializado!!!");
	}
	
	/**
	 * Inicia o Processo de Retirada dos Dados da Fila de Comandos Internos do Core
	 * Processa os Comandos e executa as Fun��es necess�rias 
	 */
	public void run() {
		while(this.isOn){
			//*********************************************
			// Retirando o Frame da Fila do Core para processamento
			byte[] data = this.systemCore.poolDataFromInternalCommandQueue();
			
			if(data!=null){
				try{
					//******************************************
					//Transforma em ASCII para Facilitar a verifica��o
					String comando = new String(data);
					
					//********************************************************
					//VERIFICANDO O COMANDO E EXECUTANDO AS OPERA��ES
					//********************************************************
					if (comando.contains(SystemProperties.COMANDO_LANTERNA_SERVER)){
						this.ligarDesligarLanterna();
					}else{
						this.sendMessageToPrompt("COMANDO DESCONHECIDO: " + comando);
					}				
				}catch(Exception ex){
					this.sendMessageToPrompt("Falha - " + ex.getMessage());
				}				
			}else{
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_INTERNAL_COMMAND_RESOLVER);
				} catch (InterruptedException e) {
					Log.e("DatagramSocketServerSenderWorker - InterruptedException", e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Ligar ou Desligar a Lanterna de Acordo com o Comando recebido
	 * @param data - Comando de Ligar/Desligar a Lanterna (Flash do dispositivo Android)
	 */
	private void ligarDesligarLanterna(){
		if(this.mCamera!=null){
			//********************************************
			//Capturando os Parametros da Camera
			Camera.Parameters params = this.mCamera.getParameters();
			//********************************************
			//Se a camera estiver Ligada Manda Desligar, do contr�rio manda Ligar
			if (params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)){
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				this.sendMessageToPrompt("Lanterna Desligada!");
			}else{
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				this.sendMessageToPrompt("Lanterna Ligada!");
			}
			//********************************************
			//Devolvendo os Parametros para a Camera
			this.mCamera.setParameters(params);
		}
	}
	
	/**
	 * Seta a Camera carregada na Activity para acesso as fun��es necess�rias
	 * @param camera
	 */
	public void setCamera(Camera camera){
		this.mCamera = camera;
	}
	
	
	/**
	 * Termina a Thread de Comunica��o 
	 */
	public void turnOff(){
		this.isOn = false;
	}
	
	/**
	 * Envia mensagem para ser apresentada no Prompt
	 * @param txt
	 */
	private void sendMessageToPrompt(String txt){
		Message msg = new Message();
		msg.obj = "InternalCommandResolver: " + txt;
		this.msgPromptHandler.sendMessage(msg);
	}

}
