import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
public class imagejoiner {
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
	public static BufferedImage loadimage;
	public static int loadimagex = 0;
	public static int loadimagey = 0;
	public static boolean first = true;
	public static String args1 = "";
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("You need 2 images to join.");
			System.exit(0);
		}
		try{
			loadimage = ImageIO.read(new File("images\\" + args[0] + ".png"));
			args1 = args[1];
		} catch(Exception e) {
			System.out.println("File \"" + args[0] + "\" count not be loaded. Program will exit.");
			System.exit(0);
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
			g.drawImage(screenimage, 0, 0, null);
			g.drawImage(tinyimage, 800, 700, null);
			for (int y = 0; y < loadimage.getHeight(); y += 1) {
				for (int x = 0; x < loadimage.getWidth(); x += 1) {
					if (loadimage.getRGB(x, y) < 0) {
						g.setColor(new Color(loadimage.getRGB(x, y)));
						g.fillRect((loadimagex + x) * 8, (loadimagey + y) * 8, 8, 8);
					}
				}
			}
			g.drawImage(loadimage, 800 + loadimagex, 700 + loadimagey, null);
		}
		public void mousePressed(MouseEvent evt) {
			loadimage();
			repaint();
		}
		public void mouseReleased(MouseEvent evt) {
		}
		public void mouseDragged(MouseEvent evt) {
		}
		public void mouseMoved(MouseEvent evt) {
			int posx = evt.getX() / 8;
			int posy = evt.getY() / 8;
			if (posx + loadimage.getWidth() <= 100 && posy + loadimage.getHeight() <= 100) {
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
		}
		public void loadimage() {
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
			loadimagex = 0;
			loadimagey = 0;
			if (first) {
				try{
					loadimage = ImageIO.read(new File("images\\" + args1 + ".png"));
				} catch(Exception e) {
					System.out.println("File \"" + args1 + "\" count not be loaded. Program will exit.");
					System.exit(0);
				}
				first = false;
			} else {
				done = true;
			}
		}
	}
}