import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.sql.Timestamp;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.net.SocketTimeoutException;
public class client{
//Packet-> single char 1 byte
	//100-1300 bytes- 100-1300 bytes


//MTU for pinging google is around 1470 bytes.
  //103.27.8.44
//  Udayins-MacBook-Pro:~ udayin$ ping -c 3 -D -s 1470 103.27.8.44
//PING 103.27.8.44 (103.27.8.44): 1470 data bytes
//1478 bytes from 103.27.8.44: icmp_seq=0 ttl=59 time=4.456 ms
//1478 bytes from 103.27.8.44: icmp_seq=1 ttl=59 time=17.184 ms
//1478 bytes from 103.27.8.44: icmp_seq=2 ttl=59 time=4.581 ms
  // max RTT for MTU is 17.184 ms
  // Let us take the max RTT for a packet to be 20 ms.

  public static void main(String[] args) throws Exception {
    DatagramSocket ds = new DatagramSocket(4000);
    
    InetAddress ia = InetAddress.getByName("10.208.20.239");
    //InetAddress ia = InetAddress.getByName("10.208.20.223");
    //InetAddress ia = InetAddress.getByName("10.227.10.1");

    Scanner apples=new Scanner(System.in);
    System.out.print("Enter FileName(without .txt): ");
    String hello=apples.nextLine();
    System.out.print("Enter P(100-1300 bytes):");
    int P = apples.nextInt();
    System.out.print("Enter T(2-20000,even):");
    int T=apples.nextInt();
    
    //System.out.println();
    
    String filetext="";
    int count=1;
    int saveT=T;
    int SeqNum=0;
    
    while (count!=0)
{
	T=saveT;
    
    String str=Integer.toString(SeqNum);
    SeqNum++;

    java.util.Date date= new java.util.Date(); 
	 Timestamp ts=new Timestamp(date.getTime());  //initial timestamp for timer
      byte[] seqnumber=str.getBytes();
      String refcount=Integer.toString(T);
      byte[] reflectioncount=refcount.getBytes();
      int a2=seqnumber.length;
      int a3=reflectioncount.length;
      String nonheader="";  
      for (int x=0;x<P-32-a2-a3-3;x++)   
      {  nonheader =nonheader+"b";  }
      String total= refcount+"."+str+"."+ts+"."+nonheader;
      int roundtrip=T;

    boolean truth=true;
// Running a single packet
    while (roundtrip>0){
      DatagramPacket dp = new DatagramPacket(total.getBytes(), total.length(), ia, 3000);
    ds.send(dp);
    System.out.println(roundtrip+"."+str);




   ds.setSoTimeout(20);   // set the timeout in millisecounds.

        while(true){        // recieve data until timeout
            try {
    byte[] buf = new byte[2048];   
    DatagramPacket dp2 = new DatagramPacket(buf, 2048);
    ds.receive(dp2);
    
    String strRecv1 = new String(dp2.getData(), 0, dp2.getLength());
    String decRC="";
    int hell=0;
    for (int x=0;x<strRecv1.length();x++)
    {
      if (strRecv1.charAt(x)!='.'){decRC=decRC+strRecv1.charAt(x);}
      else {hell=x; break;}
    }
    int s=Integer.parseInt(decRC)-1;
    int siz=strRecv1.length();
    total=Integer.toString(s)+strRecv1.substring(hell,siz);
    
     roundtrip=s;
            }
            catch (SocketTimeoutException e) {
            // timeout exception.
            truth=false; break; 
            }   
        }   
     }
     // Running the single packet ends. The above loop runs 50 times.

     java.util.Date datec= new java.util.Date();
	 Timestamp current=new Timestamp(datec.getTime());

      long timediff=(datec.getTime()-date.getTime());
      long sec=timediff/1000;
      long msec=timediff%1000;
      if (truth) 
        {filetext=filetext+"SeqNo."+SeqNum+": "+Long.toString(sec)+"s "+Long.toString(msec)+"ms \n";} 
      else 
        {filetext=filetext+"--SeqNo."+SeqNum+": "+Long.toString(sec)+"s "+Long.toString(msec)+"ms \n";}
    count--;
}



try { File file = new File(hello+".txt");
      if (!file.exists()) {
        file.createNewFile();}
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(filetext);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();}

    ds.close();
  }
}
