import java.util.Scanner;

public class ChainReaction1 {

    static Scanner sc = new Scanner(System.in);
    static int totalMoves = 0; // tracks how many moves have been played overall

    public static int criticalValue(int i, int j, int r, int c) {
        // corner cells explode at 2 orbs, edge cells at 3, middle cells at 4
        if ((i == 0 && j == 0) || (i == 0 && j == c - 1) || (i == r - 1 && j == 0) || (i == r - 1 && j == c - 1))
            return 2;
        else if ((j == 0) || (i == 0) || (i == r - 1) || (j == c - 1))
            return 3;
        else
            return 4;
    }

    // Cells are stored as " ", " R", " RR", " RRR", etc.
    // The leading space is index 0; color chars start at index 1.
    public static char colorOf(String cell) {
        if (cell.length() <= 1) return ' ';
        return cell.charAt(1);
    }

    // number of orbs in a cell = length - 1 (because of the leading space)
    public static int orbCount(String cell) {
        return cell.length() - 1;
    }

    public static void explode(String arr[][], int i, int j, char turn, int r, int c) {
        for (int k = 0; k < r; k++) {
            for (int m = 0; m < c; m++) {
                if ((k == i && Math.abs(m - j) == 1) || (m == j && Math.abs(i - k) == 1)) {
                    char cellColor = colorOf(arr[k][m]);

                    if (cellColor == ' ') {
                        // empty neighbour: drop one orb of current player's color
                        arr[k][m] = arr[k][m] + turn;
                    } else if (cellColor == turn) {
                        // same color: just add one
                        arr[k][m] = arr[k][m] + turn;
                    } else {
                        // opponent color: convert all existing orbs, then add one
                        int len = orbCount(arr[k][m]);
                        arr[k][m] = " ";
                        for (int l = 0; l < len; l++) {
                            arr[k][m] = arr[k][m] + turn;
                        }
                        arr[k][m] = arr[k][m] + turn;
                    }

                    // check if this neighbour itself reached critical mass
                    int criticalmass = criticalValue(k, m, r, c);
                    if (orbCount(arr[k][m]) >= criticalmass) {
                        arr[k][m] = " ";
                        // only recurse if the opponent still has orbs somewhere — otherwise
                        // the chain would loop forever on a board owned entirely by 'turn'
                        if (opponentHasOrb(arr, turn, r, c)) {
                            explode(arr, k, m, turn, r, c);
                        }
                    }
                }
            }
        }
    }

    public static boolean isgameover(String arr[][], char turn, int r, int c) {
        // game is over only if opponent has zero orbs AND current player has at least one
        boolean currentHasOrb = false;
        for (int k = 0; k < r; k++) {
            for (int m = 0; m < c; m++) {
                char col = colorOf(arr[k][m]);
                if (col != ' ' && col != turn) {
                    return false; // opponent still has an orb
                }
                if (col == turn) {
                    currentHasOrb = true;
                }
            }
        }
        return currentHasOrb;
    }

    // returns true if any cell holds the opponent's color
    public static boolean opponentHasOrb(String arr[][], char turn, int r, int c) {
        for (int k = 0; k < r; k++) {
            for (int m = 0; m < c; m++) {
                char col = colorOf(arr[k][m]);
                if (col != ' ' && col != turn) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void placement(String arr[][], int i, int j, char turn, int r, int c) {
        // bounds check
        if (i < 0 || i >= r || j < 0 || j >= c) {
            System.out.println("Invalid position. Try again.");
            System.out.println(turn + "'s turn:");
            input(arr, turn, r, c);
            return;
        }

        char cellColor = colorOf(arr[i][j]);

        // can only place on empty or own-color cells
        if (cellColor != ' ' && cellColor != turn) {
            System.out.println("That cell belongs to the opponent. Choose another cell.");
            System.out.println(turn + "'s turn:");
            input(arr, turn, r, c);
            return;
        }

        arr[i][j] = arr[i][j] + turn;
        totalMoves++;

        int criticalmass = criticalValue(i, j, r, c);
        if (orbCount(arr[i][j]) >= criticalmass) {
            arr[i][j] = " ";
            explode(arr, i, j, turn, r, c);
        }

        printmatrix(arr, r, c);

        // need at least one move from each side before the game can end
        if (totalMoves >= 2 && isgameover(arr, turn, r, c)) {
            System.out.println(turn + " is the winner");
            return;
        }

        // switch turn
        if (turn == 'R') {
            turn = 'G';
        } else {
            turn = 'R';
        }
        System.out.println(turn + "'s turn:");
        input(arr, turn, r, c);
    }

    public static void printmatrix(String arr[][], int r, int c) {
        System.out.println("_______________________________________________________________________________");
        for (int k = 0; k < r; k++) {
            for (int m = 0; m < c; m++) {
                System.out.print(arr[k][m] + "\t|");
            }
            System.out.println("");
            System.out.println("_______________________________________________________________________________");
        }
    }

    public static void input(String arr[][], char turn, int r, int c) {
        System.out.println("Enter the position(i and j):");
        int i = sc.nextInt();
        int j = sc.nextInt();
        placement(arr, i, j, turn, r, c);
    }

    public static char choose() {
        System.out.println("Who is starting(R or G?)");
        String turn = sc.next();
        return turn.charAt(0);
    }

    public static void main(String arg[]) {
        int r, c;
        System.out.println("Enter No of rows and columns");
        r = sc.nextInt();
        c = sc.nextInt();
        String arr[][] = new String[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                arr[i][j] = " ";
            }
        }
        char turn = choose();
        printmatrix(arr, r, c);
        System.out.println(turn + "'s turn:");
        input(arr, turn, r, c);
    }
}
