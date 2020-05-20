package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

import java.io.*;
import java.util.ArrayList;

public class Lexer {
    //Array declarations for keywords, separators, operators, numbers, and alphabet
    String[] keywords = new String[19];
    Character[] operatorsAndSeparators = new Character[12];
    Character[] alphabet = new Character[26];
    Character[] numbers = new Character[9];
    ArrayList tokens = new ArrayList<Token>();

    Lexer(String inputName){
        //Initializations for keywords, separators, operators, numbers, and alphabet
        initializeKeywords();
        initializeSeparatorsAndOperators();
        initializeNumbers();
        initializeAplhabet();

        //Calls the lex function which is where the lexing happens
        lex(inputName);
    }

    public ArrayList<Token> getTokens(){
        return tokens;
    }

    public void initializeKeywords(){
        keywords[0] = "add";
        keywords[1] = "and";
        keywords[2] = "bool";
        keywords[3] = "else";
        keywords[4] = "eq";
        keywords[5] = "for";
        keywords[6] = "halt";
        keywords[7] = "if";
        keywords[8] = "input";
        keywords[9] = "mult";
        keywords[10] = "not";
        keywords[11] = "num";
        keywords[12] = "or";
        keywords[13] = "output";
        keywords[14] = "proc";
        keywords[15] = "string";
        keywords[16] = "sub";
        keywords[17] = "then";
        keywords[18] = "while";
    }

    public void initializeSeparatorsAndOperators(){
        operatorsAndSeparators[0] = '<';
        operatorsAndSeparators[1] = '>';
        operatorsAndSeparators[2] = '(';
        operatorsAndSeparators[3] = ')';
        operatorsAndSeparators[4] = '{';
        operatorsAndSeparators[5] = '}';
        operatorsAndSeparators[6] = '=';
        operatorsAndSeparators[7] = ',';
        operatorsAndSeparators[8] = ';';
        operatorsAndSeparators[9] = 'F';
        operatorsAndSeparators[10] = 'T';
        operatorsAndSeparators[11] = '0';
    }

    public void initializeNumbers(){
        numbers[0] = '1';
        numbers[1] = '2';
        numbers[2] = '3';
        numbers[3] = '4';
        numbers[4] = '5';
        numbers[5] = '6';
        numbers[6] = '7';
        numbers[7] = '8';
        numbers[8] = '9';
    }

    public void initializeAplhabet(){
        alphabet[0] = 'a';
        alphabet[1] = 'b';
        alphabet[2] = 'c';
        alphabet[3] = 'd';
        alphabet[4] = 'e';
        alphabet[5] = 'f';
        alphabet[6] = 'g';
        alphabet[7] = 'h';
        alphabet[8] = 'i';
        alphabet[9] = 'j';
        alphabet[10] = 'k';
        alphabet[11] = 'l';
        alphabet[12] = 'm';
        alphabet[13] = 'n';
        alphabet[14] = 'o';
        alphabet[15] = 'p';
        alphabet[16] = 'q';
        alphabet[17] = 'r';
        alphabet[18] = 's';
        alphabet[19] = 't';
        alphabet[20] = 'u';
        alphabet[21] = 'v';
        alphabet[22] = 'w';
        alphabet[23] = 'x';
        alphabet[24] = 'y';
        alphabet[25] = 'z';
    }

    //Function that checks the alphabet array to see if there is something that
    //matches the character that was passed in
    public String checkForLetter(char character){
        for (Character value : alphabet) {
            if (value == character) {
                return value.toString();
            }
        }
        return null;
    }

    //Function that checks the keywords array to see if there is something that
    //matches the string that was passed in
    public String checkForKeywords(String str){
        for (String keyword : keywords) {
            if (keyword.equals(str)) {
                return keyword;
            }
        }
        return null;
    }

    //Function that checks the operatorsAndSeparators array to see if there is something that
    //matches the character that was passed in
    public String checkForOperatorOrSeparator(char character){
        for (Character operatorsAndSeparator : operatorsAndSeparators) {
            if (operatorsAndSeparator == character) {
                return operatorsAndSeparator.toString();
            }
        }
        return null;
    }

    //Function that checks the numbers array to see if there is something that
    //matches the character (number) that was passed in
    public String checkForNumber(char character){
        for (Character number : numbers) {
            if (number == character) {
                return number.toString();
            }
        }
        return null;
    }

    public void lex(String inputName){
        //Make the input and output file objects
        File file = new File(inputName);
        try {
            File fileOutput = new File("LexerOutput.txt");

            FileWriter writer = new FileWriter(fileOutput);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String str; // Stores each line read
            int maxLine; // Figure out how long each line is
            int i = 0; // Used for each character in the line
            boolean addToI; // If we need to add to I to skip characters
            int temp = 0; // Will add this to I if need be to skip characters
            int counter = 1; // For the number of each token
            int lineCounter = 1; // used for error handling for correct line number
            int lineColumn = 1;

            //While loop that will read each line
            while((str = br.readLine()) != null){
                //Skips line if empty
                if(str.isEmpty()){
                    lineCounter++;
                    continue;
                }
                maxLine = str.length();
                //While loop that checks each character in the line
                while (i < maxLine) {
                    addToI = false;
                    //Check if the character is a letter
                    if(checkForLetter(str.charAt(i)) != null){
                        temp = i;
                        //Figure out the length of the input (longest match is used)
                        while(temp < maxLine && (str.charAt(temp) == '0' || checkForLetter(str.charAt(temp)) != null || checkForNumber(str.charAt(temp)) != null)){
                            temp++;
                        }
                        String stringTemp = checkForKeywords(str.substring(i, temp));

                        //If it is a keyword then it will recognize it as a keyword token
                        //else it must be a variable
                        if(stringTemp != null){
                            Token t = new Token(stringTemp);
                            t.type = Token.token_type.KEYWORD;
                            t.line = lineCounter;
                            t.col = lineColumn;
                            tokens.add(t);
                            writer.write(counter + ":" + stringTemp + " (tok_" + stringTemp + ")");
                        } else{
                            Token t = new Token(str.substring(i, temp));
                            t.type = Token.token_type.VAR;
                            t.line = lineCounter;
                            t.col = lineColumn;
                            tokens.add(t);
                            writer.write(counter + ":" + str.substring(i, temp) + " (tok_var)");
                        }
                        writer.write(System.lineSeparator());
                        counter++;
                        //Add to I so that we skip over a certain number of characters
                        addToI = true;
                    } else if(checkForOperatorOrSeparator(str.charAt(i)) != null){
                        //Check if an operator or separator and write that to the output.txt file
                        String stringTemp = checkForOperatorOrSeparator(str.charAt(i));
                        if(str.charAt(i) == '0'){
                            if((i + 1) < maxLine && (checkForNumber(str.charAt(i + 1)) != null || str.charAt(i + 1) == '0')){
                                System.out.println("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: numbers cannot start with 0");
                                writer.write("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: numbers cannot start with 0");
                                writer.flush();
                                writer.close();
                                System.out.println("Error encountered with number, please check LexerOutput.txt for details");
                                System.exit(0);
                            } else{
                                Token t = new Token(stringTemp);
                                t.type = Token.token_type.INT;
                                t.line = lineCounter;
                                t.col = lineColumn;
                                tokens.add(t);
                                writer.write(counter + ":" + (str.charAt(i)) + " (tok_int)");
                                writer.write(System.lineSeparator());
                                counter++;
                            }
                        } else{
                            Token t = new Token(stringTemp);
                            t.type = Token.token_type.OPERATOR_OR_SEPARATOR;
                            t.line = lineCounter;
                            t.col = lineColumn;
                            tokens.add(t);
                            writer.write(counter + ":" + (str.charAt(i)) + " (tok_operator or separator)");
                            writer.write(System.lineSeparator());
                            counter++;
                        }
                    } else if(checkForNumber(str.charAt(i)) != null){
                        //Check if a number
                        temp = i;
                        //Check how long the number is (longest match used)
                        while(temp < maxLine && (checkForNumber(str.charAt(temp)) != null || str.charAt(temp) == '0')){
                            temp++;
                        }
                        String stringTemp = str.substring(i, temp);
                        Token t = new Token(stringTemp);
                        t.type = Token.token_type.INT;
                        t.line = lineCounter;
                        t.col = lineColumn;
                        tokens.add(t);
                        writer.write(counter + ":" + str.substring(i, temp) + " (tok_int)");
                        writer.write(System.lineSeparator());
                        counter++;
                        //Add to I so that we skip over a certain number of characters
                        addToI = true;
                    } else if(str.charAt(i) == '-'){
                        //Check if a negative number
                        if(checkForNumber(str.charAt(i+1)) != null){
                            temp = i;
                            temp++;
                            //Check how long number is (longest match is used)
                            while(temp < maxLine && (checkForNumber(str.charAt(temp)) != null || str.charAt(temp) == '0')){
                                temp++;
                            }
                            String stringTemp = "-" + str.substring(i+1, temp);
                            Token t = new Token(stringTemp);
                            t.type = Token.token_type.NEGATIVE_INT;
                            t.line = lineCounter;
                            t.col = lineColumn;
                            tokens.add(t);
                            writer.write(counter + ":" + str.substring(i, temp) + " (tok_int)");
                            writer.write(System.lineSeparator());
                            counter++;
                            //Add to I so that we skip over a certain number of characters
                            addToI = true;
                        }
                    } else if(str.charAt(i) == '"'){
                        //Check if a string
                        int numCharacters = 0;
                        //Check how long string is
                        while(str.charAt((i + 1) + numCharacters) != '"'){
                            numCharacters++;
                            if((((i + 1) + numCharacters) >= str.length()) || str.charAt((i + 1) + numCharacters) == ';'){
                                System.out.println("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: strings must be closed");
                                writer.write("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: strings must be closed");
                                writer.flush();
                                writer.close();
                                System.out.println("Error encountered with string, please check LexerOutput.txt for details");
                                System.exit(0);
                            }
                        }
                        String substring = str.substring(i, (i + 1) + numCharacters + 1);
                        //If string is longer that 8 characters then report an error and stop program
                        //else write token to output.txt
                        if(numCharacters > 8){
                            System.out.println("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: " + "'" + substring + "' strings have at most 8 characters");
                            writer.write("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: " + "'" + substring + "' strings have at most 8 characters");
                            writer.flush();
                            writer.close();
                            System.out.println("Error encountered with string, please check LexerOutput.txt for details");
                            System.exit(0);
                        } else{
                            for (int m = 1; m < substring.length()-1; m++){
                                if(checkForLetter(substring.charAt(m)) == null){
                                    if(checkForNumber(substring.charAt(m)) == null) {
                                        if(substring.charAt(m) != '0') {
                                            if(substring.charAt(m) != ' ') {
                                                System.out.println("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: " + "'" + substring + "' string has illegal character");
                                                writer.write("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: " + "'" + substring + "' string has illegal character");
                                                writer.flush();
                                                writer.close();
                                                System.out.println("Error encountered with string, please check LexerOutput.txt for details");
                                                System.exit(0);
                                            }
                                        }
                                    }
                                }
                            }
                            if(str.charAt((i + 1) + numCharacters) == '"'){
                                Token t = new Token(substring);
                                t.type = Token.token_type.STRING;
                                t.line = lineCounter;
                                t.col = lineColumn;
                                tokens.add(t);
                                writer.write(counter + ":" + substring + " (tok_str)");
                                writer.write(System.lineSeparator());
                                counter++;
                            }
                            //Add to I so that we skip over a certain number of characters
                            addToI = true;
                            temp = ((i + 1) + numCharacters + 1);
                        }
                    } else if(str.charAt(i) != ' '){
                        //If the character that is read in is not a white space
                        //then report an error and stop the program
                        System.out.println("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: " + "'" + str.charAt(i) + "' is not a valid character");
                        writer.write("Lexical Error [line: " + lineCounter + ", col: " + lineColumn + "]: " + "'" + str.charAt(i) + "' is not a valid character");
                        writer.flush();
                        writer.close();
                        System.out.println("Error encountered with invalid character, please check LexerOutput.txt for details");
                        System.exit(0);
                    }

                    if(addToI){
                        i = temp;
                        lineColumn = temp;
                    } else{
                        i++;
                        //lineColumn++;
                    }
                    lineColumn++;
                }
                i = 0;
                lineCounter++;
                lineColumn = 0;
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Lexer completed successfully. Generated LexerOutput.txt with details.");
    }

    /*public static void main(String[] args) {
        //First tested input file
        String inputName = "input.txt";
        new Lexer(inputName);

        //Second tested input file
        //String inputName = "input.txt";
        //new Lexer(inputName);

        //Third tested input file
        //String inputName = "input2.txt";
        //new Lexer(inputName);

        //Fourth tested input file
        //String inputName = "input1.txt";
        //new Lexer(inputName);
    }*/
}