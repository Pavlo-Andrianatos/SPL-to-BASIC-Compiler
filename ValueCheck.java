package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

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

        if(tree.setDefiniteValues() || tree.valueChecks()){
            System.out.println("Value Errors detected, please fix and run compiler again.");
            System.exit(0);
        } else{
            System.out.println("Value Checking completed successfully.");
        }

        tree.reassignSymbolTable();

        //tree.justPrint("", false);
    }

    public Node getTree(){
        return tree;
    }

    /*public static void main(String[] args) {
        //First tested input file
        String inputName = "input.txt";
        new ValueCheck(inputName);

        //Second tested input file
        //String inputName = "input.txt";
        //new ValueCheck(inputName);

        //Third tested input file
        //String inputName = "input2.txt";
        //new ValueCheck(inputName);

        //Fourth tested input file
        //String inputName = "input1.txt";
        //new ValueCheck(inputName);
    }*/
}
