//Version 3
import java.util.Scanner;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URLClassLoader;
//import java.net.URI;
import java.net.URL;
//import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
//TO ADD:
//option for file format
//option for hex stats
public class imagemaker extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	public static int readint(String line) {
		int digit = -1;
		int number = 0;
		boolean negative = false;
		int pos = 0;
		int length = line.length();
		while (pos < length) {
			digit = (int)(line.charAt(pos));
			if (digit > 47 && digit < 58)
				digit = digit - 48;
			else if (digit == 45 && number == 0)
				negative = true;
			if (digit >= 0 && digit < 10) {
				if (number < 214748364 || (number == 214748364 && digit <= 7))
					number = number * 10 + digit;
				else
					return number;
			}
			pos = pos + 1;
		}
		if (negative)
			return -number;
		return number;
	}
	public static int screenwidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	public static int screenheight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	public static String line() {
		Scanner scan = new Scanner(System.in);
		return scan.nextLine();
	}
	private static Filer filer = new Filer("options.txt");
	private byte done = 0;
	public BufferedImage mainimage = null;
	public BufferedImage copyimage = null;
	private BufferedImage bgimage = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
	private BufferedImage panelimage = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
	private int width = 500;
	private int height = 600;
	public int imagex = 0;
	public int imagey = 0;
	private byte action = 1;
	public int red = 255;
	public int green = 255;
	public int blue = 255;
	public int alpha = 255;
	private byte origin = 0;
	private int movex = 0;
	private int movey = 0;
	private boolean clicked = false;
	private int copytop = 0;
	private int copyleft = 0;
	private int copyright = 0;
	private int copybottom = 0;
	private boolean inbounds = false;
	private int initialx = 0;
	private int initialy = 0;
	private int finalx = 0;
	private int finaly = 0;
	private boolean greenrect = false;
	private boolean statson = false;
	private byte backsetting = 0;
	private Color backcolor = new Color(192, 192, 192);
	private boolean border = false;
	private boolean showoptions = false;
	private byte Xs = 1;
	private Color Xcolor = Color.BLACK;
	private int size = 8;
	private Link undo = null;
	private Link redo = null;
	private int panelnum = 0;
	private boolean showpanels = false;
	private Object[] panels = null;
	private Method[][] methods = null;
	private String[] names = null;
	private Object panel = null;
	private Method paint = null;
	private Method click = null;
	private Method release = null;
	private Method scroll = null;
	public static void main(String[] args) {
		imagemaker thepanel = new imagemaker();
		String filename = "";
		if (args.length > 0) {
			filename = args[0];
			try{
				thepanel.mainimage = imagecopy(ImageIO.read(new File("images/" + filename + ".png")));
			} catch(Exception e) {
				System.out.println("File \"" + filename + "\" could not be loaded. Continuing without file.");
			}
		}
		if (filer.fileisthere()) {
			filer.readfile();
			String[] lines = filer.getlines();
			for (int pos = 0; pos < lines.length; pos += 1) {
				if (lines[pos].startsWith("stats="))
					thepanel.statson = lines[pos].endsWith("true");
				else if (lines[pos].startsWith("border="))
					thepanel.border = lines[pos].endsWith("true");
				else if (lines[pos].startsWith("Xs=\\")) {
					thepanel.Xs = 2;
					thepanel.Xcolor = new Color(readint(lines[pos].substring(4, lines[pos].length())));
				} else if (lines[pos].startsWith("Xs="))
					thepanel.Xs = (byte)(readint(lines[pos].substring(3, lines[pos].length())));
				else if (lines[pos].startsWith("background=\\")) {
					thepanel.backsetting = 3;
					thepanel.backcolor = new Color(readint(lines[pos].substring(12, lines[pos].length())));
				} else if (lines[pos].startsWith("background="))
					thepanel.backsetting = (byte)(readint(lines[pos].substring(11, lines[pos].length())));
				else if (lines[pos].startsWith("size="))
					thepanel.size = readint(lines[pos].substring(5, lines[pos].length()));
			}
			if (thepanel.backsetting != 0 || thepanel.Xs != 1 || thepanel.size != 8)
				thepanel.bgsetup(thepanel.bgimage.createGraphics());
		}
		File addons = new File("addons");
		File[] panelfiles = addons.listFiles();
		String name = "";
		int count = 0;
		boolean[] goodfile = new boolean[panelfiles.length];
		Object[] allpanels = new Object[panelfiles.length];
		Method[][] allmethods = new Method[panelfiles.length][4];
		String[] allnames = new String[panelfiles.length];
		try {
			URLClassLoader urlcl = new URLClassLoader(new URL[] {addons.toURI().toURL()});
			Class<?> theclass = null;
			for (int spot = 0; spot < panelfiles.length; spot += 1) {
				name = panelfiles[spot].getName();
				if (name.endsWith(".class")) {
					try {
						theclass = urlcl.loadClass(name.substring(0, name.length() - 6));
						allmethods[spot][0] = theclass.getMethod("paint", Graphics.class);
						allmethods[spot][1] = theclass.getMethod("click", MouseEvent.class);
						allmethods[spot][2] = theclass.getMethod("release", MouseEvent.class);
						allmethods[spot][3] = theclass.getMethod("scroll", MouseWheelEvent.class);
						allpanels[spot] = theclass.getConstructor(imagemaker.class).newInstance(thepanel);
						allnames[spot] = (String)(theclass.getMethod("name", null).invoke(allpanels[spot], null));
						goodfile[spot] = true;
						count = count + 1;
					} catch(Exception e) {
					}
				}
			}
			thepanel.panels = new Object[count];
			thepanel.methods = new Method[count][0];
			thepanel.names = new String[count];
			count = 0;
			for (int spot = 0; spot < goodfile.length; spot += 1) {
				if (goodfile[spot]) {
					thepanel.panels[count] = allpanels[spot];
					thepanel.methods[count] = allmethods[spot];
					thepanel.names[count] = allnames[spot];
					count = count + 1;
				}
			}
		} catch(Exception e) {
		}
		JFrame window = new JFrame("Image Maker");
		window.setContentPane(thepanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(thepanel.width + 16, thepanel.height + 38);
		window.setLocation((screenwidth() - thepanel.width) / 2 - 8, (screenheight() - thepanel.height) / 2 - 30);
		window.setVisible(true);
		thepanel.requestFocus();
		window.toFront();
		while (thepanel.done == 0) {
			while (thepanel.done == 0) {
				try {
					Thread.sleep(0);
				} catch(Exception e) {
				}
			}
			if (thepanel.done == 2) {
				System.out.println("Enter the name of the file you wish to import to the copy image:");
				try {
					name = line();
					thepanel.copyimage = ImageIO.read(new File("images/" + name + ".png"));
					System.out.println("The image has been imported.  Press \"PASTE\" to place it.");
				} catch(Exception e) {
					System.out.println("Sorry, your file could not be loaded.");
					thepanel.copyimage = null;
				}
				thepanel.done = 0;
				thepanel.requestFocus();
				window.toFront();
			}
		}
		window.dispose();
		if (filename.equals(""))
			System.out.println("Enter the name of the file you wish to save:");
		else
			System.out.println("Enter the name that you wish to save \"" + filename + "\" as:");
		name = "";
		try {
			name = line();
			File file = new File("images/" + name + ".png");
			ImageIO.write(thepanel.mainimage, "png", file);
			System.out.println("You successfully saved your image!");
		} catch(Exception e) {
			System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be saved.");
		}
	}
	public imagemaker() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setBackground(new Color(255, 192, 0));
		bgsetup(bgimage.createGraphics());
		setup(panelimage.createGraphics());
	}
	public void paintComponent(Graphics g) {
		try {
			safepaintComponent(g);
		} catch(OutOfMemoryError e) {
			System.out.print("Out of memory; clearing event history,");
			undo = null;
			redo = null;
			System.out.print(" collecting garbage,");
			Runtime.getRuntime().gc();
			System.out.println(" and repainting.");
			safepaintComponent(g);
		}
	}
	public void safepaintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bgimage, 0, 0, null);
		int iwidth = 0;
		int iheight = 0;
		int max = 200 / size + 100;
		int min = max - 200;
		int max2 = 400 / size;
//drawing the image
		if (mainimage != null) {
			iwidth = mainimage.getWidth();
			iheight = mainimage.getHeight();
			int pixel = 0;
			if (imagex + iwidth > min && imagex < max && imagey + iheight > min && imagey < max)
				g.drawImage(mainimage.getSubimage(Math.max(0, min - imagex), Math.max(0, min - imagey), Math.min(200, imagex - min + iwidth) - Math.max(0, imagex - min), Math.min(200, imagey - min + iheight) - Math.max(0, imagey - min)), Math.max(0, imagex - min) + 100, Math.max(0, imagey - min) + 400, null);
			if (imagex + iwidth > 0 && imagex < max2 && imagey + iheight > 0 && imagey < max2)
				g.drawImage(mainimage, Math.max(0, imagex * size), Math.max(0, imagey * size), Math.min(400, (iwidth + imagex) * size), Math.min(400, (iheight + imagey) * size), Math.max(0, -imagex), Math.max(0, -imagey), Math.min(iwidth, max2 - imagex), Math.min(iheight, max2 - imagey), null);
		}
//borders
		if (mainimage != null && border) {
			int left = 0;
			int right = 0;
			int top = 0;
			int bottom = 0;
			g.setColor(Color.YELLOW);
//left small
			if (imagex > min && imagex <= max && imagey > min - iheight - 1 && imagey <= max + 1) {
				top = Math.max(imagey - 1 - min, 0) + 400;
				bottom = Math.min(imagey + iheight - min, 199) + 400;
				left = imagex + 99 - min;
				g.drawLine(left, top, left, bottom);
			}
//right small
			if (imagex >= min - iwidth && imagex < max - iwidth && imagey > min - iheight - 1 && imagey <= max + 1) {
				top = Math.max(imagey - 1 - min, 0) + 400;
				bottom = Math.min(imagey + iheight - min, 199) + 400;
				right = imagex + iwidth + 100 - min;
				g.drawLine(right, top, right, bottom);
			}
//top small
			if (imagex > min - iwidth - 1 && imagex <= max + 1 && imagey > min && imagey <= max) {
				left = Math.max(imagex - min, 0) + 100;
				right = Math.min(imagex + iwidth - min, 200) + 99;
				top = imagey + 399 - min;
				g.drawLine(left, top, right, top);
			}
//bottom small
			if (imagex > min - iwidth - 1 && imagex <= max + 1 && imagey >= min - iheight && imagey < max - iheight) {
				left = Math.max(imagex - min, 0) + 100;
				right = Math.min(imagex + iwidth - min, 200) + 99;
				bottom = imagey + iheight + 400 - min;
				g.drawLine(left, bottom, right, bottom);
			}
			int msize = size / 4;
			int msize2 = msize * 2;
			int msize3 = msize * 3;
//left large
			if (imagex > 0 && imagex <= max2 && imagey > 0 - iheight && imagey <= max2) {
				top = Math.max(imagey, 0) * size;
				bottom = Math.min(imagey + iheight, max2) * size;
				g.setColor(Color.RED);
				g.fillRect(imagex * size - size, top, msize, bottom - top);
				g.fillRect(imagex * size - msize, top, msize, bottom - top);
				g.setColor(Color.GREEN);
				g.fillRect(imagex * size - msize3, top, msize2, bottom - top);
			}
//right large
			if (imagex >= 0 - iwidth && imagex < max2 - iwidth && imagey > 0 - iheight && imagey <= max2) {
				top = Math.max(imagey, 0) * size;
				bottom = Math.min(imagey + iheight, max2) * size;
				g.setColor(Color.RED);
				g.fillRect((imagex + iwidth) * size, top, msize, bottom - top);
				g.fillRect((imagex + iwidth) * size + msize3, top, msize, bottom - top);
				g.setColor(Color.GREEN);
				g.fillRect((imagex + iwidth) * size + msize, top, msize2, bottom - top);
			}
//top large
			if (imagey > 0 && imagey <= max2 && imagex > 0 - iwidth && imagex <= max2) {
				left = Math.max(imagex, 0) * size;
				right = Math.min(imagex + iwidth, max2) * size;
				g.setColor(Color.RED);
				g.fillRect(left, imagey * size - size, right - left, msize);
				g.fillRect(left, imagey * size - msize, right - left, msize);
				g.setColor(Color.GREEN);
				g.fillRect(left, imagey * size - msize3, right - left, msize2);
			}
//bottom large
			if (imagey >= 0 - iheight && imagey < max2 - iheight && imagex > 0 - iwidth && imagex <= max2) {
				left = Math.max(imagex, 0) * size;
				right = Math.min(imagex + iwidth, max2) * size;
				g.setColor(Color.RED);
				g.fillRect(left, (imagey + iheight) * size, right - left, msize);
				g.fillRect(left, (imagey + iheight) * size + msize3, right - left, msize);
				g.setColor(Color.GREEN);
				g.fillRect(left, (imagey + iheight) * size + msize, right - left, msize2);
			}
//topleft large
			if (imagex > 0 && imagex <= max2 && imagey > 0 && imagey <= max2) {
				left = imagex * size - size;
				top = imagey * size - size;
				g.setColor(Color.RED);
				g.fillRect(left, top, size, msize);
				g.fillRect(left, top + msize, msize, msize3);
				g.fillRect(left + msize3, top + msize3, msize, msize);
				g.setColor(Color.GREEN);
				g.fillRect(left + msize, top + msize, msize3, msize2);
				g.fillRect(left + msize, top + msize3, msize2, msize);
			}
//topright large
			if (imagex >= 0 - iwidth && imagex < max2 - iwidth && imagey > 0 && imagey <= max2) {
				left = (imagex + iwidth) * size;
				top = imagey * size - size;
				g.setColor(Color.RED);
				g.fillRect(left, top, size, msize);
				g.fillRect(left + msize3, top + msize, msize, msize3);
				g.fillRect(left, top + msize3, msize, msize);
				g.setColor(Color.GREEN);
				g.fillRect(left, top + msize, msize3, msize2);
				g.fillRect(left + msize, top + msize3, msize2, msize);
			}
//bottomleft large
			if (imagex > 0 && imagex <= max2 && imagey >= 0 - iheight && imagey < max2 - iheight) {
				left = imagex * size - size;
				top = (imagey + iheight) * size;
				g.setColor(Color.RED);
				g.fillRect(left, top + msize3, size, msize);
				g.fillRect(left, top, msize, msize3);
				g.fillRect(left + msize3, top, msize, msize);
				g.setColor(Color.GREEN);
				g.fillRect(left + msize, top + msize, msize3, msize2);
				g.fillRect(left + msize, top, msize2, msize);
			}
//bottomright large
			if (imagex >= 0 - iwidth && imagex < max2 - iwidth && imagey >= 0 - iheight && imagey < max2 - iheight) {
				left = (imagex + iwidth) * size;
				top = (imagey + iheight) * size;
				g.setColor(Color.RED);
				g.fillRect(left, top + msize3, size, msize);
				g.fillRect(left + msize3, top, msize, msize3);
				g.fillRect(left, top, msize, msize);
				g.setColor(Color.GREEN);
				g.fillRect(left, top + msize, msize3, msize2);
				g.fillRect(left + msize, top, msize2, msize);
			}
		}
//movement box shadow
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(100, 400, 200, -min);
		g.fillRect(100, 400 - min, -min, max2);
		g.fillRect(max + 100, 400 - min, -min, max2);
		g.fillRect(100, 400 + max, 200, -min);
//other panel
		if (panelnum != 0) {
			try {
				paint.invoke(panel, g);
			} catch(Exception e) {
				System.out.println("" + e);
				e.printStackTrace();
			}
//main panel
//color selection
		} else {
			g.drawImage(panelimage, 0, 0, null);
			g.setColor(new Color(red, green, blue, alpha));
			g.fillRect(426, 26, 48, 48);
			g.setColor(Color.CYAN);
			g.drawRect(405, 421 - (red + 8) / 16 * 20, 17, 17);
			g.drawRect(404, 420 - (red + 8) / 16 * 20, 19, 19);
			g.drawRect(429, 421 - (green + 8) / 16 * 20, 17, 17);
			g.drawRect(428, 420 - (green + 8) / 16 * 20, 19, 19);
			g.drawRect(453, 421 - (blue + 8) / 16 * 20, 17, 17);
			g.drawRect(452, 420 - (blue + 8) / 16 * 20, 19, 19);
			g.drawRect(477, 421 - (alpha + 8) / 16 * 20, 17, 17);
			g.drawRect(476, 420 - (alpha + 8) / 16 * 20, 19, 19);
//draw option
			if (action < 4) {
				g.drawRect(411, 429 + action * 35, 76, 31);
				g.drawRect(410, 428 + action * 35, 78, 33);
			} else {
				g.setColor(Color.RED);
				g.drawRect(309, 264 + action * 35, 76, 31);
				g.drawRect(308, 263 + action * 35, 78, 33);
			}
//show copy/cut red rect, or paste image
			if ((action == 4 || action == 5) && clicked && inbounds) {
				g.setColor(new Color(255, 0, 0, 128));
				g.fillRect((copyleft + imagex) * size, (copytop + imagey) * size, (copyright - copyleft) * size, (copybottom - copytop) * size);
			} else if (action == 6 && clicked && copyimage != null && inbounds) {
				int pixel = 0;
				int cwidth = copyimage.getWidth();
				int cheight = copyimage.getHeight();
				for (int y = 0; y < Math.min(cheight, 400 / size - imagey - copytop); y += 1) {
					for (int x = 0; x < Math.min(cwidth, 400 / size - imagex - copyleft); x += 1) {
						pixel = copyimage.getRGB(x, y);
						g.setColor(new Color(pixel >> 16 & 255, pixel >> 8 & 255, pixel & 255, (pixel >> 24 & 255) / 2));
						g.fillRect((x + imagex + copyleft) * size, (y + imagey + copytop) * size, size, size);
					}
				}
			}
//hiding undo/redo
			if (undo == null) {
				g.setColor(new Color(255, 192, 0, 192));
				g.fillRect(0, 505, 35, 20);
			}
			if (redo == null) {
				g.setColor(new Color(255, 192, 0, 192));
				g.fillRect(40, 505, 35, 20);
			}
//what's getting clicked on
			if (greenrect) {
				g.setColor(new Color(0, 255, 0, 128));
				if (origin == 101)
					g.fillRect(0, 400, 75, 30);
				else if (origin == 102)
					g.fillRect(0, 435, 75, 30);
				else if (origin == 103)
					g.fillRect(310, 510, 20, 20);
				else if (origin == 104)
					g.fillRect(335, 510, 20, 20);
				else if (origin == 105)
					g.fillRect(360, 510, 20, 20);
				else if (origin == 106) {
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(344 - count, 563 - count, 343 + count, 563 - count);
					}
				} else if (origin == 107) {
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(344 + count, 564 - count, 344 + count, 563 + count);
					}
				} else if (origin == 108) {
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(343 - count, 564 - count, 343 - count, 563 + count);
					}
				} else if (origin == 109) {
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(344 - count, 564 + count, 343 + count, 564 + count);
					}
				} else if (origin == 110)
					g.fillRect(0, 505, 35, 20);
				else if (origin == 111)
					g.fillRect(40, 505, 35, 20);
				else if (origin == 112)
					g.fillRect(78, 505, 19, 19);
			}
		}
//stats
		if (statson) {
			g.setFont(new Font("Monospaced", Font.BOLD, 12));
			g.setColor(new Color(64, 64, 64));
			if (mainimage == null) {
				g.drawString("W:", 4, 537);
				g.drawString("H:", 4, 552);
				g.drawString("X:", 4, 567);
				g.drawString("Y:", 4, 582);
			} else {
				g.drawString("W:" + iwidth, 4, 537);
				g.drawString("H:" + iheight, 4, 552);
				g.drawString("X:" + imagex, 4, 567);
				g.drawString("Y:" + imagey, 4, 582);
			}
			g.setColor(Color.RED);
			g.drawString(red + "", 2, 597);
			g.setColor(Color.GREEN);
			g.drawString(green + "", 27, 597);
			g.setColor(Color.BLUE);
			g.drawString(blue + "", 52, 597);
			g.setColor(Color.WHITE);
			g.drawString(alpha + "", 77, 597);
		}
//options "panel"
		if (showoptions)
			optionspaintComponent(g);
//panels "panel"
		else if (showpanels)
			panelspaintComponent(g);
	}
	public void mousePressed(MouseEvent evt) {
		try {
			clickaction(evt);
		} catch(OutOfMemoryError e) {
			System.out.print("Out of memory; clearing event history,");
			undo = null;
			redo = null;
			System.out.print(" collecting garbage,");
			Runtime.getRuntime().gc();
			System.out.println(" and repressing.");
			clickaction(evt);
		}
	}
	public void mouseReleased(MouseEvent evt) {
		try {
			safemouseReleased(evt);
		} catch(OutOfMemoryError e) {
			System.out.print("Out of memory; clearing event history,");
			undo = null;
			redo = null;
			System.out.print(" collecting garbage,");
			Runtime.getRuntime().gc();
			System.out.println(" and rereleasing.");
			safemouseReleased(evt);
		}
	}
	public void safemouseReleased(MouseEvent evt) {
		origin = 0;
		greenrect = false;
		if (panelnum != 0) {
			try {
				release.invoke(panel, evt);
			} catch(Exception e) {
				System.out.println("" + e);
				e.printStackTrace();
			}
		} else if (clicked && inbounds) {
//finished selecting copy/cut area
			if (action == 4)
				copyimage = imagecopy(mainimage.getSubimage(copyleft, copytop, copyright - copyleft, copybottom - copytop));
			else if (action == 5) {
				int cwidth = copyright - copyleft;
				int cheight = copybottom - copytop;
				copyimage = imagecopy(mainimage.getSubimage(copyleft, copytop, cwidth, cheight));
//Type 6: cut: [x, y] [image]
				redo = null;
				undo = new Link(6, undo);
				undo.ints = new int[] {copyleft, copytop};
				undo.image = imagecopy(copyimage);
				mainimage.setRGB(copyleft, copytop, cwidth, cheight, new int[cwidth * cheight], 0, cwidth);
//ready to paste the image
			} else if (action == 6) {
				int cwidth = copyimage.getWidth();
				int cheight = copyimage.getHeight();
				if (mainimage != null) {
					if (copyleft < 0 || copyright > mainimage.getWidth() || copytop < 0 || copybottom > mainimage.getHeight()) {
//Type 7: paste + resize: [oldw, oldh, oldx, oldy, pastew, pasteh, pastex, pastey] [placeimage]
						int mwidth = mainimage.getWidth();
						int mheight = mainimage.getHeight();
						int posleft = Math.max(0, copyleft);
						int postop = Math.max(0, copytop);
						int negleft = Math.max(0, -copyleft);
						int negtop = Math.max(0, -copytop);
						redo = null;
						undo = new Link(7, undo);
						undo.ints = new int[] {mwidth, mheight, negleft, negtop, cwidth, cheight, posleft, postop};
						if (copyleft < mwidth && copytop < mheight && copyright > 0 && copybottom > 0) {
							int poswidth = Math.min(mwidth, copyright) - posleft;
							int posheight = Math.min(mheight, copybottom) - postop;
							undo.image = imagecopy(mainimage.getSubimage(posleft, postop, poswidth, posheight));
						}
						BufferedImage tempimage = new BufferedImage(Math.max(copyright, mwidth) - Math.min(copyleft, 0), Math.max(copybottom, mheight) - Math.min(copytop, 0), BufferedImage.TYPE_INT_ARGB);
						tempimage.setData(mainimage.getRaster().createTranslatedChild(negleft, negtop));
						pasteon(copyimage, posleft, postop, tempimage);
						mainimage = tempimage;
						imagex = imagex - negleft;
						imagey = imagey - negtop;
					} else {
//Type 6: paste: [x, y] [image]
						redo = null;
						undo = new Link(6, undo);
						undo.ints = new int[] {copyleft, copytop};
						undo.image = imagecopy(mainimage.getSubimage(copyleft, copytop, cwidth, cheight));
						pasteon(copyimage, copyleft, copytop, mainimage);
					}
				} else {
//Type 8: paste + new image: -
					redo = null;
					undo = new Link(8, undo);
					mainimage = imagecopy(copyimage);
					imagex = copyleft;
					imagey = copytop;
				}
			}
		}
		clicked = false;
		repaint();
	}
	public void mouseDragged(MouseEvent evt) {
		try {
			clickaction(evt);
		} catch(OutOfMemoryError e) {
			System.out.print("Out of memory; clearing event history,");
			undo = null;
			redo = null;
			System.out.print(" collecting garbage,");
			Runtime.getRuntime().gc();
			System.out.println(" and redragging.");
			clickaction(evt);
		}
	}
	public void mouseMoved(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void bgsetup(Graphics g) {
//backgrounds
		g.setColor(backcolor);
		g.fillRect(0, 0, 400, 400);
		if (Xs != 0) {
			g.setColor(Xcolor);
			if (size == 8) {
				for (int y = 0; y < 400; y += 8) {
					for (int x = 0; x < 400; x += 8) {
						g.drawLine(x + 1, y + 1, x + 6, y + 6);
						g.drawLine(x + 1, y + 6, x + 6, y + 1);
					}
				}
			} else if (size == 4) {
				for (int y = 0; y < 400; y += 4) {
					for (int x = 0; x < 400; x += 4) {
						g.drawLine(x + 1, y + 1, x + 2, y + 2);
					}
				}
			}
		}
		g.setColor(new Color(128, 128, 128));
		g.fillRect(100, 400, 200, 200);
//switch panels button
		g.setColor(Color.WHITE);
		g.fillRect(400, 442, 100, 19);
		g.setColor(Color.BLACK);
		g.drawRect(401, 443, 97, 16);
		g.setColor(new Color(64, 64, 64));
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		g.drawString("Switch Panels", 405, 456);
	}
	public void setup(Graphics g) {
//color selection
		int level = 0;
		for (int y = 422; y >= 122; y -= 20) {
			g.setColor(new Color(level << 16));
			g.fillRect(406, y, 16, 16);
			g.setColor(new Color(level << 8));
			g.fillRect(430, y, 16, 16);
			g.setColor(new Color(level));
			g.fillRect(454, y, 16, 16);
			g.setColor(new Color(192, 192, 192));
			g.fillRect(478, y, 16, 16);
			g.setColor(Color.BLACK);
			g.fillRect(480, y + 2, 2, 2);
			g.fillRect(490, y + 2, 2, 2);
			g.fillRect(482, y + 4, 2, 2);
			g.fillRect(488, y + 4, 2, 2);
			g.fillRect(484, y + 6, 4, 4);
			g.fillRect(482, y + 10, 2, 2);
			g.fillRect(488, y + 10, 2, 2);
			g.fillRect(480, y + 12, 2, 2);
			g.fillRect(490, y + 12, 2, 2);
			g.setColor(new Color(255, 255, 255, level));
			g.fillRect(478, y, 16, 16);
			level = level + 16;
		}
		level = 0;
		g.setColor(Color.RED);
		g.fillRect(406, 102, 16, 16);
		g.setColor(Color.GREEN);
		g.fillRect(430, 102, 16, 16);
		g.setColor(Color.BLUE);
		g.fillRect(454, 102, 16, 16);
		g.setColor(Color.WHITE);
		g.fillRect(478, 102, 16, 16);
//color display square
		for (int count = 0; count < 9; count += 1) {
			g.setColor(new Color(count * 16, count * 16, count * 16));
			g.drawRect(425 - count, 25 - count, 49 + 2 * count, 49 + 2 * count);
		}
		g.setColor(new Color(192, 192, 192));
		g.fillRect(426, 26, 48, 48);
		g.setColor(Color.BLACK);
		g.fillRect(432, 32, 6, 6);
		g.fillRect(462, 32, 6, 6);
		g.fillRect(438, 38, 6, 6);
		g.fillRect(456, 38, 6, 6);
		g.fillRect(444, 44, 12, 12);
		g.fillRect(438, 56, 6, 6);
		g.fillRect(456, 56, 6, 6);
		g.fillRect(432, 62, 6, 6);
		g.fillRect(462, 62, 6, 6);
//draw tools and such
		g.setColor(Color.BLUE);
		g.fillRect(412, 465, 75, 30);
		g.fillRect(412, 500, 75, 30);
		g.fillRect(412, 535, 75, 30);
		g.fillRect(0, 400, 75, 30);
		g.setColor(new Color(128, 0, 0));
		g.fillRect(400, 570, 100, 30);
		g.setColor(Color.GREEN);
		g.drawRect(413, 466, 72, 27);
		g.drawRect(413, 501, 72, 27);
		g.drawRect(413, 536, 72, 27);
		g.drawRect(1, 401, 72, 27);
		g.drawRect(401, 571, 97, 27);
		g.fillRect(310, 405, 75, 30);
		g.fillRect(310, 440, 75, 30);
		g.fillRect(310, 475, 75, 30);
		g.setColor(Color.BLUE);
		g.drawRect(311, 406, 72, 27);
		g.drawRect(311, 441, 72, 27);
		g.drawRect(311, 476, 72, 27);
		g.setColor(new Color(128, 128, 128));
		g.fillRect(0, 435, 75, 30);
		g.fillRect(0, 470, 75, 30);
		g.fillRect(310, 510, 20, 20);
		g.fillRect(335, 510, 20, 20);
		g.fillRect(360, 510, 20, 20);
		g.fillRect(0, 505, 35, 20);
		g.fillRect(40, 505, 35, 20);
		g.fillRect(78, 505, 19, 19);
		g.setColor(Color.BLACK);
		g.drawRect(1, 436, 72, 27);
		g.drawRect(1, 471, 72, 27);
		g.drawRect(311, 511, 17, 17);
		g.drawRect(336, 511, 17, 17);
		g.drawRect(361, 511, 17, 17);
		g.drawRect(1, 506, 32, 17);
		g.drawRect(41, 506, 32, 17);
		g.drawRect(79, 506, 16, 16);
		g.setColor(new Color(192, 192, 192));
		g.setFont(new Font("Monospaced", Font.BOLD, 24));
		g.drawString("Draw", 422, 487);
		g.drawString("Erase", 415, 522);
		g.drawString("Save", 422, 592);
		g.drawString("Reset", 3, 422);
		g.setColor(new Color(64, 64, 64));
		g.drawString("Copy", 320, 427);
		g.drawString("Cut", 327, 462);
		g.drawString("Paste", 313, 497);
		g.setColor(new Color(192, 192, 192));
		g.setFont(new Font("Monospaced", Font.BOLD, 16));
		g.drawString("Absorb", 420, 554);
		g.setColor(Color.WHITE);
		g.drawString("Import", 8, 454);
		g.drawString("Options", 3, 489);
//undo
		g.fillRect(7, 514, 21, 2);
		g.drawLine(8, 516, 12, 520);
		g.drawLine(8, 513, 12, 509);
		g.drawLine(9, 516, 13, 520);
		g.drawLine(9, 513, 13, 509);
		g.drawLine(10, 516, 13, 519);
		g.drawLine(10, 513, 13, 510);
//redo
		g.fillRect(47, 514, 21, 2);
		g.drawLine(62, 509, 66, 513);
		g.drawLine(62, 520, 66, 516);
		g.drawLine(61, 509, 65, 513);
		g.drawLine(61, 520, 65, 516);
		g.drawLine(61, 510, 64, 513);
		g.drawLine(61, 519, 64, 516);
//store button
		g.drawLine(81, 508, 85, 512);
		g.drawLine(89, 516, 93, 520);
		g.drawLine(81, 520, 85, 516);
		g.drawLine(89, 512, 93, 508);
		g.fillRect(85, 509, 1, 3);
		g.fillRect(89, 509, 1, 3);
		g.fillRect(82, 512, 3, 1);
		g.fillRect(82, 516, 3, 1);
		g.fillRect(90, 512, 3, 1);
		g.fillRect(90, 516, 3, 1);
		g.fillRect(85, 517, 1, 3);
		g.fillRect(89, 517, 1, 3);
		g.fillRect(87, 514, 1, 1);
//copyimage changing
//rotate
//g.drawArc(PosX, PosY, Width, Height, StartAngle, ExtendAngle)
		g.drawArc(314, 514, 10, 10, 0, 270);
		g.drawLine(324, 520, 326, 518);
		g.drawLine(324, 520, 322, 518);
//vertical flip
		g.drawLine(345, 515, 345, 525);
		g.drawLine(345, 515, 343, 517);
		g.drawLine(345, 515, 347, 517);
		g.drawLine(345, 525, 343, 523);
		g.drawLine(345, 525, 347, 523);
//horizontal flip
		g.drawLine(365, 520, 375, 520);
		g.drawLine(365, 520, 367, 518);
		g.drawLine(365, 520, 367, 522);
		g.drawLine(375, 520, 373, 518);
		g.drawLine(375, 520, 373, 522);
//nudging
//nudge background
		g.setColor(Color.BLUE);
		g.fillRect(320, 540, 48, 48);
		g.setColor(Color.GREEN);
		g.drawLine(320, 540, 367, 587);
		g.drawLine(320, 587, 367, 540);
//nudge up
		g.setColor(new Color(192, 192, 192));
		g.fillRect(343, 542, 2, 17);
		g.drawLine(345, 543, 349, 547);
		g.drawLine(342, 543, 338, 547);
		g.drawLine(345, 544, 349, 548);
		g.drawLine(342, 544, 338, 548);
		g.drawLine(345, 545, 348, 548);
		g.drawLine(342, 545, 339, 548);
//nudge right
		g.fillRect(349, 563, 17, 2);
		g.drawLine(360, 558, 364, 562);
		g.drawLine(360, 569, 364, 565);
		g.drawLine(359, 558, 363, 562);
		g.drawLine(359, 569, 363, 565);
		g.drawLine(359, 559, 362, 562);
		g.drawLine(359, 568, 362, 565);
//nudge down
		g.fillRect(343, 569, 2, 17);
		g.drawLine(349, 580, 345, 584);
		g.drawLine(338, 580, 342, 584);
		g.drawLine(349, 579, 345, 583);
		g.drawLine(338, 579, 342, 583);
		g.drawLine(348, 579, 345, 582);
		g.drawLine(339, 579, 342, 582);
//nudge left
		g.fillRect(322, 563, 17, 2);
		g.drawLine(323, 565, 327, 569);
		g.drawLine(323, 562, 327, 558);
		g.drawLine(324, 565, 328, 569);
		g.drawLine(324, 562, 328, 558);
		g.drawLine(325, 565, 328, 568);
		g.drawLine(325, 562, 328, 559);
	}
	public void clickaction(MouseEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int posx = 0;
		int posy = 0;
//defer to options "panel"
		if (showoptions) {
			if (origin == 0) {
				evt.translatePoint(-165, -165);
				if (mousex >= 165 && mousex < 335 && mousey >= 165 && mousey < 435)
					optionsmousePressed(evt);
				else
					showoptions = false;
				origin = -1;
			}
//defere to panels "panel"
		} else if (showpanels) {
			if (origin == 0) {
				int panelsize = panels.length * 15 + 40;
				evt.translatePoint(-165, (panelsize - 600) / 2);
				if (mousex >= 165 && mousex < 335 && mousey >= (600 - panelsize) / 2 && mousey < (600 + panelsize) / 2)
					panelsmousePressed(evt);
				else
					showpanels = false;
				origin = -1;
			}
//switch panels
		} else if (mousex >= 400 && mousex < 500 && mousey >= 442 && mousey < 461 && origin == 0) {
			showpanels = true;
			origin = -1;
//drag the image
		} else if (mousex >= 100 && mousex < 300 && mousey >= 400 && mousey < 600 && (origin == 0 || origin == 4)) {
			if (origin == 4) {
				imagex = movex + mousex;
				imagey = movey + mousey;
			} else if (origin == 0) {
				movex = imagex - mousex;
				movey = imagey - mousey;
				origin = 4;
			}
//defer to other panel
		} else if (panelnum != 0) {
			try {
				click.invoke(panel, evt);
			} catch(Exception e) {
				System.out.println("" + e);
				e.printStackTrace();
			}
//main area
		} else if (mousex >= 0 && mousex < 400 && mousey >= 0 && mousey < 400 && (origin == 0 || origin == 1)) {
			origin = 1;
			posx = mousex / size;
			posy = mousey / size;
//draw
			if (action == 1) {
				if (mainimage != null) {
					int colorx = posx - imagex;
					int colory = posy - imagey;
					if (colorx < 0 || colorx >= mainimage.getWidth() || colory < 0 || colory >= mainimage.getHeight()) {
//Type 2: draw + resize: [oldw, oldh, oldx, oldy, drawx, drawy]
						int placex = Math.max(0, -colorx);
						int placey = Math.max(0, -colory);
						int mwidth = mainimage.getWidth();
						int mheight = mainimage.getHeight();
						redo = null;
						undo = new Link(2, undo);
						colorx = Math.max(0, colorx);
						colory = Math.max(0, colory);
						undo.ints = new int[] {mwidth, mheight, placex, placey, colorx, colory};
						BufferedImage tempimage = new BufferedImage(Math.max(mwidth + placex, colorx + 1), Math.max(mheight + placey, colory + 1), BufferedImage.TYPE_INT_ARGB);
						tempimage.setData(mainimage.getRaster().createTranslatedChild(placex, placey));
						mainimage = tempimage;
						imagex = Math.min(imagex, posx);
						imagey = Math.min(imagey, posy);
					} else if (mainimage.getRGB(colorx, colory) != (alpha << 24 | red << 16 | green << 8 | blue)) {
//Type 1: draw: [x, y, oldcolor]
						redo = null;
						undo = new Link(1, undo);
						undo.ints = new int[] {colorx, colory, mainimage.getRGB(colorx, colory)};
					}
					mainimage.setRGB(colorx, colory, alpha << 24 | red << 16 | green << 8 | blue);
				} else {
//Type 3: draw + new image: -
					redo = null;
					undo = new Link(3, undo);
					mainimage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
					imagex = posx;
					imagey = posy;
					mainimage.setRGB(0, 0, alpha << 24 | red << 16 | green << 8 | blue);
				}
//erase
			} else if (action == 2 && mainimage != null) {
				if (posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey) {
					int colorx = posx - imagex;
					int colory = posy - imagey;
					int mwidth = mainimage.getWidth();
					int mheight = mainimage.getHeight();
					int left = 0;
					int right = mwidth;
					int up = 0;
					int down = mheight;
					boolean leave = false;
					for (int y = 0; y < mheight; y += 1) {
						for (int x = 0; x < mwidth; x += 1) {
							if ((mainimage.getRGB(x, y) >> 24 & 255) > 0 && (x != colorx || y != colory)) {
								up = y;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					if (!leave) {
//Type 5: erase + nullify: [imagex, imagey, size] [image]
						redo = null;
						undo = new Link(5, undo);
						undo.ints = new int[] {imagex, imagey, size};
						undo.image = mainimage;
						mainimage = null;
						imagex = 0;
						imagey = 0;
						return;
					}
					leave = false;
					for (int y = mheight - 1; y >= 0; y -= 1) {
						for (int x = mwidth - 1; x >= 0; x -= 1) {
							if ((mainimage.getRGB(x, y) >> 24 & 255) > 0 && (x != colorx || y != colory)) {
								down = y + 1;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					leave = false;
					for (int x = 0; x < mwidth; x += 1) {
						for (int y = 0; y < mheight; y += 1) {
							if ((mainimage.getRGB(x, y) >> 24 & 255) > 0 && (x != colorx || y != colory)) {
								left = x;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					leave = false;
					for (int x = mwidth - 1; x >= 0; x -= 1) {
						for (int y = mheight - 1; y >= 0; y -= 1) {
							if ((mainimage.getRGB(x, y) >> 24 & 255) > 0 && (x != colorx || y != colory)) {
								right = x + 1;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					if (left != 0 || right != mwidth || up != 0 || down != mheight) {
//Type 4: erase + resize: [oldw, oldh, placex, placey, colorx, colory, oldcolor] [links:[x, y] [image]]
						if (left != 0) {
							undo = new Link(0, undo);
							undo.ints = new int[] {0, 0};
							undo.image = imagecopy(mainimage.getSubimage(0, 0, left, mheight));
						}
						if (right != mwidth) {
							undo = new Link(0, undo);
							undo.ints = new int[] {right, 0};
							undo.image = imagecopy(mainimage.getSubimage(right, 0, mwidth - right, mheight));
						}
						if (up != 0) {
							undo = new Link(0, undo);
							undo.ints = new int[] {left, 0};
							undo.image = imagecopy(mainimage.getSubimage(left, 0, right - left, up));
						}
						if (down != mheight) {
							undo = new Link(0, undo);
							undo.ints = new int[] {left, down};
							undo.image = imagecopy(mainimage.getSubimage(left, down, right - left, mheight - down));
						}
						redo = null;
						undo = new Link(4, undo);
						undo.ints = new int[] {mwidth, mheight, left, up, colorx, colory, mainimage.getRGB(colorx, colory)};
						mainimage.setRGB(colorx, colory, 0);
						mainimage = imagecopy(mainimage.getSubimage(left, up, right - left, down - up));
						imagex = imagex + left;
						imagey = imagey + up;
					} else if (mainimage.getRGB(colorx, colory) != 0) {
//Type 1: erase: [x, y, oldcolor]
						redo = null;
						undo = new Link(1, undo);
						undo.ints = new int[] {colorx, colory, mainimage.getRGB(colorx, colory)};
						mainimage.setRGB(colorx, colory, 0);
					}
				}
//absorb
			} else if (action == 3 && mainimage != null) {
				if (posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey) {
					if ((mainimage.getRGB(posx - imagex, posy - imagey) >> 24 & 255) > 0) {
						alpha = mainimage.getRGB(posx - imagex, posy - imagey) >> 24 & 255;
						red = mainimage.getRGB(posx - imagex, posy - imagey) >> 16 & 255;
						green = mainimage.getRGB(posx - imagex, posy - imagey) >> 8 & 255;
						blue = mainimage.getRGB(posx - imagex, posy - imagey) & 255;
					}
				}
//cutting + copying
			} else if ((action == 4 || action == 5) && mainimage != null) {
				if (!clicked) {
					clicked = true;
					initialy = posy;
					initialx = posx;
				}
				finaly = posy;
				finalx = posx;
				copyleft = Math.min(Math.max(Math.min(initialx, finalx) - imagex, 0), mainimage.getWidth() - 1);
				copyright = Math.max(Math.min(Math.max(initialx, finalx) + 1 - imagex, mainimage.getWidth()), 1);
				copytop = Math.min(Math.max(Math.min(initialy, finaly) - imagey, 0), mainimage.getHeight() - 1);
				copybottom = Math.max(Math.min(Math.max(initialy, finaly) + 1 - imagey, mainimage.getHeight()), 1);
				inbounds = true;
//pasting
			} else if (action == 6 && copyimage != null) {
				clicked = true;
				copytop = posy - imagey;
				copyleft = posx - imagex;
				copybottom = copytop + copyimage.getHeight();
				copyright = copyleft + copyimage.getWidth();
				inbounds = true;
			}
//select draw
		} else if (mousex >= 412 && mousex < 487 && mousey >= 465 && mousey < 495 && (origin == 0 || origin == 2)) {
			origin = 2;
			action = 1;
//select erase
		} else if (mousex >= 412 && mousex < 487 && mousey >= 500 && mousey < 530 && (origin == 0 || origin == 2)) {
			origin = 2;
			action = 2;
//select absorb
		} else if (mousex >= 412 && mousex < 487 && mousey >= 535 && mousey < 565 && (origin == 0 || origin == 2)) {
			origin = 2;
			action = 3;
//select copy
		} else if (mousex >= 310 && mousex < 385 && mousey >= 405 && mousey < 435 && (origin == 0 || origin == 2)) {
			origin = 2;
			action = 4;
//select cut
		} else if (mousex >= 310 && mousex < 385 && mousey >= 440 && mousey < 470 && (origin == 0 || origin == 2)) {
			origin = 2;
			action = 5;
//select paste
		} else if (mousex >= 310 && mousex < 385 && mousey >= 475 && mousey < 505 && (origin == 0 || origin == 2)) {
			origin = 2;
			action = 6;
//change color selection
		} else if (mousex >= 406 && mousex < 494 && (mousex - 406) % 24 < 16 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 < 16 && (origin == 0 || origin == 3)) {
			origin = 3;
			posx = (mousex - 406) / 24;
			posy = (mousey - 102) / 20;
			if (posx == 0) {
				if (posy == 0) {
					red = 255;
				} else {
					red = 256 - 16 * posy;
				}
			} else if (posx == 1) {
				if (posy == 0) {
					green = 255;
				} else {
					green = 256 - 16 * posy;
				}
			} else if (posx == 2) {
				if (posy == 0) {
					blue = 255;
				} else {
					blue = 256 - 16 * posy;
				}
			} else {
				if (posy == 0) {
					alpha = 255;
				} else {
					alpha = 256 - 16 * posy;
				}
			}
//save
		} else if (mousex >= 400 && mousex < 500 && mousey >= 570 && mousey < 600 && origin == 0 && mainimage != null)
			done = 1;
//reset
		else if (mousex >= 0 && mousex < 75 && mousey >= 400 && mousey < 430 && origin == 0) {
			imagex = 0;
			imagey = 0;
			origin = 101;
			greenrect = true;
//import
		} else if (mousex >= 0 && mousex < 75 && mousey >= 435 && mousey < 465 && origin == 0) {
			done = 2;
			origin = 102;
			greenrect = true;
//rotate
		} else if (mousex >= 310 && mousex < 330 && mousey >= 510 && mousey < 530 && origin == 0 && copyimage != null) {
			int cwidth = copyimage.getWidth();
			int cheight = copyimage.getHeight();
			BufferedImage tempimage = new BufferedImage(cheight, cwidth, BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < cheight; y += 1) {
				for (int x = 0; x < cwidth; x += 1) {
					tempimage.setRGB(cheight - y - 1, x, copyimage.getRGB(x, y));
				}
			}
			copyimage = tempimage;
			origin = 103;
			greenrect = true;
//vertical flip
		} else if (mousex >= 335 && mousex < 355 && mousey >= 510 && mousey < 530 && origin == 0 && copyimage != null) {
			int cwidth = copyimage.getWidth();
			int cheight = copyimage.getHeight();
			BufferedImage tempimage = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < cheight; y += 1) {
				for (int x = 0; x < cwidth; x += 1) {
					tempimage.setRGB(x, cheight - y - 1, copyimage.getRGB(x, y));
				}
			}
			copyimage = tempimage;
			origin = 104;
			greenrect = true;
//horizontal flip
		} else if (mousex >= 360 && mousex < 380 && mousey >= 510 && mousey < 530 && origin == 0 && copyimage != null) {
			int cwidth = copyimage.getWidth();
			int cheight = copyimage.getHeight();
			BufferedImage tempimage = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < cheight; y += 1) {
				for (int x = 0; x < cwidth; x += 1) {
					tempimage.setRGB(cwidth - x - 1, y, copyimage.getRGB(x, y));
				}
			}
			copyimage = tempimage;
			origin = 105;
			greenrect = true;
//nudge
		} else if (mousex >= 320 && mousex < 368 && mousey >= 540 && mousey < 588 && origin == 0) {
			posx = mousex - 320;
			posy = mousey - 540;
			if (posx > posy) {
				if (posx + posy < 47) {
					imagey = imagey - 1;
					origin = 106;
				} else if (posx + posy > 47) {
					imagex = imagex + 1;
					origin = 107;
				}
			} else if (posy > posx) {
				if (posx + posy < 47) {
					imagex = imagex - 1;
					origin = 108;
				} else if (posx + posy > 47) {
					imagey = imagey + 1;
					origin = 109;
				}
			}
			greenrect = true;
//options
		} else if (mousex >= 0 && mousex < 75 && mousey >= 470 && mousey < 500 && origin == 0) {
			showoptions = true;
			origin = -1;
//undo
		} else if (mousex >= 0 && mousex < 35 && mousey >= 505 && mousey < 525 && undo != null && origin == 0) {
			undo();
			origin = 110;
			greenrect = true;
//redo
		} else if (mousex >= 40 && mousex < 75 && mousey >= 505 && mousey < 525 && redo != null && origin == 0) {
			redo();
			origin = 111;
			greenrect = true;
		} else if (mousex >= 78 && mousex < 97 && mousey >= 505 && mousey < 524 && origin == 0) {
//store image + color
//Type 9: switch images: [imagex, imagey, size] [image]
			origin = 112;
			if (mainimage != null) {
				undo = new Link(9, undo);
				undo.ints = new int[] {imagex, imagey, size};
				undo.image = imagecopy(mainimage);
			}
			greenrect = true;
		} else
			inbounds = false;
		repaint();
	}
	public void mouseWheelMoved(MouseWheelEvent evt) {
		try{
			safemouseWheelMoved(evt);
		} catch(OutOfMemoryError e) {
			System.out.print("Out of memory; clearing event history,");
			undo = null;
			redo = null;
			System.out.print(" collecting garbage,");
			Runtime.getRuntime().gc();
			System.out.print(" and rescrolling.");
			safemouseWheelMoved(evt);
		}
	}
	public void safemouseWheelMoved(MouseWheelEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int add = evt.getWheelRotation();
		if (panelnum != 0) {
			try {
				scroll.invoke(panel, evt);
			} catch(Exception e) {
				System.out.println("" + e);
				e.printStackTrace();
			}
		} else if (mousex >= 406 && mousex < 494 && (mousex - 406) % 24 < 16 && mousey >= 102 && mousey < 438 && origin == 0) {
			int posx = (mousex - 406) / 24;
			if (posx == 0 && red - add < 256 && red - add >= 0)
				red = red - add;
			else if (posx == 1 && green - add <= 255 && green - add >= 0)
				green = green - add;
			else if (posx == 2 && blue - add <= 255 && blue - add >= 0)
				blue = blue - add;
			else if (posx == 3 && alpha - add <= 255 && alpha - add >= 0)
				alpha = alpha - add;
		} else if (mousex >= 0 && mousex < 400 && mousey >= 0 && mousey < 400 && origin == 0) {
			if (add == 1 && undo != null)
				undo();
			else if (add == -1 && redo != null)
				redo();
		} else if (mousex >= 320 && mousex < 368 && mousey >= 540 && mousey < 588 && origin == 0) {
			int posx = mousex - 320;
			int posy = mousey - 540;
			if (posx > posy) {
				if (posx + posy < 47) {
					imagey = imagey + add;
				} else if (posx + posy > 47) {
					imagex = imagex + add;
				}
			} else if (posy > posx) {
				if (posx + posy < 47) {
					imagex = imagex + add;
				} else if (posx + posy > 47) {
					imagey = imagey + add;
				}
			}
		}
		repaint();
	}
	public void undo() {
		if (undo.type == 1) {
//Type 1: draw: [x, y, oldcolor]
//Type 1: erase: [x, y, oldcolor]
//	interpret: the location and color of the old pixel
			redo = new Link(1, redo);
			redo.ints = new int[] {undo.ints[0], undo.ints[1], mainimage.getRGB(undo.ints[0], undo.ints[1])};
			mainimage.setRGB(undo.ints[0], undo.ints[1], undo.ints[2]);
			undo = undo.next;
		} else if (undo.type == 2) {
//Type 2: draw + resize: [oldw, oldh, oldx, oldy, drawx, drawy]
//	interpret: the size and location of the old image within the new image, the pixel location
			redo = new Link(2, redo);
			redo.ints = new int[] {undo.ints[4] - undo.ints[2], undo.ints[5] - undo.ints[3], mainimage.getRGB(undo.ints[4], undo.ints[5])};
			mainimage = imagecopy(mainimage.getSubimage(undo.ints[2], undo.ints[3], undo.ints[0], undo.ints[1]));
			imagex = imagex + undo.ints[2];
			imagey = imagey + undo.ints[3];
			undo = undo.next;
		} else if (undo.type == 3) {
//Type 3: draw + new image: -
//	interpret: a new image was created
			redo = new Link(3, redo);
			redo.ints = new int[] {imagex, imagey, mainimage.getRGB(0, 0), size};
			mainimage = null;
			imagex = 0;
			imagey = 0;
			undo = undo.next;
		} else if (undo.type == 4) {
//Type 4: erase + resize: [oldw, oldh, placex, placey, colorx, colory, oldcolor] [links:[x, y] [image]]
//	interpret: the old size, the location to place the new image on the old image, a chain
//		containing the locations and images that were erased
			redo = new Link(4, redo);
			redo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), undo.ints[2], undo.ints[3], undo.ints[4], undo.ints[5]};
			BufferedImage tempimage = new BufferedImage(undo.ints[0], undo.ints[1], BufferedImage.TYPE_INT_ARGB);
			tempimage.setData(mainimage.getRaster().createTranslatedChild(undo.ints[2], undo.ints[3]));
			mainimage = tempimage;
			mainimage.setRGB(undo.ints[4], undo.ints[5], undo.ints[6]);
			imagex = imagex - undo.ints[2];
			imagey = imagey - undo.ints[3];
			undo = undo.next;
			while (undo != null && undo.type == 0) {
				mainimage.setData(undo.image.getRaster().createTranslatedChild(undo.ints[0], undo.ints[1]));
				undo = undo.next;
			}
		} else if (undo.type == 5) {
//Type 5: erase + nullify: [imagex, imagey, size] [image]
//	interpret: the screen location and image that got deleted, plus the size
			redo = new Link(5, redo);
			undo.ints[2] = (undo.ints[2] - size) / 4 * 25;
			mainimage = undo.image;
			imagex = undo.ints[0] + undo.ints[2];
			imagey = undo.ints[1] + undo.ints[2];
			undo = undo.next;
		} else if (undo.type == 6) {
//Type 6: cut: [x, y] [image]
//Type 6: paste: [x, y] [image]
//	interpret: the location and image to restore
			redo = new Link(6, redo);
			redo.ints = new int[] {undo.ints[0], undo.ints[1]};
			redo.image = imagecopy(mainimage.getSubimage(undo.ints[0], undo.ints[1], undo.image.getWidth(), undo.image.getHeight()));
			mainimage.setData(undo.image.getRaster().createTranslatedChild(undo.ints[0], undo.ints[1]));
			undo = undo.next;
		} else if (undo.type == 7) {
//Type 7: paste + resize: [oldw, oldh, oldx, oldy, pastew, pasteh, pastex, pastey] [pasteimage]
//	interpret: the size and location of the old image within the new image, the size and
//		location of the new image, the image to revert
			redo = new Link(7, redo);
			redo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), undo.ints[2], undo.ints[3], undo.ints[6], undo.ints[7]};
			redo.image = imagecopy(mainimage.getSubimage(undo.ints[6], undo.ints[7], undo.ints[4], undo.ints[5]));
			mainimage = imagecopy(mainimage.getSubimage(undo.ints[2], undo.ints[3], undo.ints[0], undo.ints[1]));
			if (undo.image != null)
				mainimage.setData(undo.image.getRaster().createTranslatedChild(undo.ints[6], undo.ints[7]));
			imagex = imagex + undo.ints[2];
			imagey = imagey + undo.ints[3];
			undo = undo.next;
		} else if (undo.type == 8) {
//Type 8: paste + new image: -
//	interpret: a new image was created
			redo = new Link(8, redo);
			redo.ints = new int[] {imagex, imagey, size};
			redo.image = mainimage;
			mainimage = null;
			imagex = 0;
			imagey = 0;
			undo = undo.next;
		} else {
//Default: switch images: [imagex, imagey, size] [image]
//	interpret: the screen location and image to restore, plus the size
			redo = new Link(undo.type, redo);
			redo.ints = new int[] {imagex, imagey, size};
			redo.image = mainimage;
			undo.ints[2] = (undo.ints[2] - size) / 4 * 25;
			mainimage = undo.image;
			imagex = undo.ints[0] + undo.ints[2];
			imagey = undo.ints[1] + undo.ints[2];
			undo = undo.next;
		}
	}
	public void redo() {
		if (redo.type == 1) {
//Type 1: draw: [x, y, newcolor]
//Type 1: erase: [x, y, newcolor]
//	interpret: the location and color of the new pixel
			undo = new Link(1, undo);
			undo.ints = new int[] {redo.ints[0], redo.ints[1], mainimage.getRGB(redo.ints[0], redo.ints[1])};
			mainimage.setRGB(redo.ints[0], redo.ints[1], redo.ints[2]);
			redo = redo.next;
		} else if (redo.type == 2) {
//Type 2: draw + resize: [drawx, drawy, newcolor]
//	interpret: the location and color of the new pixel, the location of the image
			undo = new Link(2, undo);
			undo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), Math.max(-redo.ints[0], 0), Math.max(-redo.ints[1], 0), Math.max(0, redo.ints[0]), Math.max(0, redo.ints[1])};
			BufferedImage tempimage = new BufferedImage(Math.max(undo.ints[0] + undo.ints[2], undo.ints[4] + 1), Math.max(undo.ints[1] + undo.ints[3], undo.ints[5] + 1), BufferedImage.TYPE_INT_ARGB);
			tempimage.setData(mainimage.getRaster().createTranslatedChild(undo.ints[2], undo.ints[3]));
			mainimage = tempimage;
			imagex = imagex - undo.ints[2];
			imagey = imagey - undo.ints[3];
			mainimage.setRGB(undo.ints[4], undo.ints[5], redo.ints[2]);
			redo = redo.next;
		} else if (redo.type == 3) {
//Type 3: draw + new image: [imagex, imagey, newcolor, size]
//	interpret: the location and color of the new pixel, plus the size
			undo = new Link(3, undo);
			mainimage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			mainimage.setRGB(0, 0, redo.ints[2]);
			redo.ints[3] = (redo.ints[3] - size) / 4 * 25;
			imagex = redo.ints[0] + redo.ints[3];
			imagey = redo.ints[1] + redo.ints[3];
			redo = redo.next;
		} else if (redo.type == 4) {
//Type 4: erase + resize: [neww, newh, newx, newy, colorx, colory]
//	interpret: the location and size of the new image within the old image, the location of the
//		pixel to erase
			int mwidth = mainimage.getWidth();
			int mheight = mainimage.getHeight();
			int left = redo.ints[2];
			int right = left + redo.ints[0];
			int up = redo.ints[3];
			int down = up + redo.ints[1];
			if (left != 0) {
				undo = new Link(0, undo);
				undo.ints = new int[] {0, 0};
				undo.image = imagecopy(mainimage.getSubimage(0, 0, left, mheight));
			}
			if (right != mwidth) {
				undo = new Link(0, undo);
				undo.ints = new int[] {right, 0};
				undo.image = imagecopy(mainimage.getSubimage(right, 0, mwidth - right, mheight));
			}
			if (up != 0) {
				undo = new Link(0, undo);
				undo.ints = new int[] {left, 0};
				undo.image = imagecopy(mainimage.getSubimage(left, 0, redo.ints[0], up));
			}
			if (down != mheight) {
				undo = new Link(0, undo);
				undo.ints = new int[] {left, down};
				undo.image = imagecopy(mainimage.getSubimage(left, down, redo.ints[0], mheight - down));
			}
			undo = new Link(4, undo);
			undo.ints = new int[] {mwidth, mheight, left, up, redo.ints[4], redo.ints[5], mainimage.getRGB(redo.ints[4], redo.ints[5])};
			mainimage.setRGB(redo.ints[4], redo.ints[5], 0);
			mainimage = imagecopy(mainimage.getSubimage(left, up, redo.ints[0], redo.ints[1]));
			imagex = imagex + left;
			imagey = imagey + up;
			redo = redo.next;
		} else if (redo.type == 5) {
//Type 5: erase + nullify: -
//	interpret: set the image to null
			undo = new Link(5, undo);
			undo.ints = new int[] {imagex, imagey, size};
			undo.image = mainimage;
			mainimage = null;
			imagex = 0;
			imagey = 0;
			redo = redo.next;
		} else if (redo.type == 6) {
//Type 6: cut: [x, y] [image]
//Type 6: paste: [x, y] [image]
//	interpret: the location and image to set
			undo = new Link(6, undo);
			undo.ints = new int[] {redo.ints[0], redo.ints[1]};
			int rwidth = redo.image.getWidth();
			int rheight = redo.image.getHeight();
			undo.image = imagecopy(mainimage.getSubimage(redo.ints[0], redo.ints[1], rwidth, rheight));
			mainimage.setData(redo.image.getRaster().createTranslatedChild(redo.ints[0], redo.ints[1]));
			redo = redo.next;
		} else if (redo.type == 7) {
//Type 7: paste + resize: [neww, newh, placex, placey, pastex, pastey] [pasteimage]
//	interpret: the image size, the location of the old image within the new image, the location
//		of the image to paste, the image
			int mwidth = mainimage.getWidth();
			int mheight = mainimage.getHeight();
			int rwidth = redo.image.getWidth();
			int rheight = redo.image.getHeight();
			undo = new Link(7, undo);
			undo.ints = new int[] {mwidth, mheight, redo.ints[2], redo.ints[3], rwidth, rheight, redo.ints[4], redo.ints[5]};
			if (redo.ints[4] < mwidth + redo.ints[2] && redo.ints[5] < mheight + redo.ints[3] && redo.ints[4] + rwidth > redo.ints[2] && redo.ints[5] + rheight > redo.ints[3]) {
				int swidth = Math.min(mwidth - redo.ints[4], rwidth - redo.ints[2]);
				int sheight = Math.min(mheight - redo.ints[5], rheight - redo.ints[3]);
				undo.image = imagecopy(mainimage.getSubimage(redo.ints[4], redo.ints[5], swidth, sheight));
			}
			BufferedImage tempimage = new BufferedImage(redo.ints[0], redo.ints[1], BufferedImage.TYPE_INT_ARGB);
			tempimage.setData(mainimage.getRaster().createTranslatedChild(redo.ints[2], redo.ints[3]));
			tempimage.setData(redo.image.getRaster().createTranslatedChild(redo.ints[4], redo.ints[5]));
			mainimage = tempimage;
			imagex = imagex - redo.ints[2];
			imagey = imagey - redo.ints[3];
			redo = redo.next;
		} else if (redo.type == 8) {
//Type 8: paste + new image: [imagex, imagey, size] [image]
//	interpret: the screen location and color of the new image, plus the size
			undo = new Link(8, undo);
			mainimage = redo.image;
			redo.ints[2] = (redo.ints[2] - size) / 4 * 25;
			imagex = redo.ints[0] + redo.ints[2];
			imagey = redo.ints[1] + redo.ints[2];
			redo = redo.next;
		} else {
//Default: switch images: [imagex, imagey, size] [image]
//	interpret: the screen location and image to restore, plus the size
			undo = new Link(redo.type, undo);
			undo.ints = new int[] {imagex, imagey, size};
			undo.image = mainimage;
			redo.ints[2] = (redo.ints[2] - size) / 4 * 25;
			mainimage = redo.image;
			imagex = redo.ints[0] + redo.ints[2];
			imagey = redo.ints[1] + redo.ints[2];
			redo = redo.next;
		}
	}
	public static BufferedImage imagecopy(BufferedImage img) {
		if (img == null)
			return null;
		BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		temp.setData(img.getRaster());
		return temp;
	}
	public static void pasteon(BufferedImage a, int lx, int ly, BufferedImage b) {
		int pa = 0;
		int pb = 0;
		int aa = 0;
		int fa = 0;
		int br = 0;
		int bg = 0;
		int bb = 0;
		int[] argbs = null;
		int[] brgbs = null;
		int awidth = a.getWidth();
		int aheight = a.getHeight();
		for (int y = 0; y < aheight; y += 1) {
			for (int x = 0; x < awidth; x += 1) {
				pa = a.getRGB(x, y);
				pb = b.getRGB(lx + x, ly + y);
				aa = pa >> 24 & 255;
				fa = (255 - (255 - (pb >> 24 & 255)) * (255 - aa) / 255);
				if (fa != 0) {
					br = pb >> 16 & 255;
					bg = pb >> 8 & 255;
					bb = pb & 255;
					b.setRGB(lx + x, ly + y,
						fa << 24 |
						br + ((pa >> 16 & 255) - br) * aa / fa << 16 |
						bg + ((pa >> 8 & 255) - bg) * aa / fa << 8 |
						bb + ((pa & 255) - bb) * aa / fa);
				}
			}
		}
	}
	public void optionspaintComponent(Graphics g) {
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(0, 0, 500, 160);
		g.fillRect(0, 160, 160, 280);
		g.fillRect(340, 160, 160, 280);
		g.fillRect(0, 440, 500, 160);
		g.setColor(new Color(128, 128, 128));
		g.drawRect(164, 164, 171, 271);
		g.drawRect(163, 163, 173, 273);
		g.drawRect(162, 162, 175, 275);
		g.drawRect(161, 161, 177, 277);
		g.drawRect(160, 160, 179, 279);
		g.setColor(new Color(255, 192, 0));
//height = 15 * (16)rows + 10 + 20(save)
		g.fillRect(165, 165, 170, 270);
		g.translate(165, 185);
		g.setColor(new Color(128, 0, 0));
		g.fillRect(5, -15, 160, 20);
		g.setColor(Color.GREEN);
		g.drawRect(6, -14, 157, 17);
		g.setColor(Color.WHITE);
		g.fillRect(19, 21, 9, 9);
		g.fillRect(19, 51, 9, 9);
		g.fillOval(19, 81, 9, 9);
		g.fillOval(19, 96, 9, 9);
		g.fillOval(19, 111, 9, 9);
		g.fillOval(19, 141, 9, 9);
		g.fillOval(19, 156, 9, 9);
		g.fillOval(19, 171, 9, 9);
		g.fillOval(19, 186, 9, 9);
		g.fillOval(19, 216, 9, 9);
		g.fillOval(19, 231, 9, 9);
		g.setColor(new Color(96, 96, 96));
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		g.drawString("S  A  V  E", 50, -1);
		g.drawString("Stats:", 5, 15);
		g.drawString("    Stats On", 5, 30);
		g.drawString("Border:", 5, 45);
		g.drawString("    Border On", 5, 60);
		g.drawString("Xs:", 5, 75);
		g.drawString("    None", 5, 90);
		g.drawString("    Standard (Black)", 5, 105);
		g.drawString("    Custom Color", 5, 120);
		g.drawString("Background:", 5, 135);
		g.drawString("    Standard (Gray)", 5, 150);
		g.drawString("    White", 5, 165);
		g.drawString("    Black", 5, 180);
		g.drawString("    Custom Color", 5, 195);
		g.drawString("Pixel Size:", 5, 210);
		g.drawString("    8x8", 5, 225);
		g.drawString("    4x4", 5, 240);
		g.drawRect(18, 20, 10, 10);
		g.drawRect(18, 50, 10, 10);
		g.drawOval(19, 81, 9, 9);
		g.drawOval(19, 96, 9, 9);
		g.drawOval(19, 111, 9, 9);
		g.drawOval(19, 141, 9, 9);
		g.drawOval(19, 156, 9, 9);
		g.drawOval(19, 171, 9, 9);
		g.drawOval(19, 186, 9, 9);
		g.drawOval(19, 216, 9, 9);
		g.drawOval(19, 231, 9, 9);
		if (statson) {
			g.drawLine(20, 22, 26, 28);
			g.drawLine(21, 22, 26, 27);
			g.drawLine(20, 23, 25, 28);
			g.drawLine(20, 28, 26, 22);
			g.drawLine(20, 27, 25, 22);
			g.drawLine(21, 28, 26, 23);
		}
		if (border) {
			g.drawLine(20, 52, 26, 58);
			g.drawLine(21, 52, 26, 57);
			g.drawLine(20, 53, 25, 58);
			g.drawLine(20, 58, 26, 52);
			g.drawLine(20, 57, 25, 52);
			g.drawLine(21, 58, 26, 53);
		}
		g.fillRect(22, 144 + 15 * backsetting, 4, 4);
		g.fillRect(22, 84 + 15 * Xs, 4, 4);
		g.fillRect(22, 249 - 15 * (size / 4), 4, 4);
	}
	public void optionsmousePressed(MouseEvent evt) {
		int posy = evt.getY();
		if (posy >= 5 && posy < 25) {
			try {
				filer.newfile();
				filer.writeline("stats=" + statson);
				filer.writeline("border=" + border);
				if (Xs < 2)
					filer.writeline("Xs=" + Xs);
				else
					filer.writeline("Xs=\\" + (Xcolor.getRGB() & 16777215));
				if (backsetting < 3)
					filer.writeline("background=" + backsetting);
				else
					filer.writeline("background=\\" + (backcolor.getRGB() & 16777215));
				filer.write("size=" + size);
				filer.savefile();
				System.out.println("You successfully saved your settings!");
			} catch(Exception e) {
				System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be saved.");
			}
		} else {
			posy = (evt.getY() - 24) / 15;
			switch(posy) {
				case 1:
					statson = !statson;
					break;
				case 3:
					border = !border;
					break;
				case 5:
					Xs = 0;
					break;
				case 6:
					Xs = 1;
					Xcolor = Color.BLACK;
					break;
				case 7:
					Xs = 2;
					Xcolor = new Color(red, green, blue);
					break;
				case 9:
					backsetting = 0;
					backcolor = new Color(192, 192, 192);
					break;
				case 10:
					backsetting = 1;
					backcolor = Color.WHITE;
					break;
				case 11:
					backsetting = 2;
					backcolor = Color.BLACK;
					break;
				case 12:
					backsetting = 3;
					backcolor = new Color(red, green, blue);
					break;
				case 14:
					if (size == 4) {
						size = 8;
						imagex = imagex - 25;
						imagey = imagey - 25;
					}
					break;
				case 15:
					if (size == 8) {
						size = 4;
						imagex = imagex + 25;
						imagey = imagey + 25;
					}
					break;
			}
			if (posy > 4 && posy <  16 && posy != 8 && posy != 13)
				bgsetup(bgimage.createGraphics());
			repaint();
		}
	}
	public void panelspaintComponent(Graphics g) {
		int panelsize = panels.length * 15 + 40;
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(0, 0, 500, (590 - panelsize) / 2);
		g.fillRect(0, (590 - panelsize) / 2, 160, panelsize + 10);
		g.fillRect(340, (590 - panelsize) / 2, 160, panelsize + 10);
		g.fillRect(0, (610 + panelsize) / 2, 500, (591 - panelsize) / 2);
		g.setColor(new Color(128, 128, 128));
		g.drawRect(164, (598 - panelsize) / 2, 171, panelsize + 1);
		g.drawRect(163, (596 - panelsize) / 2, 173, panelsize + 3);
		g.drawRect(162, (594 - panelsize) / 2, 175, panelsize + 5);
		g.drawRect(161, (592 - panelsize) / 2, 177, panelsize + 7);
		g.drawRect(160, (590 - panelsize) / 2, 179, panelsize + 9);
		g.setColor(new Color(255, 192, 0));
		g.fillRect(165, (600 - panelsize) / 2, 170, panelsize);
		g.translate(165, (600 - panelsize) / 2);
		g.setColor(Color.WHITE);
		g.fillOval(19, 21, 9, 9);
		for (int y = 0; y < panels.length; y += 1) {
			g.fillOval(19, 36 + y * 15, 9, 9);
		}
		g.setColor(new Color(96, 96, 96));
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		g.drawOval(19, 21, 9, 9);
		g.drawString("Panels:", 5, 15);
		g.drawString("    Standard Panel", 5, 30);
		for (int y = 0; y < panels.length; y += 1) {
			g.drawOval(19, 36 + y * 15, 9, 9);
			g.drawString("    " + names[y], 5, 45 + y * 15);
		}
		g.fillRect(22, 24 + 15 * panelnum, 4, 4);
	}
	public void panelsmousePressed(MouseEvent evt) {
		int posy = (evt.getY() - 4) / 15 - 1;
		if (posy >= 0 && posy <= panels.length) {
			panelnum = posy;
			if (panelnum != 0) {
				panel = panels[panelnum - 1];
				paint = methods[panelnum - 1][0];
				click = methods[panelnum - 1][1];
				release = methods[panelnum - 1][2];
				scroll = methods[panelnum - 1][3];
			}
		}
		repaint();
	}
	public static class Link {
		int type = 0;
		Link next = null;
		int[] ints = null;
		BufferedImage image = null;
		public Link(int i, Link l) {
			type = i;
			next = l;
		}
	}
}