package connections;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Connections {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		Queue<Integer> q = new Queue<Integer>();
		
		int[] path = new int[g.members.length];
		Arrays.fill(path, -1);
		
		int p1Vertex = g.map.get(p1);
		int p2Vertex = g.map.get(p2);
		q.enqueue(p1Vertex);
		path[p1Vertex] = p1Vertex;
		
		while(!q.isEmpty()) {
			int vertex = q.dequeue();
			
			Friend f = g.members[vertex].first;
			while (f != null) {
				if (f.fnum == p2Vertex) { // if p2 found
					path[f.fnum] = vertex;
					return generatePath(g, path, p1Vertex, p2Vertex);
				}	
				if (path[f.fnum] == -1) {
					q.enqueue(f.fnum);
					path[f.fnum] = vertex;
				}
				f = f.next;
			}
		}
		return null;
	}
	
	private static ArrayList<String> generatePath(Graph g, int[] prevVertices, int start, int end) {
		int vertex = end;
		Stack<Integer> pathStack = new Stack<Integer>();
		ArrayList<String> path = new ArrayList<String>();
		while (vertex != start) {
			pathStack.push(vertex);
			vertex = prevVertices[vertex];
		}
		pathStack.push(start);
		while (!pathStack.isEmpty()) {
			path.add(g.members[pathStack.pop()].name);
		}
		return path;
	}
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		ArrayList<ArrayList<String>> cliqueList = new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[g.members.length];
		Queue<Integer> q = new Queue<Integer>();
		
		for (int i = 0; i < g.members.length; i++) {
			if (visited[i])
				continue;
			
			ArrayList<String> newClique = new ArrayList<String>();
			q.enqueue(i);
			while (!q.isEmpty()) {
				int vertex = q.dequeue();
				if (visited[vertex])
					continue;
				
				if(g.members[vertex].student && g.members[vertex].school.equals(school)) {
					newClique.add(g.members[vertex].name);
				}
				visited[vertex] = true;
		
				Friend f = g.members[vertex].first;
				while (f != null) {
					if (!visited[f.fnum] 
							&& g.members[f.fnum].student 
							&& g.members[f.fnum].school.equals(school)) {
						q.enqueue(f.fnum);
					}
					f = f.next;
				}
			}
			if (newClique.size() != 0)
				cliqueList.add(newClique);
			
		}
		if (cliqueList.size() == 0)
			return null;
		
		return cliqueList;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		int numMembers = g.members.length;
		if (numMembers <= 2)
			return null;
		
		ArrayList<String> connectorList = new ArrayList<String>();
		boolean[] visited = new boolean[numMembers];
		int[] dfsNumList = new int[numMembers];
		int[] backNumList = new int[numMembers];
		Stack<Integer> vStack = new Stack<Integer>();
		int dfsNum = 0;
		int backNum = 0;
		boolean noValidNeighbors = true;
		
		// get number of root neighbors
		int numRootNeighbors = 0;
		Friend rootF = g.members[0].first;
		while (rootF != null) {
			numRootNeighbors++;
			rootF = rootF.next;
		}
		
		for(int i = 0; i < numMembers; i++) {
			if (visited[i])
				continue;
			
			dfsNumList[i] = dfsNum;
			backNumList[i] = backNum;
			vStack.push(i);
			
			while (!vStack.isEmpty()) {
				int vertex = vStack.peek();
				if(!visited[vertex]) {
					dfsNum++;
					backNum++;
					dfsNumList[vertex] = dfsNum;
					backNumList[vertex] = backNum;
				}
				visited[vertex] = true;
				
				// get neighbors
				noValidNeighbors = true;
				Friend f = g.members[vertex].first;
				while (f != null) {
					int neighbor = f.fnum;
					if (!visited[neighbor]) {
						vStack.push(neighbor);
						noValidNeighbors = false;
						break;
					} else {
						backNumList[vertex] = Math.min(backNumList[vertex], dfsNumList[neighbor]);
					}
					f = f.next;
				}
				if (noValidNeighbors) {
					vStack.pop();
					if (vStack.isEmpty())
						break;
					int prevVertex = vStack.peek();
					
					// case where root is not a connector
					if (prevVertex == 0 && numRootNeighbors == 1 && vStack.size() == 1)
						break;
					
					if (dfsNumList[prevVertex] > backNumList[vertex])
						backNumList[prevVertex] = Math.min(backNumList[prevVertex], backNumList[vertex]);
					
					else if (dfsNumList[prevVertex] <= dfsNumList[vertex])
						if (!connectorList.contains(g.members[prevVertex].name))
							connectorList.add(g.members[prevVertex].name);
				}
			}
		}
		if (connectorList.size() == 0)
			return null;
		
		return connectorList;
	}
}