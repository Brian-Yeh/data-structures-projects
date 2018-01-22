package se;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class SearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public SearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		Scanner sc;
		sc = new Scanner(new File(docFile));
		
		HashMap<String,Occurrence> docHash = new HashMap<String,Occurrence>();
		while (sc.hasNext()) {
			String newKeyword = getKeyword(sc.next());
			if (newKeyword == null)
				continue;		
			if (docHash.containsKey(newKeyword)) { // if key exists update frequency
				docHash.get(newKeyword).frequency++; 
			} else {
				docHash.put(newKeyword, new Occurrence(docFile, 1));
			}
		}
		sc.close();
		
		return docHash;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for (String keyword: kws.keySet()) {
 			Occurrence keyOccur = kws.get(keyword);
			if (keywordsIndex.containsKey(keyword)) {
				keywordsIndex.get(keyword).add(keyOccur);
				insertLastOccurrence(keywordsIndex.get(keyword));
			} else {
				ArrayList<Occurrence> occurList = new ArrayList<Occurrence>();
				occurList.add(keyOccur);
				keywordsIndex.put(keyword, occurList);
			}
		}
	}	
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		int wordLen = word.length()-1;
		 // remove trailing non-alphabetic characters
		while (wordLen >= 0 && !Character.isAlphabetic(word.charAt(wordLen))) {
			wordLen--;
		}
		if (wordLen == -1)
			return null;
		word = word.substring(0, ++wordLen);
		word = word.toLowerCase();
		
		for (int i = 0; i < word.length(); i++) {
			if (!Character.isAlphabetic(word.charAt(i))) // if any character invalid 
				return null;
		}
		if (noiseWords.contains(word))
			return null;
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		if (occs.size() <= 1) {
			return null;
		}
		Occurrence lastOccur = occs.get(occs.size()-1);
		int lastOccurFreq = lastOccur.frequency;
		occs.remove(occs.size()-1);
		
		// reverse binary search
		int left = 0, right = occs.size()-1, mid = 0;
	
		while (left <= right) {
			mid = (left + right) / 2;
			midpoints.add(mid);
			int midFreq = occs.get(mid).frequency; 
			
			if (lastOccurFreq < midFreq)
				left = mid+1;
			else if (lastOccurFreq > midFreq)
				right = mid-1;
			else {
				occs.add(mid, lastOccur);
				return midpoints;
			}	
		}
		if (occs.get(mid).frequency < lastOccurFreq)
			occs.add(mid, lastOccur);
		else
			occs.add(mid+1, lastOccur);
		
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence> kw1List, kw2List;
		kw1List = keywordsIndex.containsKey(kw1) ? keywordsIndex.get(kw1) : new ArrayList<Occurrence>();
		kw2List = keywordsIndex.containsKey(kw2) ? keywordsIndex.get(kw2) : new ArrayList<Occurrence>();
		ArrayList<String> top5List = new ArrayList<String>();
		
		int kw1Index = 0, kw2Index = 0;
		while (kw1Index < kw1List.size() && kw2Index < kw2List.size()) {
			String newDoc;
			if(kw1List.get(kw1Index).frequency >= kw2List.get(kw2Index).frequency) {
				newDoc = kw1List.get(kw1Index).document;
				kw1Index++;
			} else {
				newDoc = kw2List.get(kw2Index).document;
				kw2Index++;
			}
			if (!top5List.contains(newDoc))
				top5List.add(newDoc);
		}
		// if kw1List has elements remaining
		if (kw1Index < kw1List.size()) {
			while(kw1Index < kw1List.size()) {
				String newDoc = kw1List.get(kw1Index).document;
				kw1Index++;
				if (!top5List.contains(newDoc))
					top5List.add(newDoc);
			}
		}
		// if kw2List has elements remaining
		if (kw2Index < kw2List.size()) {
			while (kw2Index < kw2List.size()) {
				String newDoc = kw2List.get(kw2Index).document;
				kw2Index++;
				if (!top5List.contains(newDoc))
					top5List.add(newDoc);
			}
		}
		while (top5List.size() > 5) {
			top5List.remove(top5List.size()-1);
		}
		return top5List;
	}
}
