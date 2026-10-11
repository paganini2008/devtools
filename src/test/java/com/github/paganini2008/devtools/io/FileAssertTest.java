/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileAssertTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FileAssertTest {

	@TempDir
	File tempDir;

	private File file;
	private File missing;

	@BeforeEach
	public void setUp() throws IOException {
		file = new File(tempDir, "f.txt");
		Files.write(file.toPath(), new byte[] { 1 });
		missing = new File(tempDir, "missing");
	}

	@Test
	public void isFile_throwsWhenArgumentIsAnExistingFile() throws IOException {
		IOException e = assertThrows(IOException.class, () -> FileAssert.isFile(file));
		assertEquals("File '" + file + "' is existed but is a file.", e.getMessage());
		FileAssert.isFile(tempDir);
		FileAssert.isFile(missing);
		FileAssert.isFile(null);
		assertEquals("custom", assertThrows(IOException.class, () -> FileAssert.isFile(file, "custom")).getMessage());
	}

	@Test
	public void isNotFile_throwsWhenArgumentIsNotAnExistingFile() throws IOException {
		FileAssert.isNotFile(file);
		assertThrows(IOException.class, () -> FileAssert.isNotFile(tempDir));
		assertThrows(IOException.class, () -> FileAssert.isNotFile(missing));
		assertThrows(IOException.class, () -> FileAssert.isNotFile(null));
		assertEquals("m", assertThrows(IOException.class, () -> FileAssert.isNotFile(missing, "m")).getMessage());
	}

	@Test
	public void isDirectory_throwsWhenArgumentIsAnExistingDirectory() throws IOException {
		IOException e = assertThrows(IOException.class, () -> FileAssert.isDirectory(tempDir));
		assertEquals("File '" + tempDir + "' is existed but is a directory.", e.getMessage());
		FileAssert.isDirectory(file);
		FileAssert.isDirectory(missing);
		FileAssert.isDirectory(null);
	}

	@Test
	public void isNotDirectory_throwsWhenArgumentIsNotAnExistingDirectory() throws IOException {
		FileAssert.isNotDirectory(tempDir);
		IOException e = assertThrows(IOException.class, () -> FileAssert.isNotDirectory(file));
		assertEquals("File '" + file + "' doesn't exists or it's not a directory.", e.getMessage());
		assertThrows(IOException.class, () -> FileAssert.isNotDirectory(missing));
		assertThrows(IOException.class, () -> FileAssert.isNotDirectory(null));
	}

	@Test
	public void existed_throwsWhenFileExists() throws IOException {
		IOException e = assertThrows(IOException.class, () -> FileAssert.existed(file));
		assertEquals("File '" + file + "' is already existed.", e.getMessage());
		FileAssert.existed(missing);
		FileAssert.existed(null);
	}

	@Test
	public void notExisted_throwsFileNotFoundWhenMissing() throws IOException {
		FileAssert.notExisted(file);
		FileNotFoundException e = assertThrows(FileNotFoundException.class, () -> FileAssert.notExisted(missing));
		assertEquals("File '" + missing + "' is not existed.", e.getMessage());
		assertThrows(FileNotFoundException.class, () -> FileAssert.notExisted(null));
	}

	@Test
	public void cannotWriteAndCannotRead_throwWhenFileIsMissing() throws IOException {
		FileAssert.cannotWrite(file);
		FileAssert.cannotRead(file);
		IOException e = assertThrows(IOException.class, () -> FileAssert.cannotWrite(missing));
		assertEquals("File '" + missing + "' doesn't exists or can not be writen.", e.getMessage());
		e = assertThrows(IOException.class, () -> FileAssert.cannotRead(missing));
		assertEquals("File '" + missing + "' doesn't exists or can not be read.", e.getMessage());
		assertThrows(IOException.class, () -> FileAssert.cannotRead(null));
		assertThrows(IOException.class, () -> FileAssert.cannotWrite(null));
	}
}
