package de.ma.modal;

import java.awt.GridLayout;
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
import javax.swing.SwingConstants;

public class GenerateGraphs {
	int maxDegree;
	int diameter;

	int curInitVertex;
	int curVertices;
	int maxVertices;
	boolean reflexive;
	boolean transitive;
	boolean serial;
	boolean partReflexive;
	boolean useOrbits;
	BufferedReader genReader;
	BufferedReader dirReader;

	boolean preDecode;
	Graph curGraph;

	String lastLine = ""; // remove duplicates at 2 vertices

	JFrame frame;
	JProgressBar progress;

	// TODO with o
	String directgCmd = "directg.exe -T";

	public GenerateGraphs(int maxD, int diam, int maxV, boolean ref, boolean trans, boolean ser, boolean partRef, boolean orb) {
		this.maxDegree = maxD;
		this.diameter = diam;
		this.reflexive = ref;
		this.transitive = trans;
		this.serial = ser;
		this.useOrbits = orb;

		this.maxVertices = maxV;
		// number of vertices for full graph
		if (maxVertices == 0) {
			for (int i = 0; i <= diameter / 2; i++) {
				maxVertices += Math.pow(maxDegree, i);
			}
		}

		if (reflexive)
			this.partReflexive = false;
		else
			this.partReflexive = partRef;
		this.curVertices = 0;

		frame = new JFrame("In progress...");
		frame.setLayout(new GridLayout(2, 0));
		JLabel l = new JLabel("Generating satisfying graphs.", SwingConstants.CENTER);
		frame.add(l);
		
		JPanel p = new JPanel();
		int sum = 0;
		
		for (int i = 1; i <= maxVertices; i++) {
			try {
				Process gengCount = Runtime.getRuntime().exec("geng.exe -cD" + (maxDegree+1) + " -u " + i);
				BufferedReader reader = new BufferedReader(new InputStreamReader(gengCount.getErrorStream()));
				
				reader.readLine();
				String[] output = reader.readLine().split(" ");
				sum += Integer.parseInt(output[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		progress = new JProgressBar(0, sum);
		p.add(progress);
		frame.add(p);
		frame.setSize(350, 100);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		try {
			generateGraphs();

			Process directg = Runtime.getRuntime().exec(directgCmd);

			dirReader = new BufferedReader(new InputStreamReader(directg.getInputStream()));
			BufferedWriter dirWriter = new BufferedWriter(new OutputStreamWriter(directg.getOutputStream()));
			dirWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Graph nextGraph() {
		boolean nextTree = false;
		String graph;
		String digraph;

		try {
			// do as long as graphs with more vertices exists
			do {
				// do as long as no vertex needs to be added
				while (!nextTree) {
					
					// check for generated digraphs with different init vertex
					if (!preDecode && (curInitVertex < curVertices)) {
						
						if (initNextGraph())
							return curGraph;

					// TODO partially reflexive graphs
					} else {

						// check for directed graphs
						if ((digraph = dirReader.readLine()) != null) {

							decode(digraph);

						} else {

							// check for undirected graphs
							if ((graph = genReader.readLine()) != null) {
								
								progress.setValue(progress.getValue()+1);

								if (!graph.equals(lastLine)) {
									Process directg = Runtime.getRuntime().exec(directgCmd);
									BufferedWriter dirWriter = new BufferedWriter(
											new OutputStreamWriter(directg.getOutputStream()));
									dirReader = new BufferedReader(new InputStreamReader(directg.getInputStream()));
									dirWriter.write(graph + "\n");
									dirWriter.close();
								}

								lastLine = graph;
							} else {
								nextTree = true;
							}
						}
					}
				}
				nextTree = false;
			} while(generateGraphs());

		} catch (IOException e) {
			e.printStackTrace();
		}

		// no more graphs available
		return null;
	}

	private boolean initNextGraph() {
		if (useOrbits && (curGraph.getOrbit(curInitVertex) != curInitVertex)) {
			curInitVertex++;
			return false;
		}
		curGraph.setInitVertex(curInitVertex);
		curInitVertex++;

		// remove graphs with higher max degree
		// remove graphs which have unreachable vertices
		// remove graphs with depth higher than modal depth
		if ((curGraph.getMaxDegree() <= maxDegree) && curGraph.allVertReach()
				&& (curGraph.getDepth() <= diameter / 2)) {

			// TODO vielleicht transitive hülle nutzen
			if (transitive) {
				// uRv & vRw -> uRw
				for (Integer u : curGraph.getVertices()) {
					Vertex uVertex = curGraph.getVertex(u);

					for (Integer v : uVertex.getEdges()) {
						Vertex vVertex = curGraph.getVertex(v);

						for (Integer w : vVertex.getEdges()) {

							if (!curGraph.containsEdge(u, w)) {
								return false;
							}
						}
					}
				}
			}

			if (serial) {
				for (Integer index : curGraph.getVertices()) {
					Vertex v = curGraph.getVertex(index);

					if (v.getEdges().isEmpty()) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean generateGraphs() {
		try {
			curVertices++;
			if (curVertices <= maxVertices) {
				Process geng = Runtime.getRuntime().exec("geng.exe -cD" + (maxDegree+1) + " " + curVertices);
				genReader = new BufferedReader(new InputStreamReader(geng.getInputStream()));

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

		if (reflexive) {
			for (Integer index : graph.getVertices()) {
				graph.addEdge(index, index);
			}
		}

		curGraph = graph;
		if(useOrbits)
			calculateOrbits();
		curInitVertex = 0;
	}

	private void calculateOrbits() {
		try {
			Process dreadnaut = Runtime.getRuntime().exec("dreadnaut.exe");

			BufferedWriter dreadWriter = new BufferedWriter(new OutputStreamWriter(dreadnaut.getOutputStream()));
			BufferedReader dreadReader = new BufferedReader(new InputStreamReader(dreadnaut.getInputStream()));

			// directed graphs as input
			dreadWriter.write("d\n");

			int n = curGraph.getVertices().size();
			dreadWriter.write("n=" + n + " g\n");

			// input edges of the current graph
			for (int i = 0; i < n; i++) {
				for (Integer edge : curGraph.getVertex(i).getEdges()) {
					dreadWriter.write(edge + "\n");
				}

				if (i == n - 1)
					dreadWriter.write(".\n");
				else
					dreadWriter.write(";\n");
			}

			// execute and output orbits
			dreadWriter.write("x o\n");
			dreadWriter.close();

			String line;
			String orbits = null;
			while ((line = dreadReader.readLine()) != null) {
				orbits = line;
			}

			for (String s : orbits.split(";")) {

				int orbit = -1;
				for (String t : s.split(" ")) {
					
					if (!t.isEmpty() && !t.startsWith("(")) {

						if (t.contains(":")) {

							String[] range = t.split(":");
							int start = Integer.parseInt(range[0]);
							int end = Integer.parseInt(range[1]);

							for (int index = start; index <= end; index++) {
								if (orbit == -1) {
									orbit = index;
								}

								curGraph.addOrbit(index, orbit);
							}
						} else {
							int index = Integer.parseInt(t);

							if (orbit == -1) {
								orbit = index;
							}

							curGraph.addOrbit(index, orbit);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
