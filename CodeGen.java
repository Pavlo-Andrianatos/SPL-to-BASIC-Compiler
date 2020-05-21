package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGen {

    Node tree;

    CodeGen(String inputName){
        ValueCheck valueCheck = new ValueCheck(inputName);

        tree = valueCheck.getTree();

        try {
            File fileOutput = new File("testBASIC.txt");
            FileWriter writer = new FileWriter(fileOutput);

            tree.writeToBASICPhaseOne(tree, writer);

            writer.write("H_0 " + Node.basicLineNumber + " END");

            writer.flush();
            writer.close();

            String stringBASIC = new String(Files.readAllBytes(Paths.get("testBasic.txt")));

            tree.writeToBASICPhaseTwo(stringBASIC);

            System.out.println("BASIC translation completed successfully. Generated finalBASIC.txt with details.");
        } catch (Exception e){
            e.printStackTrace();
        }
        //tree.justPrint("", false);
    }

    public static void main(String[] args) {
        //First tested input file
        String inputName = "f.txt";
        new CodeGen(inputName);

        //Second tested input file
        //String inputName = "input1.txt";
        //new CodeGen(inputName);
    }
}
