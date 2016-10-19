import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Sudoku {
    protected int field[][];
    protected List<TreeSet<Integer>> possibleValues;
    protected int count;
    protected boolean needCount;
    public static final int FSIZE = 9;

    protected void printState() {
        System.out.println("-----------");
        for (int row = 0; row < FSIZE; row++) {
            for (int column = 0; column < FSIZE; column++) {
                System.out.print(field[row][column]);
                if (column % 3 == 2) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    protected void createField() {
        field = new int[FSIZE][FSIZE];
        possibleValues = new ArrayList<>();
        for (int row = 0; row < FSIZE; row++) {
            for (int column = 0; column < FSIZE; column++) {
                field[row][column] = 0;
            }
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("gistfile1.txt"))) {
            String ch;
            int raw = 0;

            while ((ch = bufferedReader.readLine()) != null) {
                int pos = 0;
                int column = 0;
                while (column < FSIZE) {
                    switch (ch.charAt(pos)) {
                        case '*':
                            column++;
                        case ' ':
                            pos++;
                            break;
                        default:
                            if ((ch.charAt(pos) - '0' < 10) && (ch.charAt(pos) - '0' > 0)) {
                                field[raw][column] = ch.charAt(pos) - '0';
                                pos++;
                                column++;
                            } else {
                                pos++;
                            }
                            break;
                    }

                }
                raw++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedList<Integer> queue = new LinkedList<>();
        for (int i = 0; i < FSIZE; i++) {
            for (int j = 0; j < FSIZE; j++) {
                possibleValues.add(new TreeSet<>());
                if (field[i][j] == 0) {
                    for (Integer digit = 1; digit < 10; digit++) {
                        possibleValues.get(i * FSIZE + j).add(digit);
                    }
                } else {
                    queue.add(i * FSIZE + j);
                }
            }
        }
        simplifyField(queue);
    }

    protected void simplifyField(Queue<Integer> queue) {
        for (int i : queue) {
            int row = i / FSIZE;
            int column = i % FSIZE;
            int digit = field[row][column];
            boolean check = checkRow(row, digit, true) && checkColumn(column, digit, true) && checkBlock(row, column, digit, true);
        }

    }

    protected boolean checkBlock(int row, int column, int digit, boolean needReplace) {
        row = (row / 3) * 3;
        column = (column / 3) * 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!needReplace) {
                    if (field[row + i][column + j] == digit) {
                        return false;
                    }
                } else {
                    possibleValues.get((row + i) * FSIZE + column + j).remove(new Integer(digit));
                }
            }
        }
        return true;
    }

    protected boolean checkRow(int row, int digit, boolean needReplace) {
        for (int column = 0; column < FSIZE; column++) {
            if (!needReplace) {
                if (field[row][column] == digit) {
                    return false;
                }
            } else {
                possibleValues.get(row * FSIZE + column).remove(new Integer(digit));
            }
        }
        return true;
    }

    protected boolean checkColumn(int column, int digit, boolean needReplace) {
        for (int row = 0; row < FSIZE; row++) {
            if (!needReplace) {
                if (field[row][column] == digit) {
                    return false;
                }
            } else {
                possibleValues.get(row * FSIZE + column).remove(new Integer(digit));
            }
        }
        return true;
    }

    protected void setNeedSolutions(boolean b) {
        needCount = !b;
    }


    public static void main(String[] args) {
        Sudoku s = new Sudoku();
        s.createField();
        s.setNeedSolutions(args.length != 0);
        s.findSolution(0, 0);
        System.out.println(s.count);
    }

    public void findSolution(int row, int column) {
        if (row > 8) {
            count++;
            if (!needCount) {
                printState();
            }
            return;
        }

        if (field[row][column] != 0) {
            nextCell(row, column);
        } else {
            for (int digit : possibleValues.get(row * FSIZE + column)) {
                if (checkRow(row, digit, false) && checkColumn(column, digit, false) && checkBlock(row, column, digit, false)) {
                    field[row][column] = digit;
                    nextCell(row, column);
                }
            }
            field[row][column] = 0;
        }
    }

    public void nextCell(int row, int column) {
        if (column < FSIZE - 1)
            findSolution(row, column + 1);
        else
            findSolution(row + 1, 0);
    }
}