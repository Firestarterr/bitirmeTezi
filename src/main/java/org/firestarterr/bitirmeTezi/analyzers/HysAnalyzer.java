package org.firestarterr.bitirmeTezi.analyzers;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.firestarterr.bitirmeTezi.model.*;
import org.firestarterr.bitirmeTezi.model.Package;
import org.firestarterr.bitirmeTezi.xmlmodals.Logentry;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HysAnalyzer {

//    @Autowired
//    transient CommitService commitService;
//
//    @Autowired
//    transient DeveloperService developerService;
//
//    @Autowired
//    transient FileService fileService;
//
//    @Autowired
//    transient ModuleService moduleService;
//
//    @Autowired
//    transient ProjectService projectService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    List<Project> projects = new ArrayList<>();
    List<Package> packages = new ArrayList<>();
    List<Issue> issues = new ArrayList<>();
    List<File> files = new ArrayList<>();
    List<Developer> developers = new ArrayList<>();
    List<Commit> commitSuccess = new ArrayList<>();

    @Test
    public void run() throws IOException, MappingException, MarshalException, ValidationException, ParseException {

        FileReader reader = new FileReader("C:\\Users\\murat\\Desktop\\workspace\\hysbitirme\\log.txt");

        // Define mapping.xml file address
        final String MAPPING_FILE = "C:\\Users\\murat\\Desktop\\workspace\\tranet\\tranet-core\\src\\main\\java\\org\\orcateam\\tranet\\core\\bitirme\\xmlmodals\\mapping.xml";
        // Load Mapping
        Mapping mapping = new Mapping();
        mapping.loadMapping(MAPPING_FILE);

        List<Logentry> log = new ArrayList<>();
        // create a new Marshaller
        Unmarshaller unmarshaller = new Unmarshaller(log);
        unmarshaller.setMapping(mapping);

        log = (List<Logentry>) unmarshaller.unmarshal(reader);

        Project project = new Project();
        project.setName("hys");

        Package root = new Package();
        root.setName("root");
        root.setProject(project);
        root.setCreatedDate(null);
        root.setRelCreatedDate(null);
        root.setUpdatedDate(null);
        root.setRelUpdatedDate(null);
        packages.add(root);

        projects.add(project);

        for (Logentry logentry : log) {
            createCommitData(logentry);
        }

        analyzeData();
        System.out.println("hys-ended.");
    }

    public Commit createCommitData(Logentry logentry) throws ParseException {
        Commit commit = new Commit();
        Developer dev = findOrCreateDeveloperList(logentry.author);
        dev.getCommits().add(commit);
        commit.setDeveloper(dev);
        Date date = sdf.parse(logentry.date);
        commit.setCommitDate(date);
        if (commit.getDeveloper().getIsOrcaDeveloper()) {
            commit.setRelCreatedDate(date);
        }

        String message = logentry.msg;
        if (message.startsWith("HYS-")) {
            int firstSpace = message.indexOf(" ");
            String issueName = null;
            try {
                issueName = message.substring(0, firstSpace);
            } catch (Exception e) {
                issueName = message;
            }
            String cm;
            try {
                cm = message.substring(firstSpace + 1);
            } catch (Exception e) {
                cm = "";
            }
            commit.setRelatedIssue(findOrCreateIssueList(issueName));
            commit.setName(cm);
        } else {
            commit.setName(message);
        }

        for (String path : logentry.getPaths().getPath()) {
            updateCommitFiles(commit, path);
        }
        updateDeveloperAndCommitUpdateDates(commit);

        commitSuccess.add(commit);
        return commit;
    }

    public Developer findOrCreateDeveloperList(String name) {
        for (Developer developer : developers) {
            if (developer.getName().equals(name)) {
                return developer;
            }
        }
        Developer dev = createDeveloperData(name);
        developers.add(dev);
        return dev;
    }

    public Issue findOrCreateIssueList(String name) {
        for (Issue issue : issues) {
            if (issue.getName().equals(name)) {
                return issue;
            }
        }
        Issue issue = new Issue();
        issue.setName(name);
        issues.add(issue);
        return issue;
    }

    public Developer createDeveloperData(String devName) {
        Developer developer = new Developer();
        developer.setName(devName);
        boolean isOrca = isOrcaDeveloper(devName);
        developer.setIsOrcaDeveloper(isOrca);
//        developers = developerService.save(developers);
        developer.setCreatedDate(null);
        developer.setRelCreatedDate(null);
        developer.setUpdatedDate(null);
        developer.setRelUpdatedDate(null);
        return developer;
    }

    public boolean isOrcaDeveloper(String devName) {
        return devName.equals("murats")
                || devName.equals("recepa")
                || devName.equals("kamilo")
                || devName.equals("elifg");
    }

    public void updateCommitFiles(Commit commit, String path) {
        if (path.lastIndexOf(".") == -1) {
            return;
        }
        File file = findOrCreateFileData(path);
        commit.getFiles().add(file);
        if (file.getUpdatedDate() == null) {
            file.setUpdatedDate(commit.getCommitDate());
        }
        file.setCreatedDate(commit.getCommitDate());
        if (commit.getDeveloper().getIsOrcaDeveloper()) {
            if (file.getRelUpdatedDate() == null) {
                file.setRelUpdatedDate(commit.getCommitDate());
            }
            file.setRelCreatedDate(commit.getCommitDate());
        }
        file.getCommits().add(commit);
        Integer locEdited = 1;
        if (commit.getLocEdited() == null) {
            commit.setLocEdited(locEdited);
        } else {
            commit.setLocEdited(commit.getLocEdited() + locEdited);
        }
        if (commit.getDeveloper().getLocEdited() == null) {
            commit.getDeveloper().setLocEdited(locEdited);
        } else {
            commit.getDeveloper().setLocEdited(commit.getDeveloper().getLocEdited() + locEdited);
        }
        if (file.getLocEdited() == null) {
            file.setLocEdited(locEdited);
        } else {
            file.setLocEdited(file.getLocEdited() + locEdited);
        }
        if (file.getModule().getLocEdited() == null) {
            file.getModule().setLocEdited(locEdited);
        } else {
            file.getModule().setLocEdited(file.getModule().getLocEdited() + locEdited);
        }
        commit.getLocEditedPerFile().put(file, locEdited);
    }

    public File findOrCreateFileData(String fileString) {
        int lastDot = fileString.lastIndexOf(".");
        String fileNameWithPath = null;
        fileNameWithPath = fileString.substring(0, lastDot).trim();

        int lastIndex = fileNameWithPath.lastIndexOf("/");
        String fileName;
        if (lastIndex == -1) {
            fileName = fileNameWithPath;
        } else {
            fileName = fileNameWithPath.substring(lastIndex + 1);
        }
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        File file = createFileData(fileString);

        files.add(file);
        return file;
    }

    public File createFileData(String fileString) {
        File file = new File();

        int lastDot = fileString.lastIndexOf(".");
        String fileNameWithPath = fileString.substring(11, lastDot).trim();
        String ext = fileString.substring(lastDot + 1).trim();

        int lastIndex = fileNameWithPath.lastIndexOf("/");
        String fileName;
        String fullPath = null;
        if (lastIndex == -1) {
            fileName = fileNameWithPath;
            fullPath = "";
        } else {
            fileName = fileNameWithPath.substring(lastIndex + 1).trim();
            fullPath = fileNameWithPath.substring(0, lastIndex).trim();
        }

        file.setFullPath(fullPath);
        file.setName(fileName);

        file.setModule(findOrCreateModuleData(fullPath));
        file.setProject(projects.get(0));
        file.setFileExt(ext);
        file.setUpdatedDate(null);
        file.setRelUpdatedDate(null);
        file.setCreatedDate(null);
        file.setRelCreatedDate(null);
//        file = fileService.save(file);
        return file;
    }

    public Package findOrCreateModuleData(String fullPath) {
        int index = fullPath.indexOf("/");
        if (index == -1) {
            return packages.get(0);
        } else {
            String moduleName = fullPath.substring(0, index).trim();
            for (Package module : packages) {
                if (module.getName().equals(moduleName)) {
                    return module;
                }
            }
            Package module = new Package();
            module.setName(moduleName);
            module.setProject(projects.get(0));
            module.setCreatedDate(null);
            module.setRelCreatedDate(null);
            module.setUpdatedDate(null);
            module.setRelUpdatedDate(null);
            packages.add(module);
            return module;
        }
    }

    public void updateDeveloperAndCommitUpdateDates(Commit commit) {
        Developer dev = commit.getDeveloper();
        if (dev.getUpdatedDate() == null) {
            dev.setUpdatedDate(commit.getCommitDate());
        }
        dev.setCreatedDate(commit.getCommitDate());

        for (Package pack : commit.getPackages()) {
            if (pack.getUpdatedDate() == null) {
                pack.setUpdatedDate(commit.getCommitDate());
            }
            pack.setCreatedDate(commit.getCommitDate());

            if (commit.getDeveloper().getIsOrcaDeveloper()) {
                if (pack.getRelUpdatedDate() == null) {
                    pack.setRelUpdatedDate(commit.getCommitDate());
                }
                pack.setRelCreatedDate(commit.getCommitDate());
            }
        }
    }

    public void analyzeData() {
        for (Commit commit : commitSuccess) {
            Developer dev = commit.getDeveloper();
            dev.increaseRecordCount();
            if (dev.getIsOrcaDeveloper()) {
                dev.increaseRelRecordCount();
            }

            for (Package pack : commit.getPackages()) {
                pack.increaseRecordCount();
                if (commit.getDeveloper().getIsOrcaDeveloper()) {
                    pack.increaseRelRecordCount();
                }
            }

            //private Map<Developer, Integer> developerChangeCountMap
            for (File file : commit.getFiles()) {
                file.increaseRecordCount();
                if (dev.getIsOrcaDeveloper()) {
                    file.increaseRelRecordCount();
                }
                if (file.getDeveloperChangeCountMap().keySet().contains(dev)) {
                    file.getDeveloperChangeCountMap().put(dev, file.getDeveloperChangeCountMap().get(dev) + 1);
                } else {
                    file.getDeveloperChangeCountMap().put(dev, 1);
                }
            }
        }


        for (File file : files) {
            for (Map.Entry<Developer, Integer> entry : file.getDeveloperChangeCountMap().entrySet()) {
                for (Map.Entry<Developer, Integer> iter : file.getDeveloperChangeCountMap().entrySet()) {
                    if (entry.getKey().getName().equals(iter.getKey().getName())) {
                        continue;
                    }
                    Developer dev1 = findOrCreateDeveloperList(entry.getKey().getName());
                    Developer dev2 = findOrCreateDeveloperList(iter.getKey().getName());
                    Integer minContribution = Math.min(entry.getValue(), iter.getValue());

                    //private Map<Developer, Integer> cooperationCount
                    if (dev1.getCooperationCount().get(dev2) == null) {
                        dev1.getCooperationCount().put(dev2, minContribution);
                    } else {
                        dev1.getCooperationCount().put(dev2, minContribution + dev1.getCooperationCount().get(dev2));
                    }
                    if (dev2.getCooperationCount().get(dev1) == null) {
                        dev2.getCooperationCount().put(dev1, minContribution);
                    } else {
                        dev2.getCooperationCount().put(dev1, minContribution + dev2.getCooperationCount().get(dev1));
                    }

                    //private Map<File, Map<Developer, Integer>> cooperatedOnFiles
                    if (dev1.getCooperatedOnFiles().get(file) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev2, minContribution);
                        dev1.getCooperatedOnFiles().put(file, coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev1.getCooperatedOnFiles().get(file);
                        if (coopMap.get(dev2) == null) {
                            coopMap.put(dev2, minContribution);
                            dev1.getCooperatedOnFiles().put(file, coopMap);
                        } else {
                            coopMap.put(dev2, minContribution + coopMap.get(dev2));
                            dev1.getCooperatedOnFiles().put(file, coopMap);
                        }
                    }
                    if (dev2.getCooperatedOnFiles().get(file) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev1, minContribution);
                        dev2.getCooperatedOnFiles().put(file, coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev2.getCooperatedOnFiles().get(file);
                        if (coopMap.get(dev1) == null) {
                            coopMap.put(dev1, minContribution);
                            dev2.getCooperatedOnFiles().put(file, coopMap);
                        } else {
                            coopMap.put(dev1, minContribution + coopMap.get(dev1));
                            dev2.getCooperatedOnFiles().put(file, coopMap);
                        }
                    }

                    //private Map<Module, Map<Developer, Integer>> cooperatedOnModules
                    if (dev1.getCooperatedOnModules().get(file.getModule()) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev2, minContribution);
                        dev1.getCooperatedOnModules().put(file.getModule(), coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev1.getCooperatedOnModules().get(file.getModule());
                        if (coopMap.get(dev2) == null) {
                            coopMap.put(dev2, minContribution);
                            dev1.getCooperatedOnModules().put(file.getModule(), coopMap);
                        } else {
                            coopMap.put(dev2, minContribution + coopMap.get(dev2));
                            dev1.getCooperatedOnModules().put(file.getModule(), coopMap);
                        }
                    }
                    if (dev2.getCooperatedOnModules().get(file.getModule()) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev1, minContribution);
                        dev2.getCooperatedOnModules().put(file.getModule(), coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev2.getCooperatedOnModules().get(file.getModule());
                        if (coopMap.get(dev1) == null) {
                            coopMap.put(dev1, minContribution);
                            dev2.getCooperatedOnModules().put(file.getModule(), coopMap);
                        } else {
                            coopMap.put(dev1, minContribution + coopMap.get(dev1));
                            dev2.getCooperatedOnModules().put(file.getModule(), coopMap);
                        }
                    }

                }
            }

            //change frequency calculations
            Double age = Double.valueOf(file.getUpdatedDate().getTime() - file.getCreatedDate().getTime());
            age = age / file.getRecordCount();
            file.setChangeFrequencyPerDay(age);

            if (file.getRelCreatedDate() != null) {
                Double relAge = Double.valueOf(file.getRelUpdatedDate().getTime() - file.getRelCreatedDate().getTime());
                relAge = relAge / file.getRelRecordCount();
                file.setRelChangeFrequencyPerDay(relAge);
            }
        }

        for (Developer developer : developers) {
            for (Map.Entry<Developer, Integer> entry : developer.getCooperationCount().entrySet()) {
                developer.getCooperationCount().put(entry.getKey(), entry.getValue() / 2);
            }

            for (Map.Entry<File, Map<Developer, Integer>> coopMapEntry : developer.getCooperatedOnFiles().entrySet()) {
                for (Map.Entry<Developer, Integer> entry : coopMapEntry.getValue().entrySet()) {
                    coopMapEntry.getValue().put(entry.getKey(), entry.getValue() / 2);
                }
            }

            for (Map.Entry<Package, Map<Developer, Integer>> coopMapEntry : developer.getCooperatedOnModules().entrySet()) {
                for (Map.Entry<Developer, Integer> entry : coopMapEntry.getValue().entrySet()) {
                    coopMapEntry.getValue().put(entry.getKey(), entry.getValue() / 2);
                }
            }

            //change frequency calculations
            Double age = Double.valueOf(developer.getUpdatedDate().getTime() - developer.getCreatedDate().getTime());
            age = age / developer.getRecordCount();
            developer.setChangeFrequencyPerDay(age);

            if (developer.getRelCreatedDate() != null) {
                Double relAge = Double.valueOf(developer.getRelUpdatedDate().getTime() - developer.getRelCreatedDate().getTime());
                relAge = relAge / developer.getRelRecordCount();
                developer.setRelChangeFrequencyPerDay(relAge);
            }

        }

        //TODO burayı düzelt
//        for (Package pack : packages) {
//
//            //change frequency calculations
//            Double age = Double.valueOf(pack.getUpdatedDate().getTime() - pack.getCreatedDate().getTime());
//            age = age / pack.getRecordCount();
//            pack.setChangeFrequencyPerMs(age);
//
//            if (pack.getRelCreatedDate() != null) {
//                Double relAge = Double.valueOf(pack.getRelUpdatedDate().getTime() - pack.getRelCreatedDate().getTime());
//                relAge = relAge / pack.getRelRecordCount();
//                pack.setRelChangeFrequencyPerMs(relAge);
//            }
//        }

    }

}
