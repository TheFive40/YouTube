package test;

import java.io.File;
import java.net.URISyntaxException;

public class Main5 {
    public static void main(String[] args) throws URISyntaxException {

        File file = new File(Main5.class.getResource("/englishPCFG (1).ser.gz").toString());

        System.out.println(file);
    }
}

