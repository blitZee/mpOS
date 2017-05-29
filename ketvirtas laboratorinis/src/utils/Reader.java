package utils;

import Rm.Rm;
import processes.MIKOSProcess;
import resources.Resource;
import resources.Type;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Irmis on 2017-05-28.
 */
public class Reader implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean work = true;
        System.out.println("Hello. Welcome to MikOS. Type \"help\" to view the command list");

        while (work) {
            scanner.nextLine();
            synchronized (Rm.class) {
                System.out.print(">>");
                String input = scanner.nextLine();
                String[] inArray = input.split(" ");
                System.out.println(inArray[0]);
                switch (inArray[0].toLowerCase()) {
                    case "quit": {
                        Rm.cont = false;
                        return;
                    }
                    case "load": {
                        //rm.load(fileName(inArray), programName(inArray));
                        String programName = programName(inArray);
                        if (programName != null && programName.length() == 2) {
                            //rm.load(programName);
                            Rm.addResource(Type.VARTOTOJO_ATMINTIS, programName, null);
                        } else {
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
                        int id = programId(inArray);
                        if (id < 0) {
                            System.out.println("Incorrect id");
                        } else {
                            Rm.addResource(Type.PROGRAM_START, "" + id, null);
                        }
                        break;
                    }
                    case "kill": {
                        int id = programId(inArray);
                        if (id < 0) {
                            System.out.println("Incorrect Id");
                            break;
                        } else {
                            MIKOSProcess process = Utils.findProcess("ProcessKiller");
                            Resource r = new Resource();
                            r.content = "" + id;
                            process.RES.add(r);
                            Rm.addResource(Type.PROGRAM_KILL, "", null);
                        }
                        break;
                    }
                    case "show": {
                        String programName = programName(inArray);
                        if (programName != null)
                            Rm.showBlock(programName);
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "showcs": {
                        String programName = programName(inArray);
                        if (programName != null)
                            Rm.showCseg(Integer.parseInt(programName));
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "showds": {
                        String programName = programName(inArray);
                        if (programName != null)
                            Rm.showDseg(Integer.parseInt(programName));
                        else {
                            System.out.println("No program name given");
                        }
                        break;
                    }
                    case "step": {
                        Rm.stepMode = true;
                    }
                }
            }
        }

    }

    private static void printHelp() {
        System.out.println("• Help - Prints this message;");
        System.out.println("• Load - Loads a program from HDD to real memory -p - Program name (up to 2 characters; Must be unique)");
        System.out.println("• Start - Starts a program. -p - id");
        System.out.println("• Kill - Kills a loaded program. -p - program id.");
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
                if (array.length > i + 1)
                    return array[i + 1];
            }
        }
        return null;
    }

    private static int programId(String[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i].equals("-p")) {
                if (array.length > i + 1)
                    return Integer.parseInt(array[i + 1]);
            }
        }
        return -1;
    }

}