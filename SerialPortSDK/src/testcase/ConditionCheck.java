/*******************************************************************************
 * Copyright 2015 Bin Liu (flylb1@gmail.com)
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
