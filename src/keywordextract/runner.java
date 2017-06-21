package keywordextract;

import java.io.IOException;
import java.util.List;

public class runner {

	public static void main(String[] args) throws IOException {
		List<CardKeyword> keywordsList;

		for (int i=0;i<1;i++){
			String text = "amin mailnia amin aminmf milani";
			keywordsList = KeywordsExtractor.getKeywordsList(text);
			for (CardKeyword k: keywordsList)
				System.out.println(k.getStem() + ", " + k.getFrequency());

		}
	}

}
