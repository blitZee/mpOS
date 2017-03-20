import java.io.IOException;
import java.util.Scanner;

import Rm.Rm;
import testTools.Test;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Main {

    public static void main(String[] args) {
        try {
            Rm rm = new Rm();
            Scanner scanner = new Scanner(System.in);
            boolean work = true;
            System.out.println("Hello. Welcome to MikOS. Type \"help\" to view the command list");
            while (work) {
                String input = scanner.nextLine();
                String[] inArray = input.split(" ");
                System.out.println(inArray[0]);
                switch (inArray[0].toLowerCase()) {
                    case "quit": {
                        try {
                            rm.hdd.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        work = false;
                        break;
                    }
                    case "load": {
                        //rm.load(fileName(inArray), programName(inArray));
                        String programName = programName(inArray);
                        if(programName != null)
                            rm.load(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "save": {
                        throw new UnsupportedOperationException();
                    }
                    case "help": {
                        printHelp();
                        break;
                    }
                    case "start": {
                        String programName = programName(inArray);
                        if(programName != null)
                            rm.start(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "clear": {
                        String programName = programName(inArray);
                        if(programName != null)
                            rm.removeVm(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "test": {
                        //new testTools.Test(rm).testSf();
                        //new Test(rm).testLoad(inArray[1]);
                        rm.load(inArray[1]);
                        rm.start(inArray[1]);
                        break;
                    }
                    case "show": {
                        String programName = programName(inArray);
                        if(programName != null)
                            rm.showBlock(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "showcs": {
                        String programName = programName(inArray);
                        if(programName != null)
                            rm.showCseg(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "showds": {
                        String programName = programName(inArray);
                        if(programName != null)
                            rm.showDseg(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }


                }
            }
            System.out.println("Exiting");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("• Help - Prints this message;");
        System.out.println("• Load - Loads a program from HDD to real memory -p - Program name (up to 2 characters; Must be unique)");
        System.out.println("• Start - Starts a program. -p - Program name");
        System.out.println("• Clear - Clears a loaded program. -p - Program name.");
        System.out.println("• Quit - Shuts down the OS");
    }

    private static String fileName(String[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i].equals("-f"))
                return array[i + 1];
        }
        return null;
    }

    private static String programName(String[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i].equals("-p"))
                return array[i + 1];
        }
        return null;
    }
}
