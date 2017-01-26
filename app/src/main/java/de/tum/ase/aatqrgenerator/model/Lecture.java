package de.tum.ase.aatqrgenerator.model;

import java.util.List;

public class Lecture {
    public String slug;
    public String title;
    public Semester semester;
    public List<Session> sessions;
    public List<ExerciseGroup> exerciseGroups;
    public String href;
}
