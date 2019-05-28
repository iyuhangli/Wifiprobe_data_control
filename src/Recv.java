import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;



public class Recv {

	private static Connection conn; 
	public static final int DEFAULT_PORT = 9002;// 
	public static final int MAX_MSG_LEN = 1600; // 
	public static ExecutorService dataHandlePool = Executors
			.newFixedThreadPool(64);
	
	public Recv() {
		String url = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&amp&useSSL=false";
		String username = "root";
		String password = "siyecao"; 
		//device
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			conn = DriverManager.getConnection(url, username, password);
		}
		catch ( ClassNotFoundException cnfex )
		{
			//device error
			System.err.println("Cann't find connect jar files" );
			cnfex.printStackTrace(); 
			// terminate program 
			System.exit( 1 );
		}
		catch ( SQLException sqlex ) 
		{
			//connect error
			System.err.println( "Unable to connect to database" ); 
			sqlex.printStackTrace(); 
			System.exit( 1 ); // terminate program 
		} 
	}

	public static void start(int port) 
	{
		//device
		try 
		{
			String url = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&amp&useSSL=false";
			String username = "root";
			String password = "siyecao"; 
			Class.forName( "com.mysql.jdbc.Driver" );
			conn = DriverManager.getConnection(url, username, password);
		}
		catch ( ClassNotFoundException cnfex )
		{
			//device error
			System.err.println("Cann't find connect jar files" );
			cnfex.printStackTrace(); 
			// terminate program 
			System.exit( 1 );
		}
		catch ( SQLException sqlex ) 
		{
			//connect error
			System.err.println( "Unable to connect to database" ); 
			sqlex.printStackTrace(); 
			System.exit( 1 ); // terminate program 
		} 
		
		try 
		{
			@SuppressWarnings("resource")
			DatagramSocket udp = new DatagramSocket(8094);
			DatagramPacket dPacket;
			byte[] echo = new byte[1];
			echo[0] = (byte)1;
			while (true) 
			{
				dPacket = new DatagramPacket(new byte[MAX_MSG_LEN], MAX_MSG_LEN);
				udp.receive(dPacket);
				String res = new String(dPacket.getData(),0,dPacket.getLength());
				String pn=null;
				StringTokenizer st = new StringTokenizer(res);
				pn=st.nextToken();
				String dateofdec = getStringDateShort();
				String timeofdec =getTimeShort(); 
				int j=0;
				//System.out.println("0000"+pn+"0000"+dateofdec+"0000"+timeofdec+"0000");
				while(st.hasMoreTokens())
				{
					StringTokenizer str=new StringTokenizer(st.nextToken(),"|");
					String [] strArray = new String [20];
					int i=0;
					while(str.hasMoreTokens())
					{
						strArray[i]=str.nextToken();
						//System.out.println("00000"+strArray[i]+"0000000"+i);
						i++;
					}
					j++;
					String sql="INSERT INTO `mydb`.`info` (`probeNo`, `SourceMAC`, `DestMAC`, `FrameBtype`, `FrameStype`, `Channel`, `RSSI`, `First`, `Second`, `DoD`, `ToD`) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1,1);
					pstmt.setString(2,strArray[0]);
					pstmt.setString(3,strArray[1]);
					pstmt.setString(4,strArray[2]);
					pstmt.setString(5,strArray[3]);
					pstmt.setString(6,strArray[4]);
					pstmt.setString(7,strArray[5]);
					pstmt.setString(8,strArray[6]);
					pstmt.setString(9,strArray[7]);
					pstmt.setString(10,dateofdec);
					pstmt.setString(11,timeofdec);
					pstmt.executeUpdate() ;
					//System.out.println("1111"+t+"1111");
					pstmt.close();
				} 
				System.out.println(j+"   "+timeofdec);
				//Return a byte to probe
				InetAddress addr = dPacket.getAddress();
				dPacket = new DatagramPacket(echo, echo.length);
				dPacket.setAddress(addr);
				udp.send(dPacket);
				}
		} 
		catch (SocketException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getStringDateShort() {  
	    Date currentTime = new Date();  
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
	    String dateString = formatter.format(currentTime);  
	    return dateString;  
	}  
	
	public static String getTimeShort() {  
	    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
	    Date currentTime = new Date();  
	    String dateString = formatter.format(currentTime);  
	    return dateString;  
	}  
	
	public static void main(String[] args) {
		if (args != null && args.length == 1) {
			start(Integer.parseInt(args[0]));
		}else {
			start(8094);
		}
	}
}
