package de.ma.modal;

import java.util.ArrayList;

public class Vertex {
	private final Graph graph;
	private final int index;
	private final ArrayList<Integer> edges = new ArrayList<>();
	
	public Vertex(Graph g, int i){
		graph = g;
		index = i;
	}

	public Graph getGraph() {
		return graph;
	}

	public int getIndex() {
		return index;
	}
	
	public ArrayList<Integer> getEdges(){
		return edges;
	}
	
	public boolean addEdge(int i){
		if(hasEdge(i))
			return false;
		else
			return edges.add(i);
	}
	
	public void removeEdge(Integer i){
		edges.remove(i);
	}
	
	public boolean hasEdge(int i){
		return edges.contains(i);
	}
	
	public Vertex clone(){
		Vertex v = new Vertex(getGraph(), index);
		for (int edge : edges) {
			v.addEdge(edge);
		}
		return v;
	}
	
	public int getDepth(){
		int depth = 0;
		
		for (Integer e : getEdges()) {
			depth = Math.max(depth, 1+getGraph().getVertex(e).getDepth());
		}
		
		return depth;
	}
}
