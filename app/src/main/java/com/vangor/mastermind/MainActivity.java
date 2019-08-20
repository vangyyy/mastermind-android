package com.vangor.mastermind;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import game.core.Ball;
import game.core.BallColor;
import game.core.Field;
import game.core.GameState;

import static android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;

public class MainActivity extends AppCompatActivity implements NewGameListener {
	private final int TILE_SIZE = 120;
	private final int TILE_MARGIN = 5;

	private Context context;
	// TODO: Fix, game crashes if not initialized with max sized field
	private Field field = new Field(10, 4, false);
	private int rows = field.getRowCount();
	private int cols = field.getColCount();
	private Button[][] buttons = new Button[rows][cols];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();

		logScreenSize();

		Button btnNewGame = findViewById(R.id.buttonNewGame);
		btnNewGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NewGameDialog newGameDialog = new NewGameDialog();
				newGameDialog.show(getSupportFragmentManager(), "dialogTAG");
			}
		});

		populateButtons();
	}

	public void checkAndRender() {
		switch (field.getState()) {
			case PLAYING:
				if (field.isRowFilled()) {
					field.updateGameState();
					field.generateClue();
					HashMap clueMap = field.getClueHashMap(field.getClue(), field.getCurrentRow());
					Toast.makeText(context, clueMap.get("places") + " place/s, " +
							clueMap.get("colors") + " color/s", Toast.LENGTH_SHORT).show();
					field.nextRow();
				} else {
					Toast.makeText(context, R.string.incomplete_row, Toast.LENGTH_SHORT).show();
				}
				break;
			case SOLVED:
				Toast.makeText(context, R.string.win_msg, Toast.LENGTH_SHORT).show();
				break;
			case FAILED:
				Toast.makeText(context, R.string.lost_msg, Toast.LENGTH_SHORT).show();
				break;
		}
		populateButtons();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void populateButtons() {
		TableLayout table = findViewById(R.id.gameTable);
		table.removeAllViews();

		for (int row = 0; row < rows; row++) {
			TableRow ballRow = new TableRow(context);
			table.addView(ballRow);
			if (field.getCurrentRow() == row) {
				ballRow.setOnTouchListener(new OnSwipeTouchListener(context) {
					@Override
					public void onSwipeRight() {
						checkAndRender();
					}

					@Override
					public void onSwipeLeft() {
						field.clearCurrentRow();
						populateButtons();
					}
				});
			}

			HashMap clueMap = field.getClueHashMap(field.getClue(), row);

			// Create place clue button
			Button placesClueButton = new Button(context);
			ballRow.addView(placesClueButton);
			placesClueButton.setText(
					getResources().getString(R.string.places_clue_format, clueMap.get("places"))
			);

			for (int col = 0; col < cols; col++) {
				Button button = new Button(context);
				ballRow.addView(button);
				buttons[row][col] = button;

				// On button click function
				final int BUTTON_ROW_POSITION = row;
				final int BUTTON_COL_POSITION = col;
				if (field.getCurrentRow() == row) {
					button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							gridButtonClicked(BUTTON_ROW_POSITION, BUTTON_COL_POSITION);
						}
					});
				}
			}

			// Create color clue button
			Button colorsClueButton = new Button(context);
			ballRow.addView(colorsClueButton);
			colorsClueButton.setText(
					getResources().getString(R.string.colors_clue_format, clueMap.get("colors"))
			);


			if (row <= field.getCurrentRow()) {
				placesClueButton.setVisibility(View.INVISIBLE);
				colorsClueButton.setVisibility(View.INVISIBLE);
			}

			int[] gradientColors = {
					getResources().getColor(R.color.colorPrimary),
					getResources().getColor(R.color.colorPrimaryDark)
			};

			// Styling
			GradientDrawable gradient = new GradientDrawable(LEFT_RIGHT, gradientColors);
			gradient.setCornerRadius(999f);

			placesClueButton.setBackground(gradient);
			colorsClueButton.setBackground(gradient);

			ViewGroup.LayoutParams params = colorsClueButton.getLayoutParams();
			params.width = TILE_SIZE;
			params.height = TILE_SIZE;
			placesClueButton.setLayoutParams(params);
			colorsClueButton.setLayoutParams(params);

			placesClueButton.setElevation(3f);
			colorsClueButton.setElevation(3f);

			placesClueButton.setTextColor(getResources().getColor(R.color.darkBackgroundText));
			colorsClueButton.setTextColor(getResources().getColor(R.color.darkBackgroundText));

			setMargins(placesClueButton, TILE_MARGIN, 0, TILE_MARGIN, TILE_MARGIN);
			setMargins(colorsClueButton, TILE_MARGIN, 0, TILE_MARGIN, TILE_MARGIN);
		}
		renderButtons();
	}

	private void gridButtonClicked(int row, int col) {
		Button button = buttons[row][col];
		if (field.getState() == GameState.PLAYING) {
			try {
				Ball ball = field.getBall(row, col);
				BallColor nextColor = BallColor.getNext(ball);
				field.setBall(col + 1, nextColor);
			} catch (Exception e) {
				Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
		populateButtons();
	}

	private void renderButtons() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {

				Button button = buttons[row][col];
				Ball ball = field.getBall(row, col);

				// Free tile colors
				int ballColorLight = getResources().getColor(R.color.freeTileColor);
				int ballColorDark = getResources().getColor(R.color.freeTileColor);

				if (row == 0) {
					// Hidden tile colors
					if (field.getState() == GameState.PLAYING) {
						ballColorLight = getResources().getColor(R.color.colorPrimary);
						ballColorDark = getResources().getColor(R.color.colorPrimaryDark);
					} else {
						ballColorLight = Color.parseColor(ball.getColor().getColorLight());
						ballColorDark = Color.parseColor(ball.getColor().getColorDark());
					}
					button.setTextColor(getResources().getColor(R.color.darkBackgroundText));
					button.setText("?");
				} else if (ball != null) {
					// Filled tile colors
					ballColorLight = Color.parseColor(ball.getColor().getColorLight());
					ballColorDark = Color.parseColor(ball.getColor().getColorDark());
				}

				int[] gradientColors = {
						ballColorLight,
						ballColorDark
				};

				GradientDrawable gradient = new GradientDrawable(BOTTOM_TOP, gradientColors);
				gradient.setCornerRadius(999f);
				button.setBackground(gradient);

				ViewGroup.LayoutParams params = button.getLayoutParams();
				params.width = TILE_SIZE;
				params.height = TILE_SIZE;
				button.setLayoutParams(params);

				button.setElevation(3f);

				setMargins(button, TILE_MARGIN, 0, TILE_MARGIN, TILE_MARGIN);
			}
		}
	}

	private void setMargins(View view, int left, int top, int right, int bottom) {
		if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
			p.setMargins(left, top, right, bottom);
			view.requestLayout();
		}
	}

	private void logScreenSize() {
		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		int screenWidth = display.widthPixels;
		int screenHeight = display.heightPixels;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int densityDpi = (int) (metrics.density * 160f);
		Log.d("=== Screen size: ",
				"height: " + screenHeight + ", width: " + screenWidth + ", density(DPI): " + densityDpi);
	}

	@Override
	public void newGame(int rows, int columns) {
		this.rows = rows;
		this.cols = columns;
		field = new Field(this.rows++, this.cols, false);
		Log.d("==== New game: ",
				"Rows: " + rows + " ,cols: " + columns + " currentRow: " + field.getCurrentRow());
		populateButtons();
	}
}
