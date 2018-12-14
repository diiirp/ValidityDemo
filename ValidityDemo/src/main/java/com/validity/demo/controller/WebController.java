package com.validity.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.gson.Gson;
import com.validity.demo.utils.AppConstants;
import com.validity.demo.utils.AppConstants.GROUPS;
import com.validity.demo.utils.InputFileHandler;


@Controller
public class WebController {

	/*
 controller which processes the input file, groups the data based on uniqueness or duplicity into a
 map, then converts the data into json before sending it back to the view.
	 */

	@GetMapping ("/getSortedData")
	public String groupDataByDuplicity(	
			Model model) throws Exception {

 
		Map<AppConstants.GROUPS,List<List<String>>> result = new HashMap<AppConstants.GROUPS,List<List<String>>>();

		InputFileHandler parseInput = new InputFileHandler(AppConstants.INPUT_FILE_PATH);
		parseInput.parseData();
		List<String> dataReceived = new ArrayList <String> (parseInput.getSourceData());

		if (dataReceived.size() >0 ) { 

			// remove header row
			dataReceived.remove(0);

			List<List<String>> duplicateList = new  ArrayList<>();
			List<List<String>> uniqueList = new  ArrayList<>(Collections.emptyList());
			int i = 0;
			while(i < dataReceived.size())
			{

				List<Integer> dupIndex = new ArrayList <> ();

				for(int j=0; j<dataReceived.size(); j++)
				{

					if(i==j) continue; 

					if (parseInput.findDuplicates(dataReceived.get(i), dataReceived.get(j))) {
						if (!dupIndex.contains(i))dupIndex.add(i);
						dupIndex.add(j); 
					}
				}

				if(dupIndex.size() > 0) { 

					//collect duplicates
					List<String>  dupes = new  ArrayList<>();
					for(int ind:dupIndex) {
						dupes.add( dataReceived.get(ind));
					}				
					duplicateList.add(dupes);

					// remove duplicates from bottom up
					Collections.reverse(dupIndex);
					dupIndex.forEach( s -> dataReceived.remove(s.intValue()));
					i =0; // reset cursor
				} else { 
					i++;// unique record, so continue
				}  

			}
			//collect all unique
			if (!dataReceived.isEmpty())uniqueList.add(dataReceived);

			// add it to the map			  
			result.put(GROUPS.UNIQUE_LIST, uniqueList);
			result.put(GROUPS.DUPLICATE_LIST, duplicateList);
		}

		//convert into json
		Gson gson = new Gson();

		model.addAttribute("resultList",gson.toJson(result));
		return "results";

	}


}
