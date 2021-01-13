import java.util.*;

class SimulatedAnnealing extends Heuristics {
    double temperature = 1000.0; 
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
        ArrayList<Pair<Integer, Integer>> bestSol = cloneSolution(initialSol);
        ArrayList<Pair<Integer, Integer>> currSol = cloneSolution(initialSol);
        double initialPenalty = eval.getPenalty(initialSol);
        double bestPenalty = eval.getPenalty(initialSol);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < numOfIts; i ++) {
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
            
            double currPenalty = eval.getPenalty(bestSol);
            double llhPenalty = eval.getPenalty(llhSol);

            if(eval.getPenalty(currSol) > eval.getPenalty(llhSol)) {
                currSol = cloneSolution(llhSol);
                currPenalty = eval.getPenalty(currSol);

                if(bestPenalty > currPenalty) {
                    bestSol = cloneSolution(llhSol);
                    bestPenalty = currPenalty;
                  //  System.out.println("pernah 2");
                }
            } else if(probability(currPenalty, llhPenalty, temperature) > Math.random())
                currSol = cloneSolution(llhSol);

            temperature -= coolingfactor;
        }
        System.out.println();
        System.out.println("Simulated Annealing : ");
        System.out.println("Solution generated in " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println("Penalty : " + bestPenalty);
        System.out.println("Intial Penalty : " + initialPenalty);
        return bestSol;
    }

    private double probability(double currPenalty, double llhPenalty, double t) {
        return Math.exp((currPenalty - llhPenalty) / t);
    }
}