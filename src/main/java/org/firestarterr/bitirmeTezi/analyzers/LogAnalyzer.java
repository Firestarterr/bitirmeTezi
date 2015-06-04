package org.firestarterr.bitirmeTezi.analyzers;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class LogAnalyzer {

    @Test
    public void runTests() throws IOException, MarshalException, MappingException, ParseException, ValidationException {

        HysAnalyzer hysAnalyzer = new HysAnalyzer();
        hysAnalyzer.run();

        OrcaAnalyzer orcaAnalyzer = new OrcaAnalyzer();
        orcaAnalyzer.run();

//        TranetAnalyzer tranetAnalyzer = new TranetAnalyzer();
//        tranetAnalyzer.run();

        System.out.println("analyze-ended.");
    }

}
