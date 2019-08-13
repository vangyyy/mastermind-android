package game.core;

public enum BallColor {
	CITRUS_PEEL     ("#FDC830", "#F37335"),
	SIN_CITY_RED    ("#ED213A", "#93291E"),
	BLUE_RASPBERRY  ("#56CCF2", "#2F80ED"),
	BLACK_ROSE      ("#f4c4f3", "#fc67fa"),
	FROST           ("#004e92", "#000428"),
	LUSH            ("#a8e063", "#56ab2f"),
	TERMINUS        ("#0f9b0f", "#0f3800"), // Change
	BIGHEAD         ("#c94b4b", "#4b134f");

	private String colorLight;
	private String colorDark;

	BallColor(String colorLight, String colorDark) {
		this.colorLight = colorLight;
		this.colorDark = colorDark;
	}

	public static BallColor getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}

	public static BallColor getNext(BallColor ballColor) {
		return (ballColor == null || ballColor.ordinal() == values().length - 1) ? values()[0] : values()[ballColor.ordinal() + 1];
	}

	public static BallColor getNext(Ball ball) {
		return (ball == null || ball.getColor().ordinal() == values().length - 1) ? values()[0] : values()[ball.getColor().ordinal() + 1];
	}

	public String getColorLight() {
		return colorLight;
	}

	public String getColorDark() {
		return colorDark;
	}
}

