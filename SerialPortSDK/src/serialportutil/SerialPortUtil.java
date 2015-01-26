package serialportutil;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

 class SerialWriter extends Thread {
    private OutputStream out;
    private volatile boolean stop = false;
    private Vector<Integer> dataVector = new Vector<Integer>();

    public SerialWriter(OutputStream out) {
        setName("SerialWriter");
        this.out = out;
    }

    public void write(int c) {
        dataVector.add(c);
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        try {
            while (!stop) {// while not stop ,do receive command
                if ((dataVector.size() > 0)) {
                    out.write(dataVector.remove(0));
                }
                Thread.sleep(3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // System.out.println("SerialWriter exit;");
    }
}

 class SerialReader extends Thread {
    private volatile boolean stop = false;
    private MySerialPort mySerialPort;
    private InputStream in;

    public SerialReader(MySerialPort mySerialPort) {
        setName("SerialReader");
        this.mySerialPort = mySerialPort;
        this.in = mySerialPort.getPortInStream();
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        try {
            int len = 0;
            synchronized (buffer) {
                while (!stop) {// while not stop ,do receive command
                    if ((len = this.in.read(buffer)) > -1) {
                        for (int i = 0; i < len; i++) {
                            if ('\r' == (char) buffer[i]) {
                                if (!SerialPortUtil.IGNORE_OUTPUT_CR) {// skip \r
                                    mySerialPort.consolePrint(buffer[i]);
                                }
                            } else {
                                mySerialPort.consolePrint(buffer[i]);
                            }
                        }
                    }
                    Thread.sleep(100);
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        // System.out.println("SerialReader exit;");
    }
}

 class MySerialPort {
    private MyOutputStream myOutputStream;

    private int CONNECTED = 0;

    private int DISCONNECT = -1;

    private String portName;

    private int portSpeed;

    private int portDataBit;

    private int portStopBit;

    private int portParityBit;

    private InputStream portInStream;

    private OutputStream portOutStream;

    private SerialPort serialPort;

    private int portState;

    private SerialWriter serialWriterThread;

    private SerialReader serialReaderThread;

    public String getPortName() {
        return portName;
    }

    public void  setPortName(String portName) {
        this.portName = portName;
    }

    public int  getPortSpeed() {
        return portSpeed;
    }

    public void  setPortSpeed(int portSpeed) {
        this.portSpeed = portSpeed;
    }

    public int  getPortDataBit() {
        return portDataBit;
    }

    public void  setPortDataBit(int portDataBit) {
        this.portDataBit = portDataBit;
    }

    public int  getPortStopBit() {
        return portStopBit;
    }

    public void  setPortStopBit(int portStopBit) {
        this.portStopBit = portStopBit;
    }

    public int getPortParityBit() {
        return portParityBit;
    }

    public void  setPortParityBit(int portParityBit) {
        this.portParityBit = portParityBit;
    }

    public OutputStream getPortOutStream() {
        return portOutStream;
    }

    public int  getPortState() {
        return portState;
    }

    public void  setPortState(int portState) {
        this.portState = portState;
    }

    public MyOutputStream  getMyOutputStream() {
        return myOutputStream;
    }

    public void  setMyOutputStream(MyOutputStream myOutputStream) {
        this.myOutputStream = myOutputStream;
    }

    public SerialWriter  getSerialWriterThread() {
        return serialWriterThread;
    }

    public InputStream  getPortInStream() {
        return portInStream;
    }

    MySerialPort(String name) {
        super();
        this.portName = name;
        this.setPortState(DISCONNECT);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Name[" + this.getPortName() + "] ");
        sb.append("DataBit[" + this.getPortDataBit() + "] ");
        sb.append("PortSpeed[" + this.getPortSpeed() + "] ");
        sb.append("ParityBit[" + this.getPortParityBit() + "] ");
        sb.append("StopBit[" + this.getPortStopBit() + "] ");

        return sb.toString();
    }

    public void  consolePrint(int v) {
        if (myOutputStream != null) {
            try {
                myOutputStream.write(v);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void  consoleFlush() {
        if (myOutputStream != null) {
            try {
                myOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public CommPortIdentifier  getPort(String name) {
        Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
            if (port.getName().equals(name)) {
                return port;
            }
        }
        return null;
    }

    public void  connect() {
        CommPortIdentifier port = getPort(this.portName);
        if (port != null) {
            try {
                System.out.println("Connecting to " + this.portName //
                        + " [speed:" + this.portSpeed + "]" //
                        + " [databit:" + this.portDataBit + "] [stopbit:" + this.portStopBit + "] [paritybit:" + this.portParityBit + "]");

                serialPort = (SerialPort) port.open("Java Serial", 10000);
                serialPort.setSerialPortParams(this.portSpeed, this.portDataBit, this.portStopBit, this.portParityBit);

                serialPort.notifyOnDataAvailable(true);
                serialPort.notifyOnBreakInterrupt(true);
                serialPort.notifyOnOutputEmpty(true);
                serialPort.enableReceiveTimeout(30);

                try {
                    portInStream = serialPort.getInputStream();
                    portOutStream = serialPort.getOutputStream();
                    serialWriterThread = new SerialWriter(portOutStream);
                    serialWriterThread.start();

                    // SerialReader
                    serialReaderThread = new SerialReader(this);
                    serialReaderThread.start();

                    // serialPort.removeEventListener();// TODO
                    // SerialPortEventListener listen = new MySerialPortEventlisten(this, portInStream, portOutStream);
                    // serialPort.addEventListener(listen);

                    this.portState = CONNECTED;
                    System.out.println("Connected!");

                } catch (IOException e) {
                    this.portState = DISCONNECT;
                    System.out.println("Can't open input stream: write-only");
                }

            } catch (PortInUseException e) {
                this.portState = DISCONNECT;
                System.out.println(e.getMessage());
            } catch (UnsupportedCommOperationException e) {
                this.portState = DISCONNECT;
                System.out.println(e.getMessage());
            } finally {
            }
        } else {
        }
    }

    public void  disConnect() {
        if (serialPort == null) {
            System.out.println("serialPort is null");
            return;
        }

        if (this.serialPort != null && this.portState == CONNECTED) {
            try {
                this.portState = DISCONNECT;
                serialWriterThread.setStop(true);
                serialReaderThread.setStop(true);
                serialWriterThread.join();
                serialReaderThread.join();
                this.portInStream.close();
                this.portOutStream.close();

                this.portInStream = null;
                this.portOutStream = null;
                System.out.println("Disconnected from " + this.portName);

                serialPort.close();
                serialPort = null;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// class  MySerialPortEventlisten implements SerialPortEventListener {
//
// private MySerialPort serialPort;
//
// private InputStream portInStream;
//
// private byte[] buffer = new byte[7 * 1024];
//
// public MySerialPortEventlisten(MySerialPort serialPort, InputStream portInStream, OutputStream portOutStream) {
// super();
// this.serialPort = serialPort;
// this.portInStream = portInStream;
// }
//
// public void serialEvent(SerialPortEvent event) {
// switch (event.getEventType()) {
// case SerialPortEvent.CTS:
// System.out.println("CTS event occured.");
// break;
// case SerialPortEvent.CD:
// System.out.println("CD event occured.");
// break;
// case SerialPortEvent.BI:
// System.out.println("BI event occured.");
// break;
// case SerialPortEvent.DSR:
// System.out.println("DSR event occured.");
// break;
// case SerialPortEvent.FE:
// System.out.println("FE event occured.");
// break;
// case SerialPortEvent.OE:
// System.out.println("OE event occured.");
// break;
// case SerialPortEvent.PE:
// System.out.println("PE event occured.");
// break;
// case SerialPortEvent.RI:
// System.out.println("RI event occured.");
// break;
// case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
// // System.out.println("OUTPUT_BUFFER_EMPTY event occured.");
// break;
// case SerialPortEvent.DATA_AVAILABLE:
// try {
// int len;
// synchronized (buffer) {
// while (portInStream.available() != 0 && (len = portInStream.read(buffer)) != -1) {
// for (int i = 0; i < len; i++) {
// serialPort.consolePrint(buffer[i]);
// }
// }
// }
// } catch (Exception e) {
// System.out.println(e.getMessage());
// System.out.println(e);
// }
//
// break;
// }
// serialPort.consoleFlush();
// }
// }

 class SerialPortUtil {

    private static PrintStream systemOut;
    public static boolean REPLACE_CR_WITH_LFCR = false;
    public static boolean TIME_PREFIX = false;
    public static boolean IGNORE_OUTPUT_CR = true;

    public static MySerialPort connect(SerialPortConf serialPortConf) {
        if (serialPortConf == null) {
            return null;
        }
        MySerialPort serialPort = new MySerialPort(serialPortConf.getPortname());
        serialPort.setPortName(serialPortConf.getPortname());
        serialPort.setPortSpeed(serialPortConf.getSpeed());
        serialPort.setPortDataBit(serialPortConf.getDatabit());
        serialPort.setPortStopBit(serialPortConf.getStopbit());
        serialPort.setPortParityBit(serialPortConf.getParityBit());

        serialPort.connect();
        return serialPort;
    }

    public static void disConnect(MySerialPort serialPort) {
        if (serialPort != null) {
            serialPort.disConnect();
            serialPort = null;
        }
    }

    public static void sendCommand(MySerialPort serialPort, String cmd, int delay, int repeat) {
        for (int i = 0; i < repeat; i++) {
            sendCommand(serialPort, cmd, delay);
        }
    }

    public static void sendCommand(MySerialPort serialPort, String cmd, int delay) {
        if (serialPort != null) {
            try {
                SerialWriter serialPortWriterThread = serialPort.getSerialWriterThread();
                byte[] databytes = cmd.getBytes();
                for (int i = 0; i < databytes.length; i++) {
                    serialPortWriterThread.write(databytes[i]);
                }
                // serialPort.getPortOutStream().write();
                // serialPort.getPortOutStream().flush();
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendCommand(MySerialPort serialPort, int cmd, int delay) {
        sendCommand(serialPort, cmd, delay, 1);
    }

    public static void sendCommand(MySerialPort serialPort, int cmd, int delay, int repeat) {
        for (int i = 0; i < repeat; i++) {
            if (serialPort != null) {
                try {
                    SerialWriter serialPortWriterThread = serialPort.getSerialWriterThread();
                    serialPortWriterThread.write(cmd);
                    // serialPort.getPortOutStream().write(cmd);// send to serial port
                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addOutputStream(MySerialPort serialPort, OutputStream out) {
        if (serialPort == null) {
            System.out.println("Add OutputStream error");
            return;
        }
        MyOutputStream myOutputStream = serialPort.getMyOutputStream();

        List<OutputStream> list = myOutputStream.getOutList();
        if (!list.contains(out)) {
            myOutputStream.getOutList().add(out);
            if (out == System.out) {
                systemOut = System.out; // backup
            }
        }
    }

    public static void removeOutputStreams(MySerialPort serialPort) {
        if (serialPort == null) {
            System.out.println("Add OutputStream error");
            return;
        }
        MyOutputStream myOutputStream = serialPort.getMyOutputStream();

        try {
            List<OutputStream> list = myOutputStream.getOutList();
            for (OutputStream out : list) {
                if (systemOut == out) {

                } else {
                    out.close();
                    System.out.println("Close:" + out.getClass());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public static List<OutputStream> buildOutputList(OutputStream[] outs) {
    // List<OutputStream> outputList = new ArrayList<OutputStream>();
    // for (int i = 0; i < outs.length; i++) {
    // outputList.add(outs[i]);
    // }
    // return outputList;
    // }

    public static final String DATE_FORMAT_NOW = "yyyy.MM.dd HH:mm:ss ";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    // public static String getResource(Class<?> c, String name) throws MalformedURLException, URISyntaxException {
    // return c.getResource(name).toURI().toURL().getFile();
    // }

    private static ScriptEngine engine = null;
    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");

        try {
            /* add file log */
            engine.eval("function LOG_FILE(a)        {    serialCommand.addFileLog(a);    }");
            /* script datetimeprefix */
            engine.eval("function SHOW_DATE_TIME(a)        {    serialCommand.showDateTime(a);    }");
            /* script println */
            engine.eval("function PRINT_LN(a)        {    serialCommand.println(a);    }");

            engine.eval("function SEND_CMD1(a)       {    serialCommand.sendCommand(a);    }");
            engine.eval("function SEND_CMD2(a, b)    {    serialCommand.sendCommand(a,b);  }");
            engine.eval("function SEND_CMD3(a, b, c) {    serialCommand.sendCommand(a,b,c);}");

            String funcSetSerialPortConfigure = //
            //
            //

            "function SET_SERIAL( portName,  portSpeed,  portDataBit,  portStopBit, portParityBit){\r\n"
                    + "			   serialCommand.setSerialPortConf(portName,portSpeed,portDataBit,portStopBit,portParityBit);\r\n" //
                    + "}"
                    //
                    //
                    + "function SEND() {\r\n" + //
                    "	if (arguments.length == 1)\r\n" + //
                    "	{\r\n" + //
                    "		SEND_CMD1(arguments[0]);\r\n" + //
                    "	}\r\n" + //
                    "	if (arguments.length == 2)\r\n" + //
                    "	{\r\n" + //
                    "		SEND_CMD2(arguments[0],arguments[1]);\r\n" + //
                    "	}\r\n" + //
                    "	if (arguments.length == 3)\r\n" + //
                    "	{\r\n" + //
                    "		SEND_CMD3(arguments[0],arguments[1],arguments[2]);\r\n" + //
                    "	}\r\n" + //
                    "}";

            engine.eval(funcSetSerialPortConfigure);

        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static ScriptEngine initScriptEngine(AbstractSerialCommand serialCommand) throws ScriptException {
        engine.put("serialCommand", serialCommand);
        engine.put("SerialPortUnit", serialCommand.serialPortConf);
        return engine;
    }

}
