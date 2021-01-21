import java.lang.*;
import java.util.*;

class Student {
    String studentId;
    ArrayList<String> courseIds;

    public Student(){}

    public Student(String studentId, ArrayList<String> courseIds) {
        this.studentId = studentId;
        this.courseIds = courseIds;
    }

    public void addCourseId(String cs) {
        if(courseIds == null)
            courseIds = new ArrayList();
        courseIds.add(cs);
    }

    public void clearCourses() { 
        if(courseIds == null) {
            courseIds = new ArrayList();
            return;
        }
        courseIds.clear();
    }
}