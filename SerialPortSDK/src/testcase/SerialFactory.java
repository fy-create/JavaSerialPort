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

import serialportutil.SerialPortConf;

public class SerialFactory {
    public static SerialPortConf COM1 = new SerialPortConf("COM1", 115200, 8, 1, 0);

    public static SerialPortConf COM15 = new SerialPortConf("COM15", 115200, 8, 1, 0);

    public static SerialPortConf ttyUSB0 = new SerialPortConf("/dev/ttyUSB0", 115200, 8, 1, 0);

    public static SerialPortConf DEFAULT_PORT = COM15;

}
