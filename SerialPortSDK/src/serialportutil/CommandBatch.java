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

import java.util.ArrayList;
import java.util.List;

public class CommandBatch {
    protected static List<Class<?>> clazzes = new ArrayList<Class<?>>();

    public static void addClazz(Class<?> clazz) {
        clazzes.add(clazz);
    }

    public static void go() {
        if (clazzes.size() == 0) {
            return;
        }
        try {
            try {
                for (Class<?> commandClazz : clazzes) {
                    Object ins = commandClazz.newInstance();
                    if (ins instanceof AbstractSerialCommand) {
                        System.out.println("Start>>" + ins.getClass());
                        ((AbstractSerialCommand) ins).start();
                        ((AbstractSerialCommand) ins).join();
                        System.out.println("End<<<<" + ins.getClass());
                        System.out.println();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void goScript() {
        if (clazzes.size() == 0) {
            return;
        }
        try {
            for (Class<?> commandClazz : clazzes) {
                Object ins = commandClazz.newInstance();
                if (ins instanceof AbstractSerialCommand) {
                    ((AbstractSerialCommand) ins).runScript();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
