package fungsi;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import javax.swing.ImageIcon;

public class Painter extends Canvas {
    Image image;

    public void setImage(String file) {
        URL url = null;
        try {
            url = new File(file).toURI().toURL();
        } catch (MalformedURLException ex) {
            System.out.println("Notif : " + ex);
        }
        image = getToolkit().getImage(url);
        repaint();
    }

    public void setImage(Blob img) {
        try {
            if (img != null) {
                image = getToolkit().createImage(img.getBytes(1, (int) img.length()));
            }
        } catch (SQLException e) {
            System.out.println("Notif : " + e);
        }
        repaint();
    }

    public void setImageIcon(ImageIcon file) {
        image = file.getImage();
        repaint();
    }

    public void removeImage() {
        if (image != null) {
            image = null;
        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        try {
            double d = image.getHeight(this) / this.getHeight();
            double w = image.getWidth(this) / d;
            double x = this.getWidth() / 2 - w / 2;
            g.drawImage(image, (int) x, 0, (int) (w), this.getHeight(), this);
        } catch (Exception e) {
        }
    }
}
