package com.apps.vk.sudokusolver;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class XGrid {

    private int rows;
    private int cols;
    private int[][] gridArr;

    public XGrid(String sudokuGrid) {
        String[] gridRowsArr = sudokuGrid.trim().split("\n");
        int[][] arr = new int[9][9];

        int i = 0;
        for (String row : gridRowsArr) {
            String[] rowValuesArr = row.trim().split(" ");
            int j = 0;
            for (String value : rowValuesArr) {
                arr[i][j] = Integer.parseInt(value.trim());
                j++;
            }
            i++;
        }


        this.rows = arr.length;
        this.cols = arr[0].length;
        this.gridArr = arr;
    }

    public XGrid(int[][] gridArr) {

        this.rows = gridArr.length;
        this.cols = gridArr[0].length;
        this.gridArr = gridArr;
    }

    public int[][] getGridArr() {
        return this.gridArr;
    }

    public int getNumOfRows() {
        return rows;
    }

    public int getNumOfCols() {
        return cols;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                sb.append(gridArr[i][j] + " ");
            }
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    public List<Integer> getPossibleNumbersAt(XGrid grid, int rowIndex, int colIndex) {
        List<Integer> possibleNumbers = new ArrayList<Integer>();

        List<Integer> filledInTheGroup = this.getFilledNumbersInTheGroup(grid, rowIndex, colIndex);
        List<Integer> filledInTheColumn = this.getFilledNumbersInTheColumn(grid, colIndex);
        List<Integer> filledInTheRow = this.getFilledNumbersInTheRow(grid, rowIndex);

        for (int i = 1; i <= 9; i++) {
            if (!filledInTheGroup.contains(i) && !filledInTheColumn.contains(i) && !filledInTheRow.contains(i)) {
                possibleNumbers.add(i);
            }

        }

        return possibleNumbers;
    }

    public List<Integer> getFilledNumbersInTheRow(XGrid grid, int rowIndex) {
        List<Integer> numbers = new ArrayList<Integer>();
        int[] rowArray = getRowArray(grid, rowIndex);

        for (int num : rowArray) {
            if (num > 0) {
                numbers.add(num);
            }

        }

        return numbers;
    }

    public int[] getRowArray(XGrid grid, int rowIndex) {
        int arr[] = new int[cols];

        for (int i = 0; i < cols; i++) {
            arr[i] = grid.gridArr[rowIndex][i];
        }

        return arr;
    }

    public List<Integer> getFilledNumbersInTheColumn(XGrid grid, int colIndex) {
        List<Integer> numbers = new ArrayList<Integer>();
        int[] colArray = getColArray(grid, colIndex);

        for (int num : colArray) {
            if (num > 0) {
                numbers.add(num);
            }

        }

        return numbers;
    }

    public int[] getColArray(XGrid grid, int colIndex) {

        int arr[] = new int[rows];

        for (int i = 0; i < rows; i++) {
            arr[i] = grid.gridArr[i][colIndex];
        }

        return arr;
    }

    public List<Integer> getFilledNumbersInTheGroup(XGrid grid, int x, int y) {
        List<Integer> numbers = new ArrayList<Integer>();

        int rMin = 0;
        int cMin = 0;
        int rMax = 2;
        int cMax = 2;

        if (x > 2 && x < 6) {
            rMin = 3;
            rMax = 5;
        } else if (x > 5) {
            rMin = 6;
            rMax = 8;
        }

        if (y > 2 && y < 6) {
            cMin = 3;
            cMax = 5;
        } else if (y > 5) {
            cMin = 6;
            cMax = 8;
        }

        for (int i = rMin; i <= rMax; i++) {
            for (int j = cMin; j <= cMax; j++) {
                if (grid.gridArr[i][j] > 0) {
                    numbers.add(grid.gridArr[i][j]);
                }

            }

        }

        return numbers;
    }

    public int valueAt(int x, int y) {

        return this.gridArr[x][y];
    }

    public void setValueAt(int x, int y, int num) {

        this.gridArr[x][y] = num;
    }

    public boolean solve(XGrid grid, AsyncTask task) {

        if (task.isCancelled()) {
            return false;
        }

        Cell cell = new Cell(0, 0);

        if (!findUnassignedLocation(grid.gridArr, cell)) {
            this.gridArr = grid.gridArr;
            return true;
        }

        for (int num = 1; num <= 9; num++) {
            if (isSafe(grid, cell, num)) {
                grid.gridArr[cell.row][cell.col] = num;

                if (solve(grid, task))
                    return true;

                grid.gridArr[cell.row][cell.col] = 0;
            }
        }
        return false;

    }

    private boolean isSafe(XGrid grid, Cell cell, int num) {

        List<Integer> possibleNumbers = getPossibleNumbersAt(grid, cell.row, cell.col);

        if (possibleNumbers.contains(Integer.valueOf(num))) {
            return true;
        }

        return false;
    }

    private boolean findUnassignedLocation(int[][] gridArr, Cell cell) {
        for (cell.row = 0; cell.row < rows; cell.row++)
            for (cell.col = 0; cell.col < cols; cell.col++)
                if (gridArr[cell.row][cell.col] == 0)
                    return true;
        return false;
    }

    private class Cell {

        public int row;

        public int col;

        public Cell(int i, int j) {
            this.row = i;
            this.col = j;

        }

    }

}
