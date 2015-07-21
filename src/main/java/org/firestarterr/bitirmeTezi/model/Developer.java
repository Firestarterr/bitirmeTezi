package org.firestarterr.bitirmeTezi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Developer extends BaseEntity {

    private Integer locEdited;
    private boolean isOrcaDeveloper;

    private List<Commit> commits = new ArrayList<>(0);

    //bu map commit bazında developer coop tutuyor.
    private Map<Developer, Integer> cooperationCount = new HashMap<>(0);

    //bu map file bazında developer coop count için kullanılabilinir.
    private Map<File, Map<Developer, Integer>> cooperatedOnFiles = new HashMap<>(0);

    //bu map package bazında developer coop count için kullanılabilinir.
    private Map<Package, Map<Developer, Integer>> cooperatedOnModules = new HashMap<>(0);


    public Integer getLocEdited() {
        return locEdited;
    }

    public void setLocEdited(Integer locEdited) {
        this.locEdited = locEdited;
    }

    public boolean getIsOrcaDeveloper() {
        return isOrcaDeveloper;
    }

    public void setIsOrcaDeveloper(boolean isOrcaDeveloper) {
        this.isOrcaDeveloper = isOrcaDeveloper;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public Map<Developer, Integer> getCooperationCount() {
        return cooperationCount;
    }

    public void setCooperationCount(Map<Developer, Integer> cooperationCount) {
        this.cooperationCount = cooperationCount;
    }

    public Map<File, Map<Developer, Integer>> getCooperatedOnFiles() {
        return cooperatedOnFiles;
    }

    public void setCooperatedOnFiles(Map<File, Map<Developer, Integer>> cooperatedOnFiles) {
        this.cooperatedOnFiles = cooperatedOnFiles;
    }

    public Map<Package, Map<Developer, Integer>> getCooperatedOnModules() {
        return cooperatedOnModules;
    }

    public void setCooperatedOnModules(Map<Package, Map<Developer, Integer>> cooperatedOnModules) {
        this.cooperatedOnModules = cooperatedOnModules;
    }
}
