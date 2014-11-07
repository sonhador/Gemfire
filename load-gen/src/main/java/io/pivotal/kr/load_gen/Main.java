/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 MongJu Jung <mjung@pivotal.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
