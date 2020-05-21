package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.File;
import java.io.FileWriter;

public class ValueCheck {

    Node tree;

    ValueCheck(String inputName){
        TypeCheck typeCheck = new TypeCheck(inputName);

        tree = typeCheck.getTree();

        /*if(tree.setDefiniteValues()){
            System.out.println("Value Errors detected, please fix and run compiler again.");
            System.exit(0);
        } else{
            System.out.println("Value Checking completed successfully.");
        }*/

        boolean checkOne = tree.setDefiniteValues();
        //boolean checkTwo = tree.valueChecks();

        if(checkOne /*|| checkTwo*/){
            System.out.println("Value Errors detected, please fix and run compiler again.");
            System.exit(0);
        } else{
            //System.out.println("Value Checking completed successfully. Generated ValueCheck.txt with details.");
            checkOne = tree.setDefiniteValues();
            boolean checkTwo = tree.valueChecks();

            if(checkOne || checkTwo){
                System.out.println("Value Errors detected, please fix and run compiler again.");
                System.exit(0);
            } else{
                System.out.println("Value Checking completed successfully. Generated ValueCheck.txt with details.");
            }
        }

        tree.reassignSymbolTable();

        //tree.justPrint("", false);

        try {
            File fileOutput = new File("ValueCheck.txt");
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
        String inputName = "input.txt";
        new ValueCheck(inputName);

        //Second tested input file
        //String inputName = "input1.txt";
        //new ValueCheck(inputName);
    }*/
}
