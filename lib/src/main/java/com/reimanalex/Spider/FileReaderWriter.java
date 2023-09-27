package com.reimanalex.Spider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.extern.log4j.Log4j2;


/****************************************************************************
 * <b>Title:</b> FileReaderWriter.java
 * <b>Project:</b> Spider-lib
 * <b>Description:</b> Class that can read and write files.
 * <b>Copyright:</b> Copyright (c) 2023
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Alex Reiman
 * @version 3.x
 * @since Jul 31, 2023
 * <b>updates:</b>
 *  
 ****************************************************************************/
@Log4j2
public class FileReaderWriter{
	
	private String fileSeparator = System.getProperty("file.separator");
	/**
	 * Reads a file and turns it into a byte array.
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public byte[] reader(String path, String name) throws IOException {
		// Try with resources to create a FileInputStream
		try(FileInputStream in = new FileInputStream(path + fileSeparator+ name)) {
			//return a byte array
			return in.readAllBytes();
		}
		
	}
	
	/**
	 * Creates a file and writes data into it.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public File writer(String path, String name, byte[] data) throws IOException {
		// Create a file
		File output = new File(path + fileSeparator + name);
		if (output.createNewFile()) {
			log.info("File created: " + fileSeparator + path + fileSeparator +name);
		} else {
			log.info(fileSeparator + path + fileSeparator + name + " already exists");
		}
		//Try with resources to create new FileOutputStream
		try (FileOutputStream out = new FileOutputStream(output)) {
			//Write data to new file.
			out.write(data);
		}
		return output;
	}
	
	/**
	 * Takes in a string of data and writes it to a File.
	 * @param data
	 * @param path
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File writeHTML(String data, String path, String fileName) throws IOException {
		// Convert data to byte array.
		byte[] input = data.getBytes();
		// Use File Writer to write the file.
		return writer(path, fileName, input);
	}
}
