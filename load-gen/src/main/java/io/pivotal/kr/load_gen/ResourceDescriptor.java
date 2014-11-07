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
import java.util.ArrayList;
import java.util.List;

public class ResourceDescriptor {
	private String path;
	private long startPos;
	private long endPos;
	
	private LineReader reader;
	
	private List<Line> lines = new ArrayList<Line>();
	private List<String> linesToSelect = new ArrayList<String>();
	
	public ResourceDescriptor(String path, long startPos, long endPos) {
		this.path = path;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public void openFile() throws IOException {
		reader = new LineReader(204800, this.path);
		reader.seek(startPos);
	}
	
	public void closeFile() throws IOException {
		reader.close();
	}
	
	public void rewind() throws IOException  {
		reader.seek(this.startPos);
	}
	
	public String[] readLines() throws IOException {
		lines.clear();
		
		boolean remainsToRead = reader.readLines(lines);
		
		if (remainsToRead == false || lines.size() == 0) {
			return null;
		}
		
		if (lines.get(0).getOffset() >= this.endPos) {
			return null;
		}
		
		linesToSelect.clear();
		
		for (int i=0; i<lines.size(); i++) {
			if (lines.get(i).getOffset() >= this.endPos) {
				break;
			}

			linesToSelect.add(lines.get(i).getLine());
		}
		
		return linesToSelect.toArray(new String[0]);
	}
}
