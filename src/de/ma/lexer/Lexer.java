package de.ma.lexer;


public class Lexer {
	private char peek = ' ';
	private int inputIndex = 0;
//	final ArrayList<String> variables = new ArrayList<>();	// vielleicht nicht nötig
	String input;

	public void setInput(String s) {
		input = s;
		inputIndex = 0;
//		variables.clear();
	}

	void readch() {
		if(inputIndex < input.length()){
			peek = (char) input.charAt(inputIndex);
			inputIndex++;
		}else{
			peek = '.';
		}
	}

	boolean readch(char c) {
		readch();
		if (peek != c){
			inputIndex--;
			return false;
		}
		peek = ' ';
		return true;
	}

	public Token scan() {
		do {
			readch();
		} while (peek == ' ' || peek == '\t' || peek == '\r' || peek == '\n');
		
		if(peek == '.'){
			return Word.eof;
		}
		
		/* hier werden Token erkannt, die aus mehr als einem Zeichen bestehen */
		
		switch (peek) {
		case '-':
			if (readch('>'))
				return Word.implication;
			else
				return new Token('-');
		case '~':
			if (readch('>'))
				return Word.negimplication;
			else
				return new Token('~');
		case '<':
			if (readch('-')){
				if (readch('>'))
					return Word.bicondition;
				else
					inputIndex--;
			}
			return new Token('<');
		}
		
		/* hier werden Variblen erkannt */

		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(peek);
				readch();
			} while (Character.isLetter(peek));
			if(peek != '.')
				inputIndex--;
			String s = b.toString();
//			variables.add(s);		// Wort eintragen
			Word w = new Word(s, Tag.VAR);
			return w;
		}
		
		/* jedes andere Zeichen bildet ein eigenes Token */

		Token tok = new Token(peek);
		peek = ' ';
		return tok;
	}
}
