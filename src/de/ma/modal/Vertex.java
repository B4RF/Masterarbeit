package de.ma.modal;

import java.util.ArrayList;

public class Vertex {
	private final int index;
	private final ArrayList<Integer> edges = new ArrayList<>();
	
	public Vertex(int i){
		index = i;
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
	
	public void removeEdge(int i){
		edges.remove(i);
	}
	
	public boolean hasEdge(int i){
		return edges.contains(i);
	}
	
	public Vertex clone(){
		Vertex v = new Vertex(index);
		for (int edge : edges) {
			v.addEdge(edge);
		}
		return v;
	}
}
