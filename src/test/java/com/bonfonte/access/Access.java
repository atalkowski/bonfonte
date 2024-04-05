package com.bonfonte.access;

public class Access {
    private String privateString     = "privateString";
    protected String protectedString = "protectedString";
    public String publicString       = "publicString";
    String defaultString             = "defaultString";
    public String getPrivateString() {
    	return privateString;
    }
    
    public Access() {
    	
    }
}
