class Degree implements Comparable<Degree>{
    // const
    public static int SORT_TYPE_DEGREE = 1;
    public static int SORT_TYPE_WEIGHTED_DEGREE = 2; // product of degree & student
    public static int SORT_TYPE_LWD_LE = 3;
    
    String courseId;
    int degree, weightedDegree, enrollment, sortType = SORT_TYPE_DEGREE;

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
        } else if(sortType == SORT_TYPE_LWD_LE) {
            int comp = c.weightedDegree - this.weightedDegree;
            if(comp == 0) {
                return this.enrollment - c.enrollment;
            }
            return comp;
        }
        return 0;
    }
}