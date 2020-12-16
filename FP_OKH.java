import java.util.*;
import java.lang.*;
import java.io.*;

class FP_OKH {
    static final String PREFIX_STUDENT = "stu_";
    static final String PREFIX_COURSE = "crs_";
    static final String PREFIX_CONFLICTS_MATRIX = "conflict_";
    static final String PREFIX_COURSES_DEGREE = "courses_degree_";
    static final String EXT_COURSE = ".crs";
    static final String EXT_STUDENT = ".stu";
    static final String EXT_SOLUTION = ".sol";
    static final String EXT_OUT = ".txt";
    static final String ROOT_DIR = "Toronto/";
    static final String OUTPUT_DIR = ROOT_DIR + "output/";
    static final String SOLUTION_DIR = OUTPUT_DIR + "solution/";
    static final int METHOD_LARGEST_DEGREE_FIRST = 1;
    static final int METHOD_LEAST_REMAINING_COLOR_FIRST = 2;
    static final int METHOD_GREATEST_NUMBER_OF_STUDENTS_FIRST = 3;
    static final int METHOD_LARGEST_WEIGHTED_DEGREE_FIRST = 4;
    static final int METHOD_COLORING_NO_SORTING = 5;
    static BufferedReader br;
    static PrintWriter out;
    static Scanner in = new Scanner(System.in);
    static String fileNames[] = {"car-f-92", "car-s-91", "ear-f-83", "hec-s-92", "kfu-s-93", "lse-f-91", 
                    "pur-s-93", "rye-s-93", "sta-f-83", "tre-s-92", "uta-s-92", "ute-s-92", "yor-f-83"};
    static ArrayList<Pair<ArrayList<Course>, ArrayList<Student>>> cases = new ArrayList();
    static ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> currConflictsMatrix;
    static ArrayList<Degree> currCoursesDegree;
    //  AraryList<Student> students;
    public static void main(String[] args) throws Exception{
        // read data
        cases.clear();
        System.out.println("Sedang memuat data ...");
        for(int i = 0; i < fileNames.length; i ++) {
            // read this course
            ArrayList<Course> crs = readCourses(i);
            ArrayList<Student> stdnt = readStudents(i);
            Pair<ArrayList<Course>, ArrayList<Student>> pr = new Pair(crs, stdnt);
            cases.add(pr);
            System.out.println("Data case " + fileNames[i] + " telah dimuat.");
        }
        
        System.out.println();
        System.out.println("Pilih case : ");
        for(int i = 1; i <= fileNames.length; i ++) {
            System.out.println(i + ". " + fileNames[i - 1]);
        }
        System.out.print("Masukkan pilihan Anda : ");
        int choice = in.nextInt();
        
        while(true) {
            if(choice >= 1 && choice <= fileNames.length) {
                //dumpCaseToScreen(choice - 1);
                generateConflictMatrixFile(choice - 1);  
                generateCoursesDegreeFile(choice - 1, Degree.SORT_TYPE_DEGREE);
                int bestMethod = METHOD_LEAST_REMAINING_COLOR_FIRST;
                int minTimeSlots = Integer.MAX_VALUE;
                int minTimeSlotsGraphColoringLeastFirst = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LEAST_REMAINING_COLOR_FIRST);
                minTimeSlots = minTimeSlotsGraphColoringLeastFirst;
                int minTimeSlotsGraphColoring = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_COLORING_NO_SORTING);
                if(minTimeSlots > minTimeSlotsGraphColoring) {
                    minTimeSlots = minTimeSlotsGraphColoring;
                    bestMethod = METHOD_COLORING_NO_SORTING;
                }
                int minTimeSlotsLargestDegreeFirst = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LARGEST_DEGREE_FIRST);
                if(minTimeSlots > minTimeSlotsLargestDegreeFirst) {
                    minTimeSlots = minTimeSlotsLargestDegreeFirst;
                    bestMethod = METHOD_LARGEST_DEGREE_FIRST;
                }
                // generate degree kembali dengan weighted degree 
                generateCoursesDegreeFile(choice - 1, Degree.SORT_TYPE_WEIGHTED_DEGREE);
                int minTimeSlotsLargestWeigthedDegreeFirst = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LARGEST_WEIGHTED_DEGREE_FIRST);
                if(minTimeSlots > minTimeSlotsLargestWeigthedDegreeFirst) {
                    minTimeSlots = minTimeSlotsLargestWeigthedDegreeFirst;
                    bestMethod = METHOD_LARGEST_WEIGHTED_DEGREE_FIRST;
                }
                Util.generateSolution(currConflictsMatrix, currCoursesDegree, bestMethod, choice - 1);
                System.out.println("Min timeslots (Least Remaining Color First) : " + minTimeSlotsGraphColoringLeastFirst);
                System.out.println("Min timeslots (No Sorting) : " + minTimeSlotsGraphColoring);
                System.out.println("Min timeslots (Largest Degree First) : " + minTimeSlotsLargestDegreeFirst);
                System.out.println("Min timeslots (Largest Weighted Degree First) : " + minTimeSlotsLargestWeigthedDegreeFirst);
                System.out.println("Best Solution File Generated!");
                break;    
            } else {
                System.out.println("Masukkan pilihan yang valid!");
                System.out.print("Masukkan pilihan Anda : ");
                choice = in.nextInt();
            }
        }
        // test data
       // for(int i = 0; i < fileNames.length; i ++)
        //    dumpCasesToScreen(i);
        
       // generateConflictMatrixFile(0);
    } 

    static void dumpCasesToScreen(int caseIndex) {
        Pair<ArrayList<Course>, ArrayList<Student>> pr = cases.get(caseIndex);
        ArrayList<Course> courses = pr.first;
        ArrayList<Student> students = pr.second;

        System.out.println("Case " + fileNames[caseIndex] + ": ");
        // print courses
        for(int i = 0; i < courses.size(); i ++) {
            Course crs = courses.get(i);
            System.out.println("Course " + crs.courseId + " : " + crs.studentCnt + " Students");
        }
        // print student
        for(int i = 0; i < students.size(); i ++) {
            Student stdnt = students.get(i);
            System.out.println("Student " + stdnt.studentId + " has courses : ");
            for(int j = 0; j < stdnt.courseIds.size(); i ++) {
                System.out.print(stdnt.courseIds.get(j) + " ");
            }
            System.out.println();
        }
    }

    static void generateCoursesDegreeFile(int caseIndex, int degreeType) throws Exception {
        ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix;
        ArrayList<Course> courses = cases.get(caseIndex).first;
        if(currConflictsMatrix != null)
            conflictsMatrix = currConflictsMatrix;
        else {
            conflictsMatrix = Util.generateConflictMatrix(caseIndex, ""); 
            currConflictsMatrix = conflictsMatrix;
        }
        currCoursesDegree = Util.generateCourseDegree(conflictsMatrix, courses, degreeType);
        String path = OUTPUT_DIR;
        String fileName = PREFIX_COURSES_DEGREE + fileNames[caseIndex] + EXT_OUT;
        File dir = new File(path);
        
        if(!dir.exists())
            dir.mkdir();

        File newFile = new File(path + fileName);
        newFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(newFile, false);
        out = new PrintWriter(path + fileName);

        for(int i = 0; i < currCoursesDegree.size(); i ++) {
            System.out.println("Course Degree " + currCoursesDegree.get(i).courseId + " created.");
            String outLine = currCoursesDegree.get(i).courseId + ", Degree : " + currCoursesDegree.get(i).degree + ", Weighted Degree : " + currCoursesDegree.get(i).weightedDegree;
            out.println(outLine);
        }   
        out.close();
    }

    static void generateConflictMatrixFile(int caseIndex) throws Exception {
        ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix;
        String path = OUTPUT_DIR;
        String fileName = PREFIX_CONFLICTS_MATRIX + fileNames[caseIndex] + EXT_OUT;
        String fullPath = path + fileName;
        if(currConflictsMatrix != null)
            conflictsMatrix = currConflictsMatrix;
        else {
            conflictsMatrix = Util.generateConflictMatrix(caseIndex, fullPath);
            currConflictsMatrix = conflictsMatrix;
        }

        File dir = new File(path);
        
        if(!dir.exists())
            dir.mkdir();

        File newFile = new File(fullPath);

        if(newFile.exists())
            return;

        newFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(newFile, false);
        out = new PrintWriter(fullPath);

        for(int i = 0; i < conflictsMatrix.size(); i ++) {
            System.out.println("Course " + conflictsMatrix.get(i).first + " created.");
            ArrayList<Pair<String, Boolean>> conflicts = conflictsMatrix.get(i).second;
            String outLine = "";//conflictsMatrix.get(i).first + " : ";
            for(int j = 0; j < conflicts.size(); j ++) {
                //outLine += "(" + conflicts.get(j).first + ", " + (conflicts.get(j).second ? "1" : "0") + ") ";
                outLine += (conflicts.get(j).second ? "1" : "0") + " ";
            }
            out.println(outLine);
        }   
        out.close();
    } 

    static ArrayList<Course> readCourses(int caseIndex) throws Exception {
        File fl = new File(ROOT_DIR + fileNames[caseIndex] + EXT_COURSE);
        String data;
        ArrayList<Course> res = new ArrayList();

        br = new BufferedReader(new FileReader(fl));

        while((data = br.readLine()) != null) {
            String dataArr[] = data.split(" ");
            Course cs = new Course(dataArr[0], Integer.valueOf(dataArr[1]));   
    
            res.add(cs);
        }

        return res;
    }   

    static ArrayList<Student> readStudents(int caseIndex) throws Exception {
        File fl = new File(ROOT_DIR + fileNames[caseIndex] + EXT_STUDENT);
        String data;
        int cnt = 1;
        ArrayList<Student> res = new ArrayList();

        br = new BufferedReader(new FileReader(fl));

        while((data = br.readLine()) != null) {
            String dataArr[] = data.split(" ");
            Student stdnt = new Student();

            stdnt.studentId = String.valueOf(cnt ++); 

            for(int i = 0; i < dataArr.length; i ++)
                stdnt.addCourseId(dataArr[i]);

            res.add(stdnt);
        }

        return res;
    }

    static class Student {
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

    static class Course {
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

    static class Degree implements Comparable<Degree>{
        // const
        public static int SORT_TYPE_DEGREE = 1;
        public static int SORT_TYPE_WEIGHTED_DEGREE = 2; // product of degree & student
        
        String courseId;
        int degree, weightedDegree, sortType = SORT_TYPE_DEGREE;

        public Degree(){}

        public Degree(String courseId, int degree, int weightedDegree, int sortType) {
            this.courseId = courseId;
            this.degree = degree;
            this.weightedDegree = weightedDegree;
            this.sortType = sortType;
        }

        @Override
        public int compareTo(Degree c) {
            // diurutkan dari yang terbesar ke terkecil
            if(sortType == SORT_TYPE_DEGREE) {
                return c.degree - this.degree;
            } else if(sortType == SORT_TYPE_WEIGHTED_DEGREE) {
                return c.weightedDegree - this.weightedDegree;
            }   
            return 0;
        }
    }

    public static class Pair<F, S> implements Comparable<Pair<F, S>> {
        public F first;
        public S second;
        private Boolean comparedBySecond = true; 
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int compareTo(FP_OKH.Pair<F, S> o) {
            if(comparedBySecond)
                return ((Comparable)this.second).compareTo((Comparable)o.second);
            return ((Comparable)this.first).compareTo((Comparable)o.first);
        }
    }

    static class Util {
        public static void generateSolution(ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix, ArrayList<Degree> coursesDegree, int methodType, int caseIndex) throws Exception {
            ArrayList<ArrayList<String>> colors = new ArrayList();
            if(methodType == METHOD_LARGEST_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree);
            } else if(methodType == METHOD_LARGEST_WEIGHTED_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree);
            } else if(methodType == METHOD_LEAST_REMAINING_COLOR_FIRST) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
            } else if(methodType == METHOD_COLORING_NO_SORTING) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
            }
            
            String path = SOLUTION_DIR;
            String fileName = fileNames[caseIndex] + EXT_SOLUTION;
            String fullPath = path + fileName;
            File dir = new File(path);
            
            if(!dir.exists())
                dir.mkdir();

            out = new PrintWriter(fullPath);
    
            for(int i = 0; i < colors.size(); i ++) {
                ArrayList<String> courses = colors.get(i);
                for(int j = 0; j < courses.size(); j ++) {
                    int courseId = Integer.parseInt(courses.get(j));
                    out.println(String.valueOf(courseId) + " " + i);
                }
            }   
            out.close();
        }

        public static int solve(ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix, ArrayList<Degree> coursesDegree, int methodType) {
            int res = 0;
            ArrayList<ArrayList<String>> colors;
            if(methodType == METHOD_LARGEST_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree);
                res = colors.size();
            } else if(methodType == METHOD_LARGEST_WEIGHTED_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree);
                res = colors.size();
            } else if(methodType == METHOD_LEAST_REMAINING_COLOR_FIRST) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
                res = colors.size();
            } else if(methodType == METHOD_COLORING_NO_SORTING) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
                res = colors.size();
            }
            return res;
        }

        public static ArrayList<ArrayList<String>> getTimeSlotsByDegree(ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix, ArrayList<Degree> coursesDegree) {
            ArrayList<ArrayList<String>> colors = new ArrayList();
            for(int i = 0; i < coursesDegree.size(); i ++) {
                if(colors.size() == 0)
                    colors.add(new ArrayList<String>(Arrays.asList(conflictsMatrix.get(i).first)));
                else {
                    int conflictsIndex = Integer.parseInt(coursesDegree.get(i).courseId) - 1;
                    ArrayList<Pair<String, Boolean>> conflicts = conflictsMatrix.get(conflictsIndex).second;
                    ArrayList<Pair<Integer, Integer>> candidateColors = new ArrayList();
                    
                    checkConflictingTimeslots(colors, conflicts, candidateColors);

                    if(candidateColors.size() == 0)
                        colors.add(new ArrayList<String>(Arrays.asList(coursesDegree.get(i).courseId)));
                    else  {
                        //Collections.sort(candidateColors);
                        colors.get(candidateColors.get(0).first).add(coursesDegree.get(i).courseId);
                    }
                }
            }

            return colors;
        }
        
        public static ArrayList<ArrayList<String>> getTimeSlotsByCommonColoring(ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix, Boolean isLeastRemainingColorFirst) {
            // pakai aturan graph coloring ini
            /*
            1. Color first vertex with first color.
            2. Do following for remaining V-1 vertices.
            ….. a) Consider the currently picked vertex and color it with the
            lowest numbered color that has not been used on any previously
            colored vertices adjacent to it. If all previously used colors
            appear on vertices adjacent to v, assign a new color to it.
            */
            ArrayList<ArrayList<String>> colors = new ArrayList();
            for(int i = 0; i < conflictsMatrix.size(); i ++) {
                if(colors.size() == 0)
                    colors.add(new ArrayList<String>(Arrays.asList(conflictsMatrix.get(i).first)));
                else {
                    ArrayList<Pair<String, Boolean>> conflicts = conflictsMatrix.get(i).second;
                    ArrayList<Pair<Integer, Integer>> candidateColors = new ArrayList();
                    
                    checkConflictingTimeslots(colors, conflicts, candidateColors);

                    if(candidateColors.size() == 0)
                        colors.add(new ArrayList<String>(Arrays.asList(conflictsMatrix.get(i).first)));
                    else  {
                        if(isLeastRemainingColorFirst)
                            Collections.sort(candidateColors);
                        colors.get(candidateColors.get(0).first).add(conflictsMatrix.get(i).first);
                    }
                }
            }
            return colors;
        }
        
        private static void checkConflictingTimeslots(ArrayList<ArrayList<String>> colors, ArrayList<Pair<String, Boolean>> conflicts,
                        ArrayList<Pair<Integer, Integer>> candidateColors) {
            for(int j = 0; j < colors.size(); j ++) {
                ArrayList<String> courseIds = colors.get(j);
                Boolean thisConflict = false;
                for(int k = 0; k < courseIds.size(); k ++) {
                    int left = 0, right = conflicts.size() - 1, mid = (left + right) / 2;
                    while(left <= right) {
                        mid = (left + right) / 2;
                        int comp = courseIds.get(k).compareTo(conflicts.get(mid).first);
                        if(comp == 0)
                            break;
                        else if(comp > 0)
                            left = mid + 1;
                        else   
                            right = mid - 1;
                    }

                    if(conflicts.get(mid).second) {
                        thisConflict = true;
                        break;
                    }
                }
                
                if(!thisConflict)
                    candidateColors.add(new Pair<Integer, Integer>(j, courseIds.size()));
            }
        }

        public static ArrayList<Degree> generateCourseDegree(ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix, ArrayList<Course> courses, int sortType) {
            ArrayList<Degree> res = new ArrayList();
            for(int i = 0; i < conflictsMatrix.size(); i ++) {
                int conflictCnt = 0;
                ArrayList<Pair<String, Boolean>> conflicts = conflictsMatrix.get(i).second;
                for(int j = 0; j < conflicts.size(); j ++) {
                    if(conflicts.get(j).second)
                        conflictCnt ++;
                }
                res.add(new Degree(conflictsMatrix.get(i).first, conflictCnt, conflictCnt * courses.get(i).studentCnt, sortType));
            }

            // diurutkan 
            Collections.sort(res);

            return res;
        }

        public static ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> generateConflictMatrix(int caseIndex, String fullPath) throws Exception {
            ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> res = new ArrayList();
            /*
            File fl = new File(fullPath);
            if(fl.exists()) {
                br = new BufferedReader(new FileReader(fullPath));
                String data;
                int i = 0;
                while((data = br.readLine()) != null) {
                    String dataArr[] = data.split(" ");
                    ArrayList<Pair<String, Boolean>> conflicts = new ArrayList();
                    for(int j = 0; j < dataArr.length; j ++) {
                        conflicts.add(new Pair<String, Boolean>(courses.get(j).courseId, dataArr[j].equals("1")));
                    }
                    res.add(new Pair<String, ArrayList<Pair<String, Boolean>>>(courses.get(i).courseId, conflicts));    
                    System.out.println("Course " + courses.get(i).courseId + " conflicts done!");
                    i ++;
                }

                return res;
            } 
            
            ArrayList<Student> students = cases.get(caseIndex).second;
            int courseLen = courses.size();

            for(int i = 0; i < courseLen; i ++) {
                ArrayList<Pair<String, Boolean>> conflicts = getCourseConflicts(courses.get(i).courseId, courses, students, res); /
                res.add(new Pair<String, ArrayList<Pair<String, Boolean>>>(courses.get(i).courseId, conflicts));
                System.out.println("Course " + courses.get(i).courseId + " conflicts done!");
            }

            */

            // langsung load item data pada students sebagai index, tanpa harus mencari berdasarkan nama

            ArrayList<Student> students = cases.get(caseIndex).second;
            ArrayList<Course> courses = cases.get(caseIndex).first;
            int courseLen = courses.size();

            for(int i = 0; i < courseLen; i ++) {
                ArrayList<Pair<String, Boolean>> conflicts = getCourseConflictsInit(i, courses); // init matrix dengan nilai 0 semua
                res.add(new Pair<String, ArrayList<Pair<String, Boolean>>>(courses.get(i).courseId, conflicts));
            }

            for(int i = 0; i < students.size(); i ++) {
                ArrayList<String> studentCourses = students.get(i).courseIds;
                for(int j = 0; j < studentCourses.size() - 1; j ++) {
                    for(int k = j + 1; k < studentCourses.size(); k ++) {
                        int first = Integer.parseInt(studentCourses.get(j)) - 1;
                        int second = Integer.parseInt(studentCourses.get(k)) - 1;
                        res.get(first).second.get(second).second = true;
                        res.get(second).second.get(first).second = true;
                    }
                }
              //  System.out.println("Conflicts checking for student " + students.get(i).studentId + " done!");
            }
            
            
            return res;
        }

        private static ArrayList<Pair<String, Boolean>> getCourseConflictsInit(int courseIndex, ArrayList<Course> courses) {
            ArrayList<Pair<String, Boolean>> res = new ArrayList();
            int courseLen = courses.size();
            for(int i = 0; i < courseLen; i ++) {
                Pair<String, Boolean> pr;
                if(courseIndex == i) 
                    pr = new Pair<String, Boolean>(courses.get(i).courseId, true);
                else
                    pr = new Pair<String, Boolean>(courses.get(i).courseId, false);
                res.add(pr);
            }
            return res;
        }

        private static ArrayList<Pair<String, Boolean>> getCourseConflicts(String courseId, ArrayList<Course> courses, 
                                    ArrayList<Student> students, ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsBefore) {
            ArrayList<Pair<String, Boolean>> res = new ArrayList();
            int startSearchIndex = conflictsBefore.size();
            // langsung mengisi sudah dicari sebelumnya 
            for(int i = 0; i < startSearchIndex; i ++) { 
                String courseName = conflictsBefore.get(i).first;
                ArrayList<Pair<String, Boolean>> conflicts = conflictsBefore.get(i).second;
                Pair<String, Boolean> pr = new Pair<String, Boolean>(courseName, conflicts.get(startSearchIndex).second);
                res.add(pr);
            }

            for(int i = startSearchIndex; i < courses.size(); i ++) {
                Pair<String, Boolean> pr;
                if(courseId.equals(courses.get(i).courseId))
                    // jika course nya sama maka tidak perlu cek apakah ada yang bentrok, soalnya pasti sama.
                    pr = new Pair<String, Boolean>(courseId, true);
                if(isConflict(courseId, courses.get(i).courseId, students))
                    pr = new Pair<String, Boolean>(courses.get(i).courseId, true);
                else
                    pr = new Pair<String, Boolean>(courses.get(i).courseId, false);
                
                res.add(pr);
            }
            return res;
        }

        private static Boolean isConflict(String course1Id, String course2Id, ArrayList<Student> students) {
            int len = students.size();
            for(int i = 0; i < len; i ++) { 
                ArrayList<String> courseIds = students.get(i).courseIds;  
                if(isCourseExist(course1Id, courseIds))
                    if(isCourseExist(course2Id, courseIds))
                        return true;
            }
            return false;
        }

        private static Boolean isCourseExist(String courseId, ArrayList<String> courseIds) {
            int left = 0, right = courseIds.size() - 1;
            while(left <= right) {
                int mid = (left + right) / 2;
                int comp = courseId.compareTo(courseIds.get(mid));
                if(comp == 0)
                    return true;
                else if(comp > 0)
                    left = mid + 1;
                else
                    right = mid - 1;
            }
            return false;
        }
    }
}