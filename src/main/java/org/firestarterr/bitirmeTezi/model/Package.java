package org.firestarterr.bitirmeTezi.model;

public class Package extends BaseEntity {

    private Project project;

    private Integer locEdited;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getLocEdited() {
        return locEdited;
    }

    public void setLocEdited(Integer locEdited) {
        this.locEdited = locEdited;
    }

}
