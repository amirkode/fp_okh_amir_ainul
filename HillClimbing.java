import java.util.*;

class HillClimbing extends Heuristics {

    HillClimbing(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix, 
                ArrayList<Pair<Integer, Integer>> initialSol, ArrayList<Course> courses,
                    ArrayList<Student> students, int numOfIts, int numOfTimeSlots) {
        this.initialSol = cloneSolution(initialSol);
        this.courses = courses;
        this.students = students;
        this.numOfIts = numOfIts;
        this.numOfTimeSlots = numOfTimeSlots;
        eval = new Timetable.ExamEvaluate(conflictsMatrix, courses, students, initialSol);
        llh = new LowLevelHeuristics();
    }

    public ArrayList<Pair<Integer, Integer>> generateSolution() {
        ArrayList<Pair<Integer, Integer>> bestSol = cloneSolution(initialSol);
        ArrayList<Pair<Integer, Integer>> currSol = cloneSolution(initialSol);
        int randCourse, randTimeSlot, courseLen = courses.size();
        double initialPenalty = eval.getPenalty(initialSol);
        double bestPenalty = eval.getPenalty(bestSol);
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < numOfIts; i ++) {
            randCourse = random(courseLen);
            randTimeSlot = random(numOfTimeSlots);
            // apakah solusi randomb bisa dilakukan checking
            if(eval.feasibleRandTimeSlot(randCourse, randTimeSlot, bestSol)) {
                currSol.get(randCourse).second = randTimeSlot;
                double currPenalty = eval.getPenalty(currSol);
                // membandingkan penalti yang ada
                if(bestPenalty > currPenalty) { 
                    bestPenalty = currPenalty;
                    bestSol = cloneSolution(initialSol);;
                }
            }
        }
        System.out.println();
        System.out.println("Hill Climbing : ");
        System.out.println("Solution generated in " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println("Penalty : " + bestPenalty);
        System.out.println("Intial Penalty : " + initialPenalty);
        return bestSol;
    }
}