import java.net.*;
import java.io.*;
import java.util.*;

public class FTPServer
{
	public static void main(String args[]) throws Exception
	{
		ServerSocket soc=new ServerSocket(5200);
		System.out.println("FTP Server Started on Port Number 5200");
		while(true)
		{
			System.out.println("Waiting for Connection ...");
			transferfile t=new transferfile(soc.accept());
			
		}
	}
}

class transferfile extends Thread
{
	Socket ClientSoc;

	DataInputStream din;
	DataOutputStream dout;
	
	transferfile(Socket soc)
	{
		try
		{
			ClientSoc=soc;						
			din=new DataInputStream(ClientSoc.getInputStream());
			dout=new DataOutputStream(ClientSoc.getOutputStream());
			System.out.println("FTP Client Connected ...");
			start();
			
		}
		catch(Exception ex)
		{
		}		
	}
	void SendFile() throws Exception
	{		
		String filename=din.readUTF();
		File f=new File(filename);
		if(!f.exists())
		{
			dout.writeUTF("File Not Found on Server");
			return;
		}
		else
		{
			dout.writeUTF("READY");
			FileInputStream fin=new FileInputStream(f);
			int ch;
			do
			{
				ch=fin.read();
				dout.writeUTF(String.valueOf(ch));
			}
			while(ch!=-1);	
			fin.close();	
			dout.writeUTF("File Received Successfully");							
		}
	}
	
	void ReceiveFile() throws Exception
	{
		String filename=din.readUTF();
		if(filename.compareTo("File not found")==0)
		{
			return;
		}
		File f=new File(filename);
		String option;
		
		if(f.exists())
		{
			dout.writeUTF("File Already Exists");
			option=din.readUTF();
		}
		else
		{
			dout.writeUTF("SendFile");
			option="Y";
		}
			
			if(option.compareTo("Y")==0)
			{
				FileOutputStream fout=new FileOutputStream(f);
				int ch;
				String temp;
				do
				{
					temp=din.readUTF();
					ch=Integer.parseInt(temp);
					if(ch!=-1)
					{
						fout.write(ch);					
					}
				}while(ch!=-1);
				fout.close();
				dout.writeUTF("File Send Successfully");
			}
			else
			{
				return;
			}
			
	}


	public void run()
	{
		while(true)
		{
			try
			{
			System.out.println("Waiting for Request ...");
			String Command=din.readUTF();
			if(Command.compareTo("GET")==0)
			{
				System.out.println("\tDownload Request Received ...");
				SendFile();
				continue;
			}
			else if(Command.compareTo("SEND")==0)
			{
				System.out.println("\tUpload Request  Receiced ...");				
				ReceiveFile();
				continue;
			}
			else if(Command.compareTo("DISCONNECT")==0)
			{
				System.out.println("\t Request Completed , Disconnect...");
				
				return ;
			}
			}
			catch(Exception ex)
			{
			}
		}
	}
}
