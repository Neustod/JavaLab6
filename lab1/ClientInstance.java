package lab1;

import Codes.Answer;
import Codes.Request;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class ClientInstance {
    
    public ClientInstance(DefaultTableModel _dtTable)
    {
        inBuff = new byte[1024];
        tableLock = new Object();
        dtTable = _dtTable;
        bDisconnect = false;
        port = 1234;
        
        try
        {
            srvIp = InetAddress.getByName("IVDell808");
            inSocket = new DatagramSocket(1235);
            
            outSocket = new DatagramSocket();
        }
        catch(SocketException | UnknownHostException exc)
        {
            outSocket = null;
            inSocket = null;
            bDisconnect = true;
        }
    }
    
    private DatagramSocket inSocket;
    private DatagramSocket outSocket;
    private final int port;
    private final Object tableLock;
    private byte[] inBuff;
    private final DefaultTableModel dtTable;
    private InetAddress srvIp;
    boolean bDisconnect;
    
    public int Connect() throws ClassNotFoundException
    {
        try
        {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(outByteStream);
            
            outStream.writeObject(Request.CONNECT);
            
            DatagramPacket outData = new DatagramPacket(outByteStream.toByteArray(), outByteStream.size(), srvIp, port);
            
            outSocket.send(outData);
        }
        catch(IOException exc)
        {
            return -1;
        }
        try
        {
            DatagramPacket inData = new DatagramPacket(inBuff, inBuff.length);
            inSocket.receive(inData);
            ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(inBuff));
            if((Answer)inStream.readObject() == Answer.SUCCESS)
            {
                JOptionPane.showMessageDialog(
                    null,
                    "SUCCESS",
                    "Code",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                return 0;
            }
        }
        catch(IOException exc)
        {
            return -1;
        }

        return -1;
    }
    
    public int Send()
    {
        int objCount = dtTable.getRowCount();
        try
        {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(outByteStream);
            
            outStream.writeObject(Request.SEND);
            outStream.writeInt(objCount);
            
            for (int i = 0; i < objCount; i++)
            {
                double a = Double.parseDouble((String)dtTable.getValueAt(i, 0));
                double b = Double.parseDouble((String)dtTable.getValueAt(i, 1));
                double dx = Double.parseDouble((String)dtTable.getValueAt(i, 2));
                double result = Double.parseDouble((String)dtTable.getValueAt(i, 3));
            
                outStream.writeObject(new RecIntegral(a, b, dx, result));
            }
            DatagramPacket outData = new DatagramPacket(outByteStream.toByteArray(), outByteStream.size(), srvIp, port);
            
            outSocket.send(outData);
        }
        catch(IntegralInputException | IOException exc)
        {
            return -1;
        }
        try
        {
            DatagramPacket inData = new DatagramPacket(inBuff, inBuff.length);
            inSocket.receive(inData);
            ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(inBuff));
            if((Answer)inStream.readObject() == Answer.SUCCESS)
            {
                JOptionPane.showMessageDialog(
                    null,
                    "SUCCESS",
                    "Code",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                return 0;
            }
        }
        catch(ClassNotFoundException | IOException exc)
        {
            return -1;
        }

        return -1;
    }
    
    public int Recieve(java.util.ArrayList<RecIntegral> alRecs)
    {
        try
        {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(outByteStream);
            
            outStream.writeObject(Request.RECIEVE);
            
            DatagramPacket outData = new DatagramPacket(outByteStream.toByteArray(), outByteStream.size(), srvIp, port);
            
            outSocket.send(outData);
        }
        catch(IOException exc)
        {
            return -1;
        }
        try
        {
            DatagramPacket inData = new DatagramPacket(inBuff, inBuff.length);
            inSocket.receive(inData);
            ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(inBuff));
            if((Answer)inStream.readObject() == Answer.SUCCESS)
            {
                int objCount = inStream.readInt();
                
                if (objCount <= 0)
                {
                    return -1;
                }
                
                dtTable.setRowCount(0);
                for (int i = 0; i < objCount; i++)
                {
                    RecIntegral tmp = (RecIntegral)inStream.readObject();
                    dtTable.addRow(new String[]{tmp.LimitL(), tmp.LimitR(), tmp.Dx(), tmp.Result()});
                    alRecs.add(tmp);
                }    
                
                JOptionPane.showMessageDialog(
                    null,
                    "SUCCESS",
                    "Code",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                return 0;
            }
        }
        catch(ClassNotFoundException | IOException exc)
        {
            return -1;
        }

        return -1;
    }
    
    public int Delete(int _index)
    {
        int i = _index;
        if (i == -1)
        {
            return -1;
        }
        
        try
        {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(outByteStream);
            
            outStream.writeObject(Request.DELETE);
            outStream.writeInt(i);
            outStream.writeObject(Request.DELETE);
            
            DatagramPacket outData = new DatagramPacket(outByteStream.toByteArray(), outByteStream.size(), srvIp, port);
            
            outSocket.send(outData);
        }
        catch(IOException exc)
        {
            return -1;
        }
        try
        {
            DatagramPacket inData = new DatagramPacket(inBuff, inBuff.length);
            inSocket.receive(inData);
            ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(inBuff));
            if((Answer)inStream.readObject() == Answer.SUCCESS)
            {
                JOptionPane.showMessageDialog(
                    null,
                    "SUCCESS",
                    "Code",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                return 0;
            }
        }
        catch(ClassNotFoundException | IOException exc)
        {
            return -1;
        }

        return -1;
    }
}
