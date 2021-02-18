package cps.message;

public class MessageContent implements java.io.Serializable {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;


	  public MessageContent(String message) {
	    this.message = message;
	  }

	  public String getMessage() {
	    return this.message;
	  }

	}