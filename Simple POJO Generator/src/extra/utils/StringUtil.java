package extra.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class StringUtil {
	/**
	 * This function takes an input String and formats it in CamelCase.
	 * it looks for "_" and treats them as word separator tokens
	 * @param str input string
	 * @return the string provided in CamelCase
	 */
	public static String toCamelCase(String str) {		
		return toCamelCase(str, "_");
	}
	
	/**
	 * This function takes an input String and formats it in CamelCase.
	 * it looks for the token provided and treats them as word separator tokens
	 * @param str input string
	 * @param token a word separator token
	 * @return the string provided in CamelCase
	 */
	public static String toCamelCase(String str, String token) {
		String result=str.substring(0, 1).toUpperCase();
		if(str.contains(token)) {
			String[] aux=str.split(token);
			result+=aux[0].substring(1).toLowerCase();
			for(int i=1;i<aux.length;i++) {
				result+=aux[i].substring(0, 1).toUpperCase();
				result+=aux[i].substring(1).toLowerCase();
			}
		} else {
			result+=str.substring(1);
		}
		return result;
	}
	
	public static String removeDiacriticalMarks(String str) {
		return Normalizer.normalize(str, Form.NFD).replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
	}
}
