package de.ma.modal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GenerateGraphs {
	int maxDegree;
	int diameter;
	
	int curInitVertex;
	int curVertices;
	int maxVertices;
	BufferedReader genReader;
	BufferedReader dirReader;
	
	boolean preDecode;
	Graph curGraph;

	String lastLine = ""; // remove duplicates at 2 vertices

	public GenerateGraphs(int maxD, int diam) {
		maxDegree = maxD + 1; // because of incoming edges
		diameter = diam;
		curVertices = 0;

		maxVertices = 0;
		// number of vertices for full graph
		for (int i = 0; i <= diameter / 2; i++) {
			maxVertices += Math.pow(maxDegree - 1, i);
		}

		try {
			generateTrees();

			Process directg = Runtime.getRuntime().exec("directg.exe -oT");

			dirReader = new BufferedReader(new InputStreamReader(directg.getInputStream()));
			BufferedWriter dirWriter = new BufferedWriter(new OutputStreamWriter(directg.getOutputStream()));
			dirWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Graph nextGraph(){
		try {
			// check for full generated graphs with different init vertex
//			System.out.println(curInitVertex + " " + curVertices);
			if(!preDecode && (curInitVertex < curVertices)){
//				System.out.println("init");
				return initNextGraph();
			}

			// check for directed graphs
			String directed = dirReader.readLine();
			if(directed != null){
//				System.out.println("decode");
				if(!directed.equals(lastLine))
					decode(directed);
				
				lastLine = directed;
				return nextGraph();
			}
			
			// check for undirected graphs
			String gentree = genReader.readLine();
			if (gentree != null) {
//				System.out.println("generate");
				Process directg = Runtime.getRuntime().exec("directg.exe -oT");
				BufferedWriter dirWriter = new BufferedWriter(new OutputStreamWriter(directg.getOutputStream()));
				dirReader = new BufferedReader(new InputStreamReader(directg.getInputStream()));
				dirWriter.write(gentree+"\n");
				dirWriter.close();
				
				return nextGraph();
			}
			
			// generate graphs with one more vertex
			if (generateTrees())
				return nextGraph();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// no more graphs available
		return null;
	}

	private Graph initNextGraph() {
		Graph graph = curGraph.clone();
		curGraph.setInitVertex(curInitVertex);
		curInitVertex++;
		
		// remove graphs with maxdegree in init vertex (no incoming edge
		// therefore one too many)
		// remove graphs with depth higher than modal depth
		// remove graphs which have unreachable vertices
		if ((graph.getInitVertex().getEdges().size() != maxDegree) && (graph.getDepth() <= diameter / 2)
				&& graph.allVertReach()) {
			return graph;
		}else{
			return nextGraph();
		}
	}

	private boolean generateTrees() {
		try {
			curVertices++;
			if (curVertices <= maxVertices) {
//				System.out.println("gentreeg.exe -D" + maxDegree + " -Z0:" + diameter + " " + curVertices);
				Process gentreeg = Runtime.getRuntime()
						.exec("gentreeg.exe -D" + maxDegree + " -Z0:" + diameter + " " + curVertices);
				genReader = new BufferedReader(new InputStreamReader(gentreeg.getInputStream()));

				curInitVertex = 0;
				preDecode = true;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void decode(String line) {
		preDecode = false;
		String[] strOutput = line.split(" ");
		ArrayList<Integer> output = new ArrayList<>();

		for (String str : strOutput) {
			output.add(Integer.parseInt(str));
		}

		Graph graph = new Graph();
		for (int i = 0; i < output.get(0); i++) {
			graph.addVertex(i);
		}

		for (int i = 2; i < output.get(1) * 2 + 2; i += 2) {
			graph.addEdge(output.get(i), output.get(i + 1));
		}
		
		curGraph = graph;
		curInitVertex = 0;
	}
}
