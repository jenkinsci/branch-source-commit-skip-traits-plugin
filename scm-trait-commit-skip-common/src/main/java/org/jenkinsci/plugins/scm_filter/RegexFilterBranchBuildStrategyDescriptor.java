package org.jenkinsci.plugins.scm_filter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

import hudson.util.FormValidation;
import jenkins.branch.BranchBuildStrategyDescriptor;

public class RegexFilterBranchBuildStrategyDescriptor extends BranchBuildStrategyDescriptor {
	public FormValidation doCheckPattern(@QueryParameter String value) {
		if (StringUtils.isBlank(value)) {
			return FormValidation.error("Cannot be empty");
		}
		try {
			Pattern.compile(value);
		} catch (PatternSyntaxException e){
			return FormValidation.error("Regex syntax error: "+e.getMessage());
		}
		return FormValidation.ok();
	}
}
