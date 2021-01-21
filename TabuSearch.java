import java.util.*;

class TabuSearch extends Heuristics {
    int maxTabuSize = 10;

    TabuSearch(ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> conflictsMatrix, 
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
        ArrayList<Pair<Integer, Integer>> bestCandidate = cloneSolution(initialSol);
        ArrayList<Pair<Integer, Integer>> currSol = cloneSolution(initialSol);
        Queue<ArrayList<Pair<Integer, Integer>>> tabuList = new LinkedList();
        
        double bestPenalty = eval.getPenalty(bestSol);
        double bestCandidatePenalty = eval.getPenalty(bestCandidate);
        
        tabuList.add(new ArrayList(initialSol));

        for(int i = 0; i < numOfIts; i ++) {
            ArrayList<ArrayList<Pair<Integer, Integer>>> sneighbor = new ArrayList();
            
            sneighbor.add(llh.move_(bestSol, 1));
            sneighbor.add(llh.swap_(bestSol, 2));
            sneighbor.add(llh.move_(bestSol, 2));
            sneighbor.add(llh.swap_(bestSol, 3));
            sneighbor.add(llh.move_(bestSol, 3));

            int chosen = -1;
            // memilih best neighborhood
            for(int j = 0; j < sneighbor.size(); j ++) {
                if(!tabuList.contains(sneighbor.get(j))) {
                    double neighborPenalty = eval.getPenaltyByTimeslotChanges(bestCandidatePenalty, sneighbor.get(j), bestCandidate);
                    if(neighborPenalty < bestCandidatePenalty) {
                        bestCandidatePenalty = neighborPenalty;
                        chosen = j;
                    }
                }
            }

            if(chosen != -1) {
                ArrayList<Pair<Integer, Integer>> chosenMethod = sneighbor.get(chosen); 
                for(int j = 0; j < chosenMethod.size(); j ++) {
                    bestCandidate.get(chosenMethod.get(j).first).second = chosenMethod.get(j).second;
                }
                //if(eval.getPenalty(bestSol) > eval.getPenalty(bestCandidate)) {
                 //   bestSol = cloneSolution(bestCandidate);
               // }
            }
            
            tabuList.add(bestCandidate);

            if(tabuList.size() > maxTabuSize)
                tabuList.remove();
           // System.out.println("iterasi ke " + i);
        }

        return bestCandidate;
    }
}