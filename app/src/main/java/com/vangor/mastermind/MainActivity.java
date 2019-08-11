package com.vangor.mastermind;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import game.core.Ball;
import game.core.BallColor;
import game.core.Field;
import game.core.GameState;

public class MainActivity extends AppCompatActivity implements NewGameListener {

	// TODO: Fix, game crashes if not initialized with max sized field
	private Field field = new Field(10, 6, false);
	private int rows = field.getRowCount();
	private int cols = field.getColCount();
	private ImageButton[][] buttons = new ImageButton[rows][cols];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		logScreenSize();

		Button btnNewGame = findViewById(R.id.buttonNewGame);
		Button btnCheck = findViewById(R.id.buttonCheck);

		btnNewGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NewGameDialog newGameDialog = new NewGameDialog();
				newGameDialog.show(getSupportFragmentManager(), "dialogTAG");
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
		TableLayout table = findViewById(R.id.gameTable);
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

				//On button click function
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

	private void logScreenSize() {
		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		int screenWidth = display.widthPixels;
		int screenHeight = display.heightPixels;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int densityDpi = (int) (metrics.density * 160f);
		Log.d("screenSize", "Height: " + screenHeight + " ,width: " + screenWidth + " " + screenHeight + " " + screenWidth + " " + densityDpi);
	}

	@Override
	public void newGame(int rows, int columns) {
		this.rows = rows;
		this.cols = columns;
		field = new Field(this.rows++, this.cols, false);
		Log.d("=======================", "Rows: " + rows + " ,cols: " + columns + " currentRow: " + field.getCurrentRow());
		populateButtons();
	}
}
