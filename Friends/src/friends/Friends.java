package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

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
		
		/** COMPLETE THIS METHOD **/
		if (!schoolFound(g, school))
			return null;
		
		ArrayList<ArrayList<String>> cliqueList = new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[g.members.length];
		Queue<Integer> q = new Queue<Integer>();
		
//		int firstInSchool = -1;
//		for (int i = 0; i < g.members.length; i++) {
//			String currentSchool = g.members[i].school;
//			if (currentSchool.equals(school)) {
//				firstInSchool = i;
//				break;
//			}
//		}
//		if (firstInSchool == -1)
//			return null;
//		
//		q.enqueue(firstInSchool);
//		visited[firstInSchool] = true;
		
		int cliqueNum = 0;
		boolean newCliqueFlag = false;
		for (int i = 0; i < g.members.length; i++) {
			newCliqueFlag = false;
			if (visited[i])
				continue;
			
			q.enqueue(i);
			while (!q.isEmpty()) {
				int vertex = q.dequeue();
				if (visited[vertex])
					continue;
				
				if(g.members[vertex].school.equals(school)) {
					cliqueList.get(cliqueNum).add(g.members[vertex].name);
					newCliqueFlag = true;
				}
				visited[vertex] = true;
		
				Friend f = g.members[vertex].first;
				while (f != null) {
					if (!visited[f.fnum]) {
						q.enqueue(f.fnum);
					}
					f = f.next;
				}
			}
			if (newCliqueFlag)
				cliqueNum++;
			
		}
		
		
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		return cliqueList;
		
	}
	
	private static boolean schoolFound(Graph g, String school) {
		for (int i = 0; i < g.members.length; i++) {
			if (g.members[i].school.equals(school)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		/** COMPLETE THIS METHOD **/
		
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		return null;
		
	}
}

