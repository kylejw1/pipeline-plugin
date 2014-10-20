/*
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.workflow.steps;

import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import java.io.InputStream;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public final class ReadFileStep extends AbstractStepImpl {

    private final String file;
    private String encoding;

    @DataBoundConstructor public ReadFileStep(String file) {
        if (file.startsWith("/") || file.contains("\\") || file.contains("..")) {
            throw new IllegalArgumentException("only relative paths using / as the separator are accepted");
        }
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public String getEncoding() {
        return encoding;
    }

    @DataBoundSetter public void setEncoding(String encoding) {
        this.encoding = Util.fixEmptyAndTrim(encoding);
    }

    @Extension public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(Execution.class);
        }

        @Override public String getFunctionName() {
            return "readFile";
        }

        @Override public String getDisplayName() {
            return "Read file from workspace";
        }

    }

    public static final class Execution extends AbstractSynchronousStepExecution<String> {

        @Inject private ReadFileStep step;
        @StepContextParameter private FilePath workspace;

        @Override protected String run() throws Exception {
            InputStream is = workspace.child(step.file).read();
            try {
                return IOUtils.toString(is, step.encoding);
            } finally {
                is.close();
            }
        }

    }

}
