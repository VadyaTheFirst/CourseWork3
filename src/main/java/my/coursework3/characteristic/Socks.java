package my.coursework3.characteristic;

import lombok.Data;

@Data

public class Socks {
    private int cottonPart;
    private SocksColor socksColor;
    private SocksSize socksSize;

    public Socks(int cottonPart, SocksColor socksColor, SocksSize socksSize) {
        if (cottonPart > 0 && cottonPart <= 100) {
            this.cottonPart = cottonPart;
        } else {
            throw new IllegalArgumentException("процентное содержание хлопка в составе носков " + cottonPart + "%");
        }
        this.socksColor = socksColor;
        this.socksSize = socksSize;
    }
}
