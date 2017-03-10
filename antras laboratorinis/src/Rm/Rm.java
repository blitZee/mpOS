package Rm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Rm {

    RmRegister mode;
    RmRegister ptr;
    RmRegister sp;
    RmRegister r1;
    RmRegister r2;
    RmRegister ch1;
    RmRegister ch2;
    RmRegister ch3;

    RmInterrupt si;
    RmInterrupt pi;

    Memory memory;

    public RmStatusFlag sf;

    int ic;
    int ti;

    public Rm() {
        mode = new RmRegister(1, "mode");
        ptr = new RmRegister(2, "ptr");
        sp = new RmRegister(1, "sp");
        r1 = new RmRegister(4, "register 1");
        r2 = new RmRegister(4, "register 2");
        ch1 = new RmRegister(1, "channel 1");
        ch2 = new RmRegister(1, "channel 2");
        ch3 = new RmRegister(1, "channel 3");
        si = new RmInterrupt(InterruptType.SI);
        pi = new RmInterrupt(InterruptType.PI);
        sf = new RmStatusFlag();
        ic = 0;
        ti = 10;
        memory = new Memory(this);
    }

    public void load(String fileName, String programName){
        try (
            BufferedReader in = new BufferedReader(new FileReader(fileName))){
            byte[][] block = new byte[16][4];
            int counter = 0;
            byte[] temp = new byte[14];
            String buffer = in.readLine();
            if(buffer.equals("DSEG")) {
                while (in.ready()) {//Kol yra ka skaityti.
                    buffer = in.readLine();
                    System.out.println("buffer " +  buffer);
                    if (buffer.equals("CSEG")){//Jei sutinkame CSEG, iseiname is ciklo
                        break;
                    }
                    if (buffer.startsWith("DW")) {
                        counter = addDataToBlock(block, buffer, counter); // Jei eilute prasideda 'DW', kviecieme funkcija,
                        // kuri prideda duomenis i bloka ir grazina nauja counter reiksme, jei ji pasikeite.

                        if (counter >= 15) { //Jei counter reiksme pasiekia bloko dydi, ji yra nunulinama ir blokas pridedamas
                            //i supervizoriaus atminti
                            memory.addToSupervisor(block, false);
                            counter = 0;
                            block = new byte[16][4];
                        }
                    }
                }
            }
            memory.addToSupervisor(block, false);
            block = new byte[16][4];

            if (buffer.equals("CSEG")){// Pabaigus DSEG, pereinam prie CSEG.
                while (in.ready()) {//Kol yra ka skaityti.
                    buffer = in.readLine();
                    //System.out.println(buffer);
                    if (buffer.equals("HALT")){//Jei sutinkame HALT, iseiname is ciklo
                        counter = addCodeToBlock(block, buffer, counter);
                        break;
                    }
                    counter = addCodeToBlock(block, buffer, counter); // Kviecieme funkcija,
                    // kuri prideda duomenis i bloka ir grazina nauja counter reiksme, jei ji pasikeite.
                    //validacija vyksta supervizoriaus atminty

                    if (counter > 15) { //Jei counter reiksme pasiekia bloko dydi, ji yra nunulinama ir blokas pridedamas
                        //i supervizoriaus atminti
                        memory.addToSupervisor(block, true);
                        counter = 0;
                        block = new byte[16][4];
                    }
                }
                memory.addToSupervisor(block, true);
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private int addDataToBlock(byte[][] block, String buffer, int counter){
        block[counter][0] = 'D';
        block[counter][1] = 'W';
        counter++;
        block[counter] = intToByte(buffer.substring(3));
        counter++;
        return counter;
    }

    private int addCodeToBlock(byte[][] block, String buffer, int counter){
        block[counter] = buffer.getBytes();
        counter++;
        return counter;
    }

    private byte[] intToByte(String string){

        byte[] buffer = new byte[4];
        int temp = Integer.valueOf(string);

        buffer[0] = (byte) (temp & 255);
        temp = temp >> 8;
        buffer[1] = (byte) (temp & 255);
        temp = temp >> 8;
        buffer[2] = (byte) (temp & 255);
        temp = temp >> 8;
        buffer[3] = (byte) (temp & 255);

        return buffer;
    }
}
