package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.File;
import java.io.FileWriter;

public class Scoping {
    Node tree;

    Scoping(String inputName){
        LL1Parser ll1 = new LL1Parser(inputName);
        tree = ll1.getTree();

        tree.assignScope();

        tree.adjustVariableNames();

        tree.undefinedVariables();

        tree.adjustProcedureNames();

        tree.undefinedProcedures();

        tree.reassignSymbolTable();

        try {
            File fileOutput = new File("Scoping.txt");
            FileWriter writer = new FileWriter(fileOutput);

            tree.print("", false, writer);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        //tree.justPrint("", false);
        System.out.println("Scoping completed successfully. Generated Scoping.txt with details");
    }

    public Node getTree(){
        return tree;
    }

    public static void main(String[] args) {
        //First tested input file
        String inputName = "input.txt";
        new Scoping(inputName);

        //Second tested input file
        //String inputName = "input1.txt";
        //new Scoping(inputName);
    }
}
