package de.ma.modal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class Graph {
	private Integer initialVertex;
	private final Map<Integer, Vertex> vertices = new HashMap<>();

	public Vertex getInitVertex() {
		return vertices.get(initialVertex);
	}

	public void setInitVertex(int index) {
		if (vertices.containsKey(index))
			initialVertex = index;
	}

	public HashSet<Integer> getVertices() {
		return new HashSet<Integer>(vertices.keySet());
	}

	public boolean addVertex(int index) {
		if (containsVertex(index))
			return false;

		vertices.put(index, new Vertex(this, index));
		return true;
	}

	public Vertex getVertex(int index) {
		return vertices.get(index);
	}

	public boolean containsVertex(int index) {
		return vertices.containsKey(index);
	}

	public Vertex removeVertex(int index) {
		if (index == initialVertex)
			initialVertex = null;
		return vertices.remove(index);
	}

	public boolean addEdge(int origin, int target) {
		if (!containsVertex(origin) || !containsVertex(target))
			return false;

		Vertex v1 = getVertex(origin);
		return v1.addEdge(target);
	}

	public boolean containsEdge(int origin, int target) {
		if (!containsVertex(origin) || !containsVertex(target))
			return false;

		Vertex v1 = getVertex(origin);

		if (v1.hasEdge(target))
			return true;

		return false;
	}

	public void removeEdge(int origin, int target) {
		Vertex v1 = getVertex(origin);

		if (containsEdge(origin, target))
			v1.removeEdge(target);
	}

	public Graph clone() {
		Graph g = new Graph();

		for (int index : vertices.keySet()) {
			g.addVertex(index);
			for (Integer edge : getVertex(index).getEdges()) {
				g.getVertex(index).addEdge(edge);
			}
		}
		if (initialVertex != null)
			g.setInitVertex(initialVertex);

		return g;
	}

	public boolean isomorphic(Graph g) {
		if (this.getInitVertex() == null || g.getInitVertex() == null)
			return false;

		return this.getInitVertex().isomorphic(g.getInitVertex());
	}

	public int getDepth() {
		HashMap<Integer, Integer> distance = new HashMap<>();
		Stack<Integer> s = new Stack<>();

		if (initialVertex == null)
			return 0;

		distance.put(initialVertex, 0);
		s.push(initialVertex);

		while (!s.isEmpty()) {
			int v = s.pop();

			for (Integer e : getVertex(v).getEdges()) {
				if (!distance.containsKey(e)) {
					int curD = distance.get(v);
					distance.put(e, curD + 1);
					s.push(e);
				}
			}

		}

		int d = 0;

		for (Integer i : distance.values()) {
			d = Math.max(d, i);
		}

		return d;
	}

	public boolean allVertReach() {
		// DFS
		HashSet<Integer> discovered = new HashSet<>();
		Stack<Integer> s = new Stack<>();

		if (initialVertex == null)
			return false;
		s.push(initialVertex);

		while (!s.isEmpty()) {
			int v = s.pop();
			if (!discovered.contains(v)) {
				discovered.add(v);
				for (Integer e : getVertex(v).getEdges()) {
					s.push(e);
				}
			}
		}

		if (discovered.containsAll(getVertices()))
			return true;

		return false;
	}
}
