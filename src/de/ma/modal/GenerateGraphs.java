package de.ma.modal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GenerateGraphs {
	int maxDegree;
	int diameter;

	int curInitVertex;
	int curVertices;
	int maxVertices;
	boolean reflexive;
	boolean transitive;
	boolean serial;
	BufferedReader genReader;
	BufferedReader dirReader;

	boolean preDecode;
	Graph curGraph;

	String lastLine = ""; // remove duplicates at 2 vertices
	
	JFrame frame;
	JProgressBar progress;

	public GenerateGraphs(int maxD, int diam, boolean ref, boolean trans, boolean ser) {
		maxDegree = maxD + 1; // because of incoming edges
		diameter = diam;
		reflexive = ref;
		transitive = trans;
		serial = ser;
		curVertices = 0;

		maxVertices = 0;
		// number of vertices for full graph
		for (int i = 0; i <= diameter / 2; i++) {
			maxVertices += Math.pow(maxDegree - 1, i);
		}
		
		frame = new JFrame("In progress...");
		JLabel l = new JLabel("generating possible graphs");
		JPanel p = new JPanel();
		int sum = maxVertices*(maxVertices+1)/2;
		progress = new JProgressBar(0, sum);
		
		p.add(progress);
		frame.add(l);
		frame.add(p);
		frame.setSize(300, 100);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

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

	public Graph nextGraph() {
		boolean nextTree = false;

		try {
			do {
				while (!nextTree) {
					
					// check for full generated graphs with different init vertex
					if (!preDecode && (curInitVertex < curVertices)) {
						// System.out.println("init");
						if (initNextGraph())
							return curGraph;

					} else {
						
						// check for directed graphs
						String directed = dirReader.readLine();
						if (directed != null) {
							// System.out.println("decode");
							decode(directed);

						} else {
							
							// check for undirected graphs
							String gentree = genReader.readLine();
							if (gentree != null) {
								// System.out.println("generate");
								if (!gentree.equals(lastLine)) {
									Process directg = Runtime.getRuntime().exec("directg.exe -oT");
									BufferedWriter dirWriter = new BufferedWriter(
											new OutputStreamWriter(directg.getOutputStream()));
									dirReader = new BufferedReader(new InputStreamReader(directg.getInputStream()));
									dirWriter.write(gentree + "\n");
									dirWriter.close();
								}

								lastLine = gentree;
							} else {
								nextTree = true;
							}
						}
					}
				}
				nextTree = false;
			} while (generateTrees()); // generate graphs with one more vertex

		} catch (IOException e) {
			e.printStackTrace();
		}

		// no more graphs available
		return null;
	}

	private boolean initNextGraph() {
		curGraph.setInitVertex(curInitVertex);
		curInitVertex++;

		// remove graphs with maxdegree in init vertex (no incoming edge
		// therefore one too many)
		// remove graphs with depth higher than modal depth
		// remove graphs which have unreachable vertices
		if ((curGraph.getInitVertex().getEdges().size() != maxDegree) && (curGraph.getDepth() <= diameter / 2)
				&& curGraph.allVertReach()) {
			
			if(reflexive){
				for (Integer index : curGraph.getVertices()) {
					Vertex v = curGraph.getVertex(index);

					if (!v.hasEdge(v.getIndex())){
						return false;
					}
				}
			}
			
			//TODO vielleicht transitive hülle nutzen
			if(transitive){
				// uRv & vRw -> uRw
				for (Integer u : curGraph.getVertices()) {
					Vertex uVertex = curGraph.getVertex(u);

					for (Integer v : uVertex.getEdges()) {
						Vertex vVertex = curGraph.getVertex(v);
						
						for (Integer w : vVertex.getEdges()) {
							
							if(!curGraph.containsEdge(u, w)){
								return false;
							}
						}
					}
				}
			}
			
			if(serial){
				for (Integer index : curGraph.getVertices()) {
					Vertex v = curGraph.getVertex(index);

					if (v.getEdges().isEmpty()){
						return false;
					}
				}
			}
			
			return true;
		} else {
			return false;
		}
	}

	private boolean generateTrees() {
		try {
			curVertices++;
			progress.setValue(progress.getValue()+curVertices);
			if (curVertices <= maxVertices) {
				// System.out.println("gentreeg.exe -D" + maxDegree + " -Z0:" +
				// diameter + " " + curVertices);
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
		frame.dispose();
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
