package org.jenkinsci.plugins.pipelinestatus;

import groovy.lang.Binding;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;
import hudson.Extension;

import javax.annotation.Nonnull;

@Extension
public class StatusVarsDSL extends GlobalVariable {
    @Nonnull
    @Override
    public String getName() {
        return "statusvars";
    }

    @Nonnull
    @Override
    public Object getValue(@Nonnull CpsScript cpsScript) throws Exception {
        Binding binding = cpsScript.getBinding();
        Object statusVars;
        if (binding.hasVariable(getName())) {
            statusVars = binding.getVariable(getName());
        } else {
            statusVars = cpsScript.getClass().getClassLoader().loadClass("org.jenkinsci.plugins.pipelinestatus.StatusVars")
                    .getConstructor(CpsScript.class)
                    .newInstance(cpsScript);
            binding.setVariable(getName(), statusVars);
        }
        return statusVars;
    }
}
