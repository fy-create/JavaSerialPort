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

public class SerialPortConf /* implements IPort */{
    private String portname;
    private int speed;
    private int databit;
    private int stopbit;
    private int parityBit;

    public SerialPortConf(String portname, int speed, int databit, int stopbit, int parityBit) {
        super();
        this.databit = databit;
        this.parityBit = parityBit;
        this.portname = portname;
        this.speed = speed;
        this.stopbit = stopbit;
    }

    public String  getPortname() {
        return portname;
    }

    public int  getSpeed() {
        return speed;
    }

    public int  getDatabit() {
        return databit;
    }

    public int  getStopbit() {
        return stopbit;
    }

    public int  getParityBit() {
        return parityBit;
    }

    @Override
    public String toString() {
        return "Portname:" + getPortname() + " Speed:" + getSpeed() + " Databit:" + getDatabit() + " Stopbit:" + getStopbit()
                + " ParityBit:" + getParityBit();
    }
}
