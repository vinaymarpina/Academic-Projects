


import java.util.ArrayList;
import java.util.HashMap;
/*
 * This class contains important methods like 
 * getAllstates()  can get all the state space of the problem in graph format
 * getTravelState()  can provide us with the valid 5 states from one side to other side
 * searchThroughGraph() can search the required path to the solution using simple search.
 */
public class SearchSpace {
	/*
	 * This Hash Map is to maintain the visited states on both sides
	 */
	HashMap visitedState = new HashMap();
	ArrayList queue = new ArrayList();
	int depth = 0;
	/*
	 * This method is a Recursive method which will generate the
	 * entire state space for the problem. this method generates 
	 * the state space in graph format which can hold all the details
	 * of the actions we made.
	 * 
	 * This method stops once we found one valid solution (3,3,R) and returns one
	 * big object which contains entire graph contains a optimal path to the 
	 * required solution
	 * 
	 * In this graph all the invalid states are removed only states which
	 * are valid for the given conditions are maintained.
	 */
	public State getAllStates(State currentState, State goalState ,ArrayList travelState){

		int mCurrent = currentState.getMissionories();
		int cCurrnet = currentState.getCannibals();
		String riverSideCurrent = currentState.getRiverSide();
		State parentCurrent = currentState.getParentState();
		ArrayList childStateList = new ArrayList();
		//System.out.println("Printing the Current State : "+mCurrent+riverSideCurrent+" "+cCurrnet+riverSideCurrent);
		visitedState.put(mCurrent+riverSideCurrent+","+cCurrnet+riverSideCurrent,true);
		
		if(comapreState(currentState, goalState)){
			return currentState;
		}else{
			for(int i=0; i<travelState.size();i++){
				State boatState = (State) travelState.get(i);
				if(mCurrent<boatState.getMissionories()
						|| cCurrnet<boatState.getCannibals()){
					continue;
				}else{
					State childState = new State();
					int mParent = 0;
					int cParent =0;
					String rSideParent = "R";
					if(currentState.getParentState() != null){
						mParent = currentState.getParentNewState().getMissionories();
						cParent = currentState.getParentNewState().getCannibals();
						rSideParent = parentCurrent.getRiverSide();
					}else{

					}
					String keyCheck = (mParent+boatState.getMissionories())+rSideParent+","+(cParent+boatState.getCannibals())+rSideParent;
					if(visitedState.containsKey(keyCheck)){
						continue;
					}else{
						childState.setMissionories(mParent+boatState.getMissionories());
						childState.setCannibals(cParent+boatState.getCannibals());
						childState.setRiverSide(rSideParent);
						childState.setTravelState(boatState.getMissionories()+"M,"+boatState.getCannibals()+"C traveling from:' "+riverSideCurrent+" 'to: '"+rSideParent+"'");
						State newParentState = new State();
						newParentState.setMissionories(mCurrent-boatState.getMissionories());
						newParentState.setCannibals(cCurrnet-boatState.getCannibals());
						newParentState.setRiverSide(riverSideCurrent);
						//visitedState.put(mParent+boatState.getMissionories()+rSideParent,cParent+boatState.getCannibals()+rSideParent);
						if((newParentState.getMissionories()>= newParentState.getCannibals()||newParentState.getMissionories()==0)
								&& (childState.getMissionories()>= childState.getCannibals()
								|| childState.getMissionories()==0)){
							childState.setParentState(currentState);
							childState.setParentNewState(newParentState);
							getAllStates(childState, goalState, travelState);
							childStateList.add(childState);
						}else{
							continue;
						}
					}
				}
			}
			currentState.setStateList(childStateList);
		}
		return currentState;
	}
	/*
	 * This method provides 5 possible travel states at a given side 
	 * those are in a boat 1M,2M,1C,2C,1M&1C
	 * returns list contains above values
	 */

	public ArrayList getTravelState(){
		ArrayList travelStateList = new ArrayList();
		for(int i=0; i<=2;i++){
			for(int j=0; j<=2; j++){
				if(i==0 && j==0){
					continue;
				}else if(i+j>2){
					continue;
				}else{
					State travelState = new State();
					travelState.setMissionories(j);
					travelState.setCannibals(i);
					travelStateList.add(travelState);
				}
			}
		}
		return travelStateList;
	}
	/*
	 * This method is needed to compare one state with other 
	 * because we do not want revisit the same state again and
	 * again.
	 */
	public boolean comapreState(State state1, State state2 ){

		if(state1.getMissionories() == state2.getMissionories() &&
				state1.getCannibals() == state2.getCannibals() && 
				state1.getRiverSide() == state2.getRiverSide()){
			return true;
		}else{
			return false;
		}
	}
	/*
	 * This method is to search through the graph and find the optimal solution
	 * this method ignores the path which are not going to lead to the solution
	 * this method saves a single path which leads to the solution.
	 * 
	 * I kept check with the Goal state and achieved this optimal single path.
	 * This search works with the concept of DFS
	 */
	public ArrayList searchThroughGraph(State state, State goalState){

		if(state.getMissionories()==goalState.getMissionories() &&
				state.getCannibals() ==goalState.getCannibals() &&
				state.getRiverSide() ==goalState.getRiverSide()){
			queue.add(state);
			depth++;
			return queue;
		}else{
			while(state.getStateList().size()!=0 && depth ==0){
				for(int i=0;i<state.getStateList().size();i++){
					State queueState = (State) state.getStateList().get(i);
					if(depth==0){
						searchThroughGraph(queueState,goalState);
					}else{
						break;
					}
				}	
			}
			if(depth == 1){
				queue.add(state);
			}
			return queue;
		}
	}

}
