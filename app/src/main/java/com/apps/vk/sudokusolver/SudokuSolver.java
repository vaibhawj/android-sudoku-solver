package com.apps.vk.sudokusolver;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

public class SudokuSolver extends Activity {
    AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_solver);

        final GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int totalWidth = displaymetrics.widthPixels;
        int widthPerField = totalWidth / (gridLayout.getColumnCount() + 1);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);

        int k = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final EditText text = new EditText(SudokuSolver.this);
                text.setGravity(Gravity.CENTER);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                params.width = widthPerField;
                text.setLayoutParams(params);

                text.setId(k);

                text.setFilters(filterArray);

                text.setKeyListener(DigitsKeyListener.getInstance("123456789"));

                text.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        int id = text.getId();
                        if (event.getAction() == KeyEvent.ACTION_DOWN && id != 80) {
                            findViewById(id + 1).requestFocus();
                        }
                        return false;
                    }
                });


                gridLayout.addView(text);


                k++;
            }
        }

        final Button clrBtn = (Button) findViewById(R.id.clearButton);
        clrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 81; i++) {
                    EditText et = (EditText) findViewById(i);
                    et.setText("");
                    et.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                }
                findViewById(0).requestFocus();
            }
        });

        final Button solBtn = (Button) findViewById(R.id.solveButton);
        solBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[][] arr = new int[9][9];

                for (int k = 0; k < 81; k++) {
                    EditText et = (EditText) findViewById(k);
                    String in = et.getText().toString().trim();
                    int i = k / 9;
                    int j = k % 9;
                    arr[i][j] = in.equals("") ? 0 : Integer.parseInt(in);
                    if (0 != arr[i][j]) {
                        et.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    }

                }

                final XGrid grid = new XGrid(arr);

                task = new AsyncTask() {

                    private ProgressDialog prog;

                    @Override
                    protected Object doInBackground(Object[] params) {
                        boolean res = grid.solve(grid, this);
                        return Boolean.valueOf(res);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        prog.dismiss();
                        Boolean res = (Boolean) o;

                        if (res) {
                            int k = 0;
                            for (int i = 0; i < 9; i++) {
                                for (int j = 0; j < 9; j++) {
                                    EditText text = (EditText) findViewById(k);
                                    text.setText("" + grid.getGridArr()[i][j]);

                                    k++;

                                }

                            }
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(SudokuSolver.this).create();
                            alertDialog.setTitle("Message");
                            alertDialog.setMessage("Could not be solved!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }

                    void handleOnBackButton() {
                        this.cancel(true);
                    }

                    @Override
                    protected void onPreExecute() {
                        prog = new ProgressDialog(SudokuSolver.this);
                        prog.setTitle("Processing");
                        prog.setMessage("Please wait...");
                        prog.setIndeterminate(true);
                        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        prog.setCancelable(true);
                        prog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                handleOnBackButton();
                            }

                        });


                        prog.show();
                    }

                    @Override
                    protected void onCancelled() {
                        if (this.prog != null) {
                            this.prog.dismiss();
                        }
                    }
                };


                task.execute();

            }
        });

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView titleView = new TextView(SudokuSolver.this);
        titleView.setText("Sudoku Solver");
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setTextSize(20f);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SudokuSolver.this).create();
                alertDialog.setTitle("How to use");
                alertDialog.setMessage("Fill the cells with known Digits. Enter 0 or keep empty for an unknown cell. Press back if it is taking lot of time in processing. It might mean, sudoku is not solvable.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        actionBar.setCustomView(titleView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sudoku_solver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
