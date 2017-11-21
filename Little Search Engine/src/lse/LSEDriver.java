package lse;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Scanner;

public class LSEDriver {
	static Scanner stdin = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.print("Enter document file name: ");
//		String docFile = stdin.nextLine();
		LittleSearchEngine lse = new LittleSearchEngine();
		try {
			lse.makeIndex("docs.txt","noisewords.txt");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
