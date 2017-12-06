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
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter p1: ");
		String p1 = sc.nextLine();
		System.out.print("Enter p2: ");
		String p2 = sc.nextLine();
		sc.close();
		ArrayList<String> shortest = Friends.shortestChain(g, p1, p2);
		if (shortest == null)
			System.out.println("No path found.");
		else {
			for (String s : shortest)
				System.out.print(s+" ");
		}
		
	}
}
