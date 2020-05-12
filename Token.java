package MyPackage;

// Author: Constantinos Pavlo Andrianatos
// Student number: 17173389

public class Token {
    token_type type;
    String value;
    int line;
    int col;

    public enum token_type{
        KEYWORD, VAR, OPERATOR_OR_SEPARATOR, INT, NEGATIVE_INT, STRING
    }

    Token(String v){
        value = v;
    }

    @Override
    public String toString(){
        String s = "Type: " + type + ", Value: " + value + "\n";
        return s;
    }
}
