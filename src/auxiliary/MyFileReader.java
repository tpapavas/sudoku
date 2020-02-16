package auxiliary;

import java.io.*;

public class MyFileReader implements Serializable {
    BufferedReader in;

    public MyFileReader(String filepath) {
        try {
            in = new BufferedReader(new FileReader(filepath));
        } catch (Exception e) {
            System.out.println("Error within fileReader constructor");
        }
    }

    public void closeReader() {
        try {
            in.close();
        } catch (Exception e) {
            System.out.println("Error closing fileReader");
        }
    }

    public void skipBlock() {
        try {
            while(!(in.readLine().equals("")))
                continue;
        } catch (IOException e) {
            System.out.println("Finished file");
        }
    }

    public BufferedReader getReader(){ return in; }
}
