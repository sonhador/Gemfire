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
