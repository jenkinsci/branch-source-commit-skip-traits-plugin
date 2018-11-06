package org.jenkinsci.plugins.scm_filter;

public class CouldNotGetCommitAuthorException extends Exception {
	public CouldNotGetCommitAuthorException(Exception e){
		super(e);
	}

	public CouldNotGetCommitAuthorException(String message) {
		super(message);
	}
}
