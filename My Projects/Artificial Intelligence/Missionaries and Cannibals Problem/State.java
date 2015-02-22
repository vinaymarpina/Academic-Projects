

import java.util.ArrayList;
/*
 * This class contains the Setter and Getters methods for a particular State
 */
public class State {
	
	private int missionories;
	private int cannibals;
	private String riverSide;
	private State parentState;
	private State parentNewState;
	private String travelState;
	private ArrayList stateList;
	
	public State(){
		
	}

	public int getMissionories() {
		return missionories;
	}

	public void setMissionories(int missionories) {
		this.missionories = missionories;
	}

	public int getCannibals() {
		return cannibals;
	}

	public void setCannibals(int cannibals) {
		this.cannibals = cannibals;
	}

	public String getRiverSide() {
		return riverSide;
	}

	public void setRiverSide(String riverSide) {
		this.riverSide = riverSide;
	}

	public State getParentState() {
		return parentState;
	}

	public void setParentState(State parentState) {
		this.parentState = parentState;
	}

	public ArrayList getStateList() {
		return stateList;
	}

	public void setStateList(ArrayList stateList) {
		this.stateList = stateList;
	}

	public State getParentNewState() {
		return parentNewState;
	}

	public void setParentNewState(State parentNewState) {
		this.parentNewState = parentNewState;
	}

	public String getTravelState() {
		return travelState;
	}

	public void setTravelState(String travelState) {
		this.travelState = travelState;
	}
	
}
