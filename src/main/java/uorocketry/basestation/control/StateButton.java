package uorocketry.basestation.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.json.JSONArray;

import com.fazecast.jSerialComm.SerialPort;

import uorocketry.basestation.connections.ComConnection;
import uorocketry.basestation.connections.ComConnectionHolder;
import uorocketry.basestation.connections.DataReciever;
import uorocketry.basestation.helper.Helper;

/**
 * DOES NOT support multiple data sources 
 * (Hardcoded, fixable)
 * 
 * @author Ajay
 *
 */
public class StateButton implements ActionListener, DataReciever {
	
	// Always zero for now
	private static final int TABLE_INDEX = 0;
	
	private static final Color AVAILABLE_COLOR = new Color(0, 33, 115);
	private static final Color SUCCESS_COLOR = new Color(3, 176, 0);
	private static final Color INACTIVE_COLOR = new Color(79, 79, 79);
    private static final Color CLICKED_COLOR = new Color(166, 178, 255);
	
	String name;
	/** What data to send */
	byte[] data;
	/** Which states can this action be completed from */
	int[] availableStates;
	/** Which states means this button has been complete */
	int[] successStates;
	
	private JButton button;
	private JPanel borderPanel;
    private Timer timer = new Timer();

	private ComConnectionHolder comConnectionHolder;
	
	public StateButton(ComConnectionHolder comConnectionHolder, String name, byte data, JSONArray successStates, JSONArray availableStates) {
		this.comConnectionHolder = comConnectionHolder;
		
		this.name = name;
		this.data = new byte[] { data };
		this.successStates = Helper.toIntArray(successStates);
		this.availableStates = Helper.toIntArray(availableStates);
		
		button = new JButton(name);
		button.addActionListener(this);
		button.setFont(new Font("Arial", Font.PLAIN, 20));
		
		borderPanel = new JPanel();
		borderPanel.setBorder(BorderFactory.createTitledBorder(name));
		borderPanel.add(button);
	}
	
	public void sendAction() {
        SerialPort serialPort = comConnectionHolder.get(TABLE_INDEX).getSerialPort();
        if (serialPort != null) {
            serialPort.writeBytes(data, data.length);
        }
    }
	
	public void stateChanged(int newState) {
        if (Helper.arrayIncludes(availableStates, newState)) {
            button.setForeground(AVAILABLE_COLOR);
        } else if (Helper.arrayIncludes(successStates, newState)) {
            button.setForeground(SUCCESS_COLOR);
        } else {
            button.setForeground(INACTIVE_COLOR);
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button) {
		    sendAction();
		}
	}
	
	@Override
    public void recievedData(ComConnection connection, byte[] data) {
	    if (comConnectionHolder.get(TABLE_INDEX).bytesEqualWithoutDelimiter(this.data, data)) {
	        sendAction();
	        
	        borderPanel.setBackground(CLICKED_COLOR);
	        timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    borderPanel.setBackground(null);
                }
            }, 750);
	    }
    }
	
	public JPanel getPanel() {
		return borderPanel;
	}

}