package s103502014.internet_Project;

import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Client extends JFrame{
	static Client cli;
	static public String ip = "", name = ""; 
	static First a;
	static JPanel mainPanel = new JPanel();
	public static JLabel l_name;
	//public static JTextPane t_display = new JTextPane();
	public static JTextArea t_display;
	public static JTextArea t_names;
	public static JTextField t_input;
	public static JTextField t_ip;
	public static JTextField t_name;
	public static JButton b_send;
	public static JScrollPane s_display;// = new JScrollPane(t_display);
	public static JScrollPane s_names;
	public static JButton b_disconnect;
	public static JButton b_clear;
	public static JButton b_name;
	public Client()
	{
		//ClientDialog cd = new ClientDialog();
		//cd.setModal(true);
		l_name = new JLabel("Name >");
		t_input = new JTextField();
		t_ip = new JTextField();
		t_name = new JTextField();
		b_send = new JButton("Send");
		b_disconnect = new JButton("Connect");
		b_name = new JButton("ChangeName");
		b_name.setEnabled(false);
		t_display = new JTextArea();
		t_display.setEditable(false);
		t_display.setEnabled(true);
		t_names = new JTextArea();
		t_names.setEditable(false);
		s_display = new JScrollPane(t_display);
		s_display.setPreferredSize(new Dimension(390,490));
		s_names = new JScrollPane(t_names);
		s_names.setPreferredSize(new Dimension(190,490));
		t_ip.setPreferredSize(new Dimension(100,24));
		t_name.setPreferredSize(new Dimension(100,24));
		b_clear = new JButton("Clear");
		mainPanel.setLayout(new BorderLayout());
		JPanel p_control = new JPanel();
		p_control.setLayout(new BorderLayout());
		JPanel p_control2 = new JPanel();
		JLabel l_ip = new JLabel("IP");
		JLabel cl_name = new JLabel("Name");
		//JPanel p_display = new JPanel();
		JPanel p_input = new JPanel();
		p_input.setLayout(new BorderLayout());
		JPanel p_input2 = new JPanel();
		p_input2.setLayout(new BorderLayout());
		JPanel p_center = new JPanel();
		p_center.setLayout(new FlowLayout());
		t_display.setEditable(false);
		t_input.setEditable(false);
		b_send.setEnabled(false);
		JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, s_names, s_display);
		centerSplit.setDividerLocation(0.25);
		
		DefaultCaret caret = (DefaultCaret)t_display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		printmes("Set the ip and your name above, and click \"Connect\"");
		b_disconnect.addActionListener(e -> {
			if(b_disconnect.getText().equals("Connect"))
			{
				//if(InetAddressValidator.isValid(t_ip.getText()))
				statusChange(1);
			}
			else
			{
				statusChange(0);
				//printmes("Connection Closed.");
			}
		});
		b_send.addActionListener(e -> {
			String message = t_input.getText();
			t_input.setText("");
			String[] m = message.split(" ",2);
			if(message.equals("\\exit"))
			{
				a.sendMessage("\\clientDisconnect");
				statusChange(0);
				//a.closeSocket();
			}
			else if(m[0].equals("\\initialSetName"))
			{
				name = m[1];
				t_name.setText(name);
				if(name.length()<=15)l_name.setText(name+" > ");
				else l_name.setText(name.substring(0,14)+"... >");
			}
			else if(m[0].equals("\\setname"))
			{
				name = m[1];
				t_name.setText(name);
				if(name.length()<=15)l_name.setText(name+" > ");
				else l_name.setText(name.substring(0,14)+"... >");
			}
			a.sendMessage(message);
		});
		t_input.addActionListener(e -> {
			String message = t_input.getText();
			t_input.setText("");
			String[] m = message.split(" ",2);
			if(message.equals("exit"))
			{
				a.sendMessage("\\clientDisconnect");
				statusChange(0);
				//a.closeSocket();
			}
			else if(m[0].equals("\\initialSetName"))
			{
				name = m[1];
				t_name.setText(name);
				if(name.length()<=15)l_name.setText(name+" > ");
				else l_name.setText(name.substring(0,14)+"... >");
			}
			else if(m[0].equals("\\setname"))
			{
				name = m[1];
				t_name.setText(name);
				if(name.length()<=15)l_name.setText(name+" > ");
				else l_name.setText(name.substring(0,14)+"... >");
			}
			a.sendMessage(message);
		});
		b_name.addActionListener(e -> {
			name = t_name.getText();
			if(name.length()<=15)l_name.setText(name+" > ");
			else l_name.setText(name.substring(0,14)+"... >");
			a.sendMessage("\\setname "+name);
		});
		b_clear.addActionListener(e -> {
			t_display.setText("==========All Cleared==========\n");
		});
		p_control.add(b_disconnect,BorderLayout.WEST);
		p_control2.add(l_ip);
		p_control2.add(t_ip);
		p_control2.add(cl_name);
		p_control2.add(t_name);
		p_control.add(p_control2, BorderLayout.CENTER);
		p_control.add(b_name,BorderLayout.EAST);
		p_input2.add(b_clear, BorderLayout.WEST);
		p_input2.add(l_name, BorderLayout.EAST);
		p_input.add(p_input2, BorderLayout.WEST);
		p_input.add(t_input, BorderLayout.CENTER);
		p_input.add(b_send, BorderLayout.EAST);
		p_center.add(centerSplit);
		
		mainPanel.add(p_control,BorderLayout.NORTH);
		mainPanel.add(p_input, BorderLayout. SOUTH);
		mainPanel.add(p_center, BorderLayout.CENTER);
		//mainPanel.add(s_display,BorderLayout.CENTER);
		this.getContentPane().add(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,600);
		this.setTitle("EZChat");
		this.setResizable(false);
		
		//System.out.print(p_center.getWidth()+" "+this.getWidth());
		this.setVisible(true);
		//connect.setVisible(true);
		/*
		p_center.setSize(new Dimension(this.getWidth(),480));
		centerSplit.setSize(p_center.getSize());
		this.setVisible(true);
		this.repaint();
		*/
	}
	public static void statusChange(int status)
	{
		if(status == 1)
		{
			t_input.setEditable(true);
			name = t_name.getText();
			ip = t_ip.getText();
			//t_name.setEditable(false);
			t_ip.setEditable(false);
			if(name.length()<=15)l_name.setText(name+" > ");
			else l_name.setText(name.substring(0,14)+"... >");
			b_send.setEnabled(true);
			b_disconnect.setText("Disconnect");
			b_name.setEnabled(true);
			a = new First(ip, name);
			a.connect();
			printmes("Starting connection...");
			cli.setTitle("EZChat ["+ip+"]");
		}
		else
		{
			t_input.setEditable(false);
			//t_name.setEditable(true);
			t_ip.setEditable(true);
			b_send.setEnabled(false);
			b_disconnect.setText("Connect");
			b_name.setEnabled(false);
			a.sendMessage("\\clientDisconnect");
			a.closeSocket();
			//printmes("Connection Closed.");
			cli.setTitle("EZChat");
		}
	}
	public void oldsendRoutine()
	{
		/*
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter IP > ");
		ip = scanner.nextLine();
		System.out.print("Enter your name > ");
		name = scanner.nextLine();
		
		//First a = new First(ip, name);
		//a.connect();
		String message;
		while(true)
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			System.out.print("");
			if(a.sendcheck)
			{
				System.out.print(name + " > ");
				message = scanner.nextLine();
				String[] m = message.split(" ",2);
				if(message.equals("exit"))
				{
					a.sendMessage("\\clientDisconnect");
					a.closeSocket();
					break;
				}
				else if(m[0].equals("\\setName"))
					name = m[1];
				a.sendMessage(message);
				a.sendcheck = false;
			}
			else
			{
				
			}
			
		}
		*/
	}
	public static void printmes(String mes)
	{
		t_display.append(mes+"\n");
		//appendToPane(t_display,"\n" + mes, Color.RED);
	}
	public static void main(String[] args) {
		try 
	    { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	    } 
	    catch(Exception e){
	    }
		cli = new Client();
	}
	static public class First {
		// sendcheck 預防prompt比server訊息快
		//public boolean sendcheck = false;
		String ip = "", name = "";
		int port = 6665;
		Socket socket;
		DataInputStream input;
		DataOutputStream output;
		public First(String ip, String name) {
			this.ip = ip;
			this.name = name;
		}
		// Recieve
		public void connect()
		{
			try {
				socket = new Socket(ip, port);
				Thread thread = new Thread(new Recieve());
				thread.start();
			} catch (UnknownHostException e) {
				//System.out.println("System < Unknown Server");
				printmes("Error: Unknown Server");
				printmes("Connection Closed.");
				statusChange(0);
			} catch (IOException e) {
				//System.out.println("System < Fuck You");
				printmes("Error: Server not found");
				printmes("Connection Closed.");
				statusChange(0);
			}
			sendMessage("\\initialSetName " + name);
		}
		public void sendMessage(String mes)
		{
			try {
				output = new DataOutputStream(socket.getOutputStream());
				output.writeUTF(mes);
			} catch (IOException e) {
				//printmes("System: Something wrong when sending.");
			}
			
		}
		
		class Recieve implements Runnable
		{
			String in;
			@Override
			public void run() {
				try {
					//System.out.println("Running");
					//printmes("Running");
					input = new DataInputStream(socket.getInputStream());
					while(true)
					{
						in = input.readUTF();
						String[] iin = in.split(" ",2);
						if(in.trim().equals("\\fuck"))
						{
							//sendcheck = true;
						}
						else if(iin[0].equals("\\names"))
						{
							if(iin[1].equals("\\start"))
								t_names.setText("");
							else if(iin[1].equals("\\end"))
							{}
							else
								t_names.append(iin[1]+"\n");
						}
						else
						{
							printmes(in);
						}
						
					}
				} catch (IOException e) {
					//printmes("System < 總之就是出問題了你這混帳");
					printmes("Socket disconnected.");
					statusChange(0);
				}
				
			}
		}
		public void closeSocket()
		{
			try {
				socket.close();
			} catch (IOException e) {
				printmes("Socket closing error");
			}
		}
	}
}
