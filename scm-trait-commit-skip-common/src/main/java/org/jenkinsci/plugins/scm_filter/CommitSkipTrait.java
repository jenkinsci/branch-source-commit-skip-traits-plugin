package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMHeadFilter;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;

import java.io.IOException;

/**
 * @author witokondoria
 */
public abstract class CommitSkipTrait extends SCMSourceTrait{

    /**
     * Constructor for stapler.
     */
    public CommitSkipTrait(){
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract void decorateContext(SCMSourceContext<?, ?> context);

    /**
     * Our descriptor.
     */
    public abstract static class CommitSkipTraitDescriptorImpl extends SCMSourceTraitDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Commit message filtering behaviour";
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicableToSCM(@NonNull SCMDescriptor<?> scm) {
            return scm instanceof GitSCM.DescriptorImpl;
        }
    }

    /**
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case unsensitive).
     */
    public abstract static class ExcludePRsSCMHeadFilter extends SCMHeadFilter {

        public ExcludePRsSCMHeadFilter() {
        }

        @Override
        abstract public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException;
    }
}
