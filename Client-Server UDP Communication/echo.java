import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class echo{
  public static void main(String[] args) throws Exception {
   
    InetAddress ia = InetAddress.getByName("10.192.51.255");
    
    DatagramSocket ds = new DatagramSocket(3000);
    byte[] buf = new byte[2048];
    
    
    while (true){
    DatagramPacket dp = new DatagramPacket(buf, 2048);
    ds.receive(dp);

    
    String strRecv1 = new String(dp.getData(), 0, dp.getLength());
    String decRC="";
    int hell=0;
    for (int x=0;x<strRecv1.length();x++)
    {
      if (strRecv1.charAt(x)!='.'){decRC=decRC+strRecv1.charAt(x);}
      else {hell=x; break;}
    }
    
    int s=Integer.parseInt(decRC)-1;
    int siz=strRecv1.length();
    String strRecv=Integer.toString(s)+strRecv1.substring(hell,siz);
    DatagramPacket dp2 = new DatagramPacket(strRecv.getBytes(), strRecv.length(), ia, 4000);
    ds.send(dp2);
    System.out.println(strRecv);
      }

    //ds.close();
  }
}



