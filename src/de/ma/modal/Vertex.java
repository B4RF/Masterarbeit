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
	
	public boolean isomorphic(Vertex v) {
		if(this.getEdges().size() != v.getEdges().size())
			return false;
		
		if(this.getEdges().size() == 0)
			return true;
		
		ArrayList<Integer> edges2 = new ArrayList<>();
		edges2.addAll(v.getEdges());
		boolean matched;
		
		for (Integer index1 : this.getEdges()) {
			matched = false;
			Vertex e1 = this.getGraph().getVertex(index1);
			
			for (Integer index2 : edges2) {
				Vertex e2 = v.getGraph().getVertex(index2);
				
				if(e1.isomorphic(e2)){
					edges2.remove(index2);
					matched = true;
					break;
				}
			}
			
			if(!matched)
				return false;
		}
		
		return true;
	}
}
