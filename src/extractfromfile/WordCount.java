package extractfromfile;

import java.util.*;

import org.apache.lucene.analysis.TokenStream;

import java.io.*;

public class WordCount {
	
	public static void main(String[] args) throws FileNotFoundException {
		// Also check for common_words	
		
		String fileName = "paper.txt";
		int minOccurances = 50;

		
		

		Scanner input = new Scanner(new File(fileName));
		// count occurrences
		Map<String, Integer> wordCounts = new TreeMap<String, Integer>();
		while (input.hasNext()) {
			String next = input.next().toLowerCase();

			// replace any punctuation char but apostrophes and dashes with a space
			next = next.replaceAll("[\\p{Punct}&&[^'-]]+", "");
			next = next.replaceAll("-", "");
			// replace most common English contractions
			next = next.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

			List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", 
					"is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "us",
					"to", "was", "will", "with", "from", "form", "within", "when", "what", "where", "without", "vs", "very", "via", "use", "un",
					"up", "down", "left", "right", "under", "one", "two", "top", "over", "less", "more", "la", "its", "high", "low", "do", "de",
					"c", "all", "you", "me", "they");
			
			// get rid of empty ones, stop words, and numeric
			if (next.equals("") || stopWords.contains(next) || next.matches("[-+]?\\d*\\.?\\d+"))
				continue;
			if (!wordCounts.containsKey(next)) {
				wordCounts.put(next, 1);
			} else {
				wordCounts.put(next, wordCounts.get(next) + 1);
			}
		}

		// System.out.println("Total words = " + wordCounts.size());

		for (String word : wordCounts.keySet()) {
			int count = wordCounts.get(word);
			if (count >= minOccurances)
				System.out.println(count + "\t" + word);
		}
	}
}