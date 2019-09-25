package game.core;

public enum BallColor {
	CITRUS_PEEL         ("#FDC830", "#F37335"),
	SIN_CITY_RED        ("#ED213A", "#93291E"),
	BLUE_RASPBERRY      ("#56CCF2", "#2F80ED"),
	NEUROMANCER         ("#F953C6", "#B91D73"),
	CLEAR_SKY           ("#005C97", "#363795"),
	LUSH                ("#A8E063", "#56AB2f"),
	VIOLET_INFLUENZA    ("#5A175E", "#380038"),
	CARAMEL             ("#825421", "#69140E");

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

