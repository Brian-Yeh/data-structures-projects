package prefixtree;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class PrefixTree {
	
	// prevent instantiation
	private PrefixTree() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null, null, null);
		Arrays.sort(allWords);
		for (int i = 0; i < allWords.length; i++) {
				insert(allWords, root, i, (short) 0);
		}
		
		return root;
	}
	
	private static TrieNode insert(String[] allWords, TrieNode root, int wordIndex, short startIndex) {
		TrieNode ptr = root.firstChild;
		TrieNode prev = null;
		String newWord = allWords[wordIndex].substring(startIndex);
		
		while (ptr != null) {
			if (newWord.charAt(0) == allWords[ptr.substr.wordIndex].charAt(ptr.substr.startIndex)) { // prefix match
				String ptrWord = allWords[ptr.substr.wordIndex].substring(ptr.substr.startIndex,ptr.substr.endIndex+1);
				int letterCount = 0; // number of matched letters in prefix
				startIndex--; // adjust startIndex for loop
				for (int j = 0; j < ptrWord.length(); j++) {
					if (newWord.charAt(j) == ptrWord.charAt(j)) {
						startIndex++;
						letterCount++;
					}
					else
						break;
				}
				// if entire substring matched, go to next child
				if (letterCount == ptrWord.length()) {
					insert(allWords, ptr, wordIndex, ++startIndex);
					return root;
				} else {
					ptr.substr.endIndex = (short) (ptr.substr.startIndex + letterCount - 1);
					// if ptr already contains a firstChild, make new firstChild point to old
					if (ptr.firstChild != null) {
						Indexes childIndex1 = new Indexes (ptr.substr.wordIndex, (short) (ptr.substr.startIndex+letterCount), (short)(ptr.substr.endIndex+letterCount));
						TrieNode newFirstChild = new TrieNode(childIndex1, ptr.firstChild, null);
						ptr.firstChild = newFirstChild;
					} else {
						Indexes childIndex1 = new Indexes (ptr.substr.wordIndex, (short) (ptr.substr.startIndex+letterCount), (short)(allWords[ptr.substr.wordIndex].length()-1));
						ptr.firstChild = new TrieNode(childIndex1, null, null);
					}

					Indexes childIndex2 = new Indexes (wordIndex, ++startIndex, (short) (allWords[wordIndex].length()-1));
					ptr.firstChild.sibling = new TrieNode(childIndex2, null, null);
					return root;
				}
			} else {
				prev = ptr;
				ptr = ptr.sibling;
				continue;
			}
		}
		// if no matches with siblings, create new sibling
		Indexes newIndexes = new Indexes(wordIndex, startIndex, (short) (allWords[wordIndex].length()-1));
		if (prev == null) {
			root.firstChild = new TrieNode(newIndexes, null, null);
		} else {
			prev.sibling = new TrieNode(newIndexes, null, null);
		}
		return root;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		TrieNode ptr = root;
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		
		if (!prefix.isEmpty() && ptr.firstChild == null) 
			return list;
		
		else if (prefix.isEmpty() && ptr.firstChild == null)
			list.add(ptr);
		
		else if (prefix.isEmpty() && ptr.firstChild != null) {
			ptr = ptr.firstChild;
			while(ptr != null) {
				list.addAll(completionList(ptr, allWords, prefix));
				ptr = ptr.sibling;
			}
			return list;
			
		} else if (!prefix.isEmpty() && ptr.firstChild != null) {
			ptr = ptr.firstChild;
			while (ptr != null) {
				if (prefix.charAt(0) == allWords[ptr.substr.wordIndex].charAt(ptr.substr.startIndex)) {
					String ptrWord = allWords[ptr.substr.wordIndex].substring(ptr.substr.startIndex, ptr.substr.endIndex+1);
					int letterCount = 0;
					for (int i = 0; i < ptrWord.length(); i++) {
						if (i == prefix.length())
							break;
						else if (prefix.charAt(i) == ptrWord.charAt(i))
							letterCount++;
						else
							break;
					}
					ArrayList<TrieNode> returnList = completionList(ptr, allWords, prefix.substring(letterCount));
					if (returnList == null)
						break; // no match found
					else
						list.addAll(completionList(ptr, allWords, prefix.substring(letterCount)));
				}
				ptr = ptr.sibling;
			}
		}

		if (list.isEmpty())
			return null;
		else
			return list;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }