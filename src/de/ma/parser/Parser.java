package de.ma.parser;

import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.ma.lexer.Lexer;
import de.ma.lexer.Tag;
import de.ma.lexer.Token;
import de.ma.tree.And;
import de.ma.tree.Biconditional;
import de.ma.tree.Binary;
import de.ma.tree.Box;
import de.ma.tree.Bracket;
import de.ma.tree.Constant;
import de.ma.tree.Diamond;
import de.ma.tree.Implication;
import de.ma.tree.Negimplication;
import de.ma.tree.Node;
import de.ma.tree.Not;
import de.ma.tree.Or;
import de.ma.tree.Unary;
import de.ma.tree.Variable;
import de.ma.tree.Xor;

public class Parser {
	private final Lexer lex;
	private Token look;

	private final Stack<Node> stack = new Stack<Node>();
	
	public Parser(Lexer l) {
		lex = l;
	}

	/* move liest das naechste Token und speichert es in look */

	void move() {
		look = lex.scan();
	}

	public Node formula(String input) {
		lex.setInput(input);
		stack.clear();

		move();

		while (look.tag != Tag.EOF) {

			if (look.tag != ')') {
				// symbol in stack pushen
				Node node = null;
				
				switch (look.tag) {
				case '(':
					stack.push(new Bracket(look));
					break;
				case '~':
					stack.push(new Not(look, null));
					break;
				case '#':
					stack.push(new Box(look, null));
					break;
				case '$':
					stack.push(new Diamond(look, null));
					break;
				case Tag.BICONDITION:
					stack.push(new Biconditional(look, null, null));
					break;
				case Tag.IMPLICATION:
					stack.push(new Implication(look, null, null));
					break;
				case Tag.NEGIMPLICATION:
					stack.push(new Negimplication(look, null, null));
					break;
				case '+':
					stack.push(new Xor(look, null, null));
					break;
				case '|':
					stack.push(new Or(look, null, null));
					break;
				case '&':
					stack.push(new And(look, null, null));
					break;
				case Tag.VAR:
					node = checkUnarys(new Variable(look));
					stack.push(node);
					break;
				case '0':
					node = checkUnarys(new Constant(look, false));
					stack.push(node);
					break;
				case '1':
					node = checkUnarys(new Constant(look, true));
					stack.push(node);
					break;
				default:
					showErrorMessage();
					throw new Error("syntax error");
				}
			} else { // stack leeren bis (
				Node node = stack.pop();

				if (stack.peek().getToken().tag != '(')
					node = opRekursion(node);

				stack.pop(); // die klammer entfernen

				node = checkUnarys(node);
				stack.push(node);
			}

			move();
		}

		// restliche operatoren im stack abarbeiten

		Node node = stack.pop();

		if (!stack.isEmpty())
			node = opRekursion(node);

		return node;
	}

	private Node checkUnarys(Node node) {
		while (!stack.isEmpty() && stack.peek().getClass().getSuperclass().equals(Unary.class)) {
			Unary u = (Unary) stack.pop();
			u.setNode(node);
			node = u;
		}

		return node;
	}

	// verursacht ausfuehrungsreihenfolge von links nach rechts
	private Node opRekursion(Node right) {
		
		try {
			Binary op = (Binary) stack.pop();
			
			Node left = stack.pop();

			op.setRight(right);
			if (!stack.isEmpty() && stack.peek().getToken().tag != '(')
				left = opRekursion(left);
			op.setLeft(left);

			return op;
		} catch (Exception e) {
			showErrorMessage();
			throw new Error("syntax error");
		}
	}

	private void showErrorMessage() {
		JOptionPane.showMessageDialog(new JFrame(), "Syntax error", "Couldn't parse the formula.", JOptionPane.ERROR_MESSAGE);
	}
}
