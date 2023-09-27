package com.reimanalex.Spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lombok.extern.log4j.Log4j2;


/****************************************************************************
 * <b>Title:</b> SpiderMain.java
 * <b>Project:</b> Spider-lib
 * <b>Description:</b> CHANGE ME!!
 * <b>Copyright:</b> Copyright (c) 2023
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Alex Reiman
 * @version 3.x
 * @since Sep 26, 2023
 * <b>updates:</b>
 *  
 ****************************************************************************/
@Log4j2
public class SpiderMain {
	//get environmental variables.
	private static final String mainHost = System.getenv("MAIN_HOST");
	private static final String authPath = System.getenv("AUTH_PATH");
	private static final String filePath = System.getenv("FILE_PATH");
	
	/**
	 * Main method that runs Spider project.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SpiderMain spider = new SpiderMain();
		spider.process();
	}
	
	/**
	 * Method that calls each of the two main methods to get the page that requires a login and that crawls the websit.
	 * @throws IOException
	 */
	public void process() throws IOException {
		accessAuthPage();
		log.info("Successfully copied page requiring authorization");
		crawl();
		log.info("Successfully copied pages from crawler");
	}
	
	/**
	 * Method that accesses a website that requires authentication and copies the page to a file.
	 * @throws IOException
	 */
	public void accessAuthPage() throws IOException {
		//instantiate other classes
		RequestController rc = new RequestController();
		FileReaderWriter frw = new FileReaderWriter();
		
		//encode username and password and add them to the authHeader
		String username = encode(System.getenv("USERNAME"));
		String password = encode(System.getenv("PASSWORD"));
		String authHeader = "requestType=reqBuild&pmid=ADMIN_LOGIN&emailAddress=" + username + "&password=" + password +"&l=";
		
		
		rc.processRequest(mainHost, 443, "POST", authPath, authHeader);
		log.info("POST made");
		String html = rc.processRequest(mainHost, 443, "GET", authPath, null);
		log.info("GET made");
		frw.writeHTML(html, filePath, "Auth.html");
	}
	
	/**
	 * Method that accesses a website, copies its html to a file, then goes to each link on the page and copies its html to 
	 * a file, repeating until there are no more links.
	 * @throws IOException
	 */
	public void crawl() throws IOException {
		FileReaderWriter frw = new FileReaderWriter();
			
		//read and find links for homepage
		Map<String, Object> homeData = getPageData(System.getenv("URL"), "/");
		String homeFile = System.getenv("FILE_NAME");
		//write home page
		frw.writeHTML(homeData.get("html").toString(), filePath, homeFile);
		//pull links from home data
		Queue<Link> links = (Queue<Link>) homeData.get("links");
		//set up set for used links.
		Set<String> usedLinks = new HashSet<>();
		//loop to read and write the links and add used links to the set so they are not repeated.
		while (!links.isEmpty()) {
			//get the first link from the queue
			Link link = links.poll();
			//if it hasn't been used
			if (!usedLinks.contains(link.getUrl()) ) {
				// break the title to a list to only get the first word
				List<String> titleList = new ArrayList<>(Arrays.asList(link.getTitle().split(" ")));
				//get page info for the link
				Map<String, Object> linkData = getPageData(link.getUrl(), "/" + titleList.get(0).toLowerCase());
				//add any links on that page to the queue of links
				Queue<Link> sublinks = (Queue<Link>) linkData.get("links");
				for (Link sublink: sublinks) {
					links.offer(sublink);
				}
				//write html to a file.
				frw.writeHTML(linkData.get("html").toString() ,filePath,  link.getTitle() + ".html");
				//add link to used links set.
				usedLinks.add(link.getUrl());
			}
		}
	}
	
	/**
	 * Method that encodes a value.
	 * @param value that you want to encode
	 * @return encoded value
	 * @throws UnsupportedEncodingException
	 */
	private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
	
	/**
	 * Function that builds page data from a url string.  It will find the host and port, read the html, and parse for links
	 * @param urlString
	 * @param path
	 * @return a map with keys of html and links that are the html of the page and links on the page.
	 * @throws IOException
	 */
	public Map<String, Object> getPageData(String urlString, String path) throws IOException {
		//instantiate RequestController and parser
		RequestController rc = new RequestController();
		WebParser wp = new WebParser();
		// instantiate a new HashMap
		Map<String, Object> pageData = new HashMap<>();
		//get info about the URL
		URL url = new URL(urlString);
		String host = url.getHost();
		int port = url.getDefaultPort();
		// make a get request using the information and add it to the page data
		String html = rc.processRequest(host, port, "GET", path, null);
		pageData.put("html", html);
		//parse for links and add them to the page data
		Queue<Link> links = wp.findLinks(html);
		pageData.put("links", links);
		return pageData;
	}
}
