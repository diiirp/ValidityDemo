package com.validity.demo.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.language.Metaphone;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class InputFileHandler {

	public List<String> sourceData = new ArrayList<>();
	private String filePath ;
	
	public InputFileHandler(String filePath) throws Exception
	{
		if (filePath == null) throw new Exception ("Input File Path cannot be null");
		this.filePath=filePath;
	}
	 
	/**
	 * @return the source data as list 
	 */
	public List<String> getSourceData() {
		return Collections.unmodifiableList(sourceData);
	}

	
/*
 * parse the csv file and get each line and add it to the list
 */
	public void parseData()
	{
		Resource resource = new ClassPathResource(this.filePath);
		try (Stream<String> stream = Files.lines(resource.getFile().toPath())) { 
			sourceData = stream
					.filter(line -> !line.startsWith(" ")) 
					.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *  uses the Apache codec Metaphoe algorithm to find the similarity between 2 strings
	 */
	public boolean findDuplicates(String firstString, String secondString)
	{
		boolean dupResult = false;
		Metaphone m = new Metaphone();
		dupResult = m.isMetaphoneEqual(firstString, secondString);
		return dupResult;
	}
}
