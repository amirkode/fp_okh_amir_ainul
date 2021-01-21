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
        // init best solution and current solution
        ArrayList<Pair<Integer, Integer>> bestSol = cloneSolution(initialSol);
        ArrayList<Pair<Integer, Integer>> currSol = cloneSolution(initialSol);
        
        int randCourse, randTimeSlot, courseLen = courses.size();
        // init best penalty
        double bestPenalty = eval.getPenalty(bestSol);
        
        for(int i = 0; i < numOfIts; i ++) {
            // choose a random course and a random timeslot 
            randCourse = random(courseLen);
            randTimeSlot = random(numOfTimeSlots);
            // apakah solusi random bisa dilakukan checking
            if(eval.feasibleRandTimeSlot(randCourse, randTimeSlot, bestSol)) {
                currSol.get(randCourse).second = randTimeSlot;
                double currPenalty = eval.getPenalty(currSol);
                // membandingkan penalti yang ada
                if(bestPenalty > currPenalty) { 
                    // set solusi yang lebih baik ke bestPenalty
                    bestPenalty = currPenalty;
                    bestSol = cloneSolution(currSol);
                }
            }
        }
        
        return bestSol;
    }
}