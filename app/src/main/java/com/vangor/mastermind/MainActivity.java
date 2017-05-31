package com.vangor.mastermind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vangor.mastermind.game.consoleui.TextUI;
import com.vangor.mastermind.game.core.Ball;
import com.vangor.mastermind.game.core.GameState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextUI textUI = new TextUI();
    TextView game;
    Button btnNewGame;
    Button btnCommand;
    Button btnCheck;
    EditText txtCommand;
    Spinner rowSpinner;
    ArrayAdapter<CharSequence> rowAdapter;
    Spinner columnSpinner;
    ArrayAdapter<CharSequence> columnAdapter;

    private int rows = textUI.getField().getRowCount();
    private int cols = textUI.getField().getColCount();
    Button buttons[][] = new Button[rows][cols];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game = (TextView) findViewById(R.id.gameText);
        btnNewGame = (Button) findViewById(R.id.buttonNewGame);
        btnCommand = (Button) findViewById(R.id.buttonCommand);
        txtCommand = (EditText) findViewById(R.id.textCommand);
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
                int rows = Integer.parseInt(rowSpinner.getSelectedItem().toString());
                int cols = Integer.parseInt(columnSpinner.getSelectedItem().toString());
                textUI.newField(rows, cols, false);
                game.setText(String.valueOf(textUI.render()));
            }
        });

        btnCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String line = txtCommand.getText().toString();
                Matcher matcher = Pattern.compile("([1-" + textUI.getField().getColCount() + "])([RGBYCDMW])").matcher(line);
                if (matcher.matches()) {
                    textUI.getField().setBall(matcher.group(1).charAt(0) - 48, textUI.signToBallColor(matcher.group(2).charAt(0)));
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter command ([1-" + textUI.getField().getColCount() + "])([RGBYCDMW])", Toast.LENGTH_LONG).show();
                }
                game.setText(String.valueOf(textUI.render()));
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textUI.getField().getState() == GameState.PLAYING && textUI.getField().isRowFilled()) {
                    textUI.getField().updateGameState();
                    textUI.getField().generateClue();
                    textUI.getField().nextRow();
                    game.setText(String.valueOf(textUI.render()));
                    Toast.makeText(getApplicationContext(), "You guessed " + textUI.clueToString(textUI.getField().getClue(), textUI.getField().getCurrentRow()+1), Toast.LENGTH_LONG).show();
                } else if (textUI.getField().getState() == GameState.PLAYING) {
                    Toast.makeText(getApplicationContext(), "Row is not complete!", Toast.LENGTH_LONG).show();
                } else if (textUI.getField().getState() == GameState.SOLVED) {
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
