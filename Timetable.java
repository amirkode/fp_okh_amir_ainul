import java.util.*;
import javax.sound.midi.SysexMessage;
import java.lang.*;
import java.io.*;

class Timetable {
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
    static final int METHOD_LWD_LE = 6;
    static BufferedReader br;
    static PrintWriter out;
    static Scanner in = new Scanner(System.in);
    static String fileNames[] = {"car-f-92", "car-s-91", "ear-f-83", "hec-s-92", "kfu-s-93", "lse-f-91", 
                    "pur-s-93", "rye-s-93", "sta-f-83", "tre-s-92", "uta-s-92", "ute-s-92", "yor-f-83"};
    static ArrayList<Pair<ArrayList<Course>, ArrayList<Student>>> cases = new ArrayList();
    static ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> currConflictsMatrix;
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
            if(choice >= 1 && choice <= 13) {
                System.out.println("case " + fileNames[choice - 1]);
                //dumpCaseToScreen(choice - 1);
                long time_conflicts_matrix_generation = 0, time_degree, time_weigthed_degree, time_lwd_le, 
                                time_method1, time_method2, time_method3, time_method4, time_method5;
                long start;
                start = System.currentTimeMillis();
                generateConflictMatrixFile(choice - 1);  
                time_conflicts_matrix_generation = System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
                System.out.println("conflict matrix size : " + currConflictsMatrix.size());
                generateCoursesDegreeFile(choice - 1, Degree.SORT_TYPE_DEGREE);
                time_degree = System.currentTimeMillis() - start;
                int bestMethod = METHOD_LEAST_REMAINING_COLOR_FIRST;
                int minTimeSlots = Integer.MAX_VALUE;
                long time_best_method;
                start = System.currentTimeMillis();
                int minTimeSlotsGraphColoringLeastFirst = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LEAST_REMAINING_COLOR_FIRST);
                minTimeSlots = minTimeSlotsGraphColoringLeastFirst;
                time_method1 = System.currentTimeMillis() - start;
                time_best_method = time_method1;
                Util.generateSolution(currConflictsMatrix, currCoursesDegree, METHOD_LEAST_REMAINING_COLOR_FIRST, choice - 1);
                start = System.currentTimeMillis();
                int minTimeSlotsGraphColoring = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_COLORING_NO_SORTING);
                time_method2 = System.currentTimeMillis() - start;
                if(minTimeSlots > minTimeSlotsGraphColoring) {
                    minTimeSlots = minTimeSlotsGraphColoring;
                    bestMethod = METHOD_COLORING_NO_SORTING;
                    time_best_method = time_method2;
                }
                Util.generateSolution(currConflictsMatrix, currCoursesDegree, METHOD_COLORING_NO_SORTING, choice - 1);
                start = System.currentTimeMillis();
                int minTimeSlotsLargestDegreeFirst = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LARGEST_DEGREE_FIRST);
                time_method3 = System.currentTimeMillis() - start + time_degree;
                if(minTimeSlots > minTimeSlotsLargestDegreeFirst) {
                    minTimeSlots = minTimeSlotsLargestDegreeFirst;
                    bestMethod = METHOD_LARGEST_DEGREE_FIRST;
                    time_best_method = time_method3;
                }
                Util.generateSolution(currConflictsMatrix, currCoursesDegree, METHOD_LARGEST_DEGREE_FIRST, choice - 1);
                // generate degree kembali dengan weighted degree 
                start = System.currentTimeMillis();
                generateCoursesDegreeFile(choice - 1, Degree.SORT_TYPE_WEIGHTED_DEGREE);
                time_weigthed_degree = System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
                int minTimeSlotsLargestWeigthedDegreeFirst = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LARGEST_WEIGHTED_DEGREE_FIRST);
                time_method4 = System.currentTimeMillis() - start + time_weigthed_degree;
                if(minTimeSlots > minTimeSlotsLargestWeigthedDegreeFirst) {
                    minTimeSlots = minTimeSlotsLargestWeigthedDegreeFirst;
                    bestMethod = METHOD_LARGEST_WEIGHTED_DEGREE_FIRST;
                    time_best_method = time_method4;
                }
                Util.generateSolution(currConflictsMatrix, currCoursesDegree, METHOD_LARGEST_WEIGHTED_DEGREE_FIRST, choice - 1);
                // generate degree kembali dengan tipe sordering LWD Le
                start = System.currentTimeMillis();
                generateCoursesDegreeFile(choice - 1, Degree.SORT_TYPE_LWD_LE);
                time_lwd_le = System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
                int minTimeSlotsLWD_LE = Util.solve(currConflictsMatrix, currCoursesDegree, METHOD_LWD_LE);
                time_method5 = System.currentTimeMillis() - start + time_lwd_le;
                if(minTimeSlots > minTimeSlotsLWD_LE) {
                    minTimeSlots = minTimeSlotsLWD_LE;
                    bestMethod = METHOD_LWD_LE;
                    time_best_method = time_method5;
                }
                Util.generateSolution(currConflictsMatrix, currCoursesDegree, METHOD_LWD_LE, choice - 1);
                start = System.currentTimeMillis();
                System.out.println("Conflicts matrix generated in " + time_conflicts_matrix_generation + " milliseconds");
                System.out.println("Min timeslots (Least Remaining Color First) : " + minTimeSlotsGraphColoringLeastFirst + ", time required : " + time_method1 + " milliseconds");
                System.out.println("Min timeslots (No Sorting) : " + minTimeSlotsGraphColoring + ", time required : " + time_method2 + " milliseconds");
                System.out.println("Min timeslots (Largest Degree First) : " + minTimeSlotsLargestDegreeFirst + ", time required : " + time_method3 + " milliseconds");
                System.out.println("Min timeslots (Largest Weighted Degree First) : " + minTimeSlotsLargestWeigthedDegreeFirst + ", time required : " + time_method4 + " milliseconds");
                System.out.println("Min timeslots (LWD + LE) : " + minTimeSlotsLWD_LE + ", time required : " + time_method5 + " milliseconds");
                System.out.println("Best Solution File Generated!");
                System.out.println("Min timeslots : " + minTimeSlots);
                System.out.println("Total time required : " + (time_conflicts_matrix_generation + time_best_method) + " milliseconds"); 
                
                // optimasi dengan heuristics yang lain
                ArrayList<Pair<Integer, Integer>> initialSol = Util.generateSolution(currConflictsMatrix, currCoursesDegree, bestMethod, choice - 1);
                Pair<ArrayList<Course>, ArrayList<Student>> thisCase = cases.get(choice - 1);
                HillClimbing hc = new HillClimbing(currConflictsMatrix, initialSol, thisCase.first, thisCase.second, 1000000, minTimeSlots);
                SimulatedAnnealing sa = new SimulatedAnnealing(currConflictsMatrix, initialSol, thisCase.first, thisCase.second, 10000, minTimeSlots);
                TabuSearch ts = new TabuSearch(currConflictsMatrix, initialSol, thisCase.first, thisCase.second, 10000, minTimeSlots);

                for(int i = 1; i <= 5; i ++) {
                    System.out.println();
                    System.out.println("Intial method : " + Util.getBestMethod(bestMethod));
                    System.out.println("Num of Timeslots : " + minTimeSlots);
                    
                    ArrayList<Long> times = new ArrayList();
                    long startTime = System.currentTimeMillis();
                    ExamEvaluate eval = new ExamEvaluate(currConflictsMatrix, thisCase.first, thisCase.second, initialSol);
                    ArrayList<Pair<Integer, Integer>> hcSol = hc.generateSolution();
                    System.out.println();
                    System.out.println("Hill Climbing : ");
                    System.out.println("Solution generated in " + (System.currentTimeMillis() - startTime) + " ms");
                    times.add(System.currentTimeMillis() - startTime);
                    System.out.println("Penalty : " + eval.getPenalty(hcSol));
                    System.out.println("Intial Penalty : " + eval.getPenalty(initialSol));
                    System.out.println("Delta : " + ((eval.getPenalty(initialSol) - eval.getPenalty(hcSol) / eval.getPenalty(initialSol))));
                    startTime = System.currentTimeMillis();
                    ArrayList<Pair<Integer, Integer>> saSol = sa.generateSolution();
                    System.out.println();
                    System.out.println("Simulated Annealing : ");
                    System.out.println("Solution generated in " + (System.currentTimeMillis() - startTime) + " ms");
                    times.add(System.currentTimeMillis() - startTime);
                    System.out.println("Penalty : " + eval.getPenalty(saSol));
                    System.out.println("Intial Penalty : " + eval.getPenalty(initialSol));
                    System.out.println("Delta : " + ((eval.getPenalty(initialSol) - eval.getPenalty(saSol) / eval.getPenalty(initialSol))));
                    startTime = System.currentTimeMillis();
                    ArrayList<Pair<Integer, Integer>> tsSol = ts.generateSolution(); System.out.println();
                    System.out.println("Tabu Search : ");
                    System.out.println("Solution generated in " + (System.currentTimeMillis() - startTime) + " ms");
                    times.add(System.currentTimeMillis() - startTime);
                    System.out.println("Penalty : " + eval.getPenalty(tsSol));
                    System.out.println("Intial Penalty : " + eval.getPenalty(initialSol));
                    System.out.println("Delta : " + ((eval.getPenalty(initialSol) - eval.getPenalty(tsSol) / eval.getPenalty(initialSol))));
                    
                    Util.writeStats(new Pair<Long, ArrayList<Pair<Integer, Integer>>>(time_best_method, initialSol), new Pair<Long, ArrayList<Pair<Integer, Integer>>>(times.get(0), hcSol),
                    new Pair<Long, ArrayList<Pair<Integer, Integer>>>(times.get(1), saSol), 
                    new Pair<Long, ArrayList<Pair<Integer, Integer>>>(times.get(2), tsSol), fileNames[choice - 1], eval, i);
                }
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
        //ArrayList<Pair<String, ArrayList<Pair<String, Boolean>>>> conflictsMatrix;
        ArrayList<Course> courses = cases.get(caseIndex).first;
        currCoursesDegree = Util.generateCourseDegree(currConflictsMatrix, courses, degreeType);
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
           // System.out.println("Course Degree " + currCoursesDegree.get(i).courseId + " created.");
            String outLine = currCoursesDegree.get(i).courseId + ", Degree : " + currCoursesDegree.get(i).degree + ", Weighted Degree : " + currCoursesDegree.get(i).weightedDegree;
            out.println(outLine);
        }   
        out.close();
    }

    static void generateConflictMatrixFile(int caseIndex) throws Exception {
        ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix;
        String path = OUTPUT_DIR;
        String fileName = PREFIX_CONFLICTS_MATRIX + fileNames[caseIndex] + EXT_OUT;
        String fullPath = path + fileName;

        conflictsMatrix = Util.generateConflictMatrix(caseIndex, fullPath);
        currConflictsMatrix = conflictsMatrix;
    
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
            ArrayList<Pair<String, Integer>> conflicts = conflictsMatrix.get(i).second;
            String outLine = "";//conflictsMatrix.get(i).first + " : ";
            for(int j = 0; j < conflicts.size(); j ++) {
                //outLine += "(" + conflicts.get(j).first + ", " + (conflicts.get(j).second ? "1" : "0") + ") ";
                outLine += (conflicts.get(j).second > 0 ? "1" : "0") + " ";
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

    static class Util {
        public static void writeStats(Pair<Long, ArrayList<Pair<Integer, Integer>>> initSol, Pair<Long, ArrayList<Pair<Integer, Integer>>> hcSol, 
        Pair<Long, ArrayList<Pair<Integer, Integer>>> saSol, Pair<Long, ArrayList<Pair<Integer, Integer>>> tsSol, String caseName, ExamEvaluate eval, int i) throws Exception {
            String path = OUTPUT_DIR + "stats";
            String fileName = caseName + "_run_" + i + EXT_OUT;
            String fullPath = path + "/" + fileName;

            File dir = new File(path);
            
            if(!dir.exists())
                dir.mkdir();
    
            File newFile = new File(fullPath);
    
            if(!newFile.exists())
                newFile.createNewFile();
    
            FileOutputStream fos = new FileOutputStream(newFile, false);
            out = new PrintWriter(fullPath);
            String outLine = "";
            double fInit = eval.getPenalty(initSol.second);
            double fHc = eval.getPenalty(hcSol.second);
            double fSa = eval.getPenalty(saSol.second);
            double fTs = eval.getPenalty(tsSol.second);
            outLine = "F(i) : " + fInit  + ", T(i) : " + ((double) initSol.first / 1000) + " s";
            out.println(outLine);
            outLine = "F(hc) : " + fHc + ", T(hc) : " + ((double) hcSol.first / 1000) + " s" + 
                        ", d : " + String.format("%.2f", ((fInit - fHc) / fInit) * 100.0) + " %";
            out.println(outLine);
            outLine = "F(sa) : " + fSa + ", T(sa) : " + ((double) saSol.first / 1000) + " s" +
                        ", d : " + String.format("%.2f", ((fInit - fSa) / fInit) * 100.0) + " %";
            out.println(outLine);
            outLine = "F(ts) : " + fTs + ", T(ts) : " + ((double) tsSol.first / 1000) +  " s" +
                        ", d : " + String.format("%.2f", ((fInit - fTs) / fInit) * 100.0) + " %";
            out.println(outLine);
            out.close();
        }

        public static String getBestMethod(int type) {
            switch(type) {
                case METHOD_LARGEST_DEGREE_FIRST: return "Largest Degree First";
                case METHOD_COLORING_NO_SORTING: return "No Sorting Coloring";
                case METHOD_LARGEST_WEIGHTED_DEGREE_FIRST: return "Largest Weighted Degree First";
                case METHOD_LEAST_REMAINING_COLOR_FIRST: return "Least Remaining Color First";
                case METHOD_LWD_LE: return "LWD + LE";
            }
            return "";
        }

        public static ArrayList<Pair<Integer, Integer>> generateSolution(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix,
                         ArrayList<Degree> coursesDegree, int methodType, int caseIndex) throws Exception {
            //System.out.println("conflicts matrix size in solution : " + conflictsMatrix.size());
            ArrayList<ArrayList<String>> colors = new ArrayList();
            if(methodType == METHOD_LARGEST_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree, false);
            } else if(methodType == METHOD_LARGEST_WEIGHTED_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree, false);
            } else if(methodType == METHOD_LEAST_REMAINING_COLOR_FIRST) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
            } else if(methodType == METHOD_COLORING_NO_SORTING) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
            } else if(methodType == METHOD_LWD_LE) 
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree, false);
               
            String path = SOLUTION_DIR;
            String fileName = fileNames[caseIndex] + '_' + methodType + EXT_SOLUTION;
            String fullPath = path + fileName;
            File dir = new File(path);
            
            if(!dir.exists())
                dir.mkdir();

            out = new PrintWriter(fullPath);
            ArrayList<Pair<Integer, Integer>> res = new ArrayList();
            for(int i = 0; i < colors.size(); i ++) {
                ArrayList<String> courses = colors.get(i);
                for(int j = 0; j < courses.size(); j ++) {
                    int courseId = Integer.parseInt(courses.get(j));
                    Pair<Integer, Integer> p = new Pair<Integer, Integer>(courseId, i);
                    p.comparedBySecond = false;
                    res.add(p);
                }
            }   
            // sort here
            Collections.sort(res);
            // out
            for(int i = 0; i < res.size(); i ++) {
                out.println(res.get(i).first + " " + res.get(i).second);
            }
            out.close();
            return res;
        }
        

        public static int solve(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix, ArrayList<Degree> coursesDegree, int methodType) {
            int res = 0;
            ArrayList<ArrayList<String>> colors;
            if(methodType == METHOD_LARGEST_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree, false);
                res = colors.size();
            } else if(methodType == METHOD_LARGEST_WEIGHTED_DEGREE_FIRST) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree, false);
                res = colors.size();
            } else if(methodType == METHOD_LEAST_REMAINING_COLOR_FIRST) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
                res = colors.size();
            } else if(methodType == METHOD_COLORING_NO_SORTING) {
                colors = getTimeSlotsByCommonColoring(conflictsMatrix, true);
                res = colors.size();
            } else if(methodType == METHOD_LWD_LE) {
                colors = getTimeSlotsByDegree(conflictsMatrix, coursesDegree, false);
                res = colors.size();
            }
            return res;
        }

        public static ArrayList<ArrayList<String>> getTimeSlotsByDegree(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix,
                                                    ArrayList<Degree> coursesDegree, Boolean isLeastRemainingColorFirst) {
            ArrayList<ArrayList<String>> colors = new ArrayList();
            //System.out.println("course degree size : " + coursesDegree.size());
            for(int i = 0; i < coursesDegree.size(); i ++) {
                if(colors.size() == 0)
                    colors.add(new ArrayList<String>(Arrays.asList(coursesDegree.get(i).courseId)));
                else {
                   // System.out.println("courseDegree " + i + " : " + coursesDegree.get(i).courseId);
                    int conflictsIndex = Integer.parseInt(coursesDegree.get(i).courseId) - 1;
                    ArrayList<Pair<String, Integer>> conflicts = conflictsMatrix.get(conflictsIndex).second;
                    ArrayList<Pair<Integer, Integer>> candidateColors = new ArrayList();
                    
                    checkConflictingTimeslots(colors, conflicts, candidateColors);

                    if(candidateColors.size() == 0)
                        colors.add(new ArrayList<String>(Arrays.asList(coursesDegree.get(i).courseId)));
                    else  {
                        if(isLeastRemainingColorFirst)
                            Collections.sort(candidateColors);
                        colors.get(candidateColors.get(0).first).add(coursesDegree.get(i).courseId);
                    }
                }
            }
            
            // cek to print colors 
           /* int total = 0;
            for(int i = 0; i < colors.size(); i ++) {
                ArrayList<String> currColor = colors.get(i);
                for(int j = 0; j < currColor.size(); j ++) {
                    System.out.print(currColor.get(j) + ", ");
                    total ++;
                }
                System.out.println();
            }
            System.out.println("total courses : " + total); */
            return colors;
        }
        
        public static ArrayList<ArrayList<String>> getTimeSlotsByCommonColoring(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix,
                                                         Boolean isLeastRemainingColorFirst) {
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
                    ArrayList<Pair<String, Integer>> conflicts = conflictsMatrix.get(i).second;
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
        
        private static void checkConflictingTimeslots(ArrayList<ArrayList<String>> colors, ArrayList<Pair<String, Integer>> conflicts,
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

                    if(conflicts.get(mid).second > 0) {
                        thisConflict = true;
                        break;
                    }
                }
                
                if(!thisConflict)
                    candidateColors.add(new Pair<Integer, Integer>(j, courseIds.size()));
            }
        }

        public static ArrayList<Degree> generateCourseDegree(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix, ArrayList<Course> courses, int sortType) {
            ArrayList<Degree> res = new ArrayList();
            for(int i = 0; i < conflictsMatrix.size(); i ++) {
                int conflictCnt = 0;
                ArrayList<Pair<String, Integer>> conflicts = conflictsMatrix.get(i).second;
                for(int j = 0; j < conflicts.size(); j ++) {
                    if(conflicts.get(j).second > 0)
                        conflictCnt ++;
                }
                Degree d = new Degree(conflictsMatrix.get(i).first, conflictCnt, conflictCnt * courses.get(i).studentCnt, sortType);
                if(sortType == Degree.SORT_TYPE_LWD_LE)
                    d.enrollment = courses.get(i).studentCnt;
                res.add(d);
            }

            // diurutkan 
            Collections.sort(res);

            return res;
        }

        public static ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> generateConflictMatrix(int caseIndex, String fullPath) throws Exception {
            ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> res = new ArrayList();
  
            // langsung load item data pada students sebagai index, tanpa harus mencari berdasarkan nama

            ArrayList<Student> students = cases.get(caseIndex).second;
            ArrayList<Course> courses = cases.get(caseIndex).first;
            int courseLen = courses.size();

            for(int i = 0; i < courseLen; i ++) {
                ArrayList<Pair<String, Integer>> conflicts = getCourseConflictsInit(i, courses); // init matrix dengan nilai 0 semua
                res.add(new Pair<String, ArrayList<Pair<String, Integer>>>(courses.get(i).courseId, conflicts));
            }

            for(int i = 0; i < students.size(); i ++) {
                ArrayList<String> studentCourses = students.get(i).courseIds;
                for(int j = 0; j < studentCourses.size() - 1; j ++) {
                    for(int k = j + 1; k < studentCourses.size(); k ++) {
                        int first = Integer.parseInt(studentCourses.get(j)) - 1;
                        int second = Integer.parseInt(studentCourses.get(k)) - 1;
                        res.get(first).second.get(second).second ++;
                        res.get(second).second.get(first).second ++;
                    }
                }
              //  System.out.println("Conflicts checking for student " + students.get(i).studentId + " done!");
            }

            System.out.println("all course generated : " + res.size());
            
            return res;
        }

        private static ArrayList<Pair<String, Integer>> getCourseConflictsInit(int courseIndex, ArrayList<Course> courses) {
            ArrayList<Pair<String, Integer>> res = new ArrayList();
            int courseLen = courses.size();
            for(int i = 0; i < courseLen; i ++) {
                res.add(new Pair<String, Integer>(courses.get(i).courseId, 0));
            }
            return res;
        }
    }

    // kelas untuk Evaluasi
    static class ExamEvaluate {
        ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix;
        ArrayList<Course> courses;
        ArrayList<Student> students;
        ArrayList<Pair<Integer, Integer>> solution;
        int numCourses = 0, numSlots = 0, numStudents = 0;
        public ExamEvaluate(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix,
                            ArrayList<Course> courses, ArrayList<Student> students, ArrayList<Pair<Integer, Integer>> solution) {
            this.conflictsMatrix = conflictsMatrix;
            this.courses = courses;
            this.students = students;
            this.solution = new ArrayList(solution);
            numCourses = courses.size();
            numStudents = students.size();
        }

        public boolean feasibleRandTimeSlot (int randCourse, int randTimeSlot, ArrayList<Pair<Integer, Integer>> currSol) {
            for(int i = 0; i < numCourses; i ++) {
                // return false jika ada konflik dan timeslot setiap iterasi sama dengan random timeslot
                if(conflictsMatrix.get(randCourse).second.get(i).second > 0 && currSol.get(i).second == randTimeSlot)
                    return false;
            }
            return true;
        }

        public double getPenalty(ArrayList<Pair<Integer, Integer>> sol) {
            double penalty = 0.0;
            int len = conflictsMatrix.size();
            for(int i = 0; i < len - 1; i ++) {
                for(int j = i + 1; j < len; j ++) {
                    int conflict = conflictsMatrix.get(i).second.get(j).second;
                    if(conflict > 0) {
                        int diff = Math.abs(sol.get(j).second - sol.get(i).second);
                        if(diff >= 1 && diff < 5) {
                            penalty += conflict * (Math.pow(2 , 4 - diff));
                        }
                    }
                }
            }
            return penalty / students.size();
        }

       // double eventInPeriodNumber()
    }
}

/* 

Problems  max_timeslot  solution_generated  verdict
CAR91     35            34                  ok
CAR92     32            32                  ok
EAR83     24            26                  not feasible
HEC92     18            18                  ok
KFU93     20            20                  ok
LSE91     18            19                  not feasible
PUR93     42            38                  ok
RYE92     23            25                  not feasible
STA83     13            13                  ok
TRE92     23            23                  ok
UTA92     35            35                  ok 
UTE92     10            11                  not feasible
YOR83     21            23                  not feasible
*/