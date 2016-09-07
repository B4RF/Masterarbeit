package de.ma.modal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;

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
	boolean partReflexive;
	boolean useOrbits;
	BufferedReader genReader;
	BufferedReader dirReader;

	boolean preDecode;
	Graph curGraph;

	Graph backupGraph;
	LinkedList<ArrayList<Integer>> partitions = new LinkedList<>();
	int curPartition = 0;
	boolean fullReflexive = false;

	String lastLine = ""; // remove duplicates at 2 vertices

	JProgressBar progress;

	String directgCmd = "directg -T";

	public GenerateGraphs(JProgressBar bar, int maxD, int diam, int maxV, boolean ref, boolean trans, boolean ser, boolean partRef,
			boolean orb) {
		this.progress = bar;
		this.maxDegree = maxD;
		this.diameter = diam;
		this.reflexive = ref;
		this.transitive = trans;
		this.serial = ser;
		this.useOrbits = orb;

		this.maxVertices = maxV;

		if (reflexive)
			this.partReflexive = false;
		else
			this.partReflexive = partRef;
		this.curVertices = 0;

		int sum = 0;
		for (int i = 1; i <= maxVertices; i++) {
			try {
				Process gengCount = Runtime.getRuntime().exec("geng -cD" + (maxDegree + 1) + " -u " + i);
				BufferedReader reader = new BufferedReader(new InputStreamReader(gengCount.getErrorStream()));

				reader.readLine();
				String[] output = reader.readLine().split(" ");
				sum += Integer.parseInt(output[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		progress.setMaximum(sum);

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

					} else {

						// check for next partially reflexive graph
						if (partReflexive && !preDecode && !fullReflexive) {

							nextReflexiveGraph();

						} else {

							// check for directed graphs
							if ((digraph = dirReader.readLine()) != null) {

								decode(digraph);

							} else {

								// check for undirected graphs
								if ((graph = genReader.readLine()) != null) {

									progress.setValue(progress.getValue() + 1);

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
				}
				nextTree = false;
			} while (generateGraphs());

		} catch (IOException e) {
			e.printStackTrace();
		}

		// no more graphs available
		return null;
	}

	private boolean initNextGraph() {
		if (useOrbits && !curGraph.isOrbitRep(curInitVertex)) {
			curInitVertex++;
			return false;
		}
		curGraph.setInitVertex(curInitVertex);
		curInitVertex++;

		// remove graphs which have unreachable vertices
		// remove graphs with depth higher than modal depth
		if (curGraph.allVertReach() && (curGraph.getDepth() <= diameter / 2)) {

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

	private void nextReflexiveGraph() {
		curGraph = backupGraph.clone();

		// generate new partitions
		if (curPartition >= partitions.size()) {

			curPartition = 0;

			// start with one vertex in a partition
			if (partitions.isEmpty()) {

				ArrayList<Integer> orbits = curGraph.getOrbitGroups();

				for (Integer i : orbits) {
					ArrayList<Integer> list = new ArrayList<>();
					list.add(curGraph.getOrbitRep(i));
					partitions.add(list);
				}

				// use partitions of size n to create size n+1
			} else {

				if (partitions.getFirst().size() == curVertices) {

					fullReflexive = true;
					partitions.clear();

				} else {

					LinkedList<ArrayList<Integer>> nextPartitions = new LinkedList<>();

					for (ArrayList<Integer> partition : partitions) {

						int lastVertex = partition.get(partition.size() - 1);
						int orbit = curGraph.getOrbit(lastVertex);
						ArrayList<Integer> group = curGraph.getOrbitGroup(orbit);

						for (Integer i : group) {
							if (i > lastVertex) {
								ArrayList<Integer> copy = new ArrayList<>(partition);
								copy.add(i);
								nextPartitions.add(copy);

								break;
							}
						}

						ArrayList<Integer> orbits = curGraph.getOrbitGroups();

						for (Integer i : orbits) {
							if (i > orbit) {
								ArrayList<Integer> copy = new ArrayList<>(partition);
								copy.add(curGraph.getOrbitRep(i));
								nextPartitions.add(copy);
							}
						}
					}

					partitions = nextPartitions;
				}
			}
			// manipulate graph to next partition
		} else {

			ArrayList<Integer> partition = partitions.get(curPartition);
			int newOrbit = curGraph.getOrbitGroups().size();

			for (Integer orbit : curGraph.getOrbitGroups()) {
				ArrayList<Integer> group = curGraph.getOrbitGroup(orbit);
				ArrayList<Integer> subgroup = new ArrayList<>();

				for (Integer vertex : partition) {
					if (group.contains(vertex)) {
						subgroup.add(vertex);
						curGraph.addEdge(vertex, vertex);
					}
				}

				if (!subgroup.isEmpty() && (subgroup.size() < group.size())) {
					for (Integer vertex : subgroup) {
						curGraph.addOrbit(vertex, newOrbit);
					}

					newOrbit++;
				}
			}

			curPartition++;
			curInitVertex = 0;
		}
	}

	private boolean generateGraphs() {
		try {
			curVertices++;
			if (curVertices <= maxVertices) {
				Process geng = Runtime.getRuntime().exec("geng -cD" + (maxDegree + 1) + " " + curVertices);
				genReader = new BufferedReader(new InputStreamReader(geng.getInputStream()));

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

		if (reflexive) {
			for (Integer index : graph.getVertices()) {
				graph.addEdge(index, index);
			}
		}

		curGraph = graph;
		curInitVertex = 0;

		if (useOrbits)
			calculateOrbits();

		if (partReflexive) {
			backupGraph = curGraph.clone();
			fullReflexive = false;
		}
	}

	private void calculateOrbits() {
		try {
			Process dreadnaut = Runtime.getRuntime().exec("dreadnaut");

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

			int orbit = 0;

			for (String s : orbits.split(";")) {

				for (String t : s.split(" ")) {

					if (!t.isEmpty() && !t.startsWith("(")) {

						if (t.contains(":")) {

							String[] range = t.split(":");
							int start = Integer.parseInt(range[0]);
							int end = Integer.parseInt(range[1]);

							for (int index = start; index <= end; index++) {

								curGraph.addOrbit(index, orbit);
							}
						} else {
							int index = Integer.parseInt(t);

							curGraph.addOrbit(index, orbit);
						}
					}
				}

				orbit++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
