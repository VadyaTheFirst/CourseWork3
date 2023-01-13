package my.coursework3.characteristic;

public enum SocksSize {
    S (31, 33),
    M (34, 36),
    L (37, 39),
    XL (40, 42),
    XXL(43, 45);

    private final int foot_size_min = 31;
    private final int foot_size_max = 45;

    SocksSize(int foot_size_min, int foot_size_max) {
    }

    public int getFoot_size_min() {
        return foot_size_min;
    }

    public int getFoot_size_max() {
        return foot_size_max;
    }
}
