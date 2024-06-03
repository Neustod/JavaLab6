package Server;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class MyWin extends JFrame implements ActionListener 
{
    // Some window's elements 
    private final DefaultTableModel dtResultsModel;
    private final JTable jtResults;
    private final JDialog jdFrame;
    private final ServerRoutine thrSrvRoutine;
    
    private final ArrayList alRecs;
    
    // Classes serial number
    private static final long serialVersionUID = 1L;

    public MyWin() 
    {
        // Initialization block
        jdFrame = new JDialog();
        alRecs = new ArrayList();
        
        //----------------==Table Filling==------------------------------
        Object[] columnNames = new String[]{"L limit", "R limit", "Step", "Result"};
        Object[][] data = new String[][]{};
        
        // Tables initialization
        dtResultsModel = new DefaultTableModel(data, columnNames);
        dtResultsModel.setColumnIdentifiers(columnNames);
        
        jtResults = new JTable(dtResultsModel);
        JScrollPane sp = new JScrollPane(jtResults);
        
        thrSrvRoutine = new ServerRoutine(dtResultsModel);
        
        Container c = getContentPane(); // Client rect
        c.setLayout(null); // НЕНАВИЖУ КОМПАНОВЩИКИ
        
        sp.setBounds(0, 0, 627, 480);
        
        //----------------==Client rect's Filling==-----------------------
        c.add(sp);
        
        //----------------==Setting window's properties==-----------------
        // Window's properties.
        setTitle("Integral Server"); // Window's title. Obviously.
        
        // Sets preffered size to window.
        setPreferredSize(new Dimension(640, 480));
        
        // Exit application on Exit button.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pack(); // Setting preffered sizes.
        setVisible(true); // Makes window visible.
        
        thrSrvRoutine.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Unprocessed action case.
    }

    // запуск оконного приложения
    public static void main(String args[]) {
        new MyWin();
    }
}
