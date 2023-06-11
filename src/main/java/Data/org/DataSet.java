package Data.org;

/**
 * This class is to select the requested dataset by the user
 */
public interface DataSet {

    /**
     * This method used to fetch the requested dataset based on the given id
     * @param Index is the id of the requested dataset
     * @return returns the corresponding dataset
     */
    static String GetDataSet(int Index) {
        // use switch pattern
        var DataSet = switch (Index) {
            case 0 -> "johnson8-2-4 Data";    // max clique is 4   |
            case 1 -> "johnson8-4-4 Data";    // max clique is 14  |
            case 2 -> "johnson32-2-4 Data";   // max clique is 16  |
            case 3 -> "johnson16-2 Data";     // max clique is 8   |
            case 4 -> "johnson16-2-4 Data";   // max clique is 8   |
            case 5 -> "c-fat200-1 Data";      // max clique is 12  |
            case 6 -> "c-fat200-2 Data";      // max clique is 24  |
            case 7 -> "c-fat200-5 Data";      // max clique is 58  |
            case 8 -> "c-fat500-1 Data";      // max clique is 14  |
            case 9 -> "c-fat500-2 Data";      // max clique is 26  |
            case 10 -> "c-fat500-5 Data";     // max clique is 64  |
            case 11 -> "hamming6-2 Data";     // max clique is 32  |
            case 12 -> "hamming6-4 Data";     // max clique is 4   |
            case 13 -> "hamming8-2 Data";     // max clique is 128 |
            case 14 -> "hamming10-2 Data";    // max clique is 512 |
            case 15 -> "hamming10-4 Data";    // max clique is 40  |
            case 16 -> "keller4 Data";        // max clique is 11  |
            case 17 -> "keller5 Data";        // max clique is 27  |
            case 18 -> "sanr400-0-5 Data";    // max clique is 13  |
            case 19 -> "sanr200-0-9 Data";    // max clique is 36  |
            case 20 -> "brock200-1 Data";     // max clique is 21  |
            case 21 -> "C4000-5 Data";        // max clique is 18  |
            case 22 -> "san1000 Data";        // max clique is 15  |
            case 23 -> "san200-0-7-1 Data";   // max clique is 30  |
            case 24 -> "DSJC500-5 Data";      // max clique is 13  |
            case 25 -> "C125-9 Data";         // max clique is 34  |
            default -> throw new IllegalStateException("Unexpected value: " + Index);
        };
        // return the path of the given dataset
        return System.getProperty("user.dir") + "\\" + DataSet + ".txt";
    }
}
