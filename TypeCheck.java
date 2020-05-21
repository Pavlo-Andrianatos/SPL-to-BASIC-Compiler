package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.File;
import java.io.FileWriter;

public class TypeCheck {

    Node tree;

    TypeCheck(String inputName){
        Scoping scoping = new Scoping(inputName);

        tree = scoping.getTree();

        tree.setDefiniteTypes();

        if(tree.typeChecks()){
            System.out.println("Type Errors detected, please fix and run compiler again.");
            System.exit(0);
        } else{
            System.out.println("Type Checking completed successfully. Generated TypeCheck.txt with details.");
        }

        tree.reassignSymbolTable();

        //tree.justPrint("", false);

        try {
            File fileOutput = new File("TypeCheck.txt");
            FileWriter writer = new FileWriter(fileOutput);

            tree.print("", false, writer);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Node getTree(){
        return tree;
    }

    /*public static void main(String[] args) {
        //First tested input file
        String inputName = "b.txt";
        new TypeCheck(inputName);

        //Second tested input file
        //String inputName = "input1.txt";
        //new TypeCheck(inputName);
    }*/
}
