package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.trait.*;

/**
 * @author witokondoria
 */
public abstract class BranchCommitSkipTrait extends SCMSourceTrait{

    /**
     * Constructor for stapler.
     */
    public BranchCommitSkipTrait(){
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract void decorateContext(SCMSourceContext<?, ?> context);

    /**
     * Our descriptor.
     */
    public abstract static class BranchCommitSkipTraitDescriptorImpl extends SCMSourceTraitDescriptor{

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Commit message filtering behaviour (branches)";
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
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case insensitive).
     */
    public abstract static class ExcludeBranchesSCMHeadFilter extends CommitSkipTrait.ExcludePRsSCMHeadFilter {

        public ExcludeBranchesSCMHeadFilter() {
        }
    }
}
