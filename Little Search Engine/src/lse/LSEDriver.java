package lse;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class LSEDriver {
	static Scanner stdin = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.print("Enter documents file name: ");
		String docsFile = stdin.nextLine();
		System.out.print("Enter noise words file name: ");
		String noiseFile = stdin.nextLine();
		
		LittleSearchEngine lse = new LittleSearchEngine();
		try {
			lse.makeIndex(docsFile, noiseFile);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
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
