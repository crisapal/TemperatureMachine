
import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;

public class ConnectionUART {
    public static void main(String[] args)  {


    GUI myGui= new GUI();

     /* COD DE TEST TRANSMISIE TEMPERATURA LA INTERVAL DE 10 SECUNDE

        // port number for MAC Computer
        SerialPort sp = SerialPort.getCommPort("/dev/cu.usbserial-2101837374711");
        // connection setting according VHDL project
        sp.setComPortParameters(300, 8, 1, 0);
        // block until bytes can be written
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

      if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }

        for(int i=0;i<4;i++) {
            try {
                InputStream is = sp.getInputStream();
                int buf1;
                int buf2;
                buf1 = is.read(); //LSB
                buf2 = is.read(); //MSB
                //byte conversion accoriding PMOD Reference Guide
                double value = (double) (buf1 + buf2 * 256) * 0.0625;
                System.out.println("Citirea "+(i+1)+":"+value );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (sp.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
            return;
        }*/

    }

    }
