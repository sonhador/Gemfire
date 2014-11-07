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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class LineReader {
	private BufferedInputStream reader;
	private RandomAccessFile file;
	private byte []cbuf;
	private byte []rbuf;
	private long offset;
	private int bufSize;
	private int rbufIdx;
	private int readBytes;
	private String lineDelimiter = null;
	
	private List<Long> offsets = new ArrayList<Long>();
	private List<String> lineElems = new ArrayList<String>();
	
	public LineReader(int bufSize, String path) throws IOException {
		this.file = new RandomAccessFile(path, "r");
		this.reader =  	new BufferedInputStream(new FileInputStream(file.getFD()));
		this.cbuf = new byte[bufSize];
		this.rbuf = new byte[bufSize];
		this.offset = 0L;
		this.bufSize = bufSize;
		this.rbufIdx = 0;
	}
	
	public long getFileSize() throws IOException {
		return file.length();
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public void seek(long offset) throws IOException {
		reader.skip(offset);
		this.offset = offset;
	}
	
	private boolean recognizeLineDelimiter(byte []line) {
		if (lineDelimiter != null) {
			return true;
		}
		
		String strLine = new String(line);
		
		if (strLine.indexOf("\n\r") >= 0) {
			lineDelimiter = "\n\r";
		} else if (strLine.indexOf("\n") >= 0) {
			lineDelimiter = "\n";
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean readLines(List<Line> lines) throws IOException {
		lines.clear();
		
		int idx = 0;
		if (rbufIdx > 0) {
			for (; idx<rbufIdx; idx++) {
				cbuf[idx] = rbuf[idx];
			}
		}
		
		if ((readBytes = reader.read(cbuf, idx, bufSize - idx)) == -1) {
			if (rbufIdx > 0) {
				lines.add(new Line(new String(ArrayUtils.subarray(rbuf, 0, rbufIdx)), offset));
			}
			
			return false;
		}
		
		if (recognizeLineDelimiter(cbuf) == false) {
			throw new IOException("line delimiter cannot be found !!");
		}
			
		offsets.clear();
		lineElems.clear();
		
		rbufIdx = 0;
		long len = offset;
		for (int i=0; i<idx+readBytes; i++) {
			rbuf[rbufIdx] = cbuf[i];
			
			if (lineDelimiter.equals("\r\n")) {
				if (cbuf[i] == '\r') {
					lineElems.add(new String(ArrayUtils.subarray(rbuf, 0, rbufIdx)));
					offsets.add(len - rbufIdx);
				} else if (cbuf[i] == '\n') {
					rbufIdx = -1;
				}
			} else {
				if (cbuf[i] == '\n') {
					lineElems.add(new String(ArrayUtils.subarray(rbuf, 0, rbufIdx)));
					offsets.add(len - rbufIdx);
					rbufIdx = -1;
				}
			}
			
			rbufIdx++;
			len++;
		}
		
		if (lineElems.size() == 0) {
			return false;
		}
		
		for (int i=0; i < lineElems.size(); i++) {
			lines.add(new Line(lineElems.get(i), offsets.get(i)));
		}

		offset = len - rbufIdx;
		
		return true;
	}
}
