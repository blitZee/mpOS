import java.util.ArrayList;
import java.util.Scanner;
import Rm.Rm;
import com.sun.xml.internal.fastinfoset.util.StringArray;

/**
 * Created by blitZ on 3/8/2017.
 */
public class main {

    public static void main(String[] args) {
        Rm rm = new Rm();
        Scanner scanner = new Scanner(System.in);
        boolean work = true;
        System.out.println("Hello. Welcome to MikOS. Type \"help\" to view the command list");
        while(work){
            String input = scanner.nextLine();
            String[] inArray = input.split(" ");
            System.out.println(inArray[0]);
            switch(inArray[0].toLowerCase()){
                case "quit": {
                    work = false;
                    break;
                }
                case "load": {
                    rm.load(fileName(inArray), programName(inArray));
                    break;
                }
                case "save": {
                    throw new UnsupportedOperationException();
                }
                case "help": {
                    printHelp();
                }
                case "start": {

                }
                case "clear": {

                }
                case "test": {
                    new test(rm).testSf();
                    break;
                }

            }
        }
        System.out.println("Exiting");
    }

    private static void printHelp(){
        System.out.println("• Help - Prints this message;");
        System.out.println("• Load - Loads a program from disk to external memory. -f - File name, -p - Program name (up to 2 characters; Must be unique)");
        System.out.println("• Start - Starts a program. -p - Program name");
        System.out.println("• Clear - Clears a loaded program. -p - Program name. Type \"all\" instead to clear everything.");
        System.out.println("• Quit - Shuts down the OS");
    }

    private static String fileName(String[] array){
        for(int i = 1; i < array.length; i++ ){
            if(array[i].equals("-f"))
                return array[i+1];
        }
        return null;
    }
    private static String programName(String[] array){
        for(int i = 1; i < array.length; i++ ){
            if(array[i].equals("-p"))
                return array[i+1];
        }
        return null;
    }
}
