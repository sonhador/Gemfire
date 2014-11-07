package io.pivotal.kr.load_gen;

public class Line {
	private String line;
	private long offset;

	public Line (String line, long offset) {
		this.line = line;
		this.offset = offset;
	}
	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
}
