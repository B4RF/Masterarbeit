package de.ma.treewalker;

public class IntWithMax {
	int currentValue;
	int maxValue;
	
	public IntWithMax(int cur, int max){
		currentValue = cur;
		maxValue = max;
	}
	
	public int getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
}
