
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;

public class GraphAdjList implements Iterable<GraphNode>{
	private int V;
	private int E;
	private int s;
	private int d;
	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	private GraphNode[] adjList;
	
	public GraphNode[] getAdjList() {
		return adjList;
	}

	public int getV() {
		return V;
	}

	public void setV(int v) {
		V = v;
	}

	public int getE() {
		return E;
	}

	public void setE(int e) {
		E = e;
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public GraphAdjList() {
		
	}
	
	public boolean loadGraph() throws Exception{
		Scanner scanner = new Scanner(new FileReader("graph.txt"));
		this.V=scanner.nextInt();
		this.E=scanner.nextInt();
		this.s=scanner.nextInt();
		adjList = new GraphNode[V+1];
		for(int i=1;i<=V;i++){
			adjList[i] = new GraphNode(i);
		}
		int e = E;
		while(e > 0){
			addEdge(scanner.nextInt(),scanner.nextInt(),scanner.nextInt());
			e--;
		}
		
		return true;
	}
	
	public GraphAdjList(int V){
		this.V=V;
		adjList = new GraphNode[V+1];
		for(int i=1;i<=V;i++){
			adjList[i] = new GraphNode(i);
		} 
	}
	
	Edge addEdge(int u,int v,int w){
		//Undirected graph.
		Edge edge = new Edge(adjList[u],adjList[v],w);
		Edge edge2 = new Edge(adjList[v],adjList[u],w);
		adjList[u].add(edge);
		adjList[v].add(edge2);
		return edge;
	}
	
	public void setAdjList(GraphNode[] adjList){
		this.adjList = adjList;
	}
	 static void Main(ArrayList<Integer> interestInput) throws Exception{
		GraphAdjList graphAdjList = new GraphAdjList();
		graphAdjList.loadGraph();
		Djkstra.Main(graphAdjList);
		ArrayList<Integer> shopNumberList = new ArrayList<Integer>();
		shopNumberList = interestInput;
		int start = shopNumberList.get(0);
		ArrayList<String> shopList = new ArrayList<String>();
		shopList = MainClass.read("ShopNumbers.txt");
		HashMap<Integer,String> shopMap = new HashMap<Integer,String>();
		for(int i=0;i<shopList.size();i++){
			String localString = shopList.get(i);
			StringTokenizer str = new StringTokenizer(localString," ");
			String shopName = "";
			int shopNumber = 0;
			while(str.hasMoreElements()){
				shopNumber = Integer.parseInt(str.nextToken());
				shopName = str.nextToken();
			}
			shopMap.put(shopNumber,shopName);
		}
		ArrayList<String> admissiblePath = new ArrayList<String>();
		ArrayList<String> inadmissiblePath = new ArrayList<String>();
		while(shopNumberList.size()>1){
			int goal = 0;
			double Min = Double.MAX_VALUE;
			for(int i=0;i<shopNumberList.size();i++){
				if(start == shopNumberList.get(i)){
					continue;
				}else{
					double temp = Djkstra.weights[start][shopNumberList.get(i)];
					if(temp<Min){
						Min = temp;
						goal = shopNumberList.get(i);
					}
				}
			}
			initGraph(graphAdjList);
			ArrayList<GraphNode> path = runAstar(graphAdjList.getAdjList()[start],graphAdjList.getAdjList()[goal],1);
			//System.out.println("Admissable Heuristic Path:");
			for(int i=path.size()-1;i>=0;i--){
				//System.out.print(shopMap.get(path.get(i).v) +": ");
				admissiblePath.add(shopMap.get(path.get(i).v));
			}
			
			initGraph(graphAdjList);
			path = runAstar(graphAdjList.getAdjList()[start],graphAdjList.getAdjList()[goal],2);
			for(int i=path.size()-1;i>=0;i--){
				//System.out.print(shopMap.get(path.get(i).v)+" ");
				inadmissiblePath.add(shopMap.get(path.get(i).v));
			}
			//System.out.println("");
			for(int i=0;i<shopNumberList.size();i++){
				if(start == shopNumberList.get(i)){
					shopNumberList.remove(i);
					break;
				}
			}
			start = goal;
		}
		System.out.println("");
		System.out.println("Admissable Heuristic Path:");
		for(int i=0;i<admissiblePath.size();i++){
			System.out.print(admissiblePath.get(i));
			if(i!=admissiblePath.size()-1){
				System.out.print(" --->");
			}
		}
		System.out.println("");
		System.out.println("*********************************************");
		System.out.println("In-Admissable Heuristic Path:");
		for(int i=0;i<inadmissiblePath.size();i++){
			System.out.print(inadmissiblePath.get(i));
			if(i!=inadmissiblePath.size()-1){
				System.out.print(" --->");
			}
		}
	}
	 
	private static void initGraph(GraphAdjList g){
		for(GraphNode node:g){
			node.cameFrom = null;
		}
	}
	/*
	 * Implementation of A* Algorithm
	 */
	private static ArrayList<GraphNode> runAstar(GraphNode g1, GraphNode g2, int heuristicNumber) {
		HashSet<GraphNode> closedSet = new HashSet<GraphNode>();
		PriorityQueue<GraphNode> openQueue = new PriorityQueue<GraphNode>();
		
		g1.hasF = true;
		g1.g_score = 0;
		double f_score = 0;
		if(heuristicNumber == 1){
			f_score = g1.g_score+ Djkstra.weights[g1.v][g2.v];
			g1.f_value = f_score;
		}else{
			g1.f_value = 0;
			//f_score = g1.g_score+ Djkstra.heuristic[g1.v][g2.v];
		}
		
		openQueue.add(g1);
		if(heuristicNumber ==1){
			while(!openQueue.isEmpty()){
				GraphNode current = openQueue.poll();
				if(current.equals(g2)){
					return reconstruct_path(g2);
				}
				closedSet.add(current);
				for(Edge edge : current.edges){
					if(closedSet.contains(edge.v)){
						continue;
					}
					double temp_gScore = current.g_score+edge.w;
					
					if(!openQueue.contains(edge.v) || temp_gScore < edge.v.g_score){
						edge.v.cameFrom = current;
						edge.v.g_score = temp_gScore;
						edge.v.f_value = edge.v.g_score+Djkstra.weights[edge.v.v][g2.v];
						
						if(!openQueue.contains(edge.v)){
							edge.v.hasF = true;
							openQueue.add(edge.v);
						}
					}
				}
				
			}
		}else{
			while(!openQueue.isEmpty()){
				GraphNode current = openQueue.poll();
				if(current.equals(g2)){
					return reconstruct_path(g2);
				}
				closedSet.add(current);
				GraphNode next = null;
				int min = Integer.MAX_VALUE;
				for(Edge edge : current.edges){
					if(closedSet.contains(edge.v)){
						continue;
					}
					double temp_gScore = current.g_score+edge.w;
					
					if(!openQueue.contains(edge.v)){
						edge.v.cameFrom = current;
						edge.v.g_score = temp_gScore;
					
								if(min > edge.w){
									min = edge.w;
									next = edge.v;
								}
							
							edge.v.f_value = min;
							
					}
				}
				//System.out.println(min);
				if(next != null){
					if(!openQueue.contains(next)){
						next.hasF = true;
						openQueue.add(next);
					}
				}else{
					System.out.println("*************NOTE****************");
					System.out.println("InAdmissible Heauristic Unable to Find the Path to the Goal Node and Stuck at Dead End");
					System.out.println("*****************************");
					return reconstruct_path(current);
				}
				
			}
			
		}
		return null;
	}
	
	public static ArrayList<GraphNode> reconstruct_path(GraphNode current){
		ArrayList<GraphNode> totalPath = new ArrayList<GraphNode>();
		GraphNode temp = current;
		while (temp != null){
			//System.out.println("a");
			totalPath.add(temp);
			temp = temp.cameFrom;
		}
		return totalPath;
	}

	@Override
	public Iterator<GraphNode> iterator() {
		return new VertexIterator();
	}
	
	private class VertexIterator implements Iterator<GraphNode>{
		private int noOfVertices = 0;
		
		@Override
		public boolean hasNext() {
			return noOfVertices==V?false:true;
		}

		@Override
		public GraphNode next() {
			noOfVertices++;
			return adjList[noOfVertices];
		}
	}
}


class Edge implements Comparable<Edge>{
	GraphNode u; //from node
	GraphNode v; // to node
	int w; // weight
	int mw;
	public int index;
	public boolean ignore;

	public Edge(GraphNode u, GraphNode v, int w) {
		super();
		this.u = u;
		this.v = v;
		this.w = w;
		this.mw=w;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (u == null) {
			if (other.u != null)
				return false;
		} else if (!u.equals(other.u))
			return false;
		if (v == null) {
			if (other.v != null)
				return false;
		} else if (!v.equals(other.v))
			return false;
		if (w != other.w)
			return false;
		return true;
	}


	@Override
	public int compareTo(Edge arg0) {
		if(arg0.w > this.w)
			return -1;
		else if(arg0.w < this.w)
			return 1;
		else
			return 0;
	}
}


class GraphNode implements Comparable<GraphNode>{
	
	int v; //node name
	LinkedList<Edge> edges;
	public GraphNode parent;
	public long d;
	public int count;
	public boolean inQ;
	public int color;
	public boolean hasF;
	public double f_value;
	public double g_score;
	public GraphNode cameFrom;

	public void remove(int removeNode) {
		this.edges.remove(removeNode);
	}

	GraphNode(int v){
		this.v=v;
		edges = new LinkedList();
	}
	
	public void add(Edge edge) {
		this.edges.add(edge);
	}
	
	public void add(Edge edge,int index) {
		this.edges.add(edge);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (v != other.v)
			return false;
		return true;
	}

	public String toString(){
		return ""+v;
	}
	

	
	@Override
	public int compareTo(GraphNode arg0) {
		if(!arg0.hasF){
			if(arg0.d > this.d)
				return -1;
			else if(arg0.d < this.d)
				return 1;
			else
				return 0;
		}else{
			if(arg0.f_value > this.f_value)
				return -1;
			else if(arg0.f_value < this.f_value)
				return 1;
			else
				return 0;
		}
	}
}
