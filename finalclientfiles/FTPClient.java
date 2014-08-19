import java.net.*;
import java.io.*;
import java.util.*;


class FTPClient
{
	public static void main(String args[]) throws Exception
	{
		while(true)
		{
			System.out.println("\n************* HACAKATHON ***********");
			System.out.println("\n\n");
			System.out.println("[ MENU ]");
			System.out.print("\n\n");
			System.out.println("1. Send File");
			System.out.print("\n");
			System.out.println("2. Receive File");
			System.out.print("\n");
			System.out.println("3. Exit");
			System.out.print("\n");
			System.out.print("\n Enter Choice :");
			
			BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));
			int choice=Integer.parseInt(inputFromUser.readLine());
			
			
			if(choice == 1 || choice == 2)
			{
				System.out.println("Enter the Server IP: ");
				String ServerIP = inputFromUser.readLine().toString();
				System.out.println("Enter the Server Port Number: ");
				int ServerPort = Integer.parseInt(inputFromUser.readLine());
				
				// Call the controller and give the ServerIP
				
				Socket LspServerSoc = new Socket(ServerIP, ServerPort); 
				if(!LspServerSoc.isConnected())
				{
					System.out.println("Invalid URL , Try Again..");
					continue;
					
				}
					
					
				transferfileClient t = new transferfileClient(LspServerSoc);
				
				t.SendReceive(choice);
				
				//Socket Close
				t.SendReceive(3);
				LspServerSoc.close();
			
			}
			else
				System.exit(1);
		
		}//while
	
	}

}
class transferfileClient
{
	Socket ServerSoc;

	DataInputStream din;
	DataOutputStream dout;
	BufferedReader br;

	
	transferfileClient(Socket LspServerSoc)
	{
		try
		{	
			ServerSoc = LspServerSoc;
			din=new DataInputStream(ServerSoc.getInputStream());
			dout=new DataOutputStream(ServerSoc.getOutputStream());
			br=new BufferedReader(new InputStreamReader(System.in));
		}
		catch(Exception ex)
		{
		}		
	}
	void SendFile() throws Exception
	{		
		
		String filename;
		System.out.println("Enter File Name :");
		filename=br.readLine();
			
		File f=new File(filename);
		if(!f.exists())
		{
			System.out.println("File not Exists...");
			dout.writeUTF("File not found");
			return;
		}
		
		dout.writeUTF(filename);
		
		String msgFromServer=din.readUTF();
		if(msgFromServer.compareTo("File Already Exists")==0)
		{
			String Option;
			System.out.println("File Already Exists on Server. Want to OverWrite (Y/N) ?");
			Option=br.readLine();			
			if(Option=="Y")	
			{
				dout.writeUTF("Y");
			}
			else
			{
				dout.writeUTF("N");
				return;
			}
		}
		
		System.out.println("Sending File ...");
		FileInputStream fin=new FileInputStream(f);
		int ch;
		do
		{
			ch=fin.read();
			dout.writeUTF(String.valueOf(ch));
		}
		while(ch!=-1);
		fin.close();
		System.out.println(din.readUTF());
		
	}
	
	void ReceiveFile() throws Exception
	{
		String fileName;
		System.out.print("Enter File Name :");
		fileName=br.readLine();
		dout.writeUTF(fileName);
		String msgFromServer=din.readUTF();
		
		if(msgFromServer.compareTo("File Not Found")==0)
		{
			System.out.println("File not found on Server ...");
			return;
		}
		else if(msgFromServer.compareTo("READY")==0)
		{
			System.out.println("Receiving File ...");
			File f=new File(fileName);
			if(f.exists())
			{
				String Option;
				System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
				Option=br.readLine();			
				if(Option=="N")	
				{
					dout.flush();
					return;	
				}				
			}
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
			System.out.println(din.readUTF());
				
		}
		
		
	}

	public void SendReceive(int choice) throws Exception
	{
		
			
			if(choice==1)
			{
				dout.writeUTF("SEND");
				SendFile();
			}
			else if(choice==2)
			{
				dout.writeUTF("GET");
				ReceiveFile();
			}
			else
			{
				dout.writeUTF("DISCONNECT");
				return;
			}
		
	}



}
