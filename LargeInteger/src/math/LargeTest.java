package math;

import java.io.IOException;
import java.util.Scanner;

public class LargeTest {

	static Scanner sc;
	
	public static void parse() 
	throws IOException {
		System.out.print("\tEnter integer => ");
		String integer = sc.nextLine();
		try {
			LargeInteger bigInteger = LargeInteger.parse(integer);
			System.out.println("\t\tValue = " + bigInteger);
		} catch (IllegalArgumentException e) {
			System.out.println("\t\tIncorrect Format");
		}
	}
	
	public static void add() 
	throws IOException {
		System.out.print("\tEnter first integer => ");
		String integer = sc.nextLine();
		LargeInteger firstBigInteger = LargeInteger.parse(integer);
		
		System.out.print("\tEnter second integer => ");
		integer = sc.nextLine();
		LargeInteger secondBigInteger = LargeInteger.parse(integer);
		
		System.out.println("\t\tSum: " + firstBigInteger.add(secondBigInteger));
	}
	
	public static void multiply() 
	throws IOException {
		System.out.print("\tEnter first integer => ");
		String integer = sc.nextLine();
		LargeInteger firstBigInteger = LargeInteger.parse(integer);
		
		System.out.print("\tEnter second integer => ");
		integer = sc.nextLine();
		LargeInteger secondBigInteger = LargeInteger.parse(integer);
		
		System.out.println("\t\tProduct: " + firstBigInteger.multiply(secondBigInteger));
		
	}
	
	public static void main(String[] args) 
	throws IOException {
		
		// TODO Auto-generated method stub
		sc = new Scanner(System.in);
		
		char choice;
		while ((choice = getChoice()) != 'q') {
			switch (choice) {
				case 'p' : parse(); break;
				case 'a' : add(); break;
				case 'm' : multiply(); break;
				default: System.out.println("Incorrect choice"); 
			}
		}
	}

	private static char getChoice() {
		System.out.print("\n(p)arse, (a)dd, (m)ultiply, or (q)uit? => ");
		String in = sc.nextLine();
		char choice;
		if (in == null || in.length() == 0) {
			choice = ' ';
		} else {
			choice = in.toLowerCase().charAt(0);
		}
		return choice;
	}

}
