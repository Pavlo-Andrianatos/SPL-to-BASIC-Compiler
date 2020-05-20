package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    static int nextID = 0;
    int id = -1;
    ArrayList<Node> children = new ArrayList<Node>();
    NodeType nodeType;
    String name;
    HashMap<Integer, ArrayList<String>> hMap;
    String scope;
    Node parent;
    String type;
    String value;

    Token token;

    boolean writtenToBASIC = false;

    static int variableCounter = 0;
    static int procedureCounter = 0;

    public enum NodeType {
        TNode, NNode
    }

    static int basicLineNumber = 10;

    static int gotoNumber = 0;

    // Constructors
    Node(String name, NodeType nodeType, HashMap<Integer, ArrayList<String>> hMap, Token token){
        this.name = name;
        this.nodeType = nodeType;
        this.hMap = hMap;
        this.scope = "0";
        this.type = "";
        this.token = token;
        this.value = "No-Value";
    }

    Node(String name, NodeType nodeType, Node children, HashMap<Integer, ArrayList<String>> hMap, Token token){
        this.name = name;
        this.nodeType = nodeType;
        this.children.add(children);
        this.hMap = hMap;
        this.scope = "0";
        this.type = "";
        this.token = token;
        this.value = "No-Value";
    }

    Node(String name, NodeType nodeType, ArrayList<Node> children, HashMap<Integer, ArrayList<String>> hMap, Token token){
        this.name = name;
        this.nodeType = nodeType;
        this.children = children;
        this.hMap = hMap;
        this.scope = "0";
        this.type = "";
        this.token = token;
        this.value = "No-Value";
    }

    //Assigns unique ID to each Node
    public void assignID(Node parent){
        this.id = nextID++;
        ArrayList arr = new ArrayList<String>();
        arr.add(this.name);
        arr.add(this.scope);
        arr.add(this.type);
        arr.add(this.value);
        hMap.put(this.id, arr);
        this.parent = parent;
        for (Node n : children){
            n.assignID(this);
        }
    }

    //Makes tree an AST by removing unnecessary nodes
    public void makeAST(){
        for (int i = 0; i < children.size(); i++){
            if(children.get(i).children.size() == 0 && children.get(i).nodeType == NodeType.NNode){
                children.remove(i);
            } else{
                children.get(i).makeAST();
            }
        }
    }

    //Assigns scope to each node
    public void assignScope(){
        if(parent != null){
            this.scope = parent.scope;
        }
        if(this.name.equals("PROGTWO")){
            int temp = Integer.parseInt(this.scope);
            temp++;
            this.scope = temp + "";
        }
        ArrayList arr = new ArrayList<String>();
        arr.add(this.name);
        arr.add(this.scope);
        arr.add(this.type);
        arr.add(this.value);
        hMap.replace(this.id, arr);
        for (Node n : children){
            n.assignScope();
        }
    }

    public void adjustVariableNames(){
        if(this.name.equals("NAME")){
            String updateName = "V" + variableCounter;
            adjustVariableNamesHelper(this, this.children.get(0).name, updateName, this.children.get(0).scope, this.children.get(0).id);
            variableCounter++;
        }
        for (Node n : children){
            n.adjustVariableNames();
        }
    }

    public void adjustVariableNamesHelper(Node saveNode, String saveName, String updateName, String scope, int id){
        Node temp = saveNode;
        //if(scope.equals("0")){
            while(!temp.name.equals("PROG") /*&& !temp.name.equals("PROGTWO")*/){
                temp = temp.parent;
            }
        /*} else{
            while(!temp.name.equals("CODE") /*&& !temp.name.equals("PROGTWO")*//*{
                temp = temp.parent;
            }*/
        //}*/

        adjustVariableNamesHelper2(temp, saveName, updateName, id);

        /*while(!temp.name.equals("PROG") /*&& !temp.name.equals("PROGTWO")){
            temp = temp.parent;
        }

        adjustVariableNamesHelper2(temp, saveName, updateName, id);*/
    }

    public void adjustVariableNamesHelper2(Node temp, String saveName, String updateName, int id){
        if(temp.parent != null && temp.parent.parent != null) {
            if (!temp.parent.parent.name.equals("CALL") && !temp.parent.name.equals("PROC")) {
                if (temp.id >= id && temp.name.equals(saveName)) {
                    temp.name = updateName;
                }
            }
        }
        for (Node n : temp.children){
            n.adjustVariableNamesHelper2(n, saveName, updateName, id);
        }
    }

    public void undefinedVariables(){
        if(this.name.equals("VAR")){
            if(!this.parent.name.equals("CALL")){
                if(!this.children.get(0).name.matches("[V][0-9]+")){
                    if(!this.children.get(0).name.equals("0") && !this.children.get(0).name.equals("1")){
                        this.children.get(0).name = "U";
                    }
                }
            }
        }
        for (Node n : children){
            n.undefinedVariables();
        }
    }

    public void adjustProcedureNames(){
        if(this.name.equals("PROC")){
            String updateName = "P" + procedureCounter;
            adjustProcedureNamesHelper(this, this.children.get(1).name, updateName, this.children.get(1).id, Integer.parseInt(this.children.get(1).scope));
            procedureCounter++;
        }
        for (Node n : children){
            n.adjustProcedureNames();
        }
    }

    public void adjustProcedureNamesHelper(Node saveNode, String saveName, String updateName, int id, int scope){
        Node temp = saveNode;
        while(!temp.name.equals("PROG")){
            if(temp.parent == null){
                break;
            }
            temp = temp.parent;
        }

        adjustProcedureNamesHelper2(temp, saveName, updateName, id, scope);

        /*while(!temp.name.equals("PROG")){
            if(temp.parent == null){
                break;
            }
            temp = temp.parent;
        }*/

        //adjustProcedureNamesHelper2(temp, saveName, updateName, id, scope);

        //adjustProcedureNamesHelper3(saveNode, saveName, updateName);
    }

    public void adjustProcedureNamesHelper2(Node temp, String saveName, String updateName, int id, int scope){
        if(temp.name.equals("CALL")) {
            int tempScope = Integer.parseInt(temp.scope);
            if (!temp.children.get(0).children.get(0).name.matches("[P][0-9]")) {
                if (temp.children.get(0).children.get(0).name.equals(saveName) /*&& temp.children.get(0).children.get(0).id <= id*/) {
                    //if(!temp.name.matches("[P][0-9]")){
                    temp.children.get(0).children.get(0).name = updateName;
                    //}
                } else if(tempScope <= scope && temp.children.get(0).children.get(0).name.equals(saveName)){
                    temp.children.get(0).children.get(0).name = updateName;
                }
            } /*else if (tempScope >= scope && temp.name.equals(saveName)) {
                //if(this.id >= id){
                    //temp.name = updateName;
                //}
            //}*/
        }
        if(temp.id == id && temp.name.equals(saveName)){
            temp.name = updateName;
        }
        for (Node n : temp.children){
            n.adjustProcedureNamesHelper2(n, saveName, updateName, id, scope);
        }
    }

    public void adjustProcedureNamesHelper3(Node temp, String saveName, String updateName){
        if((!this.parent.name.equals("NAME") && !this.parent.name.equals("VAR")) || this.parent.parent.name.equals("CALL")) {
            if (temp.name.equals(saveName)) {
                temp.name = updateName;
            }
        }
        for (Node n : temp.children){
            n.adjustProcedureNamesHelper3(n, saveName, updateName);
        }
    }

    public void undefinedProcedures(){
        if(this.name.equals("CALL")){
            if(!this.children.get(0).children.get(0).name.matches("[P][0-9]+")){
                this.children.get(0).children.get(0).name = "U";
            }
        }
        for (Node n : children){
            n.undefinedProcedures();
        }
    }

    public void reassignSymbolTable(){
        ArrayList arr = new ArrayList<String>();
        arr.add(this.name);
        arr.add(this.scope);
        arr.add(this.type);
        arr.add(this.value);
        hMap.replace(this.id, arr);
        for (Node n : children){
            n.reassignSymbolTable();
        }
    }

    public void setDefiniteTypes(){
        //if(!this.type.equals("")){
        //    return;
        //}
        if(this.name.equals("PROC")){
            this.type = "P";
            this.children.get(1).type = "P";
            setVariableTypeHelper(this.children.get(1).name, "P");
        }

        if(this.name.equals("TYPE")){
            switch(this.children.get(0).name){
                case "num":
                    this.type = "N";
                    this.children.get(0).type = "N";
                    this.parent.children.get(1).type = "N";
                    this.parent.children.get(1).children.get(0).type = "N";
                    setVariableTypeHelper(this.parent.children.get(1).children.get(0).name, "N");
                    break;
                case "string":
                    this.type = "S";
                    this.children.get(0).type = "S";
                    this.parent.children.get(1).type = "S";
                    this.parent.children.get(1).children.get(0).type = "S";
                    setVariableTypeHelper(this.parent.children.get(1).children.get(0).name, "S");
                    break;
                case "bool":
                    this.type = "B";
                    this.children.get(0).type = "B";
                    this.parent.children.get(1).type = "B";
                    this.parent.children.get(1).children.get(0).type = "B";
                    setVariableTypeHelper(this.parent.children.get(1).children.get(0).name, "B");
                    break;
                default:
                    System.out.println("ERROR with type of VAR");
                    break;
            }
        }

        if(this.name.equals("NUMEXPR")) {
            if(this.children.get(0).name.equals("VAR")){
                if(this.children.get(0).type.equals("N")){
                    this.type = "N";
                }
            }
            if (this.children.get(0).name.matches("[0-9]+")) {
                this.type = "N";
                this.children.get(0).type = "N";
                if (this.parent.name.equals("ASSIGNTWO")) {
                    this.parent.type = "N";
                }
            }
            if(this.children.get(0).name.equals("CALC")){
                if(this.children.get(0).type.equals("N")){
                    this.type = "N";
                }
            }
            if(this.children.get(0).name.equals("0") || this.children.get(0).name.equals("1")){
                this.children.get(0).type = "N";
                this.type = "N";
            }
        }

        if(this.name.equals("CALC")){
            this.children.get(1).setDefiniteTypes();
            this.children.get(2).setDefiniteTypes();
            if(this.children.get(1).type.equals("N") && this.children.get(2).type.equals("N")){
                this.type = "N";
                this.children.get(0).type = "N";
                this.parent.type = "N";
            }
        }

        if(this.name.equals("ASSIGNTWO")){
            if(this.children.get(0).name.equals("VAR")){
                if(this.children.get(0).children.get(0).name.equals("0") || this.children.get(0).children.get(0).name.equals("1")){
                    this.children.get(0).children.get(0).type = "N";
                    this.children.get(0).type = "N";
                    this.type = "N";
                }
            }
            if(this.children.get(0).name.equals("NUMEXPR")){
                if(this.children.get(0).children.get(0).name.equals("VAR")) {
                    if (this.children.get(0).children.get(0).children.get(0).name.equals("0") || this.children.get(0).children.get(0).children.get(0).name.equals("1")) {
                        this.children.get(0).children.get(0).children.get(0).type = "N";
                        this.children.get(0).children.get(0).type = "N";
                        this.children.get(0).type = "N";
                        this.type = "N";
                    }
                }
            }
            if(this.children.get(0).name.equals("NUMEXPR")){
                if(this.children.get(0).children.get(0).name.equals("CALC")) {
                    String tempType = this.children.get(0).children.get(0).children.get(1).type;
                    if(this.children.get(0).children.get(0).children.get(2).children.get(0).name.equals("1")){
                        this.children.get(0).children.get(0).children.get(2).children.get(0).type = "N";
                        this.children.get(0).children.get(0).children.get(2).type = "N";
                    }
                    if(this.children.get(0).children.get(0).children.get(2).children.get(0).name.equals("0")){
                        this.children.get(0).children.get(0).children.get(2).children.get(0).type = "N";
                        this.children.get(0).children.get(0).children.get(2).type = "N";
                    }
                    String tempType2 = this.children.get(0).children.get(0).children.get(2).type;
                    if (tempType.equals("N") && tempType2.equals("N")) {
                        this.children.get(0).children.get(0).children.get(0).type = "N";
                        this.children.get(0).children.get(0).type = "N";
                        this.children.get(0).type = "N";
                        this.type = "N";
                    }
                }
            }
        }

        if(this.name.equals("BOOL")){
            if(this.children.get(0).name.equals("T") || this.children.get(0).name.equals("F")){
                this.type = "B";
                this.children.get(0).type = "B";
            }
            if(this.children.get(0).name.equals("eq")){
                //this.children.get(1).setDefiniteTypes();
                //this.children.get(2).setDefiniteTypes();
                if(this.children.get(1).type.equals(this.children.get(2).type)){
                    this.type = "B";
                    this.children.get(0).type = "B";
                }
            }
            if(this.children.size() > 1){
                if(this.children.get(1).name.equals("BOOLCOMP")){
                    //this.children.get(0).setDefiniteTypes();
                    //this.children.get(1).children.get(1).setDefiniteTypes();
                    String tempType = this.children.get(0).type;
                    String tempType2 = this.children.get(1).children.get(1).type;
                    if(tempType.equals("N") && tempType2.equals("N")){
                        this.type = "B";
                        this.children.get(1).type = "B";
                    } else{
                        this.type = "N";
                        this.children.get(1).type = "N";
                    }
                }
            }
            if(this.children.get(0).name.equals("not")){
                this.children.get(1).setDefiniteTypes();
                if(this.children.get(1).type.equals("B")){
                    this.type = "B";
                    this.children.get(0).type = "B";
                }
            }
            if(this.children.get(0).name.equals("and")){
                this.children.get(1).setDefiniteTypes();
                this.children.get(2).setDefiniteTypes();
                String tempType = this.children.get(1).type;
                String tempType2 = this.children.get(2).type;
                if(tempType.equals(tempType2)){
                    this.type = "B";
                    this.children.get(0).type = "B";
                }
            }
            if(this.children.get(0).name.equals("or")){
                this.children.get(1).setDefiniteTypes();
                this.children.get(2).setDefiniteTypes();
                String tempType = this.children.get(1).type;
                String tempType2 = this.children.get(2).type;
                if(tempType.equals(tempType2)){
                    this.type = "B";
                    this.children.get(0).type = "B";
                }
            }
            if(this.children.get(0).name.equals("VAR")) {
                if(this.children.get(0).children.get(0).type.equals("B")){
                    if(this.type.equals("")){
                        this.type = "B";
                    }
                }
            }
        }

        for (Node n : children){
            n.setDefiniteTypes();
        }
    }

    public void setVariableTypeHelper(String saveName, String correctType){
        Node temp = this;
        while(temp.parent != null){
            temp = temp.parent;
        }
        setVariableTypeHelper2(temp, saveName, correctType);
    }

    public void setVariableTypeHelper2(Node temp, String saveName, String correctType){
        if(this.name.equals("VAR")){
            if(this.children.get(0).name.equals(saveName)){
                if(this.children.get(0).type.equals("")){
                    this.type = correctType;
                    this.children.get(0).type = correctType;
                    //this.parent.type = correctType;
                } else{
                    System.out.println("TYPE ERROR [line: " + this.token.line + ", col: " + this.token.col + "]: type already defined");
                }
            }
        }
        for (Node n : temp.children){
            n.setVariableTypeHelper2(n, saveName, correctType);
        }
    }

    public boolean typeChecks(){
        boolean errors = false;
        if(this.name.equals("IO")){
            switch (this.children.get(1).type){
                case "N":
                case "B":
                case "S":
                    break;
                default:
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).token.line + ", col: " + this.children.get(1).token.col + "]: variable needs to be a number, boolean or string");
                    errors = true;
                    break;
            }
        }

        if(this.name.equals("CALL")){
            if(!this.children.get(0).type.equals("P")){
                System.out.println("TYPE ERROR [line: " + this.children.get(0).token.line + ", col: " + this.children.get(0).token.col + "]: cannot call non P type identifier");
                errors = true;
            }
        }

        if(this.name.equals("ASSIGN")) {
            String correctType = this.children.get(0).type;

            if (this.children.get(1).children.get(0).name.startsWith("\"")) {
                if (!correctType.equals("S")) {
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: with assigning string literal");
                    errors = true;
                }
            }
            if (this.children.get(1).children.get(0).name.equals("VAR")) {
                String testType = this.children.get(1).children.get(0).type;
                if (!correctType.equals(testType)) {
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: with assigning variable");
                    errors = true;
                }
            }
            if (this.children.get(1).children.get(0).name.equals("NUMEXPR")) {
                String testType = this.children.get(1).children.get(0).children.get(0).type;
                if(!(correctType.equals("N") && testType.equals("N"))){
                    /*System.out.println(correctType);
                    System.out.println(this.children.get(1).children.get(0).name);
                    System.out.println(testType);*/
                    if(!correctType.equals(testType)){
                        System.out.println("TYPE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: with assigning integer literal");
                        errors = true;
                    }
                }
            }
            if(this.children.get(1).children.get(0).name.equals("BOOL")){
                String testType = this.children.get(1).children.get(0).type;
                if(!(correctType.equals("B") && testType.equals("B"))){
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: with assigning boolean");
                    errors = true;
                }
            }
        }

        if(this.name.equals("COND_BRANCH")){
            if(this.children.get(1).name.equals("BOOL")){
                if(!this.children.get(1).type.equals("B")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).token.line + ", col: " + this.children.get(1).token.col + "]: check is not a boolean");
                    errors = true;
                }
            }
        }

        if(this.name.equals("COND_LOOP")){
            if(this.children.get(0).name.equals("while")){
                /*System.out.println(this.id);
                System.out.println(this.children.get(1).type);
                System.out.println(this.children.get(1).children.get(0).type);
                System.out.println(this.children.get(1).children.get(1).children.get(1).type);*/
                if(!this.children.get(1).type.equals("B")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).token.line + ", col: " + this.children.get(1).token.col + "]: check is not a boolean");
                    errors = true;
                }
            }
            if(this.children.get(0).name.equals("for")){
                String tempType = this.children.get(1).children.get(0).type;
                String tempType2 = this.children.get(1).children.get(1).children.get(0).type;
                String tempType3 = this.children.get(2).type;
                String tempType4 = this.children.get(3).children.get(1).type;
                String tempType5 = this.children.get(4).children.get(0).type;
                String tempType6 = this.children.get(4).children.get(1).children.get(0).children.get(0).children.get(1).children.get(0).type;
                if(!tempType.equals("N")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: first initialization variable is not a number expression");
                    errors = true;
                }
                if(!tempType2.equals("N")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(1).children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(1).children.get(0).token.col + "]: second initialization variable is not a number expression");
                    errors = true;
                }
                if(!tempType3.equals("N")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(2).token.line + ", col: " + this.children.get(2).token.col + "]: first condition variable is not a number expression");
                    errors = true;
                }
                if(!tempType4.equals("N")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(3).children.get(1).token.line + ", col: " + this.children.get(3).children.get(1).token.col + "]: second condition variable is not a number expression");
                    errors = true;
                }
                if(!tempType5.equals("N")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(4).children.get(0).token.line + ", col: " + this.children.get(4).children.get(0).token.col + "]: first increment variable is not a number expression");
                    errors = true;
                }
                if(!tempType6.equals("N")){
                    System.out.println("TYPE ERROR [line: " + this.children.get(4).children.get(1).children.get(0).children.get(0).children.get(1).children.get(0).token.line + ", col: " + this.children.get(4).children.get(1).children.get(0).children.get(0).children.get(1).children.get(0).token.col + "]: second increment variable is not a number expression");
                    errors = true;
                }
            }
        }

        for (Node n : children){
            boolean tempBool = n.typeChecks();
            if(tempBool){
                errors = tempBool;
            }
        }
        return errors;
    }

    public boolean setDefiniteValues(){
        boolean valueError = false;
        if(this.name.equals("IO")){
            if(this.children.get(0).name.equals("input")){
                this.value = "Has-Value";
                this.children.get(0).value = "Has-Value";
                this.children.get(1).value = "Has-Value";
                this.children.get(1).children.get(0).value = "Has-Value";
                setVariableValueHelper(this.children.get(1).children.get(0).name, "Has-Value");
                //if(this.children.get(1).type.equals("N") || this.children.get(1).type.equals("S")){
                    /*this.value = "possible_value";
                    this.children.get(0).value = "possible_value";
                    this.children.get(1).value = "possible_value";
                    this.children.get(1).children.get(0).value = "possible_value";
                    setVariableValueHelper(this.children.get(1).children.get(0).name, "possible_value", this.id);*/
                /*} else{
                    this.value = "Has-Value";
                    this.children.get(0).value = "Has-Value";
                    this.children.get(1).value = "Has-Value";
                    this.children.get(1).children.get(0).value = "Has-Value";
                    setVariableValueHelper(this.children.get(1).children.get(0).name, "Has-Value", this.id);
                }*/
            }
            /*if(this.children.get(1).type.equals("N") || this.children.get(1).type.equals("S")){
                this.value = "possible_value";
                this.children.get(0).value = "possible_value";
                this.children.get(1).value = "possible_value";
                this.children.get(1).children.get(0).value = "possible_value";
                setVariableValueHelper(this.children.get(1).children.get(0).name, "possible_value");
            } else{
                this.value = "Has-Value";
                this.children.get(0).value = "Has-Value";
                this.children.get(1).value = "Has-Value";
                this.children.get(1).children.get(0).value = "Has-Value";
                setVariableValueHelper(this.children.get(1).children.get(0).name, "Has-Value");
            }*/
        }

        if(this.name.equals("ASSIGN")){
            if(this.children.get(0).name.equals("VAR")){
                if(this.children.get(1).name.equals("ASSIGNTWO")){
                    this.children.get(1).children.get(0).setDefiniteValues();
                    if(this.children.get(1).children.get(0).name.startsWith("\"") /*&& !this.children.get(1).children.get(0).name.equals("\"\"")*/){
                        this.children.get(1).children.get(0).value = "Has-Value";
                        this.children.get(1).value = "Has-Value";
                        this.children.get(0).children.get(0).value = "Has-Value";
                        this.children.get(0).value = "Has-Value";
                        this.value = "Has-Value";
                        setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                    }
                    if(this.children.get(1).children.get(0).name.equals("VAR")){
                        if(this.children.get(1).children.get(0).children.get(0).name.equals("0") || this.children.get(1).children.get(0).children.get(0).name.equals("1")){
                            this.value = "Has-Value";
                            this.children.get(1).children.get(0).children.get(0).value = "Has-Value";
                            this.children.get(1).children.get(0).value = "Has-Value";
                            this.children.get(1).value = "Has-Value";
                            this.children.get(0).value = "Has-Value";
                            this.children.get(0).children.get(0).value = "Has-Value";
                            setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        }
                        if(this.children.get(1).children.get(0).children.get(0).value.equals("Has-Value") || this.children.get(1).children.get(0).children.get(0).value.equals("possible_value")){
                            this.value = "Has-Value";
                            this.children.get(1).value = "Has-Value";
                            this.children.get(1).children.get(0).value = "Has-Value";
                            setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        } else{
                            //System.out.println("VALUE ERROR [line: " + this.token.line + ", col: " + this.token.col + "]: cannot assign undefined variable to variable");
                            //valueError = true;
                        }
                    }
                    if(this.children.get(1).children.get(0).name.equals("NUMEXPR")){
                        if(this.children.get(1).children.get(0).children.get(0).name.matches("[0-9]+")){
                            this.value = "Has-Value";
                            this.children.get(1).value = "Has-Value";
                            this.children.get(1).children.get(0).value = "Has-Value";
                            setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        }
                        if(this.children.get(1).children.get(0).value.equals("Has-Value")){
                            this.value = "Has-Value";
                            this.children.get(0).value = "Has-Value";
                            this.children.get(0).children.get(0).value = "Has-Value";
                            setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        }
                        if(this.children.get(1).children.get(0).children.get(0).name.equals("VAR")){
                            this.value = "Has-Value";
                            this.children.get(1).value = "Has-Value";
                            this.children.get(1).children.get(0).value = "Has-Value";
                            this.children.get(1).children.get(0).children.get(0).value = "Has-Value";
                            this.children.get(1).children.get(0).children.get(0).children.get(0).value = "Has-Value";
                            //setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        }
                    }
                    if(this.children.get(1).children.get(0).name.equals("BOOL")){
                        if(this.children.get(1).children.get(0).children.get(0).name.matches("T") || this.children.get(1).children.get(0).children.get(0).name.matches("F")){
                            this.value = "Has-Value";
                            this.children.get(1).value = "Has-Value";
                            this.children.get(1).children.get(0).value = "Has-Value";
                            setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        }
                        if(this.children.get(1).children.get(0).value.equals("Has-Value")){
                            this.value = "Has-Value";
                            this.children.get(0).value = "Has-Value";
                            this.children.get(0).children.get(0).value = "Has-Value";
                            setVariableValueHelper(this.children.get(0).children.get(0).name, "Has-Value");
                        }
                    }
                }
            }
        }

        if(this.name.equals("NUMEXPR")){
            if(this.children.get(0).name.matches("[0-9]+")){
                this.value = "Has-Value";
                this.children.get(0).value = "Has-Value";
            }
            if(this.children.get(0).name.equals("VAR")){
                if(this.children.get(0).value.equals("Has-Value")){
                    this.value = "Has-Value";
                    this.children.get(0).value = "Has-Value";
                }
            }
            if(this.children.get(0).name.equals("CALC")){
                this.children.get(0).setDefiniteValues();
                if(this.children.get(0).value.equals("Has-Value")){
                    this.value = "Has-Value";
                    this.children.get(0).value = "Has-Value";
                }
            }
        }

        if(this.name.equals("CALC")){
            this.children.get(1).setDefiniteValues();
            this.children.get(2).setDefiniteValues();
            if(this.children.get(1).value.equals("Has-Value")){
                if(this.children.get(2).value.equals("Has-Value")){
                    this.value = "Has-Value";
                }
            }
        }

        if(this.name.equals("BOOL")){
            if(this.children.get(0).name.equals("eq")){
                this.children.get(1).setDefiniteValues();
                this.children.get(2).setDefiniteValues();
                if(this.children.get(1).value.equals("Has-Value")){
                    if(this.children.get(2).value.equals("Has-Value")){
                        this.value = "Has-Value";
                        this.children.get(0).value = "Has-Value";
                    }
                }
            }
            if(this.children.size() > 1) {
                if (this.children.get(0).name.equals("VAR")) {
                    if (this.children.get(1).name.equals("BOOLCOMP")) {
                        this.children.get(0).setDefiniteValues();
                        this.children.get(1).setDefiniteValues();
                        if (this.children.get(0).value.equals("Has-Value")) {
                            if (this.children.get(1).children.get(1).value.equals("Has-Value")) {
                                this.value = "Has-Value";
                                this.children.get(0).value = "Has-Value";
                            }
                        }
                    }
                }
            }
            if(this.children.get(0).name.equals("not")){
                this.children.get(1).setDefiniteValues();
                if(this.children.get(1).children.get(0).value.equals("Has-Value")){
                    this.value = "Has-Value";
                    this.children.get(0).value = "Has-Value";
                }
            }
            if(this.children.get(0).name.equals("and")){
                this.children.get(1).setDefiniteValues();
                this.children.get(2).setDefiniteValues();
                if(this.children.get(1).children.get(0).value.equals("Has-Value")){
                    if(this.children.get(2).children.get(0).value.equals("Has-Value")){
                        this.value = "Has-Value";
                        this.children.get(0).value = "Has-Value";
                    }
                }
            }
            if(this.children.get(0).name.equals("or")){
                this.children.get(1).setDefiniteValues();
                this.children.get(2).setDefiniteValues();
                if(this.children.get(1).children.get(0).value.equals("Has-Value")){
                    if(this.children.get(2).children.get(0).value.equals("Has-Value")){
                        this.value = "Has-Value";
                        this.children.get(0).value = "Has-Value";
                    }
                }
            }
            if(this.children.get(0).name.equals("T") || this.children.get(0).name.equals("F")){
                this.value = "Has-Value";
                this.children.get(0).value = "Has-Value";
            }
            if(this.children.get(0).name.equals("VAR")){
                if(this.children.get(0).value.equals("Has-Value")){
                    this.value = "Has-Value";
                    this.children.get(0).value = "Has-Value";
                }
            }
        }

        for (Node n : children){
            boolean tempBool = n.setDefiniteValues();
            if(tempBool){
                valueError = tempBool;
            }
        }
        return valueError;
    }

    public void setVariableValueHelper(String saveName, String value){
        Node temp = this;
        while(temp.parent != null){
            temp = temp.parent;
        }
        setVariableValueHelper2(temp, saveName, value);
    }

    public void setVariableValueHelper2(Node temp, String saveName, String value){
        if (this.name.equals("VAR")) {
            if (this.children.get(0).name.equals(saveName)) {
                this.value = value;
                this.children.get(0).value = value;
                this.parent.value = value;
            }
        }
        for (Node n : temp.children){
            n.setVariableValueHelper2(n, saveName, value);
        }
    }

    public boolean valueChecks(){
        boolean errors = false;
        if(this.name.equals("IO")){
            if(this.children.get(0).name.equals("output")){
                if(!this.children.get(1).value.equals("Has-Value") && !this.children.get(1).value.equals("possible_value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(1).token.line + ", col: " + this.children.get(1).token.col + "]: cannot output undefined variable");
                    errors = true;
                }
            }
        }
        if(this.name.equals("ASSIGN")){
            if(!this.children.get(1).children.get(0).value.equals("Has-Value")){
                System.out.println("VALUE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: assignment does not have value");
                errors = true;
            }
        }

        if(this.name.equals("COND_BRANCH")){
            if(!this.children.get(1).value.equals("Has-Value") && !this.children.get(1).value.equals("possible_value")){
                System.out.println("VALUE ERROR [line: " + this.children.get(1).token.line + ", col: " + this.children.get(1).token.col + "]: condition does not have value");
                errors = true;
            }
        }

        if(this.name.equals("COND_LOOP")){
            if(this.children.get(0).name.equals("while")){
                if(!this.children.get(1).value.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(1).token.line + ", col: " + this.children.get(1).token.col + "]: condition does not have value");
                    errors = true;
                }
            }
            if(this.children.get(0).name.equals("for")){
                String tempType = this.children.get(1).children.get(0).value;
                String tempType2 = this.children.get(1).children.get(1).children.get(0).value;
                String tempType3 = this.children.get(2).value;
                String tempType4 = this.children.get(3).children.get(1).value;
                String tempType5 = this.children.get(4).children.get(0).value;
                String tempType6 = this.children.get(4).children.get(1).children.get(0).children.get(0).children.get(1).children.get(0).value;
                if(!tempType.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(0).token.col + "]: first initialization variable does not have value");
                    errors = true;
                }
                if(!tempType2.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(1).children.get(1).children.get(0).token.line + ", col: " + this.children.get(1).children.get(1).children.get(0).token.col + "]: second initialization variable does not have value");
                    errors = true;
                }
                if(!tempType3.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(2).token.line + ", col: " + this.children.get(2).token.col + "]: first condition variable does not have value");
                    errors = true;
                }
                if(!tempType4.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(3).children.get(1).token.line + ", col: " + this.children.get(3).children.get(1).token.col + "]: second condition variable does not have value");
                    errors = true;
                }
                if(!tempType5.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(4).children.get(0).token.line + ", col: " + this.children.get(4).children.get(0).token.col + "]: first increment variable does not have value");
                    errors = true;
                }
                if(!tempType6.equals("Has-Value")){
                    System.out.println("VALUE ERROR [line: " + this.children.get(4).children.get(1).children.get(0).children.get(0).children.get(1).children.get(0).token.line + ", col: " + this.children.get(4).children.get(1).children.get(0).children.get(0).children.get(1).children.get(0).token.col + "]: second increment variable does not have value");
                    errors = true;
                }
            }
        }

        for (Node n : children){
            boolean tempBool = n.valueChecks();
            if(tempBool){
                errors = tempBool;
            }
        }
        return errors;
    }

    public void writeToBASICPhaseOne(Node node, FileWriter writer) throws IOException {
        if(node.writtenToBASIC){
            return;
        }
        if(node.name.equals("PROC")){
            writer.write(basicLineNumber + " GOTO " + node.children.get(1).name);
            basicLineNumber += 10;
            writer.write("\n");
            writeToBASICPhaseOne(node.children.get(2), writer);
            writer.write(node.children.get(1).name + " " + basicLineNumber + " RETURN");
            basicLineNumber += 10;
            writer.write("\n");
            node.writtenToBASIC = true;
        }
        if(node.name.equals("INSTR")){
            if(node.children.get(0).name.equals("halt")){
                writer.write(basicLineNumber + " GOTO H_0"); // Figure out how to go to end of program
                basicLineNumber += 10;
                writer.write("\n");
                node.children.get(0).writtenToBASIC = true;
            } else{
                writeToBASICPhaseOne(node.children.get(0), writer);
            }
            node.writtenToBASIC = true;
        }
        if(node.name.equals("IO")){
            if(node.children.get(0).name.equals("input")){
                if(node.children.get(1).type.equals("S")) {
                    writer.write(basicLineNumber + " INPUT");
                    writeToBASICPhaseOne(node.children.get(1).children.get(0), writer);
                    writer.write("$");
                } else{
                    writer.write(basicLineNumber + " INPUT");
                    writeToBASICPhaseOne(node.children.get(1), writer);
                    //System.out.println(writer);
                }
                node.children.get(0).writtenToBASIC = true;
            }
            if(node.children.get(0).name.equals("output")){
                if(node.children.get(1).type.equals("S")) {
                    writer.write(basicLineNumber + " PRINT");
                    writeToBASICPhaseOne(node.children.get(1), writer);
                    writer.write("$");
                } else{
                    writer.write(basicLineNumber + " PRINT");
                    writeToBASICPhaseOne(node.children.get(1), writer);
                }
                node.children.get(0).writtenToBASIC = true;
            }
            basicLineNumber += 10;
            writer.write("\n");
            node.writtenToBASIC = true;
        }
        if(node.name.equals("CALL")){
            writer.write(basicLineNumber + " GOSUB");
            writeToBASICPhaseOne(node.children.get(0), writer);
            basicLineNumber += 10;
            writer.write("\n");
            node.writtenToBASIC = true;
        }
        if(node.name.equals("VAR")){
            writer.write(" " + node.children.get(0).name);
            node.writtenToBASIC = true;
            node.children.get(0).writtenToBASIC = true;
        }
        if(node.name.equals("ASSIGN")){
            writer.write(basicLineNumber + "");
            writeToBASICPhaseOne(node.children.get(0), writer);
            writer.write(" =");
            writeToBASICPhaseOne(node.children.get(1), writer);
            node.writtenToBASIC = true;
        }
        if(node.name.equals("ASSIGNTWO")){
            if(node.children.get(0).name.startsWith("\"")){
                writer.write(" " + node.children.get(0).name);
            } else{
                writeToBASICPhaseOne(node.children.get(0), writer);
            }
            basicLineNumber += 10;
            writer.write("\n");
            node.writtenToBASIC = true;
        }
        if(node.name.equals("NUMEXPR")){
            if(node.children.get(0).name.matches("[0-9]+")){
                writer.write(" " + node.children.get(0).name);
            } else{
                writeToBASICPhaseOne(node.children.get(0), writer);
            }
            node.writtenToBASIC = true;
        }
        if(node.name.equals("CALC")){
            if(node.children.get(0).name.equals("add")){
                writeToBASICPhaseOne(node.children.get(1), writer);
                writer.write(" +");
                writeToBASICPhaseOne(node.children.get(2), writer);
                //basicLineNumber += 10;
                ////writer.write("\n");
            }
            if(node.children.get(0).name.equals("sub")){
                writeToBASICPhaseOne(node.children.get(1), writer);
                writer.write(" -");
                writeToBASICPhaseOne(node.children.get(2), writer);
                //basicLineNumber += 10;
                ////writer.write("\n");
            }
            if(node.children.get(0).name.equals("mult")){
                writeToBASICPhaseOne(node.children.get(1), writer);
                writer.write(" ^");
                writeToBASICPhaseOne(node.children.get(2), writer);
                //basicLineNumber += 10;
                ////writer.write("\n");
            }
            node.writtenToBASIC = true;
        }
        if(node.name.equals("COND_BRANCH")){
            writer.write(basicLineNumber + " IF");

            writeToBASICPhaseOne(node.children.get(1), writer);
            writer.write(" THEN GOTO G_" + gotoNumber);
            basicLineNumber += 10;
            writer.write("\n");

            if(node.children.size() > 4 && node.children.get(4).children.get(0).name.equals("else")){
                writeToBASICPhaseOne(node.children.get(4).children.get(1), writer);

                writer.write(basicLineNumber + " GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                writer.write("\n");

                writeToBASICPhaseOne(node.children.get(3), writer);
                writer.write("G_" + gotoNumber + " " + "ife ");
            } else{
                writeToBASICPhaseOne(node.children.get(3), writer);

                writer.write(basicLineNumber + " GOTO G_" + gotoNumber + "if");
                basicLineNumber += 10;
                writer.write("\n");
            }
            node.writtenToBASIC = true;
            gotoNumber += 1;
        }
        if(node.name.equals("BOOL")){
            if(node.children.get(0).name.equals("and")){
                writer.write(basicLineNumber + " IF " + node.children.get(1).children.get(0).children.get(0).name + " THEN GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                //writer.write("\n");
                writer.write(basicLineNumber + " GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                //writer.write("\n");
                writer.write(basicLineNumber + " IF " + node.children.get(2).children.get(0).children.get(0).name + " THEN GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                //writer.write("\n");
                gotoNumber += 1;
            }
            if(node.children.get(0).name.equals("or")){
                writer.write(basicLineNumber + " IF " + node.children.get(1).children.get(0).children.get(0).name + " THEN GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                //writer.write("\n");
                writer.write(basicLineNumber + " IF " + node.children.get(2).children.get(0).children.get(0).name + " THEN GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                //writer.write("\n");
                gotoNumber += 1;
            }
            if(node.children.get(0).name.equals("not")){
                writer.write(basicLineNumber + " NOT " + node.children.get(1).children.get(0).children.get(0).name);
                basicLineNumber += 10;
                //writer.write("\n");
            }
            if(node.children.get(0).name.equals("eq")){
                writer.write(basicLineNumber + " VARIABLECOMP = " + node.children.get(1).children.get(0).name + " = " + node.children.get(2).children.get(0).name);
                basicLineNumber += 10;
                //writer.write("\n");
            }
            if(node.children.get(0).name.equals("T")){
                writer.write(basicLineNumber + " 1" );
                basicLineNumber += 10;
                //writer.write("\n");
            }
            if(node.children.get(0).name.equals("F")){
                writer.write(basicLineNumber + " 0");
                basicLineNumber += 10;
                //writer.write("\n");
            }
            if(node.children.get(0).name.equals("VAR")){
                writeToBASICPhaseOne(node.children.get(0), writer);
                if(node.children.size() > 1 && node.children.get(1).name.equals("BOOLCOMP")){
                    writer.write(node.children.get(1).children.get(0).name);
                    writeToBASICPhaseOne(node.children.get(1).children.get(1), writer);
                    basicLineNumber += 10;
                    //writer.write("\n");
                }
            }
            node.writtenToBASIC = true;
        }
        if(node.name.equals("COND_LOOP")){
            if(node.children.get(0).name.equals("for")){
                writeToBASICPhaseOne(node.children.get(1), writer);

                writer.write(basicLineNumber + " VARIABLECOMP =");
                writeToBASICPhaseOne(node.children.get(2), writer);
                writer.write( " =");
                writeToBASICPhaseOne(node.children.get(3).children.get(1), writer);
                basicLineNumber += 10;
                writer.write("\n");

                writer.write(basicLineNumber + " IF VARIABLECOMP THEN GOTO G_" + gotoNumber + " for");
                basicLineNumber += 10;
                writer.write("\n");

                writeToBASICPhaseOne(node.children.get(5), writer);

                writeToBASICPhaseOne(node.children.get(4), writer);

                writer.write(basicLineNumber + " GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                writer.write("\n");
                gotoNumber += 1;
            }
            if(node.children.get(0).name.equals("while")){
                writer.write(basicLineNumber + " VARIABLECOMP =");
                writeToBASICPhaseOne(node.children.get(1).children.get(0), writer);
                node.children.get(1).writtenToBASIC = true;
                node.children.get(1).children.get(0).writtenToBASIC = true;
                writer.write( " =");
                writeToBASICPhaseOne(node.children.get(1).children.get(1), writer);
                node.children.get(1).children.get(1).writtenToBASIC = true;
                basicLineNumber += 10;
                writer.write("\n");

                writer.write(basicLineNumber + " IF VARIABLECOMP THEN GOTO G_" + gotoNumber + " while");
                basicLineNumber += 10;
                writer.write("\n");

                writeToBASICPhaseOne(node.children.get(2), writer);

                writer.write(basicLineNumber + " GOTO G_" + gotoNumber);
                basicLineNumber += 10;
                writer.write("\n");
                gotoNumber += 1;
            }
            node.writtenToBASIC = true;
            node.children.get(0).writtenToBASIC = true;
        }
        for (Node n : node.children){
            n.writeToBASICPhaseOne(n, writer);
        }
    }

    public void writeToBASICPhaseTwo(String stringBASIC) throws IOException {
        //Figure out and, or
        int range = 1;
        int tempGoToNumber = gotoNumber;
        while(tempGoToNumber > 10){
            tempGoToNumber = tempGoToNumber/10;
            range += 1;
        }
        String[] lines = stringBASIC.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            if(lines[i].contains("G_") && lines[i].contains("for")){
                String gotoCheck = lines[i].substring(lines[i].indexOf("G_"), lines[i].lastIndexOf("G_") + 2 + range);
                for(int j = i+1; j < lines.length; j++){
                    if(lines[j].contains(gotoCheck)){
                        lines[i] = lines[i].replace(gotoCheck, ((j * 10) + 20) + "");
                        lines[i] = lines[i].replace("for", "");
                        lines[i] = lines[i].trim();
                        lines[j] = lines[j].replace(gotoCheck, (i * 10) + "");
                    }
                }
            }
            if(lines[i].contains("G_") && lines[i].contains("while")){
                String gotoCheck = lines[i].substring(lines[i].indexOf("G_"), lines[i].lastIndexOf("G_") + 2 + range);
                for(int j = i+1; j < lines.length; j++){
                    if(lines[j].contains(gotoCheck)){
                        lines[i] = lines[i].replace(gotoCheck, ((j * 10) + 20) + "");
                        lines[i] = lines[i].replace("while", "");
                        lines[i] = lines[i].trim();
                        lines[j] = lines[j].replace(gotoCheck, (i * 10) + "");
                    }
                }
            }
            if(lines[i].contains("G_") && lines[i].contains("ife")){
                String gotoCheck = lines[i].substring(lines[i].indexOf("G_"), lines[i].lastIndexOf("G_") + 2 + range);
                for(int j = i-1; j >= 0; j--){
                    if(lines[j].contains(gotoCheck)){
                        if(lines[j].contains("IF")){
                            lines[i] = lines[i].replace(gotoCheck, "");
                            lines[i] = lines[i].replace("ife", "");
                            lines[i] = lines[i].trim();

                            lines[j] = lines[j].replace(gotoCheck, (i * 10) + "");
                        } else{
                            lines[i] = lines[i].replace(gotoCheck, "");
                            lines[i] = lines[i].replace("ife", "");
                            lines[i] = lines[i].trim();

                            lines[j] = lines[j].replace(gotoCheck, ((i * 10) + 10) + "");
                        }
                    }
                }
            }
            if(lines[i].contains("G_") && lines[i].contains("if")){
                String gotoCheck = lines[i].substring(lines[i].indexOf("G_"), lines[i].lastIndexOf("G_") + 2 + range);
                for(int j = i-1; j >= 0; j--){
                    if(lines[j].contains(gotoCheck)){
                        if(lines[j].contains("IF")){
                            lines[i] = lines[i].replace(gotoCheck, "");
                            lines[i] = lines[i].replace("if", "");
                            lines[i] = lines[i].trim();

                            lines[j] = lines[j].replace(gotoCheck, (i * 10) + "");
                        } else{
                            lines[i] = lines[i].replace(gotoCheck, "");
                            lines[i] = lines[i].replace("ife", "");
                            lines[i] = lines[i].trim();

                            lines[j] = lines[j].replace(gotoCheck, ((i * 10) + 10) + "");
                        }
                    }
                }
            }
            if(lines[i].contains("P") && lines[i].contains("RETURN")){
                String gotoCheck = lines[i].substring(lines[i].indexOf("P"), lines[i].indexOf(" "));
                for(int j = i-1; j >= 0; j--){
                    if(lines[j].contains(gotoCheck)){
                        if(lines[j].contains("GOSUB")){
                            lines[i] = lines[i].replace(gotoCheck, "");
                            lines[i] = lines[i].trim();

                            lines[j] = lines[j].replace(gotoCheck, (i * 10) + "");
                        } else{
                            lines[i] = lines[i].replace(gotoCheck, "");
                            lines[i] = lines[i].trim();

                            lines[j] = lines[j].replace(gotoCheck, ((i * 10) + 20) + "");
                        }
                    }
                }
            }
            if(lines[i].contains("H_0") && lines[i].contains("END")){
                for(int j = i-1; j >= 0; j--){
                    if(lines[j].contains("H_0")){
                        lines[j] = lines[j].replace("H_0", ((i * 10) + 10) + "");
                    }
                }
                lines[i] = lines[i].replace("H_0", "");
                lines[i] = lines[i].trim();
            }
        }

        File fileOutput = new File("finalBASIC.txt");
        FileWriter writer2 = new FileWriter(fileOutput);

        for (int i = 0; i < lines.length; i++){
            lines[i] = lines[i].concat("\n");
            writer2.write(lines[i]);
        }

        writer2.flush();
        writer2.close();
    }

    public void justPrint(String indentation, boolean isLast) {

        System.out.print(indentation);

        if (isLast) {
            System.out.print("└─");
            indentation += " ";
        } else {
            System.out.print("├─");
            indentation += "| ";
        }

        System.out.println(id + ":" + hMap.get(id));

        for (int i  = 0; i < children.size(); i++) {
            children.get(i).justPrint(indentation, i == children.size() - 1);
        }
    }

    //Prints a tree just like in the task 01 booklet, using algorithm given in task 03 booklet
    //Outputs to a file names PareserAST.txt
    public void print(String indentation, boolean isLast, FileWriter writer) throws IOException {
        writer.write(indentation);

        //System.out.print(indentation);

        if (isLast) {
            //System.out.print("└─");
            writer.write("└─");
            indentation += " ";
        } else {
            //System.out.print("├─");
            writer.write("├─");
            indentation += "| ";
        }

        //System.out.println(id + ":" + symbolTable.get(id));
        writer.write(id + ":" + hMap.get(id));
        writer.write(System.lineSeparator());


        for (int i  = 0; i < children.size(); i++) {
            children.get(i).print(indentation, i == children.size() - 1, writer);
        }
    }

    //Prints a simpler tree with just the id's of each node just like in the task 01 booklet
    //Outputs to a file names SimpleAST.txt
    public void simplePrint(FileWriter writer) throws IOException {
        if(children.size() > 0 && this.nodeType == NodeType.NNode){
            writer.write(id + ":");

            for (int i = 0; i < children.size()-1; i++){
                    writer.write(children.get(i).id + ",");
            }

            writer.write(children.get(children.size()-1).id + "\n");
        }

        for (int i  = 0; i < children.size(); i++) {
            children.get(i).simplePrint(writer);
        }
    }
}