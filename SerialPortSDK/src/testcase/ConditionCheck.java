package testcase;

import serialportutil.AbstractSerialCommand;
import serialportutil.CommandBatch;
import serialportutil.StringOutputStream;

/**
 * Condition check <BR>
 * 1. Send command 'ls /system/bin' <BR>
 * 2. Check result <BR>
 * 3. If result include 'ps', Send command 'ps |grep sh'
 * 
 */
public class ConditionCheck extends AbstractSerialCommand {
    private StringOutputStream stringOutput = new StringOutputStream();

    @Override
    protected void setup() {
        setSerialPortConf(SerialFactory.DEFAULT_PORT);
        addOutputStream(stringOutput);
    }

    @Override
    protected void processCommand() throws Exception {

        sendCommand("ls /system/bin\n", 1000, 1);
        // Get result
        String[] items = stringOutput.toStrings();
        boolean continueProcess = false;
        for (String item : items) {
            if (item.indexOf("ps") >= 0) {
                continueProcess = true;
                System.out.println("Has 'ps' command !");
                break;
            }
        }
        if (!continueProcess) {
            return;
        }

        // Run command 'ps |grep sh' and get new result
        System.out.println("Execute 'ps |grep sh' command !");
        stringOutput.clear();
        sendCommand("ps |grep sh\n", 1000, 1);
        System.out.println(stringOutput.toString());
    }

    public static void main(String[] args) {
        CommandBatch.addClazz(ConditionCheck.class);
        CommandBatch.go();
    }
}
