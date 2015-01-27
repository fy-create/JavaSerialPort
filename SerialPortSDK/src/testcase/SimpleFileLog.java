package testcase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import serialportutil.AbstractSerialCommand;
import serialportutil.CommandBatch;

/**
 * Direct send command to serial<BR>
 * Using System.out as output
 * 
 */
public class SimpleFileLog extends AbstractSerialCommand {
    @Override
    protected void setup() {
        // Setup serial
        setSerialPortConf(SerialFactory.DEFAULT_PORT);

        // Add file output stream,serial output redirect to file
        try {
            addOutputStream(new FileOutputStream(new File("./log.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processCommand() throws Exception {
        // Send command "ps" to serial port,
        sendCommand("ps |grep sh\n", 100, 1);
    }

    public static void main(String[] args) {
        CommandBatch.addClazz(SimpleFileLog.class);
        CommandBatch.go();
    }

}
