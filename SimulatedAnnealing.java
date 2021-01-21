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
        double currPenalty = eval.getPenalty(currSol);
        for(int i = 0; i < numOfIts; i ++) {
            // pilih method Low Level Heuristics (move1, swap2, move2, swap3, move3) secara random
            int llhMethod = randomNumber(1, 5);
            ArrayList<Pair<Integer, Integer>> llhSol;
            boolean isMove = true;
            switch(llhMethod) {
                case 1: {
                    llhSol = llh.move_(currSol, 1);
                    break;
                }
                case 2: {
                    isMove = false;
                    llhSol = llh.swap_(currSol, 2);
                    break;
                }
                case 3: {
                    llhSol = llh.move_(currSol, 2);
                    break;
                }
                case 4: {
                    isMove = false;
                    llhSol = llh.swap_(currSol, 3);
                    break;
                }
                case 5: {
                    llhSol = llh.move_(currSol, 3);
                    break;
                }
                default: {
                    llhSol = llh.move_(currSol, 1);
                }
            }
            
            double llhPenalty = eval.getPenaltyByTimeslotChanges(currPenalty, llhSol, currSol);

            // menentukan apakah current solution lebih buruk dari pada solusi Low Level Heuristics
            if(currPenalty > llhPenalty) {
                for(int j = 0; j < llhSol.size(); j ++) {
                    currSol.get(llhSol.get(j).first).second = llhSol.get(j).second;
                }
                //currSol = cloneSolution(llhSol);
                currPenalty = llhPenalty;

                // menentukan apakah best solution lebih buruk dari pada current solution
                if(bestPenalty > currPenalty) {
                    bestSol = cloneSolution(currSol);
                    bestPenalty = currPenalty;
                  //  System.out.println("pernah 2");
                }
            } else if(probability(currPenalty, llhPenalty, temperature) > Math.random()) {
                // cek acceptance probability untuk solusi LLH saat ini
                //currSol = cloneSolution(llhSol);
                for(int j = 0; j < llhSol.size(); j ++) {
                    currSol.get(llhSol.get(j).first).second = llhSol.get(j).second;
                }
                currPenalty = llhPenalty;
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