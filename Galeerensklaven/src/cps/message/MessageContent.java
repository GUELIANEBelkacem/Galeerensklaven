package cps.message;

import java.io.Serializable;

public class MessageContent implements Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2676480461872565232L;
	private final String message;


	  public MessageContent(String message) {
	    this.message = message;
	  }

	  public String getMessage() {
	    return this.message;
	  }

	}