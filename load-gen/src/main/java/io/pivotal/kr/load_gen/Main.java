package io.pivotal.kr.load_gen;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Main 
{
	private static Pattern leadingQuotePattern = Pattern.compile("^\\s*\"\\s*(.+)");
	private static Pattern tailingQuotePattern = Pattern.compile("(.+)\\s*\"\\s*(.+)$");
	
	private static String stripSurroundingQuotes(String expr) {
		Matcher matcher = leadingQuotePattern.matcher(expr);
		
		if (matcher.matches()) {
			String leadingQuoteStripped = matcher.group(1);
			
			matcher = tailingQuotePattern.matcher(leadingQuoteStripped);
			
			if (matcher.matches()) {
				return matcher.group(1);
			} else {
				return leadingQuoteStripped;
			}
		} else {
			return expr;
		}
	}
	
    public static void main( String[] args ) throws IOException
    {
    	if (args.length != 5) {
    		System.err.println("Correct usage: java -jar load-gen.jar <insert|select|update|delete> <dataDir> <sqlMapConfigXMLPath> <delimiter> <numberOfThreads>");
    		
    		System.exit(-1);
    	}
    	
    	String mode = args[0];
    	String dataDir = args[1];
    	String sqlMapConfigXMLPath = args[2];
    	String delimiter = args[3];
    	String numberOfThreads = args[4];
    	
    	if (StringUtils.isEmpty(mode) ||
    		StringUtils.isEmpty(dataDir) || 
    		StringUtils.isEmpty(sqlMapConfigXMLPath) ||
    		StringUtils.isEmpty(delimiter) ||
    		StringUtils.isEmpty(numberOfThreads)) {
    		System.err.println("Correct usage: java -jar load-gen.jar <insert|select|update|delete> <dataDir> <sqlMapConfigXMLPath> <delimiter> <numberOfThreads>");
    		
    		System.exit(-1);
    	}
    	
    	dataDir = stripSurroundingQuotes(dataDir);
    	sqlMapConfigXMLPath = stripSurroundingQuotes(sqlMapConfigXMLPath);
    	delimiter = stripSurroundingQuotes(delimiter);
    	
    	new GemfireXDLoadManager(mode, dataDir, sqlMapConfigXMLPath, delimiter, Integer.parseInt(numberOfThreads));
    }
}
