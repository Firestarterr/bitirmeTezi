package org.firestarterr.bitirmeTezi.analyzers;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class OrcaAnalyzer extends BaseGitAnalyzer {

    public OrcaAnalyzer() {
        super("ORCA-REPORT");
    }

    @Override
    public void run() throws IOException, MappingException, MarshalException, ValidationException, ParseException {
        java.io.File file = new java.io.File("C:\\Users\\murat\\Desktop\\workspace\\orca-report\\log.log");

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        CommitWrapper parsed = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("*-*-")) {
                if (parsed != null) {
                    createCommit(parsed);
                }
                parsed = parseCommitData(line);
            } else if (line.contains("|")) {
                parseCommitFileData(parsed, line);
            }
        }
        br.close();
        fr.close();
        analyzeData();
        System.out.println("orca-report-ended.");
    }

    @Override
    protected String getPackageName(String path) {
        String packageName;
        packageName = "root";
        return packageName;
    }

}
