package de.ma.lexer;


public class Word extends Token {
	public String lexeme = "";

	public Word(String s, int tag) {
		super(tag);
		lexeme = s;
	}
	
	public String getVarible(){
		return lexeme;
	}

	public static final Word
		eof = new Word(".", Tag.EOF),
		implication = new Word("->", Tag.IMPLICATION),
		negimplication = new Word("~>", Tag.NEGIMPLICATION),
		bicondition = new Word("<->", Tag.BICONDITION);
}
