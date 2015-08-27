import java.util.PriorityQueue;



public class Djkstra {
	
	static private PriorityQueue<GraphNode> queue;
	
	static double[][] weights;
	static double[][] heuristic;
	
	static void Main(GraphAdjList graph){
		weights = new double[graph.getV()+1][graph.getV()+1];
		heuristic = new double[graph.getV()+1][graph.getV()+1];
		for(GraphNode node:graph){
		
			GraphNode s = graph.getAdjList()[node.v];
			queue = new PriorityQueue<GraphNode>();
			initialize(s,graph);
			while(queue.size()!=0){
				GraphNode u = queue.poll();
				for(Edge edge:u.edges){
					relax(edge, graph);
				}
			}
			for(GraphNode node1:graph){
				weights[s.v][node1.v] = node1.d * .8;
				heuristic[s.v][node1.v] = node1.d * 3;
			}
			
		}
		
		for(int i = 1;i<weights.length;i++){
			for(int j = 1;j<weights.length;j++){
				//System.out.print(weights[i][j] + " ");
			}
			//System.out.println();
		}
	}
	
	private static void relax(Edge edge,GraphAdjList graph){
		if(edge.v.d > edge.u.d + edge.w){
			edge.v.d = edge.u.d + edge.w;
			edge.v.parent = edge.u;
			queue.add(edge.v);
		}
	}

	private static void initialize(GraphNode s,GraphAdjList graph) {
		s.d = 0;
		for(GraphNode node:graph){
			if(!node.equals(s)){
				node.d = Integer.MAX_VALUE;
				node.parent= null;
				queue.add(node);
			}
		}
		queue.add(s);
	}
	
}
