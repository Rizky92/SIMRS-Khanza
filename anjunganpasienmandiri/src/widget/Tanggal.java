package widget;

import java.awt.Color;
import java.awt.Font;
import uz.ncipro.calendar.JDateTimePicker;

public class Tanggal extends JDateTimePicker {
    public Tanggal() {
        super();
        setFont(new Font("Inter Medium", Font.PLAIN, 18));
        setForeground(new Color(0, 131, 62));
        setBackground(Color.WHITE);
        setDisplayFormat("dd-MM-yyyy");
        setSize(WIDTH, 35);
    }
}
