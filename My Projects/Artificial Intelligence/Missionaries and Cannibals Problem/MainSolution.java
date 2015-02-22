

import java.util.ArrayList;

public class MainSolution {

	public static void main(String[] args){
		/*
		 * HardCoding the Initial State and Gola State
		 */
		int M = 3;
		int C = 3;
		State initialState = new State();
		State goalState = new State();
		initialState.setMissionories(M);
		initialState.setCannibals(C);
		initialState.setRiverSide("L");
		goalState.setMissionories(M);
		goalState.setCannibals(C);
		goalState.setRiverSide("R");
		
		SearchSpace searchSpace = new SearchSpace();
		ArrayList possibleTravelList = new ArrayList();
		possibleTravelList = searchSpace.getTravelState();
		State finalState = searchSpace.getAllStates(initialState, goalState, possibleTravelList);
		ArrayList finalQueue = searchSpace.searchThroughGraph(finalState,goalState);
		
		/*
		 * Below code is for the purpose of printing the Path in Proper
		 * manner. I given the spaces according to my screen resolution.
		 */
		System.out.println("Goal is to Travel from Left to Right: ");
		System.out.println("**************************************");
		System.out.println("");
		System.out.println("3M,3C,L ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 0M,0C,R");
		for(int i=finalQueue.size()-2;i>=0;i--){
			boolean left = false;
			if((finalQueue.size()-2)%2 == 0){
				left = true;
			}else{
				left = false;
			}
			State state = (State)finalQueue.get(i);
			State parentState = state.getParentNewState();
			System.out.println("             ["+state.getTravelState()+"]:");
			if(left = true && i%2 ==0){
				System.out.println(parentState.getMissionories()+"M,"+parentState.getCannibals()+"C,"+parentState.getRiverSide()+": "+
						"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ "+state.getMissionories()+"M,"+state.getCannibals()+"C,"+state.getRiverSide());
			}else{
				System.out.println(+state.getMissionories()+"M,"+state.getCannibals()+"C,"+state.getRiverSide()+" :"+
						"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ "+parentState.getMissionories()+"M,"+parentState.getCannibals()+"C,"+parentState.getRiverSide());
			}
			
			
		}
		System.out.println("");
		
	}
}
