package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FriendsDriver {

	
	public static void main(String[] args) {
		File file = new File("friends1.txt");
		Scanner fileSC = null;
		try {
			fileSC = new Scanner(file); 
		} catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		Graph g = new Graph(fileSC);
		fileSC.close();
		
		ArrayList<String> connectorList = Friends.connectors(g);
		if (connectorList == null)
			System.out.println("No Connectors");
		else {
			System.out.println("Connectors: ");
			for (String s : connectorList)
				System.out.print(s + " ");
		}
		//Scanner sc = new Scanner(System.in);
		/*
		System.out.print("Enter p1: ");
		String p1 = sc.nextLine();
		System.out.print("Enter p2: ");
		String p2 = sc.nextLine();
		
		ArrayList<String> shortest = Friends.shortestChain(g, p1, p2);
		if (shortest == null)
			System.out.println("No path found.");
		else {
			for (String s : shortest)
				System.out.print(s+" ");
		}*/
		
//		System.out.print("Enter school: ");
//		String school = sc.nextLine();
//		
//		ArrayList<ArrayList<String>> cliqueList = Friends.cliques(g, school);
//		if (cliqueList == null)
//			System.out.println("School not found");
//		else { 
//			for (int i = 0; i < cliqueList.size(); i++) {
//				System.out.println("Clique " + (i+1));
//				for (int j = 0; j < cliqueList.get(i).size(); j++) {
//					System.out.print(cliqueList.get(i).get(j)+" ");
//				}
//				System.out.println();
//			}
//		}
//		sc.close();
		
	}
}
