/*******************************************************************************
 * Copyright 
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
package serialportutil;

import gnu.io.CommDriver;
import gnu.io.CommPortIdentifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.script.ScriptEngine;

class ConsoleInput extends Thread {
    private volatile boolean stop = false;

    private MySerialPort     serialPort;

    public ConsoleInput(MySerialPort serialPort) {
        super();
        this.serialPort = serialPort;
        super.setName("ConsoleInput");
    }

    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int cmd;
        try {
            while ((!stop && (cmd = in.read()) > 0)) {
                SerialPortUtil.sendCommand(serialPort, cmd, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MyOutputStream extends OutputStream {
    private List<OutputStream> outList;

    public List<OutputStream> getOutList() {
        return outList;
    }

    public void setOutList(List<OutputStream> consoleList) {
        this.outList = consoleList;
    }

    public synchronized void addOutputStream(OutputStream out) {
        if (outList == null) {
            this.outList = new ArrayList<OutputStream>();
        }

        boolean found = false;
        for (int i = 0; i < outList.size(); i++) {
            if (outList.get(i).equals(out)) {
                found = true;
            }
        }
        if (found == false) {
            // log.info("add outputstream:" + out);
            outList.add(out);
        }
    }

    public synchronized void removeOutputStream(OutputStream out) {
        if (outList == null)
            return;

        boolean found = false;
        int pos = 0;
        for (int i = 0; i < outList.size(); i++) {
            if (outList.get(i).equals(out)) {
                found = true;
                pos = i;
            }
        }
        if (found == true) {
            // log.info("remove outputstream:" + out);
            outList.remove(pos);
        }
    }

    public MyOutputStream() {
        super();
        setOutList(new ArrayList<OutputStream>());
    }

    public void write(int b) throws IOException {
        if (outList == null || outList.size() == 0)
            return;
        for (int i = 0; i < outList.size(); i++) {
            OutputStream os = null;
            try {
                os = (OutputStream) outList.get(i);
                if (SerialPortUtil.REPLACE_CR_WITH_LFCR && b == '\n') {
                    os.write('\r');
                    os.write('\n');
                } else {
                    os.write(b);
                }
                // System.out.println(b);
                if (b == 10 && SerialPortUtil.TIME_PREFIX) {
                    os.write(SerialPortUtil.now().getBytes());
                }

            } catch (IOException e) {
                removeOutputStream(os);
                System.out.println(e);
            }
        }
    }

    public void write(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++) {
            write(b[i]);
        }
    }

    @Override
    public void flush() throws IOException {
        if (outList == null || outList.size() == 0)
            return;
        for (int i = 0; i < outList.size(); i++) {
            try {
                ((OutputStream) outList.get(i)).flush();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        super.flush();
    }

}

public abstract class AbstractSerialCommand extends Thread {
    static {
        try {
            String driverName = "gnu.io.RXTXCommDriver";
            CommDriver commDriver = (CommDriver) Class.forName(driverName).newInstance();
            commDriver.initialize();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected String           scriptFile;
    protected MySerialPort     serialPort;
    protected SerialPortConf   serialPortConf;
    private List<OutputStream> outList      = new ArrayList<OutputStream>();

    private ConsoleInput       consoleInput = null;

    private void removeOutputStreams() {
        SerialPortUtil.removeOutputStreams(serialPort);
    }

    private void processScript(String scriptFile) throws Exception {
        ScriptEngine engine = SerialPortUtil.initScriptEngine(this);
        FileReader reader = new FileReader(scriptFile);
        engine.eval(reader);
        reader.close();
    }

    private boolean preProcess() {
        /* connect to serialPort and add default outputstrem */
        serialPort = SerialPortUtil.connect(serialPortConf);
        if (serialPort == null) {
            return false;
        }
        if (serialPort.getPortState() == -1/* serialPort.DISCONNECT */) {
            System.out.println("Can not connect serialPort\r\n" + serialPortConf);
            serialPort = null;
            return false;
        }
        MyOutputStream myOutputStream = new MyOutputStream();
        serialPort.setMyOutputStream(myOutputStream);
        for (OutputStream out : outList) {
            SerialPortUtil.addOutputStream(serialPort, out);
        }
        return true;
    }

    private void afterProcess() {
        if (consoleInput != null) {
            try {
                consoleInput.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // close OutputStreams
        removeOutputStreams();

        /* Disconnect from serialPort */
        if (serialPort != null) {
            SerialPortUtil.disConnect(serialPort);
        }
    }

    protected void setup() {
    }

    protected void addConsoleInputSupport() {
        consoleInput = new ConsoleInput(serialPort);
        consoleInput.start();
    }

    protected void processCommand() throws Exception {
    }

    public AbstractSerialCommand() {
        super();
    }

    public void addOutputStream(OutputStream out) {
        if (serialPort == null) {
            outList.add(out);
        } else {
            SerialPortUtil.addOutputStream(serialPort, out);
        }
    }

    public void setSerialPortConf(SerialPortConf serialPortConf) {
        this.serialPortConf = serialPortConf;
    }

    public void setSerialPortConf(String portname, int speed, int databit, int stopbit, int parityBit) {
        this.serialPortConf = // SerialPortConf.build(portname, speed, databit, stopbit, parityBit);
        new SerialPortConf(portname, speed, databit, stopbit, parityBit);
    }

    /**
     * @param flag
     *            Add date time prefix on output
     */
    public void setSerialPortTimePrefix(boolean flag) {
        SerialPortUtil.TIME_PREFIX = flag;
    }

    public void setSerialPortCRAsLFCR(boolean flag) {
        SerialPortUtil.REPLACE_CR_WITH_LFCR = flag;
    }

    public void sendCommand(String cmd, int delay, int repeat) {
        SerialPortUtil.sendCommand(serialPort, cmd, delay, repeat);
    }

    public void sendCommand(String cmd, int delay) {
        SerialPortUtil.sendCommand(serialPort, cmd, delay);
    }

    public void sendCommand(int cmd, int delay, int repeat) {
        SerialPortUtil.sendCommand(serialPort, cmd, delay, repeat);
    }

    public void sendCommand(int cmd, int delay) {
        SerialPortUtil.sendCommand(serialPort, cmd, delay, 1);
    }

    public void run() {
        try {
            setup();
            preProcess();
            processCommand();
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            afterProcess();
        }
    }

    public void runScript() {
        try {
            setup();
            preProcess();
            processScript(scriptFile);
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            afterProcess();
        }
    }

    public static List<String> listPorts() {
        Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
        List<String> list = new ArrayList<String>();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            list.add(currPortId.getName());
        }
        return list;
    }
}
