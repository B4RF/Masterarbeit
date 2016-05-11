package de.ma.modal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GenerateGraphs {
	int maxDegree;
	int diameter;
	int maxVertices;
	int graphIndex;
	ArrayList<Graph> genGraphs = new ArrayList<>();
	
	public GenerateGraphs(int maxD, int diam){
		maxDegree = maxD;
		diameter = diam;
		maxVertices = 1;
		
		// number of vertices for full graph
		for (int i = 0; i <= diameter/2; i++) {
			maxVertices += Math.pow(maxDegree-1, i);
		}
		// number of vertices for full graph with initVertix degree+1		
//		for (int i = 1; i <= diameter/2; i++) {
//			maxVertices += (maxDegree*Math.pow(maxDegree-1, i-1));
//		}
		
		graphIndex = 0;
		computeGraphs();
	}

	public Graph nextGraph(){
		if (graphIndex < genGraphs.size()) {
			return genGraphs.get(graphIndex++);
		}else{
			return null;
		}
	}
	
	private void computeGraphs() {
		for (int curVertices = 0; curVertices <= maxVertices; curVertices++) {
			
			try {
				System.out.println("gentreeg.exe -D" + maxDegree + " -Z0:" + diameter + " " + curVertices);
				Process process = Runtime.getRuntime().exec("gentreeg.exe -D" + maxDegree + " -Z0:" + diameter + " " + curVertices);
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "CP852"));
				String line;
				String lastLine = ""; // to remove duplicates with two nodes
				
			    while ((line = in.readLine()) != null){
			        System.out.println(line);
			        if(!line.equals(lastLine))
			        	genGraphs.add(sparse6ToGraph(line));
			        lastLine = line;
			    }
			    in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Graph sparse6ToGraph(String line) {
		line = line.substring(1); // remove ":"
		int numberOfVertices;
		
        // read and remove the number of nodes
		if (line.startsWith("~~")) {
			// eight bytes
			String binary = "";
			for (int i = 2; i < 8; i++) {
				int ascii = (int) line.charAt(i);
				ascii -= 63;
				String shortBinary = Integer.toBinaryString(ascii);
				
				// auf 6 bits verlängern
				while(shortBinary.length() < 6)
					shortBinary = "0" + shortBinary;
				
				binary += shortBinary;
			}
			
			numberOfVertices = Integer.parseInt(binary, 2);
			line = line.substring(8);
		} else if(line.startsWith("~")) {
			// four bytes
			String binary = "";
			for (int i = 1; i < 4; i++) {
				int ascii = (int) line.charAt(i);
				ascii -= 63;
				String shortBinary = Integer.toBinaryString(ascii);
				
				// auf 6 bits verlängern
				while(shortBinary.length() < 6)
					shortBinary = "0" + shortBinary;
				
				binary += shortBinary;
			}
			
			numberOfVertices = Integer.parseInt(binary, 2);
			line = line.substring(4);
		} else {
			// one byte
			int ascii = (int) line.charAt(0);
			numberOfVertices = ascii - 63;
			line = line.substring(1);
		}
		
		// create graph with "numberOfVertices" vertices
		Graph graph = new Graph();
		for (int i = 0; i < numberOfVertices; i++) {
			graph.addVertex(i);
		}
		graph.setInitVertex(0);
		
		String binary = "";
		for (char c : line.toCharArray()) {
			int ascii = (int) c;
			ascii -= 63;
			
			String shortBinary = Integer.toBinaryString(ascii);
			
			// auf 6 bits verlängern
			while(shortBinary.length() < 6)
				shortBinary = "0" + shortBinary;
			
			binary += shortBinary;
		}
		
		// number of bits needed for a target vertex
		int k = Integer.toBinaryString(numberOfVertices-1).length();
		
		int target = 0;
		for (int i = 0; i < (binary.length()-k); i+=k+1) {
			if (binary.charAt(i) == '1') {
				target++;
			}
			
			String binaryOrigin = binary.substring(i+1, i+1+k);
			int origin = Integer.parseInt(binaryOrigin, 2);
			
			if (origin > target){
				target = origin;
			} else {
				if(!graph.addEdge(origin, target))
					break;
			}
		}
		
		return graph;
	}
}
