package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

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
            System.out.println("Type Checking completed successfully.");
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
        new TypeCheck(inputName);

        //Second tested input file
        //String inputName = "input.txt";
        //new TypeCheck(inputName);

        //Third tested input file
        //String inputName = "input2.txt";
        //new TypeCheck(inputName);
    }*/
}
