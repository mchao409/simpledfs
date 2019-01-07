package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import file.ChunkReader;
import file.FileChunk;
import file.SystemFile;

class TestChunkReader {
	
	public boolean equalsIgnorePadding(byte[] arr1, byte[] arr2) {
		int len = Math.min(arr1.length, arr2.length);
		for(int i = 0; i < len; i++) {
			if(arr1[i] != arr2[i]) return false;
		}
		byte[] longer;
		if(arr1.length > arr2.length) longer = arr1;
		else longer = arr2;
		for(int i = len; i < longer.length; i++) {
			if(longer[i] != 0) return false;
		}
		return true;
	}

	@Test
	void test() throws IOException {
		BufferedInputStream f = new BufferedInputStream(new FileInputStream("src/test/resources/chunkReaderTest.txt"));
		ChunkReader reader = new ChunkReader(f);
		SystemFile file = new SystemFile();
		while(reader.available()) {
			file.add_chunk(reader.read_chunk());
		}
		byte[] arr = file.get_byte_arr();
		File open = new File("src/test/resources/chunkReaderTest.txt");
		byte[] data = Files.readAllBytes(open.toPath());

		
		assertTrue(equalsIgnorePadding(data,arr));


	}

}
