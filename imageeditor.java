import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
public class imageeditor {
	public static int width = 500;
	public static int height = 600;
	public static BufferedImage mainimage;
	public static BufferedImage transparencyimage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
	public static int imagex = 0;
	public static int imagey = 0;
	public static byte action = 1;
	public static int red = 255;
	public static int green = 255;
	public static int blue = 255;
	public static byte origin = 0;
	public static int movex = 0;
	public static int movey = 0;
	public static void main(String[] args) {
		try{
			if (args.length == 1) {
				mainimage = ImageIO.read(new File("images\\" + args[0] + ".png"));
			} else {
				System.out.println("You need one image to edit.");
				System.exit(0);
			}
		} catch(Exception e) {
			System.out.println("File \"" + args[0] + "\" count not be loaded. Now exiting.");
			System.exit(0);
		}
		JFrame window = new JFrame("Image Editor");
		imagepanel thepanel = new imagepanel();
		window.setContentPane( thepanel );
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width + 16, height + 38);
		window.setLocation((1280 - width - 16) / 2, (1024 - height - 60) / 2);
		window.setVisible(true);
		while (!thepanel.done);
		window.dispose();
		System.out.println("Enter the name of the file you wish to save:");
		String name = "";
		try {
			name = stuff.line();
		} catch(Exception e) {
			System.out.println("Sorry, an error occured, " + e + ". Your file could not be saved.");
			System.exit(0);
		}
		File file = new File("images\\" + name + ".png");
		try {
			ImageIO.write(mainimage, "png", file);
			System.out.println("You successfully saved your image!");
		} catch(Exception e) {
			System.out.println("Sorry, an error occured, " + e + ". Your file could not be saved.");
		}
	}
	public static class imagepanel extends JPanel implements MouseListener, MouseMotionListener {
		public boolean done = false;
		public imagepanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
			setBackground(new Color(255, 191, 0));
		};
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setup(g);
			g.setColor(new Color(red, green, blue));
			g.fillRect(425, 25, 50, 50);
			if (imagex + mainimage.getWidth() > -75 && imagex < 125 && imagey + mainimage.getHeight() > -75 && imagey < 125)
				g.drawImage(mainimage, max(-75, imagex) + 175, max(-75, imagey) + 475, min(125, mainimage.getWidth() + imagex) + 175, min(125, mainimage.getHeight() + imagey) + 475, max(0, -imagex - 75), max(0, -imagey - 75), min(mainimage.getWidth(), 125 - imagex), min(mainimage.getHeight(), 125 - imagey), null);
			for (int y = max(0, imagey); y < min(50, imagey + mainimage.getHeight()); y += 1) {
				for (int x = max(0, imagex); x < min(50, imagex + mainimage.getWidth()); x += 1) {
					g.setColor(new Color(mainimage.getRGB(x - imagex, y - imagey) >> 16 & 255, mainimage.getRGB(x - imagex, y - imagey) >> 8 & 255, mainimage.getRGB(x - imagex, y - imagey) & 255, mainimage.getRGB(x - imagex, y - imagey) >> 24 & 255));
					g.fillRect(x * 8, y * 8, 8, 8);
				}
			}
			g.setColor(new Color(0, 0, 0, 127));
			g.fillRect(100, 400, 200, 75);
			g.fillRect(100, 475, 75, 50);
			g.fillRect(225, 475, 75, 50);
			g.fillRect(100, 525, 200, 75);
			g.setColor(Color.CYAN);
			g.drawRect(408, 101 + (16 - (red + 1) / 16) * 20, 17, 17);
			g.drawRect(407, 100 + (16 - (red + 1) / 16) * 20, 19, 19);
			g.drawRect(440, 101 + (16 - (green + 1) / 16) * 20, 17, 17);
			g.drawRect(439, 100 + (16 - (green + 1) / 16) * 20, 19, 19);
			g.drawRect(472, 101 + (16 - (blue + 1) / 16) * 20, 17, 17);
			g.drawRect(471, 100 + (16 - (blue + 1) / 16) * 20, 19, 19);
			g.drawRect(411, 429 + action * 35, 76, 31);
			g.drawRect(410, 428 + action * 35, 78, 33);
		}
		public void mousePressed(MouseEvent evt) {
			clickaction(evt);
			repaint();
		}
		public void mouseReleased(MouseEvent evt) {
			origin = 0;
		}
		public void mouseDragged(MouseEvent evt) {
			clickaction(evt);
			repaint();
		}
		public void mouseMoved(MouseEvent evt) {}
		public void mouseClicked(MouseEvent evt) {}
		public void mouseEntered(MouseEvent evt) {}
		public void mouseExited(MouseEvent evt) {}
		public void setup(Graphics g) {
			g.setColor(new Color(191, 191, 191));
			g.fillRect(0, 0, 400, 400);
			g.fillRect(100, 400, 200, 200);
			g.setColor(Color.BLACK);
			for (int y = 0; y < 400; y += 8) {
				for (int x = 0; x < 400; x += 8) {
					g.drawLine(x + 1, y + 1, x + 6, y + 6);
					g.drawLine(x + 1, y + 6, x + 6, y + 1);
				}
			}
			g.setColor(new Color(63, 63, 63));
			for (int pos = 1; pos < 198; pos += 2) {
				g.drawLine(100, pos + 400, pos + 100, 400);
				g.drawLine(pos + 101, 599, 299, pos + 401);
			}
			g.drawLine(100, 599, 299, 400);
			int level = 255;
			int shift = 16;
			for (int x = 409; x <= 473; x += 32) {
				for (int y = 102; y <= 402; y += 20) {
					g.setColor(new Color(level << shift));
					g.fillRect(x, y, 16, 16);
					level = level - 16;
				}
				g.setColor(Color.BLACK);
				g.fillRect(x, 422, 16, 16);
				level = 255;
				shift = shift - 8;
			}
			g.drawRect(424, 24, 51, 51);
			for (int count = 1; count < 8; count += 1) {
				g.setColor(new Color(count * 16, count * 16, count * 16));
				g.drawRect(424 - count, 24 - count, 51 + 2 * count, 51 + 2 * count);
			}
			g.setColor(Color.BLUE);
			g.fillRect(412, 465, 75, 30);
			g.fillRect(412, 500, 75, 30);
			g.fillRect(412, 535, 75, 30);
			g.setColor(new Color(127, 0, 0));
			g.fillRect(400, 570, 100, 30);
			g.setColor(Color.GREEN);
			g.drawRect(413, 466, 72, 27);
			g.drawRect(413, 501, 72, 27);
			g.drawRect(413, 536, 72, 27);
			g.drawRect(401, 571, 97, 27);
			g.setColor(new Color(191, 191, 191));
			g.setFont(new Font("Monospaced", Font.BOLD, 24));
			g.drawString("Draw", 422, 487);
			g.drawString("Erase", 415, 522);
			g.drawString("Save", 422, 592);
			g.setFont(new Font("Monospaced", Font.BOLD, 12));
			g.drawString("Absorb", 428, 553);
		}
		public void clickaction(MouseEvent evt) {
			int mousex = evt.getX();
			int mousey = evt.getY();
			int posx;
			int posy;
			if (mousex >= 0 && mousex < 400 && mousey >= 0 && mousey < 400 && (origin == 0 || origin == 1)) {
				origin = 1;
				posx = mousex / 8;
				posy = mousey / 8;
				if (action == 1) {
					if (posx < imagex || posx >= mainimage.getWidth() + imagex || posy < imagey || posy >= mainimage.getHeight() + imagey) {
						BufferedImage tempimage = new BufferedImage(max(posx - imagex + 1, mainimage.getWidth()) - min(posx - imagex, 0), max(posy - imagey + 1, mainimage.getHeight()) - min(posy - imagey, 0), BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = tempimage.createGraphics();
						g.drawImage(mainimage, max(0, imagex - posx), max(0, imagey - posy), max(0, imagex - posx) + mainimage.getWidth(), max(0, imagey - posy) + mainimage.getHeight(), 0, 0, mainimage.getWidth(), mainimage.getHeight(), null);
						mainimage = tempimage;
						g.dispose();
						imagex = min(imagex, posx);
						imagey = min(imagey, posy);
					}
					mainimage.setRGB(posx - imagex, posy - imagey, (255 << 24) | (red << 16) | (green << 8) | blue);
				} else if (action == 2) {
					if (posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey)
						mainimage.setRGB(posx - imagex, posy - imagey, 0);
					int left = 0;
					int right = mainimage.getWidth() - 1;
					int up = 0;
					int down = mainimage.getHeight() - 1;
					boolean leave = false;
					for (int y = 0; y < mainimage.getHeight(); y += 1) {
						for (int x = 0; x < mainimage.getWidth(); x += 1) {
							if (mainimage.getRGB(x, y) != 0) {
								up = y;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					leave = false;
					for (int y = mainimage.getHeight() - 1; y >= 0; y -= 1) {
						for (int x = mainimage.getWidth() - 1; x >= 0; x -= 1) {
							if (mainimage.getRGB(x, y) != 0) {
								down = y;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					leave = false;
					for (int x = 0; x < mainimage.getWidth(); x += 1) {
						for (int y = 0; y < mainimage.getHeight(); y += 1) {
							if (mainimage.getRGB(x, y) != 0) {
								left = x;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					leave = false;
					for (int x = mainimage.getWidth() - 1; x >= 0; x -= 1) {
						for (int y = mainimage.getHeight() - 1; y >= 0; y -= 1) {
							if (mainimage.getRGB(x, y) != 0) {
								right = x;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					if (left != 0 || right != mainimage.getWidth() - 1 || up != 0 || down != mainimage.getHeight() - 1) {
						BufferedImage tempimage = new BufferedImage(right - left + 1, down - up + 1, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = tempimage.createGraphics();
						g.drawImage(mainimage, 0, 0, tempimage.getWidth(), tempimage.getHeight(), left, up, right + 1, down + 1, null);
						mainimage = tempimage;
						g.dispose();
						imagex = imagex + left;
						imagey = imagey + up;
					}
				} else if (action == 3 && posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey) {
					if (mainimage.getRGB(posx - imagex, posy - imagey) < 0) {
						red = (mainimage.getRGB(posx - imagex, posy - imagey) >> 16) & 255;
						green = (mainimage.getRGB(posx - imagex, posy - imagey) >> 8) & 255;
						blue = mainimage.getRGB(posx - imagex, posy - imagey) & 255;
					}
				}
			} else if (mousex >= 412 && mousex < 487 && mousey >= 465 && mousey <= 495 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 1;
			} else if (mousex >= 412 && mousex < 487 && mousey >= 500 && mousey <= 530 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 2;
			} else if (mousex >= 412 && mousex < 487 && mousey >= 535 && mousey <= 565 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 3;
			} else if (mousex >= 409 && mousex < 489 && (mousex - 409) % 32 >= 0 && (mousex - 409) % 32 < 16 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 >= 0 && (mousey - 102) % 20 < 16 && (origin == 0 || origin == 3)) {
				origin = 3;
				posx = (mousex - 409) / 32;
				posy = (mousey - 102) / 20;
				if (posx == 0) {
					if (posy < 16) {
						red = 255 - 16 * posy;
					} else {
						red = 0;
					}
				} else if (posx == 1) {
					if (posy < 16) {
						green = 255 - 16 * posy;
					} else {
						green = 0;
					}
				} else if (posx == 2) {
					if (posy < 16) {
						blue = 255 - 16 * posy;
					} else {
						blue = 0;
					}
				}
			} else if (mousex >= 400 && mousex < 500 && mousey >= 570 && mousey < 600)
				done = true;
			else if (mousex >= 100 && mousex < 300 && mousey >= 400 && mousey < 600 && (origin == 0 || origin == 4)) {
				if (origin == 4) {
					imagex = movex + mousex;
					imagey = movey + mousey;
				} else if (origin == 0) {
					movex = imagex - mousex;
					movey = imagey - mousey;
				}
				origin = 4;
			}
		}
	}
	public static int max(int num1, int num2) {
		if (num1 >= num2)
			return num1;
		return num2;
	}
	public static int min(int num1, int num2) {
		if (num1 <= num2)
			return num1;
		return num2;
	}
}