package se;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SEDriver {
	static Scanner stdin = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.print("Enter documents file name: "); // docs.txt
		String docsFile = stdin.nextLine();
		System.out.print("Enter noise words file name: "); // noisewords.txt
		String noiseFile = stdin.nextLine();
		
		SearchEngine lse = new SearchEngine();
		try {
			lse.makeIndex(docsFile, noiseFile);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		// If a keyword doesn't exist, nothing will be returned,
		// use "Alice" and "world" for a guaranteed return."
		System.out.print("Enter keyword1 to search: ");
		String kw1 = stdin.next();
		System.out.print("Enter keyword2 to search: ");
		String kw2 = stdin.next();
		stdin.close();
		ArrayList<String> top5List = lse.top5search(kw1, kw2);
		for(String s:top5List) {
			System.out.println(s);
		}
	}
}
