package com.reimanalex.Spider;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/****************************************************************************
 * <b>Title:</b> WebParser.java
 * <b>Project:</b> lib
 * <b>Description:</b> Class with methods to help parse through a webpage.
 * <b>Copyright:</b> Copyright (c) 2023
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Alex Reiman
 * @version 3.x
 * @since Aug 11, 2023
 * <b>updates:</b>
 *  
 ****************************************************************************/

public class WebParser {
	/**
	 * Uses a string of the HTML of a website to find links and build a list of them.
	 * @param html
	 * @return
	 */
	public Queue<Link> findLinks(String html) {
		//build the list for the links
		Queue<Link> linkQueue = new LinkedList<>();
		//use jsoup to parse for the links
		Document doc = Jsoup.parse(html);
		List<Element> links = doc.select("a[href]");
		//loop through the links to build out the information needed.
		for (Element link: links) {
			if(link.attr("href").startsWith("/")) {
				Link linkInfo = new Link(link.text(), link.attr("href"));
				linkQueue.add(linkInfo);
			}
		}
		return linkQueue;
	}
}
