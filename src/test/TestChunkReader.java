package test;

import static org.junit.jupiter.api.Assertions.*;
import server.Constants;

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

		
		assertTrue(Constants.equalsIgnorePadding(data,arr));


	}

}
