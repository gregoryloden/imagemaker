import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
public class colorpanel {
	public imagemaker im;
	public byte origin = 0;
	public boolean clicked = false;
	public boolean inbounds = false;
	public int initialx = 0;
	public int initialy = 0;
	public int finalx = 0;
	public int finaly = 0;
	public int copytop = 0;
	public int copyleft = 0;
	public int copyright = 0;
	public int copybottom = 0;
	//settings:
	//	alpha is x = 3, red is x = 2, green is x = 1, blue is x = 0
	//	if settings[x][0] < 4
	//		if settings[x][0] == 0 use old blue value as new value
	//		if settings[x][0] == 1 use old green value as new value
	//		if settings[x][0] == 2 use old red value as new value
	//		if settings[x][0] == 3 use old alpha value as new value
	//	else if settings[x][0] == 4
	//		use settings[x][1] as new value
	//	else if settings[x][0] == 5
	//		add [x][1] to old value
	//	else if settings[x][0] == 6
	//		subtract [x][1] from old value
	//	else if settings[x][0] == 7
	//		add settings[x][0], 0, or -settings[x][0] to old value
	public int[][] settings = new int[][] {{0, 0}, {1, 0}, {2, 0}, {3, 0}};
	public int color = 0;
	public int value = 0;
	public int subtractor = 255;
	public int area = 0;
	public Font bigfont = new Font("Monospaced", Font.BOLD, 24);
	public Font medfont = new Font("Monospaced", Font.BOLD, 16);
	public String name() {
		return "Color Panel";
	}
	public colorpanel(imagemaker main) {
		im = main;
	}
	public void paint(Graphics g) {
		int innerwidth = im.innerwidth();
//output boxes
		g.setColor(Color.WHITE);
		g.fillRect(10 + innerwidth, 10, 80, 30);
		g.fillRect(10 + innerwidth, 45, 80, 30);
		g.fillRect(10 + innerwidth, 80, 80, 30);
		g.fillRect(10 + innerwidth, 115, 80, 30);
		g.setColor(Color.BLACK);
		g.drawRect(10 + innerwidth, 10, 79, 29);
		g.drawRect(10 + innerwidth, 45, 79, 29);
		g.drawRect(10 + innerwidth, 80, 79, 29);
		g.drawRect(10 + innerwidth, 115, 79, 29);
//output text
		char[] letters = new char[] {'B', 'G', 'R', 'A'};
		String colorstring = "";
		for (int spot = 0; spot < letters.length; spot += 1) {
			colorstring = letters[spot] + ":";
			if (settings[spot][0] < 4) {
				if (settings[spot][1] == 0)
					colorstring = colorstring + "+" + letters[settings[spot][0]];
				else
					colorstring = colorstring + "-" + letters[settings[spot][0]];
			} else
				colorstring = colorstring + settings[spot][1];
			if (colorstring.length() <= 5)
				g.setFont(bigfont);
			else
				g.setFont(medfont);
			g.drawString(colorstring, 15 + innerwidth, 32 + spot * 35);
		}
		g.setColor(Color.RED);
		g.drawRect(10 + innerwidth, 10 + 35 * color, 79, 29);
		g.drawRect(11 + innerwidth, 11 + 35 * color, 77, 27);
//color input boxes
		g.setColor(Color.BLUE);
		g.fillRect(10 + innerwidth, 150, 40, 40);
		g.setColor(Color.YELLOW);
		g.fillRect(50 + innerwidth, 150, 40, 40);
		g.setColor(Color.GREEN);
		g.fillRect(10 + innerwidth, 195, 40, 40);
		g.setColor(Color.MAGENTA);
		g.fillRect(50 + innerwidth, 195, 40, 40);
		g.setColor(Color.RED);
		g.fillRect(10 + innerwidth, 240, 40, 40);
		g.setColor(Color.CYAN);
		g.fillRect(50 + innerwidth, 240, 40, 40);
		g.setColor(Color.WHITE);
		g.fillRect(10 + innerwidth, 285, 40, 40);
		g.setColor(Color.BLACK);
		g.fillRect(50 + innerwidth, 285, 40, 40);
		g.drawRect(11 + innerwidth, 151, 37, 37);
		g.drawRect(51 + innerwidth, 151, 37, 37);
		g.drawRect(11 + innerwidth, 196, 37, 37);
		g.drawRect(51 + innerwidth, 196, 37, 37);
		g.drawRect(11 + innerwidth, 241, 37, 37);
		g.drawRect(51 + innerwidth, 241, 37, 37);
		g.drawRect(11 + innerwidth, 286, 37, 37);
		g.setFont(bigfont);
		g.drawString("+B", 15 + innerwidth, 177);
		g.drawString("-B", 55 + innerwidth, 177);
		g.drawString("+G", 15 + innerwidth, 222);
		g.drawString("-G", 55 + innerwidth, 222);
		g.drawString("+R", 15 + innerwidth, 267);
		g.drawString("-R", 55 + innerwidth, 267);
		g.drawString("+A", 15 + innerwidth, 312);
		g.setColor(Color.WHITE);
		g.drawRect(51 + innerwidth, 286, 37, 37);
		g.drawString("-A", 55 + innerwidth, 312);
//value input box
		g.setColor(Color.WHITE);
		g.fillRect(20 + innerwidth, 330, 60, 30);
		g.setColor(Color.BLACK);
		g.fillRect(10 + innerwidth, 330, 10, 30);
		g.drawRect(20 + innerwidth, 330, 59, 29);
		g.fillRect(80 + innerwidth, 330, 10, 30);
		g.drawString("" + value, 25 + innerwidth, 352);
		g.setColor(Color.WHITE);
		g.drawRect(10 + innerwidth, 330, 9, 29);
		g.drawRect(80 + innerwidth, 330, 9, 29);
		g.fillRect(14 + innerwidth, 335, 2, 20);
		g.fillRect(84 + innerwidth, 335, 2, 20);
		g.fillRect(86 + innerwidth, 336, 1, 3);
		g.fillRect(87 + innerwidth, 337, 1, 2);
		g.fillRect(83 + innerwidth, 336, 1, 3);
		g.fillRect(82 + innerwidth, 337, 1, 2);
		g.fillRect(16 + innerwidth, 351, 1, 3);
		g.fillRect(17 + innerwidth, 351, 1, 2);
		g.fillRect(13 + innerwidth, 351, 1, 3);
		g.fillRect(12 + innerwidth, 351, 1, 2);
		g.fillRect(20 + innerwidth, 370, 60, 30);
		g.fillRect(10 + innerwidth, 405, 80, 30);
		g.setColor(Color.BLACK);
		g.drawString("" + subtractor, 25 + innerwidth, 392);
		if (area == 0)
			g.drawString("Area", 15 + innerwidth, 427);
		else if (area == 1)
			g.drawString("Fill", 15 + innerwidth, 427);
		else if (area == 2) {
			g.setFont(medfont);
			g.drawString("Absorb", 15 + innerwidth, 427);
		}
//area selection rect
		if (clicked && inbounds) {
			int size = im.getsize();
			g.setColor(new Color(255, 0, 0, 128));
			g.fillRect((copyleft + im.imagex) * size + 1, (copytop + im.imagey) * size + 1, (copyright - copyleft) * size - 2, (copybottom - copytop) * size - 2);
			g.setColor(new Color(0, 255, 0, 128));
			g.drawRect((copyleft + im.imagex) * size, (copytop + im.imagey) * size, (copyright - copyleft) * size - 1, (copybottom - copytop) * size - 1);
		}
	}
	public void click(MouseEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int innerwidth = im.innerwidth();
		int innerheight = im.innerheight();
		int size = im.getsize();
		BufferedImage mainimage = im.mainimage;
		if (mousex >= 0 && mousex < innerwidth - innerwidth % size && mousey >= 0 && mousey < innerheight - innerheight % size && (origin == 0 || origin == 1) && mainimage != null) {
			origin = 1;
			int posx = mousex / size;
			int posy = mousey / size;
			int imagex = im.imagex;
			int imagey = im.imagey;
			int mwidth = mainimage.getWidth();
			int mheight = mainimage.getHeight();
			if (area == 0) {
				if (!clicked) {
					clicked = true;
					initialy = posy;
					initialx = posx;
				}
				finaly = posy;
				finalx = posx;
				copyleft = Math.min(Math.max(Math.min(initialx, finalx) - imagex, 0), mwidth - 1);
				copyright = Math.max(Math.min(Math.max(initialx, finalx) + 1 - imagex, mwidth), 1);
				copytop = Math.min(Math.max(Math.min(initialy, finaly) - imagey, 0), mheight - 1);
				copybottom = Math.max(Math.min(Math.max(initialy, finaly) + 1 - imagey, mheight), 1);
				inbounds = true;
			} else if (area == 1) {
				posx = posx - im.imagex;
				posy = posy - im.imagey;
				if (posx >= 0 && posy >= 0 && posx < mwidth && posy < mheight) {
					int color = mainimage.getRGB(posx, posy);
					if (color != pixelcolor(color))
						paintall(color, pixelcolor(color), new XYLink(posx, posy));
				}
			} else if (area == 2) {
				posx -= imagex;
				posy -= imagey;
				if (posx >= 0 && posy >= 0 && posx < mwidth && posy < mheight) {
					int color = mainimage.getRGB(posx, posy);
					settings[0][0] = 4;
					settings[0][1] = color & 255;
					settings[1][0] = 4;
					settings[1][1] = color >> 8 & 255;
					settings[2][0] = 4;
					settings[2][1] = color >> 16 & 255;
					settings[3][0] = 4;
					settings[3][1] = color >> 24 & 255;
				}
			}
		} else if (mousex >= 10 + innerwidth && mousex < 90 + innerwidth && mousey >= 10 && mousey < 145 && (mousey - 10) % 35 < 30 && (origin == 0 || origin == 2)) {
			origin = 2;
			color = (mousey - 10) / 35;
		} else if (mousex >= 10 + innerwidth && mousex < 90 + innerwidth && mousey >= 150 && mousey < 325 && (mousey - 150) % 45 < 40 && (origin == 0 || origin == 3)) {
			origin = 3;
			settings[color][0] = (mousey - 150) / 45;
			settings[color][1] = (mousex - 10 - innerwidth) / 40;
		} else if (mousex >= 10 + innerwidth && mousex < 90 + innerwidth && (mousex - 10 - innerwidth) % 70 < 10 && mousey >= 330 && mousey < 360 && (origin == 0 || origin == 4)) {
			origin = 4;
			add(1 - ((mousex - 10 - innerwidth) / 70) * 2);
		} else if (mousex >= 20 + innerwidth && mousex < 80 + innerwidth && mousey >= 330 && mousey < 360 && (origin == 0 || origin == 3)) {
			origin = 3;
			settings[color][0] = 4;
			settings[color][1] = value;
		} else if (mousex >= 20 + innerwidth && mousex < 80 + innerwidth && mousey >= 370 && mousey < 400 && (origin == 0)) {
			origin = 5;
			subtractor = 511 - subtractor;
		} else if (mousex >= 10 + innerwidth && mousex < 90 + innerwidth && mousey >= 405 && mousey < 435 && (origin == 0)) {
			origin = 6;
			area = (area + 1) % 3;
		} else
			inbounds = false;
	}
	public void release(MouseEvent evt) {
		origin = 0;
		if (clicked && inbounds) {
			BufferedImage mainimage = im.mainimage;
			for (int y = copytop; y < copybottom; y += 1) {
				for (int x = copyleft; x < copyright; x += 1) {
					mainimage.setRGB(x, y, pixelcolor(mainimage.getRGB(x, y)));
				}
			}
		}
		clicked = false;
	}
	public void scroll(MouseWheelEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int innerwidth = im.innerwidth();
		if (mousex >= 10 + innerwidth && mousex < 90 + innerwidth && mousey >= 330 && mousey < 360)
			add(evt.getWheelRotation());
	}
	public void add(int add) {
		value = Math.max(0, Math.min(255, value - add));
	}
	public int pixelcolor(int pixel) {
		int color = 0;
		int bit;
		for (int spot = 0; spot < settings.length; spot += 1) {
			if (settings[spot][0] < 4) {
				bit = pixel >> settings[spot][0] * 8 & 255;
				if (settings[spot][1] == 1) {
					if (bit == 0 || bit == 255)
						bit = 255 - bit;
					else
						bit = subtractor - bit;
				}
			} else
				bit = settings[spot][1];
			color |= bit << spot * 8;
		}
		return color;
	}
	public void paintall(int c, int c2, XYLink head) {
		int x;
		int y;
		BufferedImage mainimage = im.mainimage;
		int mwidth = mainimage.getWidth();
		int mheight = mainimage.getHeight();
		for (XYLink tail = head; head != null; head = head.next) {
			x = head.x;
			y = head.y;
			if (mainimage.getRGB(x, y) == c) {
				mainimage.setRGB(x, y, c2);
				if (x - 1 >= 0)
					tail = new XYLink(x - 1, y, tail);
				if (x + 1 < mwidth)
					tail = new XYLink(x + 1, y, tail);
				if (y - 1 >= 0)
					tail = new XYLink(x, y - 1, tail);
				if (y + 1 < mheight)
					tail = new XYLink(x, y + 1, tail);
			}
		}
	}
	public static class XYLink {
		int x;
		int y;
		XYLink next = null;
		public XYLink(int thex, int they) {
			x = thex;
			y = they;
		}
		public XYLink(int thex, int they, XYLink last) {
			x = thex;
			y = they;
			last.next = this;
		}
	}
}