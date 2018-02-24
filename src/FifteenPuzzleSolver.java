import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class FifteenPuzzleSolver {
	
	//////////////////////////////////////////
	private static final int SEED = 11023;
	private static final int THREADCOUNT = 4;
	//////////////////////////////////////////
	
	//Stack that holds all depth counts for threads
	public static Stack<Integer> depthCount = new Stack<Integer>();	
	public static Board board = Board.createBoard(SEED);
	public static List<Board> solution;
	
    public static Runnable myRunnable = new Runnable() {
        public void run() {

        	FifteenPuzzleSolver fps = new FifteenPuzzleSolver();
        	long ts = System.currentTimeMillis();
        	solution = fps.solve(board);
    		long elapsed = System.currentTimeMillis() - ts;
    		
    		System.out.println("Found a solution with " + solution.size() + " moves!");
    		System.out.println("Elapsed time: " + ((double)elapsed) / 1000.0 + " seconds");
    		for (int i=0;i<solution.size();i++) {
    			System.out.println(solution.get(i));
    		}
    		//End program when solution is found and printed
    		System.exit(0);
        }
    };

	
	public static void main(String [] args) {

		System.out.println("Seed: " + SEED);
		//Fill stack in reverse order
		for (int i = 99;i>=0;i--){
			depthCount.push(i);
		}

		//Remove unnecessary depths from stack
		for (int i = 0; i< board.minimumSolutionDepth(); i++){
			depthCount.pop();
		}
		
		System.out.println("Using " + THREADCOUNT + " threads to solve this board: \n" + board);
		System.out.println();

		Thread[] threads = new Thread [THREADCOUNT];
        for(int x =0; x < threads.length; x++) {
            threads[x] = new Thread(myRunnable);
            threads[x].start();
        }	
	}

	public List<Board> solve (Board board) {

		while (true) {
			int depth = depthCount.pop();
			System.out.println("Searching at a depth of: " + depth);
			List<Board> solution = doSolve(board,0,depth);
			if (solution != null) {
				return solution;
			}
		}
	}

	private List<Board> doSolve(Board board, int currentDepth, int maxDepth) {
		
		if (board.isSolved()) {
			List<Board> list = new LinkedList<Board>();
			list.add(board);
			return list;
		}
		
		// stop searching if we can't solve the puzzle within our maximum depth allotment
		if ((currentDepth + board.minimumSolutionDepth()) > maxDepth)
			return null;
		
		// search for neighboring moves...
		List<Board> nextMoves = board.generateSuccessors();
		for (Board nextBoard : nextMoves) {
			
			List<Board> solution = doSolve(nextBoard,currentDepth+1,maxDepth);
			if (solution != null) {
				// prepend this board to the solution, and return
				solution.add(0,board);
				return solution;
			}
		}
		
		// no successor moves were fruitful
		return null;
	}
}
