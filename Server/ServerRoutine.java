package Server;

import java.io.IOException;
import java.net.DatagramSocket;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class ServerRoutine extends Thread {
    public ServerRoutine(DefaultTableModel _dtTable)
    {
        try 
        {
            sServer = new DatagramSocket(1234); // Port
            
            for (int i = 0; i < 5; i++)
            {
                thrs[i] = new ServerInstance(sServer, _dtTable);
            }
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
    }
    
    private DatagramSocket sServer = null;
    ServerInstance[] thrs = new ServerInstance[5];
    
    @Override
    public void run()
    {
        for (int i = 0; i < 5; i++)
        {
            thrs[i].start();
        }
        
        for (int i = 0; i < 5; i++)
        {
            try
            {
                thrs[i].join();
            }
            catch(InterruptedException exc){}
        }
    }
}
