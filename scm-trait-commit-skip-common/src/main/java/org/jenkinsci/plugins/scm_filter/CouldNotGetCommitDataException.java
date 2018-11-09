package org.jenkinsci.plugins.scm_filter;

public class CouldNotGetCommitDataException extends Exception {
	public CouldNotGetCommitDataException(Exception e){
		super(e);
	}

	public CouldNotGetCommitDataException(String message) {
		super(message);
	}
}
