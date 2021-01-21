import java.util.*;

class SimulatedAnnealing extends Heuristics {
    double temperature = 100.0; 
    double coolingfactor = 0.995;

    SimulatedAnnealing(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix, 
                ArrayList<Pair<Integer, Integer>> initialSol, ArrayList<Course> courses,
                    ArrayList<Student> students, int numOfIts, int numOfTimeSlots) {
        this.initialSol = cloneSolution(initialSol);;
        this.courses = courses;
        this.students = students;
        this.numOfIts = numOfIts;
        this.numOfTimeSlots = numOfTimeSlots;
        eval = new Timetable.ExamEvaluate(conflictsMatrix, courses, students, initialSol);
        llh = new LowLevelHeuristics();
    }

    public ArrayList<Pair<Integer, Integer>> generateSolution() {
        // init best solution and current solution var
        ArrayList<Pair<Integer, Integer>> bestSol = cloneSolution(initialSol);
        ArrayList<Pair<Integer, Integer>> currSol = cloneSolution(initialSol);
        double bestPenalty = eval.getPenalty(initialSol);

        for(int i = 0; i < numOfIts; i ++) {
            // pilih method Low Level Heuristics (move1, swap2, move2, swap3, move3) secara random
            int llhMethod = randomNumber(1, 5);
            ArrayList<Pair<Integer, Integer>> llhSol;
            
            switch(llhMethod) {
                case 1: {
                    llhSol = llh.move1(currSol);
                    break;
                }
                case 2: {
                    llhSol = llh.swap2(currSol);
                    break;
                }
                case 3: {
                    llhSol = llh.move2(currSol);
                    break;
                }
                case 4: {
                    llhSol = llh.swap3(currSol);
                    break;
                }
                case 5: {
                    llhSol = llh.move3(currSol);
                    break;
                }
                default: {
                    llhSol = llh.move1(currSol);
                }
            }
            
            double currPenalty = eval.getPenalty(currSol);
            double llhPenalty = eval.getPenalty(llhSol);

            // menentukan apakah current solution lebih buruk dari pada solusi Low Level Heuristics
            if(eval.getPenalty(currSol) > eval.getPenalty(llhSol)) {
                currSol = cloneSolution(llhSol);
                currPenalty = eval.getPenalty(currSol);

                // menentukan apakah best solution lebih buruk dari pada current solution
                if(bestPenalty > currPenalty) {
                    bestSol = cloneSolution(llhSol);
                    bestPenalty = currPenalty;
                  //  System.out.println("pernah 2");
                }
            } else if(probability(currPenalty, llhPenalty, temperature) > Math.random()) {
                // cek acceptance probability untuk solusi LLH saat ini
                currSol = cloneSolution(llhSol);
            } else 
                coolingfactor *= 1.2;
            
            // mengurangi temperatur setiap iterasi
            temperature -= coolingfactor;
        } 

        return bestSol;
    }

    private double probability(double currPenalty, double llhPenalty, double t) {
        return Math.exp((currPenalty - llhPenalty) / t);
    }
}