import java.util.ArrayList;
import java.util.Random;

public abstract class Heuristics {
    public ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix;
    public ArrayList<Pair<Integer, Integer>> initialSol;
    public ArrayList<Course> courses;
    public ArrayList<Student> students;    
    
    public Timetable.ExamEvaluate eval;
    public LowLevelHeuristics llh;

    public int numOfIts;
    public int numOfTimeSlots;
    private Random rand = new Random();

    public abstract ArrayList<Pair<Integer, Integer>> generateSolution();

    public int random(int n) {
        // akan return 0 ... n - 1 (inklusif)
        return rand.nextInt(n);    
    }

    public int randomNumber(int min, int max) {
        // akan return min ... max (inklusif)
		return rand.nextInt(max - min) + min;
    }
    
    // clone list untuk menghindari referensi objek yang sama
    public ArrayList<Pair<Integer, Integer>> cloneSolution(ArrayList<Pair<Integer, Integer>> sol) {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList(); 
        
        for(int i = 0; i < sol.size(); i ++) {
            res.add(new Pair<Integer,Integer>(sol.get(i).first, sol.get(i).second));
        }

        return res;
    }

    class LowLevelHeuristics {

        // melakukan perubahan timeslot pada suatu course dengan random timeslot
        public ArrayList<Pair<Integer, Integer>> move(ArrayList<Pair<Integer, Integer>> sol, int numOfMoves) {
            ArrayList<Pair<Integer, Integer>> res = cloneSolution(sol);
            int randCourse, randTimeSlot, numOfCourses = courses.size();
        
            // melakukan move secara dinamis berdasarkan jumlah moves
            for(int i = 0; i < numOfMoves; i ++) {
                randCourse = random(numOfCourses);
                randTimeSlot = random(numOfTimeSlots);
                // check apakah setelah moves solusi masih bisa diterima?
                if(eval.feasibleRandTimeSlot(randCourse, randTimeSlot, res))
                    res.get(randCourse).second = randTimeSlot;
            }

            return res;
        }

        public ArrayList<Pair<Integer, Integer>> move1(ArrayList<Pair<Integer, Integer>> sol) {
            return move(sol, 1);
        }

        public ArrayList<Pair<Integer, Integer>> move2(ArrayList<Pair<Integer, Integer>> sol) {
            return move(sol, 2);
        }
        
        public ArrayList<Pair<Integer, Integer>> move3(ArrayList<Pair<Integer, Integer>> sol) {
            return move(sol, 3);
        }

        /**
         * Menukar timeslot dari n course dengan pol
         * untuk i = 1 ... n, timeslot course i diubah nilainya sama dengan course i - 1
         * untuk course 0, nilainya sama dengan course n  
         */
        public ArrayList<Pair<Integer, Integer>> swap(ArrayList<Pair<Integer, Integer>> sol, int numOfSwaps) {
            if(numOfSwaps < 2)
                return cloneSolution(sol);
            
           ArrayList<Pair<Integer, Integer>> res = cloneSolution(sol);
           ArrayList<Integer> randCourses = new ArrayList();
           int numOfCourses = courses.size();
        
           for(int i = 0; i < numOfSwaps; i ++) 
                randCourses.add(random(numOfCourses));
           
           int lastTimeSlot = res.get(randCourses.get(0)).second;

           for(int i = 1; i < numOfSwaps; i ++) {
                if(eval.feasibleRandTimeSlot(randCourses.get(i), res.get(randCourses.get(i - 1)).second, res))
                    res.get(randCourses.get(i)).second = res.get(randCourses.get(i - 1)).second;
           }
           // set course terakhir ke timslot pertama
           if(eval.feasibleRandTimeSlot(randCourses.get(0), lastTimeSlot, res))
                res.get(randCourses.get(0)).second = lastTimeSlot;

           return res;
        }

        public ArrayList<Pair<Integer, Integer>> swap2(ArrayList<Pair<Integer, Integer>> sol) {
            return swap(sol, 2);
        }

        public ArrayList<Pair<Integer, Integer>> swap3(ArrayList<Pair<Integer, Integer>> sol) {
            return swap(sol, 3);
        }
    }
}
