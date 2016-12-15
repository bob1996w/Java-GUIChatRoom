package s103502014.internet_Project;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Server extends JFrame{
	public static Server server;
	public static final int LISTEN_PORT = 6665;
	public static JLabel l_disp;
	public static JLabel l_number;
	public static JTextArea t_display;
	public static JScrollPane s_display;
	public static JScrollPane s_names;
	public static JTextArea t_names;
	// list = all connected clients
	//ArrayList<Socket> list = new ArrayList<Socket>();
	//static ArrayList<String> names = new ArrayList<String>();
	static ArrayList<Users> userlist = new ArrayList<Users>();
	class Users
	{
		public Socket client;
		public String name;
		public Users(Socket socket)
		{
			this.client = socket;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public String getName()
		{
			return this.name;
		}
		public void closeSocket()
		{
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Server()
	{
		t_names = new JTextArea();
		t_names.setEditable(false);
		t_display = new JTextArea();
		t_display.setEditable(false);
		s_display = new JScrollPane(t_display);
		s_display.setPreferredSize(new Dimension(390,515));
		s_names = new JScrollPane(t_names);
		s_names.setPreferredSize(new Dimension(190,515));
		l_disp = new JLabel("Listening Requests...");
		l_number = new JLabel("0 Online");
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new FlowLayout());
		JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, s_names, s_display);
		centerSplit.setDividerLocation(0.25);
		//JPanel leftPanel = new JPanel();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		JPanel topPanel2 = new JPanel();
		
		DefaultCaret caret = (DefaultCaret)t_display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		
		topPanel2.setLayout(new BorderLayout());
		topPanel2.add(l_disp,BorderLayout.NORTH);
		topPanel2.add(l_number,BorderLayout.SOUTH);
		topPanel.add(topPanel2);
		//mainPanel.add(t_names,BorderLayout.WEST);
		//mainPanel.add(t_display, BorderLayout.CENTER);
		centerPanel.add(centerSplit);
		//mainPanel.add(centerSplit, BorderLayout.CENTER);
		//mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		this.getContentPane().add(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,600);
		this.setTitle("EZChat Server");
		this.setResizable(false);
		this.setVisible(true);
	}
	// Listening to Server Request
	public void listenRequest()
	{
		ServerSocket serverSocket = null;
		ExecutorService threadExecutor = Executors.newCachedThreadPool();
		try
		{
			serverSocket = new ServerSocket(LISTEN_PORT, 10);
			//System.out.println("Searching for clients...");
			printmes("Waiting for clients...");
			while (true)
			{
				Socket socket = serverSocket.accept();
				Users client = new Users(socket);
				userlist.add(client);
				threadExecutor.execute( new RequestThread(client));
				//Thread thread = new Thread(new RequestThread())
			}
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			printmes("Error establishing connection");
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
					//e.printStackTrace();
					printmes("Error Closing Socket");
				}
			}
		}
	}
	public void updateNameList() throws IOException
	{
		if(userlist.size() != 0)
			this.setTitle("EZChat Server: "+userlist.size()+" connected");
		else this.setTitle("EZChat Server");
		t_names.setText("");
		for(Users u:userlist)
		{
			t_names.append(u.getName()+"\n");
		}
		for(Users u:userlist)
		{
			DataOutputStream output = null;
			try {
				output = new DataOutputStream( u.client.getOutputStream() );
				output.writeUTF("\\names \\start");
				for(Users l:userlist)
						output.writeUTF("\\names "+l.name);
				output.writeUTF("\\names \\end");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				printmes("Error sending messages");
				u.closeSocket();
				userlist.remove(u);
			}
		}
	}
	public static void printmes(String mes)
	{
		t_display.append(mes+"\n");
		//appendToPane(t_display,"\n" + mes, Color.RED);
	}
	// START HERE
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try 
	    { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	    } 
	    catch(Exception e){
	    }
		server = new Server();
		server.listenRequest();
	}
	
	// each client connects to here
	class RequestThread implements Runnable
	{
		String name="",ip="";
		public Socket clientSocket;
		//private Scanner scanner;
		int index;
		public RequestThread(Users c)
		{
			this.clientSocket = c.client;
			index = userlist.indexOf(c);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ip = clientSocket.getInetAddress().getHostAddress();
			//System.out.printf("Client %s has connected. \n", (ip = clientSocket.getInetAddress().getHostAddress() ) );
			
			DataInputStream input = null;
			DataOutputStream output = null;
			String in;
			//String out;
			
			try
			{
				input = new DataInputStream( this.clientSocket.getInputStream() );
				output = new DataOutputStream( this.clientSocket.getOutputStream() );
				while( true )
				{
					in = input.readUTF();
					String[] iin = in.split(" ", 2);
					// System.out.println(in);
					if(iin[0].equals("\\initialSetName"))
					{
						this.name = iin[1];
						//System.out.println("SetName "+this.ip+" "+this.name);
						printmes(this.name+" ["+this.ip+"] has connected.");
						output.writeUTF("Welcome, "+this.name);
						userlist.get(index).setName(this.name);
						broadcast(name+" has joined. "+userlist.size()+" online now.");
						l_number.setText(userlist.size()+" Online");
						updateNameList();
					}
					else if(iin[0].equals("\\clientDisconnect"))
					{
						break;
					}
					else if(iin[0].equals("\\setname"))
					{
						broadcast(this.name+" has changed name to "+iin[1]);
						this.name = iin[1];
						userlist.get(index).setName(this.name);
						//System.out.println("ChangeName"+this.ip+" "+this.name);
						printmes("ChangeName "+this.ip+" "+this.name);
						updateNameList();
					}
					else
					{
						//System.out.println(name + " > "+in);
						printmes(name+" > "+in);
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
				//list.remove(clientSocket);
				userlist.remove(index);
				//System.out.println(name + " calls disconnect");
				printmes(name + " calls disconnect");
				broadcast(name + " has disconnected. "+userlist.size()+" online now.");
				l_number.setText(userlist.size()+" online");
				updateNameList();
				//System.out.println("Client closed.");
			}
			catch(IOException e)
			{
				//System.out.printf("Client %s [%s] has disconnected\n", name, clientSocket.getInetAddress().getHostAddress());
				printmes("Client "+name+" ["+clientSocket.getInetAddress().getHostAddress()+" has disconnected");
				//input.close();
				//output.close();
				//clientSocket.close();
				//list.remove(clientSocket);
				userlist.remove(index);
				broadcast(name + " has disconnected. "+userlist.size()+" online now.");
				l_number.setText(userlist.size()+" online");
				try {
					updateNameList();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
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
			//System.out.println(msg);
			for(Users u:userlist)
			{
				DataOutputStream output = null;
				try {
					output = new DataOutputStream( u.client.getOutputStream() );
					output.writeUTF("*Server < "+msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					printmes("Error sending messages");
					u.closeSocket();
					userlist.remove(u);
				}
			}
		}
		public void broadcast(String name, String msg)
		{
			//System.out.println(name + " < " + msg);
			for(Users u:userlist)
			{
				DataOutputStream output = null;
				try {
					output = new DataOutputStream( u.client.getOutputStream() );
					output.writeUTF(name + " < " + msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					printmes("Error sending messages");
					u.closeSocket();
					userlist.remove(u);
				}
			}
		}
		public void broadcast(Socket exclude, String name, String msg)
		{
			//System.out.println(name + " < " + msg);
			for(Users u:userlist)
			{
				DataOutputStream output = null;
				try {
					output = new DataOutputStream( u.client.getOutputStream() );
					if(u.client == exclude)output.writeUTF(name + " > " + msg);
					else output.writeUTF(name + " < " + msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					printmes("Error sending messages");
					u.closeSocket();
					userlist.remove(u);
				}
			}
		}
	}
}
