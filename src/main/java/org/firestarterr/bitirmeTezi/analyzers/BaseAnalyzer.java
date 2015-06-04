package org.firestarterr.bitirmeTezi.analyzers;

import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.FileWrapper;
import org.firestarterr.bitirmeTezi.model.*;
import org.firestarterr.bitirmeTezi.model.Package;

import java.text.ParseException;
import java.util.*;

public abstract class BaseAnalyzer {

    List<Project> projects = new ArrayList<>();
    List<Package> packages = new ArrayList<>();
    List<Issue> issues = new ArrayList<>();
    List<File> files = new ArrayList<>();
    List<Developer> developers = new ArrayList<>();
    List<Commit> commitSuccess = new ArrayList<>();

    public BaseAnalyzer(String projectName) {
        Project project = new Project();
        project.setName(projectName);
        projects.add(project);

        Package aPackage = new Package();
        aPackage.setName("root");
        aPackage.setProject(projects.get(0));
        packages.add(aPackage);
    }

    //<editor-fold desc="Package Methods">
    protected abstract String getPackageName(String path);

    private Package findOrCreatePackage(String path, Date date, boolean isOrcaDeveloper) {
        int index = path.indexOf("/");
        if (index == -1) {
            Package root = packages.get(0);
            updateStats(root, date, isOrcaDeveloper);
            return root;
        } else {
            String packageName = getPackageName(path);
            for (Package aPackage : packages) {
                if (aPackage.getName().equals(packageName)) {
                    return aPackage;
                }
            }
            Package module = createPackage(packageName, date, isOrcaDeveloper);
            packages.add(module);
            return module;
        }
    }

    private Package createPackage(String name, Date date, boolean isOrcaDeveloper) {
        Package aPackage = new Package();
        aPackage.setName(name);
        aPackage.setProject(projects.get(0));
        updateStats(aPackage, date, isOrcaDeveloper);
        return aPackage;
    }
    //</editor-fold>

    //<editor-fold desc="Issue Methods">
    private Issue findOrCreateIssue(String name, Date date, boolean isOrcaDeveloper) {
        for (Issue issue : issues) {
            if (issue.getName().equals(name)) {
                updateStats(issue, date, isOrcaDeveloper);
                return issue;
            }
        }
        Issue issue = createIssue(name, date, isOrcaDeveloper);
        issues.add(issue);
        return issue;
    }

    private Issue createIssue(String name, Date date, boolean isOrcaDeveloper) {
        Issue issue = new Issue();
        issue.setName(name);
        updateStats(issue, date, isOrcaDeveloper);
        return issue;
    }
    //</editor-fold>

    //<editor-fold desc="File Methods">
    protected abstract FileWrapper parseFileString(String fileString);

    private File findOrCreateFile(String fileString, Date date, boolean isOrcaDeveloper) {
        FileWrapper parsed = parseFileString(fileString);
        for (File file : files) {
            if (file.getName().equals(parsed.name)) {
                updateStats(file, date, isOrcaDeveloper);
                return file;
            }
        }
        File file = createFile(parsed, date, isOrcaDeveloper);
        files.add(file);
        return file;
    }

    private File createFile(FileWrapper parsed, Date date, boolean isOrcaDeveloper) {
        File file = new File();

        file.setFullPath(parsed.path);
        file.setName(parsed.name);

        file.setModule(findOrCreatePackage(parsed.path, date, isOrcaDeveloper));
        file.setProject(projects.get(0));
        file.setFileExt(parsed.ext);
        updateStats(file, date, isOrcaDeveloper);
        return file;
    }
    //</editor-fold>

    //<editor-fold desc="Dev Methods">
    private Developer findOrCreateDeveloper(String name, Date date) {
        name = replaceDevName(name);
        for (Developer developer : developers) {
            if (developer.getName().equals(name)) {
                updateStats(developer, date, developer.getIsOrcaDeveloper());
                return developer;
            }
        }
        Developer dev = createDeveloper(name, date);
        developers.add(dev);
        return dev;
    }

    private Developer createDeveloper(String devName, Date date) {
        Developer developer = new Developer();
        developer.setName(devName);
        boolean isOrca = isOrcaDeveloper(devName);
        developer.setIsOrcaDeveloper(isOrca);
        updateStats(developer, date, developer.getIsOrcaDeveloper());
        return developer;
    }

    private boolean isOrcaDeveloper(String devName) {
        return devName.equals("Murat Can Sayılgan")
                || devName.equals("Recep Ayan")
                || devName.equals("Kamil Örs")
                || devName.equals("Elif Gül");
    }

    private String replaceDevName(String name) {
        String newName = name;
        if (name.equals("murats")) {
            newName = "Murat Can Sayılgan";
        }
        if (name.equals("recepa")) {
            newName = "Recep Ayan";
        }
        if (name.equals("kamilo")) {
            newName = "Kamil Örs";
        }
        if (name.equals("elifg")) {
            newName = "Elif Gül";
        }
        return newName;
    }
    //</editor-fold>

    //<editor-fold desc="Util Methods">
    private Integer getDifferenceAsDays(Date date1, Date date2) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(Math.abs(date2.getTime() - date1.getTime()));
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    private void updateStats(BitBaseEntity entity, Date date, boolean isOrcaDeveloper) {
        if (entity.getUpdatedDate() == null) {
            entity.setUpdatedDate(date);
        }
        entity.increaseRecordCount();
        entity.setCreatedDate(date);
        Integer differenceAsDay = getDifferenceAsDays(entity.getCreatedDate(), entity.getUpdatedDate());
        entity.setChangeFrequencyPerDay((double) (entity.getRecordCount() / differenceAsDay));
        if (isOrcaDeveloper) {
            if (entity.getRelUpdatedDate() == null) {
                entity.setRelUpdatedDate(date);
            }
            entity.increaseRelRecordCount();
            entity.setRelCreatedDate(date);
            Integer relDifferenceAsDay = getDifferenceAsDays(entity.getRelCreatedDate(), entity.getRelUpdatedDate());
            entity.setRelChangeFrequencyPerDay((double) (entity.getRelRecordCount() / relDifferenceAsDay));
        }
    }
    //</editor-fold>

    //<editor-fold desc="Commit Methods">
    protected Commit createCommit(CommitWrapper parsed) throws ParseException {
        Commit commit = new Commit();
        Developer dev = findOrCreateDeveloper(parsed.dev, parsed.date);
        dev.getCommits().add(commit);

        commit.setCommitDate(parsed.date);
        commit.setDeveloper(dev);
        commit.setName(parsed.commitName);

        if (parsed.issueName != null) {
            commit.setRelatedIssue(findOrCreateIssue(parsed.issueName, parsed.date, dev.getIsOrcaDeveloper()));
        }

        for (Map.Entry<String, Integer> entry : parsed.fileStrings.entrySet()) {
            updateCommitFiles(commit, entry.getKey(), entry.getValue());
        }

        commitSuccess.add(commit);
        return commit;
    }

    private void updateCommitFiles(Commit commit, String fileString, Integer locEdited) {
        File file = findOrCreateFile(fileString, commit.getCommitDate(), commit.getDeveloper().getIsOrcaDeveloper());
        commit.getFiles().add(file);
        file.getCommits().add(commit);
        updateCommitStats(commit, locEdited);
        updateFileStats(file, locEdited);
        commit.getLocEditedPerFile().put(file, locEdited);
    }

    private void updateCommitStats(Commit commit, Integer locEdited) {
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
    }

    private void updateFileStats(File file, Integer locEdited) {
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
    }
    //</editor-fold>


    public void analyzeData() {
        for (Commit commit : commitSuccess) {
            Developer dev = commit.getDeveloper();
            //private Map<Developer, Integer> developerChangeCountMap
            for (File file : commit.getFiles()) {
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
                    Developer dev1 = entry.getKey();
                    Developer dev2 = iter.getKey();
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
                    if (dev1.getCooperatedOnFiles().get(file) == null || dev1.getCooperatedOnFiles().get(file).isEmpty()) {
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
                    if (dev2.getCooperatedOnFiles().get(file) == null || dev2.getCooperatedOnFiles().get(file).isEmpty()) {
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
                    if (dev1.getCooperatedOnModules().get(file.getModule()) == null || dev1.getCooperatedOnModules().get(file.getModule()).isEmpty()) {
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
                    if (dev2.getCooperatedOnModules().get(file.getModule()) == null || dev2.getCooperatedOnModules().get(file.getModule()).isEmpty()) {
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
        }
    }
}
