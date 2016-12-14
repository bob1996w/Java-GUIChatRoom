package s103502014.internet_Project;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class Server {
	public static final int LISTEN_PORT = 6665;
	// list = all connected clients
	ArrayList<Socket> list = new ArrayList<Socket>();
	// Listening to Server Request
	public void listenRequest()
	{
		ServerSocket serverSocket = null;
		ExecutorService threadExecutor = Executors.newCachedThreadPool();
		try
		{
			serverSocket = new ServerSocket(LISTEN_PORT, 10);
			System.out.println("Searching for clients...");
			while (true)
			{
				Socket socket = serverSocket.accept();
				list.add(socket);
				threadExecutor.execute( new RequestThread(socket) );
				//Thread thread = new Thread(new RequestThread())
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if( threadExecutor != null )
				threadExecutor.shutdown();
			if( serverSocket != null )
			{
				try
				{
					serverSocket.close();
				}
				catch( IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	// START HERE
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server server = new Server();
		server.listenRequest();
	}
	
	// each client connects to here
	class RequestThread implements Runnable
	{
		String name="",ip="";
		private Socket clientSocket;
		private Scanner scanner;
		public RequestThread(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.printf("Client %s has connected. \n", (ip = clientSocket.getInetAddress().getHostAddress() ) );
			DataInputStream input = null;
			DataOutputStream output = null;
			String in;
			String out;
			
			try
			{
				input = new DataInputStream( this.clientSocket.getInputStream() );
				output = new DataOutputStream( this.clientSocket.getOutputStream() );
				while( true )
				{
					in = input.readUTF();
					String[] iin = in.split(" ", 2);
					// System.out.println(in);
					if(iin[0].equals("\\setName"))
					{
						this.name = iin[1];
						System.out.println("SetName "+this.ip+" "+this.name);
						output.writeUTF("Welcome, "+this.name);
						broadcast(name+" has joined. "+list.size()+" online now.");
					}
					else if(iin[0].equals("\\clientDisconnect"))
					{
						break;
					}
					else if(iin[0].equals("\\changeName"))
					{
						broadcast(this.name+" has changed name to "+iin[1]);
						this.name = iin[1];
						System.out.println("ChangeName"+this.ip+" "+this.name);
					}
					else
					{
						System.out.println(name + " > "+in);
						broadcast(clientSocket, name, in);
					}
					/*
					scanner = new Scanner(System.in);
					out = scanner.nextLine();
					if(out.equals("exit")){
						break;
					}
					*/
					
					// this String means the end of transmission
					output.writeUTF("\\fuck");
				}
				input.close();
				output.close();
				clientSocket.close();
				list.remove(clientSocket);
				System.out.println(name + " calls disconnect");
				broadcast(name + " has disconnected. "+list.size()+" online now.");
				//System.out.println("Client closed.");
			}
			catch(IOException e)
			{
				System.out.printf("Client %s [%s] has disconnected\n", name, clientSocket.getInetAddress().getHostAddress());
			}
			/*
			catch(EOFException e)
			{
				System.out.printf("Client %s has disconnected", clientSocket.getInetAddress().getHostAddress());
				clientSocket.close();
			}
			*/
		}
		public void broadcast(String msg)
		{
			System.out.println(msg);
			for(Socket client:list)
			{
				DataOutputStream output = null;
				try {
					output = new DataOutputStream( client.getOutputStream() );
					output.writeUTF("*Server < "+msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		public void broadcast(String name, String msg)
		{
			System.out.println(name + " < " + msg);
			for(Socket client:list)
			{
				DataOutputStream output = null;
				try {
					output = new DataOutputStream( client.getOutputStream() );
					output.writeUTF(name + " < " + msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		public void broadcast(Socket exclude, String name, String msg)
		{
			System.out.println(name + " < " + msg);
			for(Socket client:list)
			{
				DataOutputStream output = null;
				try {
					output = new DataOutputStream( client.getOutputStream() );
					if(client == exclude)output.writeUTF(name + " > " + msg);
					else output.writeUTF(name + " < " + msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
