package my.coursework3.characteristic;

public enum SocksColor {
    BLACK ("черный"),
    RED ("красный"),
    BLUE ("синий"),
    WHITE ("белый"),
    YELLOW ("желтый");

    private final String color;

    SocksColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }


}
