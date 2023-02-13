
package main;

import input.options.ConsoleFromUser;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to G.P.U.P!\n\n");
        ConsoleFromUser u = new ConsoleFromUser();
        try{
        u.run();
    }
       catch (Exception e){}
    }
}
 /*
 ProcessBuilder builder = new ProcessBuilder(
//                    "cmd.exe", "/c", "javac C:\\Users\\yagil\\Desktop/HelloWorld.java");
//            builder.redirectErrorStream(true);
//            Process p = builder.start();
//            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while (true) {
//                line = r.readLine();
//                if (line == null) {
//                    break;
//                }
//                System.out.println(line);
  */
