import java.io.IOException;
import java.util.Scanner;

import Rm.Rm;
import processes.Interrupt;
import resources.Resource;
import resources.Type;
import utils.OsLogger;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Main {

    public static void main(String[] args) {
        try {
            OsLogger.init("logger.txt");
            //Rm rm = new Rm();
            Rm.init();
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
                            Rm.hdd.close();
                            OsLogger.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        work = false;
                        break;
                    }
                    case "load": {
                        //rm.load(fileName(inArray), programName(inArray));
                        String programName = programName(inArray);
                        if(programName != null && programName.length() == 2) {
                            //rm.load(programName);
                            Resource r = new Resource();
                            r.content = programName;
                            r.type = Type.VARTOTOJO_ATMINTIS;
                            Rm.resourceList.add(r);
                        }
                        else {
                            System.out.println("No program name given or incorrect format");
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
                        if(programName != null && programName.length() == 2)
                            Rm.start(programName);
                        else {
                            System.out.println("No program name given or incorrect format");
                        }
                        break;
                    }
                    case "clear": {
                        int id = programId(inArray);
                        if(id < 0) {
                            System.out.println("Incorrect Id");
                            break;
                        }
                        else{
                            Rm.removeVm(id);
                        }
                        break;
                    }
                    case "test": {
                       // rm.load(inArray[1]);
                       // rm.start(inArray[1]);
                        break;
                    }
                    case "show": {
                        String programName = programName(inArray);
                        if(programName != null)
                            Rm.showBlock(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "showcs": {
                        String programName = programName(inArray);
                        if(programName != null)
                            Rm.showCseg(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "showds": {
                        String programName = programName(inArray);
                        if(programName != null)
                            Rm.showDseg(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "step": {
                        Rm.stepMode = true;
                    }


                }
                Rm.callResourceManager();
                Rm.callProcessManager();
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
        System.out.println("• Show - Shows programs memory. -p - Program name.");
        System.out.println("• Showcs - Shows programs code segment. -p - Program name.");
        System.out.println("• Step - Set programs to execute in step mode. -p - Program name.");
        System.out.println("• Nostep - Set programs to execute in non step mode. -p - Program name.");
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
            if (array[i].equals("-p")) {
                if(array.length > i + 1)
                    return array[i + 1];
            }
        }
        return null;
    }

    private static int programId(String[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i].equals("-p")) {
                if(array.length > i + 1)
                    return Integer.parseInt(array[i + 1]);
            }
        }
        return -1;
    }
}
