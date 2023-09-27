package com.reimanalex.Spider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import lombok.extern.log4j.Log4j2;

/****************************************************************************
 * <b>Title:</b> ConnectionController.java
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
public class RequestController {
	
	private Set<String> cookieSet = new HashSet<>();
	
	/**
	 * Method that processes a request using an SSL socket.
	 * @param host the host for the request
	 * @param port the port for the socket
	 * @param requestType the type of the request, must be upper case.
	 * @param path the path of the page for the request.
	 * @param postBody the body of the request
	 * @return a string of the HTML for a page if it is a get request.
	 * @throws IOException
	 */
	public String processRequest(String host, int port, String requestType, String path,  String postBody) throws IOException {
		// build string builder
		StringBuilder html = new StringBuilder();
		// create a closeable SSLSocket
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
			log.info("Socket Built");
			// open an output stream
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			// build request string based on request type and if you have cookies
			String request = requestBuilder(requestType, path, host, postBody);
			out.write(request.getBytes());
			out.flush();
			log.info("Request Made");
			
			// open input stream and append input to string builder by line
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String inData = "";
			while ((inData = in.readLine()) != null) {
				html.append(inData);
				html.append("\r\n");

				// if a line contains cookie, add that line to the set in cookies map
				if (inData.contains("Cookie")) {
					cookieSet.add(inData + "\r\n");
				}
			}
			log.info("Response recieved");
		}
		// if the request type is a GET, return the html response as a substring with the headers removed
		return (requestType.equals("GET")) ? html.substring(html.indexOf("DOCTYPE")-2, html.indexOf("</html>") + 7) : "";
	}
	
	/**
	 * Takes the cookies and formats them to a string for the request.
	 * @return a string of cookies for the request.
	 */
	public String buildCookieRequest() {
		StringBuilder builder = new StringBuilder();
		for (String cookie : cookieSet) {
			// each cookie begins with "Set-Cookie: " - always 12 characters to remove. Make substring of the cookie starting at index 12 and ending at the next space
			String str = cookie.substring(12, cookie.indexOf(" ", 12));
			builder.append(str);
		}
		// returns a single string that is a chain of all the cookies with extra characters removed
		return builder.toString();
	}
	
	/**
	 * Builds the request string based on request type and if cookies exist.
	 * @param requestType the type of the request, must be upper case.
	 * @param path the path of the page for the request.
	 * @param host the host for the request
	 * @param postBody the body of the request
	 * @return request string
	 */
	public String requestBuilder(String requestType, String path, String host, String postBody) {
		String request = "";
		if (requestType.equals("POST") && cookieSet.isEmpty()) {
			request = requestType + " " + path + " HTTP/1.1\r\n" +
					"Host: " + host + "\r\n" + 
					"Content-Length: " + postBody.length() + "\r\n" +
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"\r\n" +
					postBody + "\r\n" +
					"Connection: close\r\n\r\n";
		} else if (requestType.equals("POST") && !cookieSet.isEmpty()) {
			request = requestType + " " + path + " HTTP/1.1\r\n" +
					"Host: " + host + "\r\n" +
					"Cookie: " + buildCookieRequest() + "\r\n" +
					"Content-Length: " + postBody.length() + "\r\n" +
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"\r\n" +
					postBody + "\r\n" +
					"Connection: close\r\n\r\n";
		} else if (requestType.equals("GET") && cookieSet.isEmpty()) {
			request = requestType + " " + path + " HTTP/1.1\r\n" +
					"Host: " + host + "\r\n" +
					"\r\n" +
					"Connection: close\r\n\r\n";
		} else if (requestType.equals("GET") && !cookieSet.isEmpty()) {
			request = requestType + " " + path + " HTTP/1.1\r\n" +
					"Host: " + host + "\r\n" +
					"Cookie: " + buildCookieRequest() + "\r\n" +
					"\r\n" +
					"Connection: close\r\n\r\n";
		}
		return request;
	}
}
