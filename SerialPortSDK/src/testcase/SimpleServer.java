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

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import serialportutil.AbstractSerialCommand;
import serialportutil.CommandBatch;

class ClientServiceThread extends Thread {
    SimpleServer simpleServer;
    Socket clientSocket;
    int clientID = -1;
    boolean running = true;

    ClientServiceThread(SimpleServer simpleServer, Socket s, int i) {
        this.simpleServer = simpleServer;
        this.clientSocket = s;
        this.clientID = i;
    }

    public void run() {
        try {
            InputStream in = clientSocket.getInputStream();
            simpleServer.addOutputStream(clientSocket.getOutputStream());
            int cmd;
            try {
                while ((running && (cmd = in.read()) > 0)) {
                    simpleServer.sendCommand(cmd, 0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class SimpleServer extends AbstractSerialCommand {
    protected void setup() {
        // Setup serial
        setSerialPortConf(SerialFactory.DEFAULT_PORT);

        // Set serial port,replace '\r\n' with '\n' (telnet)
        setSerialPortCRAsLFCR(true);
    }

    @Override
    protected void processCommand() throws Exception {
        ServerSocket serverSocket = new ServerSocket(88);
        int id = 0;
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(this, clientSocket, id++);
            cliThread.start();
        }
    }

    public static void main(String[] args) throws Exception {
        CommandBatch.addClazz(SimpleServer.class);
        CommandBatch.go();
    }
}
