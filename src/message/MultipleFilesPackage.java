package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import network.FileContents;

public class MultipleFilesPackage extends MessagePackage implements Serializable, Iterable<FileContents>{
	
	List<FileContents> files;

	public MultipleFilesPackage() {
		super(9);
		files = new ArrayList<FileContents>();
	}
	
	public void addFile(FileContents f ) {
		files.add(f);
	}

	@Override
	public Iterator<FileContents> iterator() {
		return files.iterator();
	}
	
}
