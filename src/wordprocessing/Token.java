	package wordprocessing;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Token {

	public static void tokenizeAndLowerCase(String line,
			ArrayList<String> tokens) {
		// TODO Auto-generated method stub
		line = line.replaceAll("[^\\w\\s]", "");
		line = line.replaceAll("[_]", "");
		line = line.replaceAll("[0-9]", "");
		line = line.replaceAll("['']", "");
		StringTokenizer strTok = new StringTokenizer(line);
		while (strTok.hasMoreTokens()) {
			String token = strTok.nextToken();
			tokens.add(token.toLowerCase().trim());
		}
	}
}
