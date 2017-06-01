package com.vangor.mastermind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.vangor.mastermind.game.core.Ball;
import com.vangor.mastermind.game.core.BallColor;
import com.vangor.mastermind.game.core.Field;
import com.vangor.mastermind.game.core.GameState;

public class MainActivity extends AppCompatActivity {

    //TODO: inicializacia pola s [8,4]
    private Field field = new Field(10, 6, false);
    private int rows = field.getRowCount();
    private int cols = field.getColCount();
    private ImageButton buttons[][] = new ImageButton[rows][cols];

    private Button btnNewGame;
    private Button btnCheck;
    private Spinner rowSpinner;
    private ArrayAdapter<CharSequence> rowAdapter;
    private Spinner columnSpinner;
    private ArrayAdapter<CharSequence> columnAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("NewGame", "Rows: " + rows + " ,cols: " + cols + " currentRow: " + field.getCurrentRow());
        Log.d("NewGame", "Field rows: " + field.getRowCount() + " ,field cols: " + field.getColCount() + " currentRow: " + field.getCurrentRow());
        btnNewGame = (Button) findViewById(R.id.buttonNewGame);
        btnCheck = (Button) findViewById(R.id.buttonCheck);

        rowSpinner = (Spinner) findViewById(R.id.spinnerRow);
        rowAdapter = ArrayAdapter.createFromResource(this,
                R.array.rows_array, android.R.layout.simple_spinner_item);
        rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowSpinner.setAdapter(rowAdapter);

        columnSpinner = (Spinner) findViewById(R.id.spinnerColumn);
        columnAdapter = ArrayAdapter.createFromResource(this,
                R.array.cols_array, android.R.layout.simple_spinner_item);
        columnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(columnAdapter);

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rows = Integer.parseInt(rowSpinner.getSelectedItem().toString());
                cols = Integer.parseInt(columnSpinner.getSelectedItem().toString());
                field = new Field(rows++, cols, false);
                Log.d("btnNewGame", "Rows: " + rows + " ,cols: " + cols + " currentRow: " + field.getCurrentRow());
                Log.d("btnNewGame", "Field rows: " + field.getRowCount() + " ,field cols: " + field.getColCount() + " currentRow: " + field.getCurrentRow());
                populateButtons();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (field.getState() == GameState.PLAYING && field.isRowFilled()) {
                    field.updateGameState();
                    field.generateClue();
                    Toast.makeText(getApplicationContext(), field.clueToString(field.getClue(), field.getCurrentRow()), Toast.LENGTH_SHORT).show();
                    field.nextRow();
                } else if (field.getState() == GameState.PLAYING) {
                    Toast.makeText(getApplicationContext(), "Row is not complete!", Toast.LENGTH_SHORT).show();
                } else if (field.getState() == GameState.SOLVED) {
                    Toast.makeText(getApplicationContext(), "You WON!", Toast.LENGTH_SHORT).show();
                } else if (field.getState() == GameState.FAILED) {
                    Toast.makeText(getApplicationContext(), "You LOST!", Toast.LENGTH_SHORT).show();
                }
                populateButtons();
            }
        });

        populateButtons();
    }

    private void populateButtons() {
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
        table.removeAllViews();
        for (int row = 0; row < rows; row++) {
            //Create new tableRow
            TableRow tableRow = new TableRow(this);
            //table.setBackgroundResource(R.color.colorAccent);
            table.addView(tableRow);

            //Insert faces clue
            String image = field.clueToImg(field.getClue(), row, true);
            int picId = getResources().getIdentifier(image, "drawable", getApplicationContext().getPackageName());
            ImageView clue = new ImageView(this);
            clue.setImageResource(picId);
            tableRow.addView(clue);

            for (int col = 0; col < cols; col++) {
                ImageButton button = new ImageButton(this);
                tableRow.addView(button);
                buttons[row][col] = button;

                //Store button reference into an array
                final int FINAL_ROW = row;
                final int FINAL_COL = col;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gridButtonClicked(FINAL_ROW, FINAL_COL);
                    }
                });
            }

            //Insert places clue
            image = field.clueToImg(field.getClue(), row, false);
            picId = getResources().getIdentifier(image, "drawable", getApplicationContext().getPackageName());
            clue = new ImageView(this);
            clue.setImageResource(picId);
            tableRow.addView(clue);
        }
        renderImageButtons();
    }

    private void gridButtonClicked(int row, int col) {
        ImageButton button = buttons[row][col];
        if (field.getState() == GameState.PLAYING) {
            try {
                Ball ball = field.getTile(row, col);
                BallColor nextColor = BallColor.getNext(ball);
                field.setBall(col + 1, nextColor);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        populateButtons();
    }

    private void renderImageButtons() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ImageButton button = buttons[row][col];
                Ball ball = field.getTile(row, col);
                String image = (row == 0 && field.getState() == GameState.PLAYING) ? "unknown" :
                        (ball != null) ? ball.getColor().toString().toLowerCase() : "nothing";
                int picId = getResources().getIdentifier(image, "drawable", getApplicationContext().getPackageName());
                button.setBackgroundResource(picId);
            }
        }
    }
}
