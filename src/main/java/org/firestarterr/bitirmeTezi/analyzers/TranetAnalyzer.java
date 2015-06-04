package org.firestarterr.bitirmeTezi.analyzers;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class TranetAnalyzer extends BaseGitAnalyzer {

    public TranetAnalyzer() {
        super("TRANET");
    }

    @Override
    public void run() throws IOException, MappingException, MarshalException, ValidationException, ParseException {
        java.io.File file = new java.io.File("C:\\Users\\murat\\Desktop\\workspace\\tranet\\log.log");

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
        System.out.println("tranet-ended.");
    }

    @Override
    protected String getPackageName(String path) {
        String packageName;
        if (path.contains("tranet-core")) {
            packageName = "tranet-core";
        } else if (path.contains("tranet-installer")) {
            packageName = "tranet-installer";
        } else if (path.contains("tranet-web")) {
            packageName = "tranet-web";
        } else {
            packageName = "root";
        }
        return packageName;
    }
}
