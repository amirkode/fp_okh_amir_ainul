import java.lang.*;
import java.util.*;

class Course {
    String courseId;
    int studentCnt; 
    ArrayList<String> studentIds;
    
    public Course(String courseId, int studentCnt) {
        this.courseId = courseId;
        this.studentCnt = studentCnt;
    }

    public void addIfDoesntExist(String studentId) {
        if(studentIds == null)
            studentIds = new ArrayList();

        int left = 0, right = studentIds.size() - 1, mid = (left + right) / 2;
        Boolean found = false;
        while(left <= right) {
            mid = (left + right) / 2;
            int comp = studentId.compareTo(studentIds.get(mid));
            if(comp == 0) {
                found = true;
                break;
            } else if(comp > 0)
                left = mid + 1;
            else
                right = mid - 1;
        }
        // jika tidak ada masukkan ke dalam list
        if(!found) {
            studentIds.add(mid + 1, studentId);
        }
    }
}