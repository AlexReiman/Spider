package com.reimanalex.Spider;

/****************************************************************************
 * <b>Title:</b> Link.java
 * <b>Project:</b> lib
 * <b>Description:</b> Class for links that have been parsed from an HTML document.
 * <b>Copyright:</b> Copyright (c) 2023
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Alex Reiman
 * @version 3.x
 * @since Aug 22, 2023
 * <b>updates:</b>
 *  
 ****************************************************************************/

public class Link {
	//build out class properties
	private String title;
	private String url;
	
	
	String urlString = System.getenv("URL");
	/**
	 * Constructor for Link that includes env variable for URL
	 * @param title
	 * @param url
	 */
	public Link(String title, String url) {
		super();
		this.title = title;
		this.url = urlString + url;
	}
	/**
	 * Getter for the title of a link.
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Getter for the url of a link.
	 * @return url
	 */
	public String getUrl() {
		return url;
	}
}
