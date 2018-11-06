package org.jenkinsci.plugins.scm_filter;

public class CouldNotGetCommitMessageException extends Exception {
	public CouldNotGetCommitMessageException(Exception e){
		super(e);
	}

	public CouldNotGetCommitMessageException(String message) {
		super(message);
	}
}
