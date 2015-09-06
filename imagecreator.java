import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
public class imagecreator {
	public static int width = 900;
	public static int height = 800;
	public static int[][] colors = new int[100][100];
	public static BufferedImage screenimage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage tinyimage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage saveimage;
	public static int left = -1;
	public static int right = -1;
	public static int up = -1;
	public static int down = -1;
	public static byte action = 1;
	public static int red = 255;
	public static int green = 255;
	public static int blue = 255;
	public static byte origin = 0;
	public static byte imageloaded = 0;
	public static BufferedImage loadimage;
	public static int loadimagex = 0;
	public static int loadimagey = 0;
	public static void main(String[] args) {
		try{
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("load")) {
					imageloaded = 1;
					loadimage = ImageIO.read(new File("images\\" + args[1] + ".png"));
				} else if (args[0].equalsIgnoreCase("back")) {
					imageloaded = 2;
					loadimage = ImageIO.read(new File("images\\" + args[1] + ".png"));
				}
			}
		} catch(Exception e) {
			System.out.println("File \"" + args[1] + "\" count not be loaded. Program will continue without image.");
		}
		JFrame window = new JFrame("Image Creator");
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
		saveimage = new BufferedImage(right - left + 1, down - up + 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = saveimage.createGraphics();
		g.drawImage(tinyimage, 0, 0, right - left + 1, down - up + 1, left, up, right + 1, down + 1, null);
		try {
			ImageIO.write(saveimage, "png", file);
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
			if (imageloaded > 0 || imageloaded == -2) {
				for (int y = 0; y < loadimage.getHeight(); y += 1) {
					for (int x = 0; x < loadimage.getWidth(); x += 1) {
						if (loadimage.getRGB(x, y) < 0) {
							g.setColor(new Color(loadimage.getRGB(x, y)));
							g.fillRect((loadimagex + x) * 8, (loadimagey + y) * 8, 8, 8);
							if (Math.abs(imageloaded) == 2) {
								g.setColor(Color.BLACK);
								g.drawLine((loadimagex + x) * 8 + 1, (loadimagey + y) * 8 + 1, (loadimagex + x) * 8 + 6, (loadimagey + y) * 8 + 6);
								g.drawLine((loadimagex + x) * 8 + 1, (loadimagey + y) * 8 + 6, (loadimagex + x) * 8 + 6, (loadimagey + y) * 8 + 1);
							}
						}
					}
				}
				if (imageloaded == 1)
					g.drawImage(loadimage, 800 + loadimagex, 700 + loadimagey, null);
			}
			g.setColor(new Color(red, green, blue));
			g.fillRect(825, 25, 50, 50);
			g.drawImage(screenimage, 0, 0, null);
			g.drawImage(tinyimage, 800, 700, null);
			g.setColor(Color.CYAN);
			g.drawRect(808, 101 + (16 - (red + 1) / 16) * 20, 17, 17);
			g.drawRect(807, 100 + (16 - (red + 1) / 16) * 20, 19, 19);
			g.drawRect(840, 101 + (16 - (green + 1) / 16) * 20, 17, 17);
			g.drawRect(839, 100 + (16 - (green + 1) / 16) * 20, 19, 19);
			g.drawRect(872, 101 + (16 - (blue + 1) / 16) * 20, 17, 17);
			g.drawRect(871, 100 + (16 - (blue + 1) / 16) * 20, 19, 19);
			g.drawRect(811, 429 + action * 35, 76, 31);
			g.drawRect(810, 428 + action * 35, 78, 33);
		}
		public void mousePressed(MouseEvent evt) {
			if (imageloaded <= 0)
				clickaction(evt);
			else
				loadimage();
			repaint();
		}
		public void mouseReleased(MouseEvent evt) {
			origin = 0;
		}
		public void mouseDragged(MouseEvent evt) {
			clickaction(evt);
			repaint();
		}
		public void mouseMoved(MouseEvent evt) {
			int posx = evt.getX() / 8;
			int posy = evt.getY() / 8;
			if (imageloaded > 0 && posx + loadimage.getWidth() <= 100 && posy + loadimage.getHeight() <= 100) {
				loadimagex = posx;
				loadimagey = posy;
				repaint();
			}
		}
		public void mouseClicked(MouseEvent evt) {}
		public void mouseEntered(MouseEvent evt) {}
		public void mouseExited(MouseEvent evt) {}
		public void setup(Graphics g) {
			g.setColor(new Color(191, 191, 191));
			g.fillRect(0, 0, 800, 800);
			g.setColor(new Color(127, 127, 127));
			g.fillRect(800, 700, 100, 100);
			g.setColor(Color.BLACK);
			for (int y = 0; y < 800; y += 8) {
				for (int x = 0; x < 800; x += 8) {
					g.drawLine(x + 1, y + 1, x + 6, y + 6);
					g.drawLine(x + 1, y + 6, x + 6, y + 1);
				}
			}
			int level = 255;
			int shift = 16;
			for (int x = 809; x <= 873; x += 32) {
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
			g.drawRect(824, 24, 51, 51);
			for (int count = 1; count < 8; count += 1) {
				g.setColor(new Color(count * 16, count * 16, count * 16));
				g.drawRect(824 - count, 24 - count, 51 + 2 * count, 51 + 2 * count);
			}
			g.setColor(Color.BLUE);
			g.fillRect(812, 465, 75, 30);
			g.fillRect(812, 500, 75, 30);
			g.fillRect(812, 535, 75, 30);
			g.setColor(Color.GREEN);
			g.drawRect(813, 466, 72, 27);
			g.drawRect(813, 501, 72, 27);
			g.drawRect(813, 536, 72, 27);
			g.setColor(new Color(191, 191, 191));
			g.setFont(new Font("Monospaced", Font.BOLD, 24));
			g.drawString("Draw", 822, 487);
			g.drawString("Erase", 815, 522);
			g.setFont(new Font("Monospaced", Font.BOLD, 12));
			g.drawString("Absorb", 828, 553);
		}
		public void clickaction(MouseEvent evt) {
			int mousex = evt.getX();
			int mousey = evt.getY();
			int posx;
			int posy;
			if (mousex >= 0 && mousex < 800 && mousey >= 0 && mousey < 800 && (origin == 0 || origin == 1)) {
				origin = 1;
				posx = mousex / 8;
				posy = mousey / 8;
				if (action == 1) {
					Graphics2D g = screenimage.createGraphics();
					if (posx < left)
						left = posx;
					else if (posx > right)
						right = posx;
					else if (posy < up)
						up = posy;
					else if (posy > down)
						down = posy;
					else if (left == -1) {
						left = posx;
						right = posx;
						up = posy;
						down = posy;
					}
					Color thecolor = new Color(red, green, blue);
					colors[posy][posx] = (255 << 24) | (red << 16) | (green << 8) | blue;
					g.setColor(new Color(red, green, blue));
					g.fillRect(posx * 8, posy * 8, 8, 8);
					tinyimage.setRGB(posx, posy, colors[posy][posx]);
				} else if (action == 2) {
					colors[posy][posx] = 0;
					left = 100;
					right = -1;
					up = 100;
					down = -1;
					for (int y = 0; y < 100; y += 1) {
						for (int x = 0; x < 100; x += 1) {
							if (left > x && colors[y][x] != 0)
								left = x;
							if (up > y && colors[y][x] != 0)
								up = y;
							if (right < x && colors[y][x] != 0)
								right = x;
							if (down < y && colors[y][x] != 0)
								down = y;
						}
					}
					for (int y = 0; y < 8; y += 1) {
						for (int x = 0; x < 8; x += 1) {
							screenimage.setRGB(posx * 8 + x, posy * 8 + y, 0);
						}
					}
					tinyimage.setRGB(posx, posy, 0);
				} else if (action == 3 && colors[posy][posx] < 0) {
					red = (colors[posy][posx] >> 16) & 255;
					green = (colors[posy][posx] >> 8) & 255;
					blue = colors[posy][posx] & 255;
				}
			} else if (mousex >= 812 && mousex < 887 && mousey >= 465 && mousey <= 495 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 1;
			} else if (mousex >= 812 && mousex < 887 && mousey >= 500 && mousey <= 530 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 2;
			} else if (mousex >= 812 && mousex < 887 && mousey >= 535 && mousey <= 565 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 3;
			} else if (mousex >= 809 && mousex < 889 && (mousex - 809) % 32 >= 0 && (mousex - 809) % 32 < 16 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 >= 0 && (mousey - 102) % 20 < 16 && (origin == 0 || origin == 3)) {
				origin = 3;
				posx = (mousex - 809) / 32;
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
			} else if (mousex >= 800 && mousex < 900 && mousey >= 700 && mousey < 800)
				done = true;
		}
		public void loadimage() {
			if (imageloaded == 1) {
				Graphics2D g = screenimage.createGraphics();
				for (int y = 0; y < loadimage.getHeight(); y += 1) {
					for (int x = 0; x < loadimage.getWidth(); x += 1) {
						if (loadimage.getRGB(x, y) < 0) {
							g.setColor(new Color(loadimage.getRGB(x, y)));
							g.fillRect((loadimagex + x) * 8, (loadimagey + y) * 8, 8, 8);
							colors[loadimagey + y][loadimagex + x] = loadimage.getRGB(x, y);
						}
					}
				}
				tinyimage.createGraphics().drawImage(loadimage, loadimagex, loadimagey, null);
				left = 100;
				right = -1;
				up = 100;
				down = -1;
				for (int y = 0; y < 100; y += 1) {
					for (int x = 0; x < 100; x += 1) {
						if (left > x && colors[y][x] != 0)
							left = x;
						if (up > y && colors[y][x] != 0)
							up = y;
						if (right < x && colors[y][x] != 0)
							right = x;
						if (down < y && colors[y][x] != 0)
							down = y;
					}
				}
			}
			imageloaded = (byte)(-imageloaded);
		}
	}
}