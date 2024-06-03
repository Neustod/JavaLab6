package Server;

import Codes.Answer;
import Codes.Request;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.table.DefaultTableModel;
import lab1.RecIntegral;
import lab1.IntegralInputException;

/**
 *
 * @author User
 */
public class ServerInstance extends Thread {
    public ServerInstance(DatagramSocket _insocket, DefaultTableModel _dtTable)
    {
        inSocket = _insocket;
        inBuff = new byte[1024];
        inputLock = new Object();
        outputLock = new Object();
        tableLock = new Object();
        dtTable = _dtTable;
        bDisconnect = false;
        
        try
        {
            outSocket = new DatagramSocket();
        }
        catch(SocketException exc)
        {
            outSocket = null;
            bDisconnect = true;
        }
    }
    
    private final DatagramSocket inSocket;
    private DatagramSocket outSocket;
    private final Object inputLock;
    private final Object outputLock;
    private final Object tableLock;
    private final byte[] inBuff;
    private final DefaultTableModel dtTable;
    boolean bDisconnect;
    
    @Override
    public void run()
    {
        while (!bDisconnect)
        {
            DatagramPacket inData = new DatagramPacket(inBuff, inBuff.length);
            
            synchronized(inputLock)
            {   
                try
                {
                    inSocket.receive(inData);
                }
                catch(IOException exc)
                {
                    bDisconnect = true;
                    continue;
                }
            }
            
            try 
            {
                InetAddress ClientIp = inData.getAddress();
                int clientPort = inData.getPort();
                
                ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(inBuff));
                
                Request code = (Request)inStream.readObject();
                
                ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
                ObjectOutputStream outStream = new ObjectOutputStream(outByteStream);
                
                int objCount;
                switch (code)
                {
                    // CONNECT REQUEST
                    case Request.CONNECT: 
                        outStream.writeObject(Answer.SUCCESS);
                        break;
                        
                    // SEND REQUEST
                    case Request.SEND: 
                        objCount = inStream.readInt();
                        
                        if (objCount <= 0)
                        {
                            outStream.writeObject(Answer.ERROR);
                            break;
                        }
                        
                        synchronized(tableLock)
                        {
                            for (int i = 0; i < objCount; i++)
                            {
                                RecIntegral tmp = (RecIntegral)inStream.readObject();
                                dtTable.addRow(new String[]{tmp.LimitL(), tmp.LimitR(), tmp.Dx(), tmp.Result()});
                            }    
                        }
                        outStream.writeObject(Answer.SUCCESS);
                        
                        break;
                        
                    // RECIEVE REQUEST
                    case Request.RECIEVE:
                        objCount = dtTable.getRowCount();
                        
                        outStream.writeObject(Answer.SUCCESS);
                        outStream.writeInt(objCount);
                        
                        synchronized(tableLock){
                            for (int i = 0; i < objCount; i++)
                            {
                                double a = Double.parseDouble((String)dtTable.getValueAt(i, 0));
                                double b = Double.parseDouble((String)dtTable.getValueAt(i, 1));
                                double dx = Double.parseDouble((String)dtTable.getValueAt(i, 2));
                                double result = Double.parseDouble((String)dtTable.getValueAt(i, 3));
                                
                                try
                                {
                                    outStream.writeObject(new RecIntegral(a, b, dx, result));
                                }
                                catch(IntegralInputException exc)
                                {
                                    outStream.flush();
                                    outStream.writeObject(Answer.ERROR);
                                    break;
                                }
                            }
                        }
                        
                        break;
                        
                    // DELETE REQUEST
                    case Request.DELETE:
                        int index = inStream.readInt();
                        
                        if (index >= 0)
                        {
                            dtTable.removeRow(index);
                            outStream.writeObject(Answer.SUCCESS);
                        }
                        else
                        {
                            outStream.writeObject(Answer.INVALID);
                        }
                        
                        break;
                        
                    // ERROR CASE
                    default:
                        outStream.writeObject(Answer.ERROR);
                        break;
                }
                
                DatagramPacket outData = new DatagramPacket(outByteStream.toByteArray(), outByteStream.size(), ClientIp, 1235);
                synchronized(outputLock)
                {
                    outSocket.send(outData);
                }
                inStream.close();
                outStream.flush();
            }
            catch(ClassNotFoundException | IOException exc)
            {
                exc.printStackTrace();
                continue;
            }
        }
    }
}
