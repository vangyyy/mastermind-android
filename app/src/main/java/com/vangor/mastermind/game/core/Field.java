package com.vangor.mastermind.game.core;

public class Field {

	private final int rowCount;
	private final int colCount;
	private int currentRow;
	private Ball[][] tiles;
	private ClueType[][] clue;
	private GameState state = GameState.PLAYING;
	private boolean duplicates;
	private long startTime;

	public Field(int rowCount, int colCount, boolean duplicates) {
		if (colCount > BallColor.values().length) {
			throw new IllegalArgumentException("Maximum of " + BallColor.values().length + " columns is allowed!");
		}
		this.rowCount = rowCount + 1;
		this.colCount = colCount;
		this.currentRow = rowCount;
		this.duplicates = duplicates;
		tiles = new Ball[this.rowCount][this.colCount];
		clue = new ClueType[this.rowCount][this.colCount];
		if (duplicates) {
			generateRandomRow();
		} else {
			generateUniqueRow();
		}
		startTime = System.currentTimeMillis();
	}

	/**
	 * Generates sequence of balls with random colors on 0. row of playing field
	 */
	private void generateRandomRow() {
		for (int col = 0; col < colCount; col++) {
			tiles[0][col] = new Ball(BallColor.getRandom());
		}
	}

	/**
	 * Generates sequence of balls with unique colors on 0. row of playing field
	 */
	private void generateUniqueRow() {
		tiles[0][0] = new Ball(BallColor.getRandom());
		for (int col = 1; col < colCount; col++) {
			do {
				tiles[0][col] = new Ball(BallColor.getRandom());
			} while (!checkForUniqueColors(col));
		}
	}

	/**
	 * Compares color on @param col position to all previous colors in the same row
	 * Returns true if color is unique
	 */
	private boolean checkForUniqueColors(int col) {
		for (int i = 0; i < col; i++) {
			if (tiles[0][col].getColor() == tiles[0][i].getColor()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Changes game state based on current row
	 */
	public void updateGameState() {
		state = GameState.SOLVED;
		for (int col = 0; col < getColCount(); col++) {
			if (tiles[0][col].getColor() != tiles[currentRow][col].getColor()) {
				state = currentRow - 1 > 0 ? GameState.PLAYING : GameState.FAILED;
				break;
			}
		}
	}

	/**
	 * Generates a new clue and stores it on tho current row inside clue[][]
	 */
	public void generateClue() {
		int cluePosition = 0;
		for (int secretCol = 0; secretCol < getColCount(); secretCol++) {
			for (int guessCol = 0; guessCol < getColCount(); guessCol++) {
				if (tiles[0][secretCol].getColor() == tiles[currentRow][guessCol].getColor()) {
					clue[currentRow][cluePosition++] = ClueType.COLOR;
					break;
				}
			}
		}
		cluePosition = 0;
		for (int col = 0; col < getColCount(); col++) {
			if (tiles[0][col].getColor() == tiles[currentRow][col].getColor()) {
				clue[currentRow][cluePosition++] = ClueType.PLACE;
			}
		}
	}

	public void nextRow() {
		currentRow--;
	}

	public boolean isRowFilled() {
		for (int col = 0; col < getColCount(); col++) {
			if (tiles[currentRow][col] == null) {
				return false;
			}
		}
		return true;
	}

	public void setBall(int colCount, BallColor color) {
		tiles[currentRow][colCount - 1] = new Ball(color);
	}

	public Ball getTile(int row, int col) {
		return tiles[row][col];
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColCount() {
		return colCount;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public ClueType[][] getClue() {
		return clue;
	}

	public GameState getState() {
		return state;
	}

	public void restart() {
		state = GameState.PLAYING;
		currentRow = rowCount - 1;
		tiles = new Ball[rowCount][colCount];
		clue = new ClueType[rowCount][colCount];
		if (duplicates) {
			generateRandomRow();
		} else {
			generateUniqueRow();
		}
		startTime = System.currentTimeMillis();
	}

	/**
	 * Calculates points for current game based on time elapsed, and % of rows remaining
	 */
	public int calculateScore() {
		if (state == GameState.SOLVED) {
			int elapsedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
			elapsedTime = duplicates ? elapsedTime : elapsedTime / 2;
			return (colCount * 100) - elapsedTime + (int) ((float) currentRow / (rowCount - 1) * 100 * 5);
		}
		return 0;
	}

	public String clueToString(ClueType[][] clue, int row) {
		int places = 0;
		int colors = 0;
		for (int col = 0; col < getColCount(); col++) {
			places = clue[row][col] == ClueType.PLACE ? places + 1 : places;
			colors = clue[row][col] == ClueType.COLOR ? colors + 1 : colors;
		}
		return "You guessed " + places + " place/s and " + colors + " color/s right!";
	}

	public String clueToImg(ClueType[][] clue, int row, boolean first) {
		int places = 0;
		int colors = 0;
		for (int col = 0; col < getColCount(); col++) {
			places = clue[row][col] == ClueType.PLACE ? places + 1 : places;
			colors = clue[row][col] == ClueType.COLOR ? colors + 1 : colors;
		}

		if (row > getCurrentRow()) {
			return (first) ? String.format("places_%s", places) : String.format("faces_%s", colors);
		} else {
			return "blank";
		}
	}
}
