package de.ma.modal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;

import de.ma.ast.Node;
import de.ma.treewalker.MlMcTreeWalker;
import de.ma.visuals.VisualizeGraph;

public class Modal {
	private final Graph graph;
	private final Map<String, HashSet<Integer>> valuation = new HashMap<>();
	// vars stored without ~
	private final Map<String, HashSet<Integer>> negValuation = new HashMap<>();

	public Modal(Graph g) {
		graph = g;

	}

	public Graph getGraph() {
		return graph;
	}

	public HashSet<Integer> getVertices() {
		return graph.getVertices();
	}

	public HashSet<Integer> getVerticesWithVar(String var) {
		if (var.startsWith("~")) {
			var = var.substring(1);

			if (getNegValuation().containsKey(var))
				return getNegValuation().get(var);
			else
				return new HashSet<>();
		}

		if (getValuation().containsKey(var))
			return getValuation().get(var);
		else
			return new HashSet<>();
	}

	public ArrayList<String> getVarsFromVertex(Integer v) {
		ArrayList<String> vars = new ArrayList<>();

		for (String var : getValuation().keySet()) {
			if (getVerticesWithVar(var).contains(v))
				vars.add(var);
		}

		return vars;
	}

	public ArrayList<String> getNegVarsFromVertex(Integer v) {
		ArrayList<String> vars = new ArrayList<>();

		for (String var : getNegValuation().keySet()) {
			if (getVerticesWithVar("~" + var).contains(v))
				vars.add(var);
		}

		return vars;
	}
	
	// without negative vars
	public String printVarsFromVertex(Integer v) {
		String vars = "";

		for (String var : getValuation().keySet()) {
			if (getVerticesWithVar(var).contains(v)) {
				if (vars.equals(""))
					vars = var;
				else
					vars += "," + var;
			}
		}

		for (String var : getNegValuation().keySet()) {
			if (getVerticesWithVar("~" + var).contains(v)) {
				if (vars.equals(""))
					vars = "\u00AC"+var;
				else
					vars += ",\u00AC" + var;
			}
		}

		return vars;
	}

	public Map<String, HashSet<Integer>> getValuation() {
		return valuation;
	}

	public Map<String, HashSet<Integer>> getNegValuation() {
		return negValuation;
	}

	public void addValuation(Map<String, HashSet<Integer>> val) {
		getValuation().putAll(val);
	}

	public void addNegValuation(Map<String, HashSet<Integer>> val) {
		getNegValuation().putAll(val);
	}

	public boolean addVarToVertex(String var, int index) {
		if (!graph.containsVertex(index))
			return false;

		if (var.startsWith("~")) {
			
			if (getVerticesWithVar(var).contains(index))
				return false;
			if (!containsVar(var)) {
				getNegValuation().put(var.substring(1), new HashSet<Integer>());
			}

			return getNegValuation().get(var.substring(1)).add(index);
		}

		if (getVerticesWithVar("~" + var).contains(index))
			return false;
		if (!containsVar(var)) {
			getValuation().put(var, new HashSet<Integer>());
		}

		return getValuation().get(var).add(index);
	}

	public boolean removeVarFromVertex(String var, int index) {
		if (!containsVar(var))
			return false;

		if (var.startsWith("~")) {
			return getNegValuation().get(var.substring(1)).remove(index);
		}

		return getValuation().get(var).remove(index);
	}

	public boolean containsVar(String var) {
		if (var.startsWith("~")) {
			return getNegValuation().containsKey(var.substring(1));
		}
		return getValuation().containsKey(var);
	}

	public boolean mlMc(Node root) {
		MlMcTreeWalker mmtw = new MlMcTreeWalker();
		Set<Integer> satVertices = mmtw.walk(root, this);

		if (graph.getInitVertex() == null) {
			if (satVertices.isEmpty())
				return false;
			else
				return true;
		} else {
			return satVertices.contains(graph.getInitVertex().getIndex());
		}
	}

	public void draw(JLabel label) {
		VisualizeGraph v = new VisualizeGraph();
		v.visualizeGraph(this, label);
	}

	public Modal clone() {
		Modal m = new Modal(getGraph().clone());
		m.addValuation(getValuation());
		m.addNegValuation(getNegValuation());

		return m;
	}

	public boolean isCompatible(Modal m) {
//		if (!this.getGraph().isomorphic(m.getGraph()))
//			return false;

		for (int index : graph.getVertices()) {
			for (String var : this.getVarsFromVertex(index)) {
				if (m.getNegVarsFromVertex(index).contains(var))
					return false;
			}
			for (String var : this.getNegVarsFromVertex(index)) {
				if (m.getVarsFromVertex(index).contains(var))
					return false;
			}
		}
		
		return true;
	}

	public void join(Modal m) {
		for (String var : m.getValuation().keySet()) {
			for (Integer index : m.getValuation().get(var)) {
				addVarToVertex(var, index);
			}
		}

		for (String var : m.getNegValuation().keySet()) {
			for (Integer index : m.getNegValuation().get(var)) {
				addVarToVertex("~"+var, index);
			}
		}
	}
	
	public boolean hasAutWithSmallerIndex(int index){
		if(graph.isOrbitRep(index))
			return false;
		
		for (int aut : graph.getOrbitGroup(index)) {
			if(aut < index){
				ArrayList<String> varsAut = getVarsFromVertex(aut);
				ArrayList<String> varsIndex = getVarsFromVertex(index);
				
				if(varsAut.containsAll(varsIndex) && varsIndex.containsAll(varsAut))
					return true;
			}
		}
		
		return false;
	}
}
