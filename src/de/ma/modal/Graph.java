package de.ma.modal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph {
	private Integer initialVertex;
	private final Map<Integer, Vertex> vertices = new HashMap<>();
	
	public Vertex getInitVertex() {
		return vertices.get(initialVertex);
	}

	public void setInitVertex(int index) {
		if(vertices.containsKey(index))
			initialVertex = index;
	}

	public HashSet<Integer> getVertices(){
		return new HashSet<Integer>(vertices.keySet());
	}
	
	public boolean addVertex(int index){
		if(containsVertex(index))
			return false;
		
		vertices.put(index, new Vertex(index));
		return true;
	}
	
	public Vertex getVertex(int index){
		return vertices.get(index);
	}
	
	public boolean containsVertex(int index){
		return vertices.containsKey(index);
	}
	
	public Vertex removeVertex(int index){
		if(index == initialVertex)
			initialVertex = null;
		return vertices.remove(index);
	}
	
	public boolean addEdge(int origin, int target){
		if(!containsVertex(origin) || !containsVertex(target))
			return false;
		
		Vertex v1 = getVertex(origin);
		return v1.addEdge(target);
	}
	
	public boolean containsEdge(int origin, int target){
		if(!containsVertex(origin) || !containsVertex(target))
			return false;
		
		Vertex v1 = getVertex(origin);
		
		if(v1.hasEdge(target))
			return true;

		return false;
	}
	
	public void removeEdge(int origin, int target){
		Vertex v1 = getVertex(origin);
		
		if(containsEdge(origin, target))
			v1.removeEdge(target);
	}
	
	public Graph clone(){
		Graph g = new Graph();
		
		for (int index : vertices.keySet()) {
			g.addVertex(index);
			for (Integer edge : getVertex(index).getEdges()) {
				g.getVertex(index).addEdge(edge);
			}
		}
		g.setInitVertex(initialVertex);
		
		
		return g;
	}
	
	public boolean equals(Graph g){
		if(this.getInitVertex().getIndex() != g.getInitVertex().getIndex())
			return false;
		
		for (int vertex : vertices.keySet()) {
			if(!g.containsVertex(vertex))
				return false;
			
			for (int edge : this.getVertex(vertex).getEdges()) {
				if(!g.getVertex(vertex).hasEdge(edge))
					return false;
			}
		}
		
		return true;
	}
}
