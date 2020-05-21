package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class LL1Parser {
    Lexer lex;
    Token input;
    ArrayList<Token> tokens;

    Node tree;

    HashMap<Integer, ArrayList<String>> symbolTable;

    LL1Parser(String inputName){
        try{
            File fileOutput = new File("ParserAST.txt");
            FileWriter writer = new FileWriter(fileOutput);
            lex = new Lexer(inputName);
            symbolTable = new HashMap<>();
            tokens = lex.getTokens(); // Get's tokens from lexer
            input = tokens.get(0); // Gets first token
            tree = parseS(); // Tree returned from parsing

            tree.makeAST();

            tree.assignID(null);

            tree.print("", false, writer);
            writer.flush();
            writer.close();

            //Calls simplePrint function from the Node class
            fileOutput = new File("SimpleAST.txt");
            writer = new FileWriter(fileOutput);
            tree.simplePrint(writer);
            writer.flush();
            writer.close();

            //Outputs to a file called SymbolTable.txt with the contents of the symbolTable
            fileOutput = new File("SymbolTable.txt");
            writer = new FileWriter(fileOutput);
            int tempIndex = 0;
            for (int i : symbolTable.keySet()){
                writer.write(i + ":" + symbolTable.get(tempIndex).get(0) + "\n");
                tempIndex++;
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Node getTree(){
        return tree;
    }

    public HashMap<Integer, ArrayList<String>> getSymbolTable(){
        return symbolTable;
    }

    //Match function that is modelled after one found in book
    public void match(String s){
        if(tokens.size() == 0){
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: expected token but token list is empty, possible unbalanced brackets");
            System.exit(0);
        }
        if(!s.equals(input.value)){
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected: " + s + ", got input " + input);
            System.exit(0);
        }
        tokens.remove(0);
        if(!tokens.isEmpty()){
            input = tokens.get(0);
        }
    }

    public Node parseS(){
        switch(input.type){
            case VAR:
            case KEYWORD:
                tree = parsePROG();
                if(!tokens.isEmpty()){
                    System.out.println("Error, tokens still left to parse");
                    System.exit(0);
                } else{
                    System.out.println("Parsing completed successfully. Generated ParserAST.txt with details");
                }
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable or KEYWORD in parseS");
                System.exit(0);
        }
        return tree;
    }

    public Node parsePROG(){
        /*if(tokens.isEmpty() || input.value.equals("}")){
            Node n = new Node("PROG", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        switch(input.type){
            case VAR:
            case KEYWORD:
                n1Temp = parseCODE();
                //while(input.value.equals(";")){
                    //match(";");
                //}
                /*while(input.value.equals(";")){
                    match(";");
                }*/
                n2Temp = parsePROGTWO();
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable or KEYWORD in PROG");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);
        Node n = new Node("PROG", Node.NodeType.NNode, arr, symbolTable, temp);
        return n;
    }

    public Node parsePROGTWO(){
        Token temp = input;
        Node n1Temp = null;
        if(tokens.isEmpty() || input.value.equals("}")){//Check if empty / Nullable check
            Node n = new Node("PROGTWO", Node.NodeType.NNode, symbolTable, input);

            return n;
        }
        if (tokens.get(1).value.equals("proc")) {
            match(";");
            n1Temp = parsePROC_DEFS();
        } else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected proc in PROGTWO");
            System.exit(0);
        }
        Node n = new Node("PROGTWO", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parsePROC_DEFS(){
        /*if(tokens.isEmpty() || input.value.equals("}")){
            Node n = new Node("PROC_DEFS", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        if ("proc".equals(input.value)) {
            n1Temp = parsePROC();
            n2Temp = parsePROC_DEFSTWO();
        } else{
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected proc in PROC_DEFS");
            System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);
        Node n = new Node("PROC_DEFS", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parsePROC_DEFSTWO(){
        Token temp = input;
        Node n1Temp = null;
        if(tokens.isEmpty() || input.value.equals("}")){//Check if empty / Nullable check
            Node n = new Node("PROC_DEFSTWO", Node.NodeType.NNode, symbolTable, input);
            return n;
        }
        if ("proc".equals(input.value)) {
            n1Temp = parsePROC_DEFS();
        } else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected proc in PROC_DEFSTWO");
            System.exit(0);
        }
        Node n = new Node("PROC_DEFSTWO", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parsePROC(){
        /*if(tokens.isEmpty() || input.value.equals("}")){
            Node n = new Node("proc", Node.NodeType.TNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        String sTemp = "";
        if ("proc".equals(input.value)) {
            match("proc");
            sTemp = input.value;
            match(input.value);
            match("{");
            n1Temp = parsePROG();
            match("}");
        } else{
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected proc in PROC");
            System.exit(0);
        }
        Node n1 = new Node("proc", Node.NodeType.TNode, symbolTable, temp);
        Node n2 = new Node(sTemp, Node.NodeType.TNode, symbolTable, temp);

        ArrayList arr = new ArrayList<Node>();
        arr.add(n1);
        arr.add(n2);
        arr.add(n1Temp);

        Node n = new Node("PROC", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseCODE(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("CODE", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        switch(input.type){
            case VAR:
            case KEYWORD:
                n1Temp = parseINSTR();
                //while(input.value.equals(";")){
                    //match(";");
                //}
                /*while(input.value.equals(";")){
                    match(";");
                }*/
                n2Temp = parseCODETWO();
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable or keyword in CODE");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);

        Node n = new Node("CODE", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseCODETWO(){
        Token temp = input;
        Node n1Temp = null;
        if(tokens.isEmpty() /*|| input.value.equals(";")*/ || input.value.equals("}") || tokens.get(1).value.equals("proc")){//Check if empty / Nullable check
            Node n = new Node("CODETWO", Node.NodeType.NNode, symbolTable, input);
            return n;
        }
        switch(tokens.get(1).type){
            case VAR:
            case KEYWORD:
                match(";");
                n1Temp = parseCODE();
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable or keyword in CODETWO");
                System.exit(0);
        }
        Node n = new Node("CODETWO", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseINSTR(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("INSTR", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if(input.type == Token.token_type.VAR){
            boolean correct = false;
            if(tokens.size() == 1 || !tokens.get(1).value.equals("=") || tokens.get(1).value.equals(";")){
                n1Temp = parseCALL();
                correct = true;
            } else if(tokens.get(1).value.equals("=")){
                n1Temp = parseASSIGN();
                correct = true;
            }
            if(!correct){
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected ; or = in INSTR");
                System.exit(0);
            }
        } else{
            switch (input.value) {
                case "halt":
                    match("halt");
                    n1Temp = new Node("halt", Node.NodeType.TNode, symbolTable, temp);
                    break;
                case "input":
                case "output":
                    n1Temp = parseIO();
                    break;
                case "num":
                case "string":
                case "bool":
                    n1Temp = parseDECL();
                    break;
                case "if":
                    n1Temp = parseCOND_BRANCH();
                    break;
                case "while":
                case "for":
                    n1Temp = parseCOND_LOOP();
                    break;
                default:
                    System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected halt, input, output" +
                            ", num, string, bool, if ,while, or in INSTR");
                    System.exit(0);
            }

            Node n = new Node("INSTR", Node.NodeType.NNode, n1Temp, symbolTable, temp);

            return n;
        }
        Node n = new Node("INSTR", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseIO(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("IO", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        switch(input.value){
            case "input":
                match("input");
                n2Temp = new Node("input", Node.NodeType.TNode, symbolTable, temp);
                match("(");
                n1Temp = parseVAR();
                match(")");
                break;
            case "output":
                match("output");
                n2Temp = new Node("output", Node.NodeType.TNode, symbolTable, temp);
                match("(");
                n1Temp = parseVAR();
                match(")");
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected input or output in IO");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n2Temp);
        arr.add(n1Temp);

        Node n = new Node("IO", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseCALL(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("CALL", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if (input.type == Token.token_type.VAR) {
            n1Temp = parseVAR();
            /*while(input.value.equals(";")){
                match(";");
            }*/
        } else{
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable in CALL");
            System.exit(0);
        }
        Node n = new Node("CALL", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseDECL(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("DECL", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        Node n3Temp = null;
        switch(input.value){
            case "num":
            case "string":
            case "bool":
                n1Temp = parseTYPE();
                n2Temp = parseNAME();
                //while(input.value.equals(";")){
                    //match(";");
                //}
                n3Temp = parseDECLTWO();
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected num, string, bool in DECL");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);
        arr.add(n3Temp);

        Node n = new Node("DECL", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseDECLTWO(){
        Token temp = input;
        Node n1Temp = null;
        if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){//Check if empty / Nullable check
            Node n = new Node("DECLTWO", Node.NodeType.NNode, symbolTable, input);
            return n;
        } else if(input.type == Token.token_type.VAR){
            Node n = new Node("DECLTWO", Node.NodeType.NNode, symbolTable, input);
            return n;
        } else {
            if(!input.value.equals("num") || !input.value.equals("string") || !input.value.equals("bool")){
                Node n = new Node("DECLTWO", Node.NodeType.NNode, symbolTable, input);

                return n;
            }
            switch (tokens.get(1).value) {
                case "num":
                case "string":
                case "bool":
                    match(";");
                    n1Temp = parseDECL();
                    break;
                default:
                    System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected ;, }, a variable, num," +
                            "string, bool in DECLTWO");
                    System.exit(0);
            }
        }
        Node n = new Node("DECLTWO", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseTYPE(){
        /*if(input.type.equals(Token.token_type.VAR)){
            Node n = new Node("TYPE", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        switch(input.value){
            case "num":
                match("num");
                n1Temp = new Node("num", Node.NodeType.TNode, symbolTable, input);
                break;
            case "string":
                match("string");
                n1Temp = new Node("string", Node.NodeType.TNode, symbolTable, input);
                break;
            case "bool":
                match("bool");
                n1Temp = new Node("bool", Node.NodeType.TNode, symbolTable, input);
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected num, string, bool in TYPE");
                System.exit(0);
        }

        Node n = new Node("TYPE", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseNAME(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("NAME", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if (input.type == Token.token_type.VAR) {
            n1Temp = new Node(input.value, Node.NodeType.TNode, symbolTable, input);;
            match(input.value);
        } else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable in NAME");
            System.exit(0);
        }

        Node n = new Node("NAME", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseVAR(){
        /*if(input.value.equals(";") || input.value.equals("}") || input.value.equals("=") || input.value.equals(",") || input.value.equals("<") || input.value.equals(">")){
            Node n = new Node("VAR", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if (input.type == Token.token_type.VAR) {
            n1Temp = new Node(input.value, Node.NodeType.TNode, symbolTable, input);

            match(input.value);
        } else if((input.type == Token.token_type.INT && input.value.equals("0"))){
            n1Temp = new Node(input.value, Node.NodeType.TNode, symbolTable, input);

            match(input.value);
        }else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable or 0 in VAR");
            System.exit(0);
        }
        Node n = new Node("VAR", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseASSIGN(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("ASSIGN", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        if (input.type == Token.token_type.VAR) {
            n1Temp = parseVAR();
            match("=");
            n2Temp = parseASSIGNTWO();
        } else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable in ASSIGN");
            System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);

        Node n = new Node("ASSIGN", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseASSIGNTWO(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("ASSIGNTWO", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if(input.type == Token.token_type.VAR || (input.type == Token.token_type.INT && input.value.equals("0"))){
            if(tokens.size() == 1 || tokens.get(1).value.equals(";")){
                n1Temp = parseVAR();
                /*while(input.value.equals(";")){
                    match(";");
                }*/
            } else {
                n1Temp = parseNUMEXPR();
            }
        } else if(input.type == Token.token_type.STRING) {
            n1Temp = new Node(input.value, Node.NodeType.TNode, symbolTable, input);

            match(input.value);
        } else if(input.type == Token.token_type.INT || input.type == Token.token_type.NEGATIVE_INT){
            n1Temp = parseNUMEXPR();
        } else {
            switch (input.value) {
                case "bool":
                case "T":
                case "F":
                case "(":
                case "not":
                case "and":
                case "or":
                case "eq":
                    n1Temp = parseBOOL();
                    /*while(input.value.equals(";")){
                        match(";");
                    }*/
                    break;
                case "add":
                case "sub":
                case "mult":
                    n1Temp = parseNUMEXPR();
                    break;
                default:
                    System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable, 0," +
                            "a string, an integer number, bool , add, sub, mult in ASSIGNTWO");
                    System.exit(0);
            }
        }
        Node n = new Node("ASSIGNTWO", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseNUMEXPR(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals(")") || input.value.equals(",") || input.value.equals("}")){
            Node n = new Node("NUMEXPR", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if(input.type == Token.token_type.VAR || (input.type == Token.token_type.INT && input.value.equals("0"))){
            n1Temp = parseVAR();
        } else if(input.type == Token.token_type.INT || input.type == Token.token_type.NEGATIVE_INT) {
            n1Temp = new Node(input.value, Node.NodeType.TNode, symbolTable, input);

            match(input.value);
        } else {
            switch (input.value) {
                case "add":
                case "sub":
                case "mult":
                    n1Temp = parseCALC();
                    break;
                default:
                    System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected a variable, 0," +
                            "an integer number, add, sub, mult in NUMEXPR");
                    System.exit(0);
            }
        }
        Node n = new Node("NUMEXPR", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseCALC(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals(")") || input.value.equals(",") || input.value.equals("}")){
            Node n = new Node("CALC", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        Node n3Temp = null;
        switch(input.value){
            case "add":
                match("add");
                n1Temp = new Node("add", Node.NodeType.TNode, symbolTable, input);
                match("(");
                n2Temp = parseNUMEXPR();
                match(",");
                n3Temp = parseNUMEXPR();
                match(")");
                break;
            case "sub":
                match("sub");
                n1Temp = new Node("sub", Node.NodeType.TNode, symbolTable, input);
                match("(");
                n2Temp = parseNUMEXPR();
                match(",");
                n3Temp = parseNUMEXPR();
                match(")");
                break;
            case "mult":
                match("mult");
                n1Temp = new Node("mult", Node.NodeType.TNode, symbolTable, input);
                match("(");
                n2Temp = parseNUMEXPR();
                match(",");
                n3Temp = parseNUMEXPR();
                match(")");
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected add, sub, mult in CALC");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);
        arr.add(n3Temp);

        Node n = new Node("CALC", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseCOND_BRANCH(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("COND_BRANCH", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        Node n3Temp = null;
        Node n4Temp = null;
        Node n5Temp = null;
        if ("if".equals(input.value)) {
            match("if");
            n1Temp = new Node("if", Node.NodeType.TNode, symbolTable, input);
            match("(");
            n2Temp = parseBOOL();
            match(")");
            match("then");
            n3Temp = new Node("then", Node.NodeType.TNode, symbolTable, input);
            match("{");
            n4Temp = parseCODE();
            match("}");
            n5Temp = parseCOND_BRANCHTWO();
        } else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected if in COND_BRANCH");
            System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);
        arr.add(n3Temp);
        arr.add(n4Temp);
        arr.add(n5Temp);

        Node n = new Node("COND_BRANCH", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseCOND_BRANCHTWO(){
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){//Check if empty / Nullable check
            return new Node("COND_BRANCHTWO", Node.NodeType.NNode, symbolTable, input);
        }
        if ("else".equals(input.value)) {
            match("else");
            n1Temp = new Node("else", Node.NodeType.TNode, symbolTable, input);
            match("{");
            n2Temp = parseCODE();
            match("}");
        } else {
            System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected else in COND_BRANCHTWO");
            System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);

        Node n = new Node("COND_BRANCHTWO", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseBOOL(){
        /*if(input.value.equals(")") || input.value.equals(",")){
            Node n = new Node("BOOL", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        if(input.type == Token.token_type.VAR){
            n1Temp = parseVAR();
        } else {
            ArrayList arr = new ArrayList<Node>();
            Node n2Temp = null;
            Node n3Temp = null;
            switch (input.value) {
                case "(":
                    match("(");
                    n1Temp = parseVAR();
                    arr.add(n1Temp);
                    n2Temp = parseBOOLCOMP();
                    arr.add(n2Temp);
                    break;
                case "eq":
                    match("eq");
                    n1Temp = new Node("eq", Node.NodeType.TNode, symbolTable, input);
                    arr.add(n1Temp);
                    match("(");
                    n2Temp = parseVAR();
                    arr.add(n2Temp);
                    match(",");
                    n3Temp = parseVAR();
                    arr.add(n3Temp);
                    match(")");
                    break;
                case "not":
                    match("not");
                    n1Temp = new Node("not", Node.NodeType.TNode, symbolTable, input);
                    arr.add(n1Temp);
                    n2Temp = parseBOOL();
                    arr.add(n2Temp);
                    break;
                case "and":
                    match("and");
                    n1Temp = new Node("and", Node.NodeType.TNode, symbolTable, input);
                    arr.add(n1Temp);
                    match("(");
                    n2Temp = parseBOOL();
                    arr.add(n2Temp);
                    match(",");
                    n3Temp = parseBOOL();
                    arr.add(n3Temp);
                    match(")");
                    break;
                case "or":
                    match("or");
                    n1Temp = new Node("or", Node.NodeType.TNode, symbolTable, input);
                    arr.add(n1Temp);
                    match("(");
                    n2Temp = parseBOOL();
                    arr.add(n2Temp);
                    match(",");
                    n3Temp = parseBOOL();
                    arr.add(n3Temp);
                    match(")");
                    break;
                case "T":
                    match("T");
                    n1Temp = new Node("T", Node.NodeType.TNode, symbolTable, input);

                    Node n = new Node("BOOL", Node.NodeType.NNode, n1Temp, symbolTable, temp);

                    return n;
                    //break;
                case "F":
                    match("F");
                    n1Temp = new Node("F", Node.NodeType.TNode, symbolTable, input);

                    Node n2 = new Node("BOOL", Node.NodeType.NNode, n1Temp, symbolTable, temp);

                    return n2;
                    //break;
                default:
                    System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected (, eq, not, and, or, T, F in BOOL");
                    System.exit(0);
            }

            Node n = new Node("BOOL", Node.NodeType.NNode, arr, symbolTable, temp);

            return n;
        }

        Node n = new Node("BOOL", Node.NodeType.NNode, n1Temp, symbolTable, temp);

        return n;
    }

    public Node parseBOOLCOMP(){
        /*if(input.value.equals(")") || input.value.equals(",")){
            Node n = new Node("BOOLCOMP", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        switch(input.value){
            case "<":
                match("<");
                n1Temp = new Node("<", Node.NodeType.TNode, symbolTable, input);
                n2Temp = parseVAR();
                if(input.value.equals(";")){
                    match(";");
                } else {
                    match(")");
                }
                break;
            case ">":
                match(">");
                n1Temp = new Node(">", Node.NodeType.TNode, symbolTable, input);
                n2Temp = parseVAR();
                /*if(input.value.equals(";")){
                    match(";");
                } else {*/
                    match(")");
                //}
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected <, > in BOOLCOMP");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);

        Node n = new Node("BOOLCOMP", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    public Node parseCOND_LOOP(){
        /*if(tokens.isEmpty() || input.value.equals(";") || input.value.equals("}")){
            Node n = new Node("COND_LOOP", Node.NodeType.NNode, symbolTable, input);
            return n;
        }*/
        Token temp = input;
        Node n1Temp = null;
        Node n2Temp = null;
        Node n3Temp = null;
        Node n4Temp = null;
        Node n5Temp = null;
        Node n6Temp = null;
        Node n7Temp = null;
        Node n8Temp = null;
        Node n9Temp = null;
        Node n10Temp = null;
        Node n11Temp = null;
        switch(input.value){
            case "while":
                match("while");
                n1Temp = new Node("while", Node.NodeType.TNode, symbolTable, input);
                match("(");
                n2Temp = parseBOOL();
                match(")");
                match("{");
                n3Temp = parseCODE();
                match("}");
                ArrayList arr = new ArrayList<Node>();
                arr.add(n1Temp);
                arr.add(n2Temp);
                arr.add(n3Temp);

                Node n = new Node("COND_LOOP", Node.NodeType.NNode, arr, symbolTable, temp);

                return n;
                //break;
            case "for":
                match("for");
                n1Temp = new Node("for", Node.NodeType.TNode, symbolTable, input);
                match("(");
                n2Temp = parseASSIGN();
                //match("="); // parseASSIGN();
                //match("0"); // parseVAR();
                //n3Temp = new Node("0", Node.NodeType.TNode, symbolTable);
                match(";");
                n3Temp = parseVAR();
                n4Temp = parseBOOLCOMP();
                /*match("<");
                n4Temp = new Node("<", Node.NodeType.TNode, symbolTable);
                n5Temp = parseVAR();*/
                //match(";");
                n5Temp = parseASSIGN();
                /*match("="); // parseASSIGN();
                match("add"); // parseCALC();
                n8Temp = new Node("add", Node.NodeType.TNode, symbolTable);
                match("(");
                n9Temp = parseVAR();
                match(",");
                match("1"); // parseVAR();
                n10Temp = new Node("1", Node.NodeType.TNode, symbolTable);
                match(")");*/
                match(")");
                match("{");
                n6Temp = parseCODE();
                match("}");
                break;
            default:
                System.out.println("Syntax Error[line: " + input.line + ", col: " + input.col + "]: Expected while, for in COND_LOOP");
                System.exit(0);
        }
        ArrayList arr = new ArrayList<Node>();
        arr.add(n1Temp);
        arr.add(n2Temp);
        arr.add(n3Temp);
        arr.add(n4Temp);
        arr.add(n5Temp);
        arr.add(n6Temp);
        //arr.add(n7Temp);
        /*arr.add(n8Temp);
        arr.add(n9Temp);
        arr.add(n10Temp);
        arr.add(n11Temp);*/

        Node n = new Node("COND_LOOP", Node.NodeType.NNode, arr, symbolTable, temp);

        return n;
    }

    /*public static void main(String[] args) {
        //First tested input file
        String inputName = "input.txt";
        new LL1Parser(inputName);

        //Second tested input file
        //String inputName = "input.txt";
        //new LL1Parser(inputName);

        //Third tested input file
        //String inputName = "input2.txt";
        //new LL1Parser(inputName);

        //Fourth tested input file
        //String inputName = "input1.txt";
        //new LL1Parser(inputName);
    }*/
}
