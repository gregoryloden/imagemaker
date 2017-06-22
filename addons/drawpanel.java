import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
public class drawpanel {
	public imagemaker im;
	public int[] numbers = new int[4];
	public int box = 0;
	public int command = 0;
	public byte success = 0;
	public byte origin = 0;
	public String name() {
		return "Draw Panel";
	}
	public drawpanel(imagemaker main) {
		im = main;
	}
	public void paint(Graphics g) {
//numpad
		int innerheight = im.innerheight();
		g.setColor(new Color(128, 128, 128));
		for (int y = 0; y <= 96; y += 32) {
			for (int x = 0; x <= 64; x += 32) {
				g.fillRect(304 + x, 4 + innerheight + y, 28, 28);
			}
		}
		g.setColor(Color.WHITE);
		for (int y = 0; y <= 96; y += 32) {
			for (int x = 0; x <= 64; x += 32) {
				g.drawRect(305 + x, 5 + innerheight + y, 25, 25);
			}
		}
		g.setFont(new Font("Monospaced", Font.BOLD, 24));
		for (int y = 0; y <= 2; y += 1) {
			for (int x = 0; x <= 2; x += 1) {
				g.drawString((x + 3 * y + 1) + "", 311 + 32 * x, 25 + innerheight + 32 * y);
			}
		}
		g.drawString("<", 311, 121 + innerheight);
		g.drawString("0", 343, 121 + innerheight);
		g.drawString("±", 375, 121 + innerheight);
//text boxes
		int innerwidth = im.innerwidth();
		for (int y = 0; y < 96; y += 24) {
			g.setColor(Color.WHITE);
			g.fillRect(innerwidth, 470 + y, 100, 19);
			g.setColor(Color.BLACK);
			g.drawRect(innerwidth, 470 + y, 99, 18);
		}
		g.setColor(Color.WHITE);
		g.fillRect(35 + innerwidth, 565, 30, 30);
		g.setColor(Color.BLACK);
		g.drawRect(35 + innerwidth, 565, 29, 29);
		g.setColor(new Color(64, 64, 64));
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		if (command == 0) {
			g.drawString("X1:" + numbers[0], 5 + innerwidth, 484);
			g.drawString("Y1:" + numbers[1], 5 + innerwidth, 508);
			g.drawString("X2:" + numbers[2], 5 + innerwidth, 532);
			g.drawString("Y2:" + numbers[3], 5 + innerwidth, 556);
		} else if (command == 1 || command == 2) {
			g.drawString("X :" + numbers[0], 5 + innerwidth, 484);
			g.drawString("Y :" + numbers[1], 5 + innerwidth, 508);
			g.drawString("W :" + numbers[2], 5 + innerwidth, 532);
			g.drawString("H :" + numbers[3], 5 + innerwidth, 556);
		} else if (command == 3 || command == 4) {
			g.drawString("CL:" + numbers[0], 5 + innerwidth, 484);
			g.drawString("CT:" + numbers[1], 5 + innerwidth, 508);
			g.drawString("CW:" + numbers[2], 5 + innerwidth, 532);
			g.drawString("CH:" + numbers[3], 5 + innerwidth, 556);
		} else if (command == 5) {
			g.drawString("PX:" + numbers[0], 5 + innerwidth, 484);
			g.drawString("PY:" + numbers[1], 5 + innerwidth, 508);
			g.drawString(" - " + numbers[2], 5 + innerwidth, 532);
			g.drawString(" - " + numbers[3], 5 + innerwidth, 556);
		} else if (command == 6) {
			g.drawString("R :" + numbers[0], 5 + innerwidth, 484);
			g.drawString("G :" + numbers[1], 5 + innerwidth, 508);
			g.drawString("B :" + numbers[2], 5 + innerwidth, 532);
			g.drawString("A :" + numbers[3], 5 + innerwidth, 556);
		} else if (command == 7) {
			g.drawString("IX:" + numbers[0], 5 + innerwidth, 484);
			g.drawString("IY:" + numbers[1], 5 + innerwidth, 508);
			g.drawString(" - " + numbers[2], 5 + innerwidth, 532);
			g.drawString(" - " + numbers[3], 5 + innerwidth, 556);
		}
		g.setColor(Color.RED);
		g.drawRect(innerwidth, 470 + box * 24, 99, 18);
		g.drawRect(1 + innerwidth, 471 + box * 24, 97, 16);
//command boxes
		g.setColor(Color.BLUE);
		for (int y = 0; y < 280; y += 35) {
			g.fillRect(5 + innerwidth, 105 + y, 90, 30);
		}
		g.fillRect(325, 140 + innerheight, 50, 30);
		g.setColor(Color.GREEN);
		for (int y = 0; y < 280; y += 35) {
			g.drawRect(6 + innerwidth, 106 + y, 87, 27);
		}
		g.drawRect(326, 141 + innerheight, 47, 27);
		g.setFont(new Font("Monospaced", Font.BOLD, 16));
		g.setColor(new Color(192, 192, 192));
		g.drawString("drawLine", 10 + innerwidth, 125);
		g.drawString("drawRect", 10 + innerwidth, 160);
		g.drawString("fillRect", 10 + innerwidth, 195);
		g.drawString("setColor", 10 + innerwidth, 335);
		g.setFont(new Font("Monospaced", Font.BOLD, 24));
		g.drawString("Copy", 22 + innerwidth, 232);
		g.drawString("Cut", 29 + innerwidth, 267);
		g.drawString("Paste", 15 + innerwidth, 302);
		g.drawString("Move", 22 + innerwidth, 372);
		g.drawString("Go!", 331, 162 + innerheight);
		g.setColor(Color.CYAN);
		g.drawRect(4 + innerwidth, 104 + command * 35, 91, 31);
		g.drawRect(3 + innerwidth, 103 + command * 35, 93, 33);
//color display square
		for (int count = 0; count < 9; count += 1) {
			g.setColor(new Color(count * 16, count * 16, count * 16));
			g.drawRect(25 + innerwidth - count, 25 - count, 49 + 2 * count, 49 + 2 * count);
		}
		g.setColor(new Color(192, 192, 192));
		g.fillRect(26 + innerwidth, 26, 48, 48);
		g.setColor(Color.BLACK);
		g.fillRect(32 + innerwidth, 32, 6, 6);
		g.fillRect(62 + innerwidth, 32, 6, 6);
		g.fillRect(38 + innerwidth, 38, 6, 6);
		g.fillRect(56 + innerwidth, 38, 6, 6);
		g.fillRect(44 + innerwidth, 44, 12, 12);
		g.fillRect(38 + innerwidth, 56, 6, 6);
		g.fillRect(56 + innerwidth, 56, 6, 6);
		g.fillRect(32 + innerwidth, 62, 6, 6);
		g.fillRect(62 + innerwidth, 62, 6, 6);
		g.setColor(new Color(im.red, im.green, im.blue, im.alpha));
		g.fillRect(26 + innerwidth, 26, 48, 48);
//success
		if (success == 1) {
			g.setColor(new Color(0, 192, 0));
			g.drawString("\u2713", 41 + innerwidth, 588);
		} else if (success == -1) {
			g.setColor(new Color(192, 0, 0));
			g.drawString("\u2715", 39 + innerwidth, 588);
		}
	}
	public void click(MouseEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		if (origin == 0)
			success = 0;
		int innerwidth = im.innerwidth();
		int innerheight = im.innerheight();
		if (mousex >= 304 && mousex < 396 && (mousex - 304) % 32 < 28 && mousey >= 4 + innerheight && mousey < 96 + innerheight && (mousey - 4 - innerheight) % 32 < 28 && origin == 0) {
			add((mousex - 304) / 32 + (mousey - 4 - innerheight) / 32 * 3 + 1);
			origin = -1;
		} else if (mousex >= 336 && mousex < 364 && mousey >= 100 + innerheight && mousey < 128 + innerheight && origin == 0) {
			add(0);
			origin = -1;
		} else if (mousex >= 304 && mousex < 332 && mousey >= 100 + innerheight && mousey < 128 + innerheight && origin == 0) {
			numbers[box] = numbers[box] / 10;
			origin = -1;
		} else if (mousex >= 368 && mousex < 396 && mousey >= 100 + innerheight && mousey < 128 + innerheight && origin == 0) {
			numbers[box] = -numbers[box];
			origin = -1;
		} else if (mousex >= innerwidth && mousex < 100 + innerwidth && mousey >= 470 && mousey < 561 && (mousey - 470) % 24 < 19 && (origin == 0 || origin == 1)) {
			origin = 1;
			box = (mousey - 470) / 24;
		} else if (mousex >= 5 + innerwidth && mousex < 95 + innerwidth && mousey >= 105 && mousey < 380 && (mousey - 105) % 35 < 30 && (origin == 0 || origin == 2)) {
			origin = 2;
			command = (mousey - 105) / 35;
		} else if (mousex >= 325 && mousex < 375 && mousey >= 140 + innerheight && mousey < 170 + innerheight && origin == 0) {
			origin = -1;
			success = -1;
			BufferedImage mainimage = im.mainimage;
			if (command >= 0 && command <= 2) {
				int x1 = numbers[0];
				int y1 = numbers[1];
				int x2 = numbers[2];
				int y2 = numbers[3];
				if (command != 0) {
					if (x2 < 0 || y2 < 0)
						return;
					x2 += x1;
					y2 += y1;
				}
				if (mainimage == null) {
					int placex = Math.min(x1, x2);
					int placey = Math.min(y1, y2);
					im.mainimage = new BufferedImage(Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1, BufferedImage.TYPE_INT_ARGB);
					x1 -= placex;
					y1 -= placey;
					x2 -= placex;
					y2 -= placey;
					mainimage = im.mainimage;
				} else if (Math.min(x1, x2) < 0 || Math.min(y1, y2) < 0 || Math.max(x1, x2) >= mainimage.getWidth() || Math.max(y1, y2) >= mainimage.getHeight()) {
					int placex = Math.max(0, Math.max(-x1, -x2));
					int placey = Math.max(0, Math.max(-y1, -y2));
					int newwidth = Math.max(mainimage.getWidth(), Math.max(x1, x2) + 1) + placex;
					int newheight = Math.max(mainimage.getHeight(), Math.max(y1, y2) + 1) + placey;
					BufferedImage tempimage = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_ARGB);
					tempimage.setData(im.mainimage.getRaster().createTranslatedChild(placex, placey));
					im.mainimage = tempimage;
					x1 += placex;
					y1 += placey;
					x2 += placex;
					y2 += placey;
					im.imagex -= placex;
					im.imagey -= placey;
					mainimage = im.mainimage;
				}
				Graphics g = mainimage.createGraphics();
				g.setColor(new Color(im.red, im.green, im.blue, im.alpha));
				if (command == 1)
					g.drawRect(x1, y1, x2 - x1, y2 - y1);
				else if (command == 2)
					g.fillRect(x1, y1, x2 - x1 - 1, y2 - y1 - 1);
				else
					g.drawLine(x1, y1, x2, y2);
				g.dispose();
			} else if (command == 3 || command == 4) {
				if (mainimage == null)
					return;
				int cl = numbers[0];
				int ct = numbers[1];
				int cw = numbers[2];
				int ch = numbers[3];
				if (cw < 1 || ch < 1 || cl < 0 || ct < 0 || cl + cw > mainimage.getWidth() || ct + ch > mainimage.getHeight())
					return;
				im.copyimage = imagemaker.imagecopy(mainimage.getSubimage(cl, ct, cw, ch));
				if (command == 4)
					mainimage.setRGB(cl, ct, cw, ch, new int[cw * ch], 0, cw);
			} else if (command == 5) {
				if (im.copyimage == null)
					return;
				int cl = numbers[0];
				int ct = numbers[1];
				int cw = im.copyimage.getWidth();
				int ch = im.copyimage.getHeight();
				int cr = cl + cw;
				int cb = ct + ch;
				if (mainimage != null) {
					if (cl < 0 || cr > mainimage.getWidth() || ct < 0 || cb > mainimage.getHeight()) {
						int mwidth = mainimage.getWidth();
						int mheight = mainimage.getHeight();
						int negleft = Math.max(0, -cl);
						int negtop = Math.max(0, -ct);
						BufferedImage tempimage = new BufferedImage(Math.max(cr, mwidth) - Math.min(cl, 0), Math.max(cb, mheight) - Math.min(ct, 0), BufferedImage.TYPE_INT_ARGB);
						tempimage.setData(mainimage.getRaster().createTranslatedChild(negleft, negtop));
						imagemaker.pasteon(im.copyimage, Math.max(0, cl), Math.max(0, ct), tempimage);
						im.mainimage = tempimage;
						im.imagex = im.imagex - negleft;
						im.imagey = im.imagey - negtop;
					} else
						imagemaker.pasteon(im.copyimage, cl, ct, mainimage);
				} else {
					im.mainimage = imagemaker.imagecopy(im.copyimage);
					im.imagex = cl;
					im.imagey = ct;
				}
			} else if (command == 6) {
				im.red = numbers[0] & 255;
				im.green = numbers[1] & 255;
				im.blue = numbers[2] & 255;
				im.alpha = numbers[3] & 255;
			} else if (command == 7) {
				if (mainimage == null)
					return;
				im.imagex = numbers[0];
				im.imagey = numbers[1];
			}
			success = 1;
		}
	}
	public void release(MouseEvent evt) {
		origin = 0;
	}
	public void scroll(MouseWheelEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int innerwidth = im.innerwidth();
		success = 0;
		if (mousex >= innerwidth && mousex < 100 + innerwidth && mousey >= 470 && mousey < 561 && (mousey - 470) % 24 < 19) {
			box = (mousey - 470) / 24;
			int num = numbers[box] - evt.getWheelRotation();
			if (num <= 999999999 && num >= -999999999)
				numbers[box] = num;
		}
	}
	public void add(int digit) {
		if (numbers[box] >= 0 && numbers[box] < 100000000)
			numbers[box] = numbers[box] * 10 + digit;
		else if (numbers[box] < 0 && numbers[box] > -100000000)
			numbers[box] = numbers[box] * 10 - digit;
	}
}