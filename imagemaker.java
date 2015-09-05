//Version 3
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;
import java.awt.Insets;
import java.awt.Dimension;
import java.util.Scanner;
import java.awt.Toolkit;
public class imagemaker extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, HierarchyBoundsListener {
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
	public static int readint(String line) {
		int digit = -1;
		int number = 0;
		boolean negative = false;
		int pos = 0;
		int length = line.length();
		while (pos < length) {
			digit = (int)(line.charAt(pos));
			if (digit >= 48 && digit < 58)
				number = number * 10 + (digit - 48);
			else if (digit == 45 && number == 0)
				negative = true;
			pos = pos + 1;
		}
		if (negative)
			return -number;
		return number;
	}
	private static Filer filer = new Filer("options.txt");
	private byte done = 0;
	public BufferedImage mainimage = null;
	public BufferedImage copyimage = null;
	private BufferedImage bgimage;
	private BufferedImage spimage = new BufferedImage(100, 19, BufferedImage.TYPE_INT_ARGB);
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
	private int statson = 0;
	private byte backsetting = 0;
	private Color backcolor = null;
	private boolean border = false;
	private boolean showoptions = false;
	private byte Xs = 1;
	private Color Xcolor = Color.BLACK;
	private int size = 8;
	private Edit undo = null;
	private Edit redo = null;
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
	private boolean resizable = false;
	private StandardPanel spanel = new StandardPanel();
	private byte display = 0;
	private Color standarddisplaycolor = new Color(255, 192, 0);
	private Color displaycolor = standarddisplaycolor;
	private int optionsize = 390;
	private int panelsize;
//getters
	public int getsize() {return size;}
	public int innerwidth() {return Math.max(400, width - 100);}
	public int innerheight() {return Math.max(400, height - 200);}
	public static void main(String[] args) {
		imagemaker thepanel = new imagemaker();
		String filename = "";
		//load image file
		if (args.length > 0) {
			filename = args[0];
			try{
				BufferedImage theimage = ImageIO.read(new File("images/" + filename + ".png"));
				int twidth = theimage.getWidth();
				int theight = theimage.getHeight();
				thepanel.mainimage = new BufferedImage(twidth, theight, BufferedImage.TYPE_INT_ARGB);
				thepanel.mainimage.setRGB(0, 0, twidth, theight, theimage.getRGB(0, 0, twidth, theight, null, 0, twidth), 0, twidth);
			} catch(Exception e) {
				System.out.println("File \"" + filename + "\" could not be loaded. Continuing without file.");
			}
		}
		JFrame window = new JFrame("Image Maker");
		window.setContentPane(thepanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		Insets insets = window.getInsets();
		window.setSize(thepanel.width + insets.left + insets.right, thepanel.height + insets.top + insets.bottom);
		window.setMinimumSize(new Dimension(window.getWidth(), window.getHeight()));
		window.setLocation((screenwidth() - thepanel.width) / 2 - insets.left, (screenheight() - thepanel.height) / 2 - insets.top);
		thepanel.requestFocus();
		window.toFront();
		thepanel.resizable = true;
		//wait for the panel to do something
		while (thepanel.done == 0) {
			while (thepanel.done == 0) {
				try {
					Thread.sleep(10);
				} catch(Exception e) {
				}
			}
			//importing
			if (thepanel.done == 2) {
				System.out.println("Enter the name of the file you wish to import to the copy image:");
				try {
					thepanel.copyimage = ImageIO.read(new File("images/" + line() + ".png"));
					System.out.println("The image has been imported.  Press \"PASTE\" to place it.");
				} catch(Exception e) {
					System.out.println("Sorry, your file could not be loaded.");
				}
				thepanel.done = 0;
				thepanel.requestFocus();
				window.toFront();
			}
		}
		//saving the image
		window.dispose();
		if (filename.length() < 1)
			System.out.println("Enter the name of the file you wish to save:");
		else
			System.out.println("Enter the name that you wish to save \"" + filename + "\" as:");
		filename = line();
		try {
			ImageIO.write(thepanel.mainimage, "png", new File("images/" + filename + ".png"));
			System.out.println("You successfully saved your image!");
		} catch(Exception e) {
			try {
				ImageIO.write(thepanel.mainimage, "png", new File("image.png"));
				System.out.println("You were unable to save \"images/" + filename + ".png\", but \"image.png\" was successfully saved.");
			} catch(Exception f) {
				System.out.println("Sorry, an error occured:\n" + f + "\nYour image could not be saved.");
			}
		}
	}
	public imagemaker() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addHierarchyBoundsListener(this);
		//get options from file
		if (filer.fileisthere()) {
			filer.readfile();
			String[] lines = filer.getlines();
			String line;
			for (int pos = 0; pos < lines.length; pos += 1) {
				line = lines[pos];
				if (line.startsWith("stats="))
					statson = readint(line.substring(6, line.length()));
				else if (line.startsWith("border="))
					border = line.endsWith("true");
				else if (line.startsWith("Xs=\\")) {
					Xs = 2;
					Xcolor = new Color(readint(line.substring(4, line.length())));
				} else if (line.startsWith("Xs="))
					Xs = (byte)(readint(line.substring(3, line.length())));
				else if (line.startsWith("background=\\")) {
					backsetting = 3;
					backcolor = new Color(readint(line.substring(12, line.length())));
				} else if (line.startsWith("background=")) {
					backsetting = (byte)(readint(line.substring(11, line.length())));
					if (backsetting == 1)
						backcolor = Color.WHITE;
					else if (backsetting == 2)
						backcolor = Color.BLACK;
				} else if (line.startsWith("size="))
					size = readint(line.substring(5, line.length()));
				else if (line.startsWith("display=\\")) {
					display = 1;
					displaycolor = new Color(readint(line.substring(9, line.length())));
				}
			}
		}
		setBackground(displaycolor);
		//get the addon panels
		File addons = new File("addons");
		File[] panelfiles = addons.listFiles();
		if (panelfiles == null)
			panelfiles = new File[0];
		String name = "";
		int count = 0;
		boolean[] goodfile = new boolean[panelfiles.length];
		Object[] allpanels = new Object[panelfiles.length];
		Method[][] allmethods = new Method[panelfiles.length][4];
		String[] allnames = new String[panelfiles.length];
		try {
			//find class files
			URLClassLoader urlcl = new URLClassLoader(new URL[] {addons.toURI().toURL()});
			Class<?> theclass = null;
			for (int spot = 0; spot < panelfiles.length; spot += 1) {
				name = panelfiles[spot].getName();
				//try to add the class to the list
				if (name.endsWith(".class")) {
					try {
						theclass = urlcl.loadClass(name.substring(0, name.length() - 6));
						allmethods[spot][0] = theclass.getMethod("paint", Graphics.class);
						allmethods[spot][1] = theclass.getMethod("click", MouseEvent.class);
						allmethods[spot][2] = theclass.getMethod("release", MouseEvent.class);
						allmethods[spot][3] = theclass.getMethod("scroll", MouseWheelEvent.class);
						allpanels[spot] = theclass.getConstructor(imagemaker.class).newInstance(this);
						allnames[spot] = (String)(theclass.getMethod("name").invoke(allpanels[spot]));
						goodfile[spot] = true;
						count += 1;
					} catch(Exception e) {
					}
				}
			}
			//update the panels list with all available panels
			panels = new Object[count];
			methods = new Method[count][0];
			names = new String[count];
			count = 0;
			for (int spot = 0; spot < goodfile.length; spot += 1) {
				if (goodfile[spot]) {
					panels[count] = allpanels[spot];
					methods[count] = allmethods[spot];
					names[count] = allnames[spot];
					count += 1;
				}
			}
		} catch(Exception e) {
		}
		panelsize = panels.length * 15 + 40;
		bgsetup();
//switch panels button
		Graphics g = spimage.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 19);
		g.setColor(Color.BLACK);
		g.drawRect(1, 1, 97, 16);
		g.setColor(new Color(64, 64, 64));
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		g.drawString("Switch Panels", 5, 14);
	}
	public void paintComponent(Graphics g) {
		try {
			safepaintComponent(g);
		} catch(OutOfMemoryError e) {
			cleanup(" and repainting.");
			safepaintComponent(g);
		}
	}
	public void mousePressed(MouseEvent evt) {
		try {
			clickaction(evt);
		} catch(OutOfMemoryError e) {
			cleanup(" and reclicking.");
			clickaction(evt);
		}
		repaint();
	}
	public void mouseReleased(MouseEvent evt) {
		try {
			safemouseReleased(evt);
		} catch(OutOfMemoryError e) {
			cleanup(" and rereleasing.");
			safemouseReleased(evt);
		}
		repaint();
	}
	public void mouseDragged(MouseEvent evt) {
		mousePressed(evt);
	}
	public void mouseWheelMoved(MouseWheelEvent evt) {
		try{
			safemouseWheelMoved(evt);
		} catch(OutOfMemoryError e) {
			cleanup(" and rescrolling.");
			safemouseWheelMoved(evt);
		}
		repaint();
	}
	public void ancestorResized(HierarchyEvent evt) {
		if (!resizable || evt.getID() != HierarchyEvent.ANCESTOR_RESIZED)
			return;
		JFrame window = (JFrame)(getTopLevelAncestor());
		Insets insets = window.getInsets();
		width = window.getWidth() - insets.left - insets.right;
		height = window.getHeight() - insets.top - insets.bottom;
		bgsetup();
		repaint();
	}
	public void mouseMoved(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void ancestorMoved(HierarchyEvent evt) {}
	public void bgsetup() {
		int innerwidth = innerwidth();
		int innerheight = innerheight();
		int innerwidthmod = innerwidth - innerwidth % size;
		int innerheightmod = innerheight - innerheight % size;
//big background
		bgimage = new BufferedImage(innerwidthmod, innerheightmod, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bgimage.createGraphics();
		if (backcolor == null) {
			g.setColor(new Color(192, 192, 192));
			g.fillRect(0, 0, innerwidthmod, innerheightmod);
		} else {
			g.setColor(backcolor);
			g.fillRect(0, 0, innerwidthmod, innerheightmod);
		}
		if (Xs != 0) {
			g.setColor(Xcolor);
			if (size == 16) {
				for (int y = 0; y < innerheightmod; y += 16) {
					for (int x = 0; x < innerwidthmod; x += 16) {
						g.fillRect(x + 2, y + 2, 2, 2);
						g.fillRect(x + 12, y + 2, 2, 2);
						g.fillRect(x + 4, y + 4, 2, 2);
						g.fillRect(x + 10, y + 4, 2, 2);
						g.fillRect(x + 6, y + 6, 4, 4);
						g.fillRect(x + 4, y + 10, 2, 2);
						g.fillRect(x + 10, y + 10, 2, 2);
						g.fillRect(x + 2, y + 12, 2, 2);
						g.fillRect(x + 12, y + 12, 2, 2);
					}
				}
			} else if (size == 8) {
				for (int y = 0; y < innerheightmod; y += 8) {
					for (int x = 0; x < innerwidthmod; x += 8) {
						g.drawLine(x + 1, y + 1, x + 6, y + 6);
						g.drawLine(x + 1, y + 6, x + 6, y + 1);
					}
				}
			} else if (size == 4) {
				for (int y = 0; y < innerheightmod; y += 4) {
					for (int x = 0; x < innerwidthmod; x += 4) {
						g.drawLine(x + 1, y + 1, x + 2, y + 2);
					}
				}
			} else if (size == 2) {
				for (int y = 0; y < innerheightmod; y += 2) {
					for (int x = 0; x < innerwidthmod; x += 2) {
						g.fillRect(x, y, 1, 1);
					}
				}
			}
		}
	}
	//clear undo/redo memory if memory ran out
	public void cleanup(String s) {
		System.out.print("Out of memory; clearing event history,");
		undo = null;
		redo = null;
		System.out.print(" collecting garbage,");
		Runtime.getRuntime().gc();
		System.out.println(s);
	}
	public void safepaintComponent(Graphics g) {
		super.paintComponent(g);
		int innerwidth = innerwidth();
		int innerheight = innerheight();
		//draw the big background, tiny background, and switch panels button
		g.drawImage(bgimage, 0, 0, null);
		g.drawImage(spimage, innerwidth, 442, null);
		if (backcolor == null) {
			g.setColor(new Color(128, 128, 128));
			g.fillRect(100, innerheight, 200, 200);
		} else {
			g.setColor(backcolor);
			g.fillRect(100, innerheight, 200, 200);
		}
		int iwidth = 0;
		int iheight = 0;
		int bigwidth = innerwidth() / size;
		int bigheight = innerheight() / size;
		int smallminx = Math.min(0, bigwidth / 2 - 100);
		int smallminy = Math.min(0, bigheight / 2 - 100);
		int smallmaxx = smallminx + 200;
		int smallmaxy = smallminy + 200;
//drawing the image
		if (mainimage != null) {
			iwidth = mainimage.getWidth();
			iheight = mainimage.getHeight();
			//draw the image in the small area
			if (imagex + iwidth > smallminx && imagex < smallmaxx && imagey + iheight > smallminy && imagey < smallmaxy)
				g.drawImage(
					mainimage,
					Math.max(0, imagex - smallminx) + 100,
					Math.max(0, imagey - smallminy) + innerheight,
					Math.min(200, imagex + iwidth - smallminx) + 100,
					Math.min(200, imagey + iheight - smallminy) + innerheight,
					Math.max(0, smallminx - imagex),
					Math.max(0, smallminy - imagey),
					Math.min(iwidth, smallmaxx - imagex),
					Math.min(iheight, smallmaxy - imagey),
					null);
			//draw the image in the big area
			if (imagex + iwidth > 0 && imagex < bigwidth && imagey + iheight > 0 && imagey < bigheight)
				g.drawImage(
					mainimage,
					Math.max(0, imagex * size),
					Math.max(0, imagey * size),
					Math.min(bigwidth * size, (iwidth + imagex) * size),
					Math.min(bigheight * size, (iheight + imagey) * size),
					Math.max(0, -imagex),
					Math.max(0, -imagey),
					Math.min(iwidth, bigwidth - imagex),
					Math.min(iheight, bigheight - imagey),
					null);
//borders
			if (border) {
				int left;
				int right;
				int top;
				int bottom;
				int dist;
				g.setColor(Color.YELLOW);
//left small, contains corners
				if (imagex > smallminx && imagex <= smallmaxx && imagey >= smallminy - iheight && imagey <= smallmaxy + 1) {
					top = Math.max(imagey - 1 - smallminy, 0);
					dist = Math.min(imagey + iheight - smallminy + 1, 200) - top;
					left = imagex + 99 - smallminx;
					g.fillRect(left, top + innerheight, 1, dist);
				}
//right small, contains corners
				if (imagex >= smallminx - iwidth && imagex < smallmaxx - iwidth && imagey >= smallminy - iheight && imagey <= smallmaxy + 1) {
					top = Math.max(imagey - 1 - smallminy, 0);
					dist = Math.min(imagey + iheight - smallminy + 1, 200) - top;
					right = imagex + iwidth + 100 - smallminx;
					g.fillRect(right, top + innerheight, 1, dist);
				}
//top small
				if (imagex > smallminx - iwidth && imagex < smallmaxx && imagey > smallminy && imagey <= smallmaxy) {
					left = Math.max(imagex - smallminx, 0);
					dist = Math.min(imagex + iwidth - smallminx, 200) - left;
					top = imagey - smallminy - 1 + innerheight;
					g.fillRect(left + 100, top, dist, 1);
				}
//bottom small
				if (imagex > smallminx - iwidth && imagex < smallmaxx && imagey >= smallminy - iheight && imagey < smallmaxy - iheight) {
					left = Math.max(imagex - smallminx, 0);
					dist = Math.min(imagex + iwidth - smallminx, 200) - left;
					bottom = imagey + iheight - smallminy + innerheight;
					g.fillRect(left + 100, bottom, dist, 1);
				}
				int msize = size / 4;
				int msize2 = msize * 2;
				int msize3 = msize * 3;
//left large
				if (imagex > 0 && imagex <= bigwidth && imagey > -iheight && imagey < bigheight) {
					top = Math.max(imagey, 0) * size;
					dist = Math.min(imagey + iheight, bigheight) * size - top;
					left = imagex * size - size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, top, msize, dist);
						g.fillRect(left + msize3, top, msize, dist);
						g.setColor(Color.GREEN);
						g.fillRect(left + msize, top, msize2, dist);
					} else
						g.fillRect(left, top, size, dist);
				}
//right large
				if (imagex >= -iwidth && imagex < bigwidth - iwidth && imagey > -iheight && imagey < bigheight) {
					top = Math.max(imagey, 0) * size;
					dist = Math.min(imagey + iheight, bigheight) * size - top;
					right = (imagex + iwidth) * size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(right, top, msize, dist);
						g.fillRect(right + msize3, top, msize, dist);
						g.setColor(Color.GREEN);
						g.fillRect(right + msize, top, msize2, dist);
					} else
						g.fillRect(right, top, size, dist);
				}
//top large
				if (imagey > 0 && imagey <= bigheight && imagex > -iwidth && imagex < bigwidth) {
					left = Math.max(imagex, 0) * size;
					dist = Math.min(imagex + iwidth, bigwidth) * size - left;
					top = imagey * size - size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, top, dist, msize);
						g.fillRect(left, top + msize3, dist, msize);
						g.setColor(Color.GREEN);
						g.fillRect(left, top + msize, dist, msize2);
					} else
						g.fillRect(left, top, dist, size);
				}
//bottom large
				if (imagey >= -iheight && imagey < bigheight - iheight && imagex > -iwidth && imagex < bigwidth) {
					left = Math.max(imagex, 0) * size;
					dist = Math.min(imagex + iwidth, bigwidth) * size - left;
					bottom = (imagey + iheight) * size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, bottom, dist, msize);
						g.fillRect(left, bottom + msize3, dist, msize);
						g.setColor(Color.GREEN);
						g.fillRect(left, bottom + msize, dist, msize2);
					} else
						g.fillRect(left, bottom, dist, size);
				}
//topleft large
				if (imagex > 0 && imagex <= bigwidth && imagey > 0 && imagey <= bigheight) {
					left = (imagex - 1) * size;
					top = (imagey - 1) * size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, top, size, msize);
						g.fillRect(left, top + msize, msize, msize3);
						g.fillRect(left + msize3, top + msize3, msize, msize);
						g.setColor(Color.GREEN);
						g.fillRect(left + msize, top + msize, msize3, msize2);
						g.fillRect(left + msize, top + msize3, msize2, msize);
					} else
						g.fillRect(left, top, size, size);
				}
//topright large
				if (imagex >= -iwidth && imagex < bigwidth - iwidth && imagey > 0 && imagey <= bigheight) {
					left = (imagex + iwidth) * size;
					top = (imagey - 1) * size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, top, size, msize);
						g.fillRect(left + msize3, top + msize, msize, msize3);
						g.fillRect(left, top + msize3, msize, msize);
						g.setColor(Color.GREEN);
						g.fillRect(left, top + msize, msize3, msize2);
						g.fillRect(left + msize, top + msize3, msize2, msize);
					} else
						g.fillRect(left, top, size, size);
				}
//bottomleft large
				if (imagex > 0 && imagex <= bigwidth && imagey >= -iheight && imagey < bigheight - iheight) {
					left = (imagex - 1) * size;
					top = (imagey + iheight) * size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, top + msize3, size, msize);
						g.fillRect(left, top, msize, msize3);
						g.fillRect(left + msize3, top, msize, msize);
						g.setColor(Color.GREEN);
						g.fillRect(left + msize, top + msize, msize3, msize2);
						g.fillRect(left + msize, top, msize2, msize);
					} else
						g.fillRect(left, top, size, size);
				}
//bottomright large
				if (imagex >= -iwidth && imagex < bigwidth - iwidth && imagey >= -iheight && imagey < bigheight - iheight) {
					left = (imagex + iwidth) * size;
					top = (imagey + iheight) * size;
					if (size >= 4) {
						g.setColor(Color.RED);
						g.fillRect(left, top + msize3, size, msize);
						g.fillRect(left + msize3, top, msize, msize3);
						g.fillRect(left, top, msize, msize);
						g.setColor(Color.GREEN);
						g.fillRect(left, top + msize, msize3, msize2);
						g.fillRect(left + msize, top, msize2, msize);
					} else
						g.fillRect(left, top, size, size);
				}
			}
		}
//movement box shadow
		int smallborder2x = Math.max(0, 200 + smallminx - bigwidth);
		int smallborder2y = Math.max(0, 200 + smallminy - bigheight);
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(100, innerheight, 200, -smallminy);
		g.fillRect(100, innerheight - smallminy, -smallminx, 200 + smallminy - smallborder2y);
		g.fillRect(300 - smallborder2x, innerheight - smallminy, smallborder2x, 200 + smallminy - smallborder2y);
		g.fillRect(100, innerheight + 200 - smallborder2y, 200, smallborder2y);
//defer to panel
		if (panelnum == 0)
			spanel.paint(g);
		else {
			try {
				paint.invoke(panel, g);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
//stats
		if (statson > 0) {
			g.setFont(new Font("Monospaced", Font.BOLD, 12));
			g.setColor(new Color(64, 64, 64));
			if (mainimage == null) {
				g.drawString("W:", 4, 137 + innerheight);
				g.drawString("H:", 4, 152 + innerheight);
				g.drawString("X:", 4, 167 + innerheight);
				g.drawString("Y:", 4, 182 + innerheight);
			} else if (statson == 2) {
				g.drawString("W:" + Integer.toHexString(iwidth).toUpperCase(), 4, 137 + innerheight);
				g.drawString("H:" + Integer.toHexString(iheight).toUpperCase(), 4, 152 + innerheight);
				g.drawString("X:" + Integer.toString(imagex, 16).toUpperCase(), 4, 167 + innerheight);
				g.drawString("Y:" + Integer.toString(imagey, 16).toUpperCase(), 4, 182 + innerheight);
			} else {
				g.drawString("W:" + iwidth, 4, 137 + innerheight);
				g.drawString("H:" + iheight, 4, 152 + innerheight);
				g.drawString("X:" + imagex, 4, 167 + innerheight);
				g.drawString("Y:" + imagey, 4, 182 + innerheight);
			}
			if (statson == 2) {
				g.setColor(Color.RED);
				g.drawString(Integer.toHexString(red).toUpperCase(), 2, 197 + innerheight);
				g.setColor(Color.GREEN);
				g.drawString(Integer.toHexString(green).toUpperCase(), 27, 197 + innerheight);
				g.setColor(Color.BLUE);
				g.drawString(Integer.toHexString(blue).toUpperCase(), 52, 197 + innerheight);
				g.setColor(Color.WHITE);
				g.drawString(Integer.toHexString(alpha).toUpperCase(), 77, 197 + innerheight);
			} else {
				g.setColor(Color.RED);
				g.drawString(red + "", 2, 197 + innerheight);
				g.setColor(Color.GREEN);
				g.drawString(green + "", 27, 197 + innerheight);
				g.setColor(Color.BLUE);
				g.drawString(blue + "", 52, 197 + innerheight);
				g.setColor(Color.WHITE);
				g.drawString(alpha + "", 77, 197 + innerheight);
			}
		}
//options "panel"
		if (showoptions)
			optionspaintComponent(g);
//panels "panel"
		else if (showpanels)
			panelspaintComponent(g);
	}
	public void clickaction(MouseEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int innerwidth = innerwidth();
		int innerheight = innerheight();
//defer to options "panel"
		if (showoptions) {
			if (origin == 0) {
				int newx = (innerwidth - 70) / 2;
				int newy = (innerheight + 200 - optionsize) / 2;
				if (mousex >= newx && mousex < newx + 170 && mousey >= newy && mousey < newy + optionsize) {
					evt.translatePoint(-newx, -newy);
					optionsmousePressed(evt);
					origin = 5;
				} else {
					showoptions = false;
					origin = -1;
				}
			}
//defere to panels "panel"
		} else if (showpanels) {
			int newx = (innerwidth - 70) / 2;
			int newy = (innerheight + 200 - panelsize) / 2;
			if (mousex >= newx && mousex < newx + 170 && mousey >= newy && mousey < newy + panelsize) {
				if (origin == 0 || origin == 5) {
					evt.translatePoint(-newx, -newy);
					panelsmousePressed(evt);
					origin = 5;
				}
			} else if (origin == 0) {
				showpanels = false;
				origin = -1;
			}
//switch panels
		} else if (mousex >= innerwidth && mousex < innerwidth + 100 && mousey >= 442 && mousey < 461 && origin == 0) {
			showpanels = true;
			origin = -1;
//drag the image
		} else if (mousex >= 100 && mousex < 300 && mousey >= innerheight && mousey < innerheight + 200 && (origin == 0 || origin == 4)) {
			if (origin == 4) {
				imagex = movex + mousex;
				imagey = movey + mousey;
			} else if (origin == 0) {
				movex = imagex - mousex;
				movey = imagey - mousey;
				origin = 4;
			}
//defer to panel
		} else if (origin == 0 || origin == 5) {
			origin = 5;
			if (panelnum == 0)
				spanel.click(evt);
			else {
				try {
					click.invoke(panel, evt);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void safemouseReleased(MouseEvent evt) {
//defer to panel
		if (panelnum == 0)
			spanel.release(evt);
		else if (panelnum != 0) {
			try {
				release.invoke(panel, evt);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		origin = 0;
	}
	public void safemouseWheelMoved(MouseWheelEvent evt) {
		int mousex = evt.getX();
		int mousey = evt.getY();
		int innerwidth = innerwidth();
		if (mousex >= innerwidth && mousex < innerwidth + 100 && mousey >= 442 && mousey < 461) {
			int add = evt.getWheelRotation();
			panelnum = Math.max(0, Math.min(panels.length, panelnum + add));
			if (panelnum != 0) {
				int pnum = panelnum - 1;
				panel = panels[pnum];
				paint = methods[pnum][0];
				click = methods[pnum][1];
				release = methods[pnum][2];
				scroll = methods[pnum][3];
				if (mainimage != null && (undo == null || undo.type != 9)) {
//Type 9: switch images: [imagex, imagey] [image]
					redo = null;
					undo = new Edit(9, undo);
					undo.ints = new int[] {imagex, imagey};
					undo.image = imagecopy(mainimage);
				}
			}
//defer to panel
		} else if (panelnum == 0)
			spanel.scroll(evt);
		else if (panelnum != 0) {
			try {
				scroll.invoke(panel, evt);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void undo() {
		if (undo.type == 1) {
//Type 1: draw: [x, y, oldcolor]
//Type 1: erase: [x, y, oldcolor]
//	interpret: the location and color of the old pixel
			redo = new Edit(1, redo);
			redo.ints = new int[] {undo.ints[0], undo.ints[1], mainimage.getRGB(undo.ints[0], undo.ints[1])};
			mainimage.setRGB(undo.ints[0], undo.ints[1], undo.ints[2]);
			undo = undo.next;
		} else if (undo.type == 2) {
//Type 2: draw + resize: [oldw, oldh, oldx, oldy, drawx, drawy]
//	interpret: the size and location of the old image within the new image, the pixel location
			redo = new Edit(2, redo);
			redo.ints = new int[] {undo.ints[4] - undo.ints[2], undo.ints[5] - undo.ints[3], mainimage.getRGB(undo.ints[4], undo.ints[5])};
			mainimage = imagecopy(mainimage.getSubimage(undo.ints[2], undo.ints[3], undo.ints[0], undo.ints[1]));
			imagex += undo.ints[2];
			imagey += undo.ints[3];
			undo = undo.next;
		} else if (undo.type == 3) {
//Type 3: draw + new image: -
//	interpret: a new image was created
			redo = new Edit(3, redo);
			redo.ints = new int[] {imagex, imagey, mainimage.getRGB(0, 0)};
			mainimage = null;
			imagex = 0;
			imagey = 0;
			undo = undo.next;
		} else if (undo.type == 4) {
//Type 4: erase + resize: [oldw, oldh, placex, placey, colorx, colory, oldcolor] [links:[x, y] [image]]
//	interpret: the old size, the location to place the new image on the old image, a chain
//		containing the locations and images that were erased
			redo = new Edit(4, redo);
			redo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), undo.ints[2], undo.ints[3], undo.ints[4], undo.ints[5]};
			BufferedImage tempimage = new BufferedImage(undo.ints[0], undo.ints[1], BufferedImage.TYPE_INT_ARGB);
			tempimage.setData(mainimage.getRaster().createTranslatedChild(undo.ints[2], undo.ints[3]));
			mainimage = tempimage;
			mainimage.setRGB(undo.ints[4], undo.ints[5], undo.ints[6]);
			imagex -= undo.ints[2];
			imagey -= undo.ints[3];
			undo = undo.next;
			while (undo != null && undo.type == 0) {
				mainimage.setData(undo.image.getRaster().createTranslatedChild(undo.ints[0], undo.ints[1]));
				undo = undo.next;
			}
		} else if (undo.type == 5) {
//Type 5: erase + nullify: [imagex, imagey] [image]
//	interpret: the screen location and image that got deleted
			redo = new Edit(5, redo);
			mainimage = undo.image;
			imagex = undo.ints[0];
			imagey = undo.ints[1];
			undo = undo.next;
		} else if (undo.type == 6) {
//Type 6: cut: [x, y] [image]
//Type 6: paste: [x, y] [image]
//	interpret: the location and image to restore
			redo = new Edit(6, redo);
			redo.ints = new int[] {undo.ints[0], undo.ints[1]};
			redo.image = imagecopy(mainimage.getSubimage(undo.ints[0], undo.ints[1], undo.image.getWidth(), undo.image.getHeight()));
			mainimage.setData(undo.image.getRaster().createTranslatedChild(undo.ints[0], undo.ints[1]));
			undo = undo.next;
		} else if (undo.type == 7) {
//Type 7: paste + resize: [oldw, oldh, oldx, oldy, pastew, pasteh, pastex, pastey] [pasteimage]
//	interpret: the size and location of the old image within the new image, the size and
//		location of the new image, the image to revert
			redo = new Edit(7, redo);
			redo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), undo.ints[2], undo.ints[3], undo.ints[6], undo.ints[7]};
			redo.image = imagecopy(mainimage.getSubimage(undo.ints[6], undo.ints[7], undo.ints[4], undo.ints[5]));
			mainimage = imagecopy(mainimage.getSubimage(undo.ints[2], undo.ints[3], undo.ints[0], undo.ints[1]));
			if (undo.image != null)
				mainimage.setData(undo.image.getRaster().createTranslatedChild(undo.ints[6], undo.ints[7]));
			imagex += undo.ints[2];
			imagey += undo.ints[3];
			undo = undo.next;
		} else if (undo.type == 8) {
//Type 8: paste + new image: -
//	interpret: a new image was created
			redo = new Edit(8, redo);
			redo.ints = new int[] {imagex, imagey};
			redo.image = mainimage;
			mainimage = null;
			imagex = 0;
			imagey = 0;
			undo = undo.next;
		} else {
//Default: switch images: [imagex, imagey] [image]
//	interpret: the screen location and image to restore
			redo = new Edit(undo.type, redo);
			redo.ints = new int[] {imagex, imagey};
			redo.image = mainimage;
			mainimage = undo.image;
			imagex = undo.ints[0];
			imagey = undo.ints[1];
			undo = undo.next;
		}
	}
	public void redo() {
		if (redo.type == 1) {
//Type 1: draw: [x, y, newcolor]
//Type 1: erase: [x, y, newcolor]
//	interpret: the location and color of the new pixel
			undo = new Edit(1, undo);
			undo.ints = new int[] {redo.ints[0], redo.ints[1], mainimage.getRGB(redo.ints[0], redo.ints[1])};
			mainimage.setRGB(redo.ints[0], redo.ints[1], redo.ints[2]);
			redo = redo.next;
		} else if (redo.type == 2) {
//Type 2: draw + resize: [drawx, drawy, newcolor]
//	interpret: the location and color of the new pixel, the location of the image
			undo = new Edit(2, undo);
			undo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), Math.max(-redo.ints[0], 0), Math.max(-redo.ints[1], 0), Math.max(0, redo.ints[0]), Math.max(0, redo.ints[1])};
			BufferedImage tempimage = new BufferedImage(Math.max(undo.ints[0] + undo.ints[2], undo.ints[4] + 1), Math.max(undo.ints[1] + undo.ints[3], undo.ints[5] + 1), BufferedImage.TYPE_INT_ARGB);
			tempimage.setData(mainimage.getRaster().createTranslatedChild(undo.ints[2], undo.ints[3]));
			mainimage = tempimage;
			imagex -= undo.ints[2];
			imagey -= undo.ints[3];
			mainimage.setRGB(undo.ints[4], undo.ints[5], redo.ints[2]);
			redo = redo.next;
		} else if (redo.type == 3) {
//Type 3: draw + new image: [imagex, imagey, newcolor]
//	interpret: the location and color of the new pixel
			undo = new Edit(3, undo);
			mainimage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			mainimage.setRGB(0, 0, redo.ints[2]);
			imagex = redo.ints[0];
			imagey = redo.ints[1];
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
				undo = new Edit(0, undo);
				undo.ints = new int[] {0, 0};
				undo.image = imagecopy(mainimage.getSubimage(0, 0, left, mheight));
			}
			if (right != mwidth) {
				undo = new Edit(0, undo);
				undo.ints = new int[] {right, 0};
				undo.image = imagecopy(mainimage.getSubimage(right, 0, mwidth - right, mheight));
			}
			if (up != 0) {
				undo = new Edit(0, undo);
				undo.ints = new int[] {left, 0};
				undo.image = imagecopy(mainimage.getSubimage(left, 0, redo.ints[0], up));
			}
			if (down != mheight) {
				undo = new Edit(0, undo);
				undo.ints = new int[] {left, down};
				undo.image = imagecopy(mainimage.getSubimage(left, down, redo.ints[0], mheight - down));
			}
			undo = new Edit(4, undo);
			undo.ints = new int[] {mwidth, mheight, left, up, redo.ints[4], redo.ints[5], mainimage.getRGB(redo.ints[4], redo.ints[5])};
			mainimage.setRGB(redo.ints[4], redo.ints[5], 0);
			mainimage = imagecopy(mainimage.getSubimage(left, up, redo.ints[0], redo.ints[1]));
			imagex += left;
			imagey += up;
			redo = redo.next;
		} else if (redo.type == 5) {
//Type 5: erase + nullify: -
//	interpret: set the image to null
			undo = new Edit(5, undo);
			undo.ints = new int[] {imagex, imagey};
			undo.image = mainimage;
			mainimage = null;
			imagex = 0;
			imagey = 0;
			redo = redo.next;
		} else if (redo.type == 6) {
//Type 6: cut: [x, y] [image]
//Type 6: paste: [x, y] [image]
//	interpret: the location and image to set
			undo = new Edit(6, undo);
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
			undo = new Edit(7, undo);
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
			imagex -= redo.ints[2];
			imagey -= redo.ints[3];
			redo = redo.next;
		} else if (redo.type == 8) {
//Type 8: paste + new image: [imagex, imagey] [image]
//	interpret: the screen location and color of the new image
			undo = new Edit(8, undo);
			mainimage = redo.image;
			imagex = redo.ints[0];
			imagey = redo.ints[1];
			redo = redo.next;
		} else {
//Default: switch images: [imagex, imagey] [image]
//	interpret: the screen location and image to restore
			undo = new Edit(redo.type, undo);
			undo.ints = new int[] {imagex, imagey};
			undo.image = mainimage;
			mainimage = redo.image;
			imagex = redo.ints[0];
			imagey = redo.ints[1];
			redo = redo.next;
		}
	}
	//create a copy of the image
	public static BufferedImage imagecopy(BufferedImage img) {
		if (img == null)
			return null;
		BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		temp.setData(img.getRaster());
		return temp;
	}
	//paste a onto b
	public static void pasteon(BufferedImage a, int lx, int ly, BufferedImage b) {
		int pa = 0;
		int pb = 0;
		int aa = 0;
		int fa = 0;
		int br = 0;
		int bg = 0;
		int bb = 0;
		int awidth = a.getWidth() + lx;
		int aheight = a.getHeight() + ly;
		for (int y = ly; y < aheight; y += 1) {
			for (int x = lx; x < awidth; x += 1) {
				pa = a.getRGB(x - lx, y - ly);
				pb = b.getRGB(x, y);
				aa = pa >> 24 & 255;
				if (aa != 0) {
					//final alpha: distances to full alpha multiplied by each other (3/4 & 3/4 -> 15/16)
					fa = 255 - (255 - (pb >> 24 & 255)) * (255 - aa) / 255;
					br = pb >> 16 & 255;
					bg = pb >> 8 & 255;
					bb = pb & 255;
					b.setRGB(x, y,
						fa << 24 |
						br + ((pa >> 16 & 255) - br) * aa / fa << 16 |
						bg + ((pa >> 8 & 255) - bg) * aa / fa << 8 |
						bb + ((pa & 255) - bb) * aa / fa);
				}
			}
		}
	}
	public void optionspaintComponent(Graphics g) {
//panel background
		int innerwidth = innerwidth();
		int innerheight = innerheight();
		int realwidth = innerwidth + 100;
		int realheight = innerheight + 200;
		int newx = (realwidth - 170) / 2;
		int newy = (realheight - optionsize) / 2;
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(0, 0, realwidth, newy - 5);
		g.fillRect(0, newy - 5, newx - 5, optionsize + 10);
		g.fillRect(newx + 175, newy - 5, realwidth - newx - 175, optionsize + 10);
		g.fillRect(0, newy + optionsize + 5, realwidth, realheight - newy - optionsize - 5);
		g.setColor(new Color(128, 128, 128));
		g.drawRect(newx - 1, newy - 1, 171, optionsize + 1);
		g.drawRect(newx - 2, newy - 2, 173, optionsize + 3);
		g.drawRect(newx - 3, newy - 3, 175, optionsize + 5);
		g.drawRect(newx - 4, newy - 4, 177, optionsize + 7);
		g.drawRect(newx - 5, newy - 5, 179, optionsize + 9);
		g.setColor(new Color(255, 192, 0));
//height = 15 * rows + 10 + 20(save)
		g.fillRect(newx, newy, 170, optionsize);
		g.translate(newx, newy);
//main panel drawing
		g.setColor(new Color(128, 0, 0));
		g.fillRect(5, 5, 160, 20);
		g.setColor(Color.GREEN);
		g.drawRect(6, 6, 157, 17);
		g.setColor(Color.WHITE);
		g.fillOval(19, 41, 9, 9);
		g.fillOval(19, 56, 9, 9);
		g.fillOval(19, 71, 9, 9);
		g.fillRect(19, 101, 9, 9);
		g.fillOval(19, 131, 9, 9);
		g.fillOval(19, 146, 9, 9);
		g.fillOval(19, 161, 9, 9);
		g.fillOval(19, 191, 9, 9);
		g.fillOval(19, 206, 9, 9);
		g.fillOval(19, 221, 9, 9);
		g.fillOval(19, 236, 9, 9);
		g.fillOval(19, 266, 9, 9);
		g.fillOval(19, 281, 9, 9);
		g.fillOval(19, 296, 9, 9);
		g.fillOval(19, 311, 9, 9);
		g.fillOval(19, 326, 9, 9);
		g.fillOval(19, 356, 9, 9);
		g.fillOval(19, 371, 9, 9);
		g.setColor(new Color(96, 96, 96));
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		g.drawString("S  A  V  E", 50, 19);
		g.drawString("Stats:", 5, 35);
		g.drawString("    Stats Off", 5, 50);
		g.drawString("    Decimal Stats", 5, 65);
		g.drawString("    Hex Stats", 5, 80);
		g.drawString("Border:", 5, 95);
		g.drawString("    Border On", 5, 110);
		g.drawString("Xs:", 5, 125);
		g.drawString("    None", 5, 140);
		g.drawString("    Standard (Black)", 5, 155);
		g.drawString("    Custom Color", 5, 170);
		g.drawString("Background:", 5, 185);
		g.drawString("    Standard (Gray)", 5, 200);
		g.drawString("    White", 5, 215);
		g.drawString("    Black", 5, 230);
		g.drawString("    Custom Color", 5, 245);
		g.drawString("Pixel Size:", 5, 260);
		g.drawString("    16x16", 5, 275);
		g.drawString("    8x8", 5, 290);
		g.drawString("    4x4", 5, 305);
		g.drawString("    2x2", 5, 320);
		g.drawString("    1x1", 5, 335);
		g.drawString("Display:", 5, 350);
		g.drawString("    Orange", 5, 365);
		g.drawString("    Custom Color", 5, 380);
		g.drawOval(19, 41, 9, 9);
		g.drawOval(19, 56, 9, 9);
		g.drawOval(19, 71, 9, 9);
		g.drawRect(18, 100, 10, 10);
		g.drawOval(19, 131, 9, 9);
		g.drawOval(19, 146, 9, 9);
		g.drawOval(19, 161, 9, 9);
		g.drawOval(19, 191, 9, 9);
		g.drawOval(19, 206, 9, 9);
		g.drawOval(19, 221, 9, 9);
		g.drawOval(19, 236, 9, 9);
		g.drawOval(19, 266, 9, 9);
		g.drawOval(19, 281, 9, 9);
		g.drawOval(19, 296, 9, 9);
		g.drawOval(19, 311, 9, 9);
		g.drawOval(19, 326, 9, 9);
		g.drawOval(19, 356, 9, 9);
		g.drawOval(19, 371, 9, 9);
		if (border) {
			g.drawLine(20, 102, 26, 108);
			g.drawLine(21, 102, 26, 107);
			g.drawLine(20, 103, 25, 108);
			g.drawLine(20, 108, 26, 102);
			g.drawLine(20, 107, 25, 102);
			g.drawLine(21, 108, 26, 103);
		}
		g.fillRect(22, 44 + 15 * statson, 4, 4);
		g.fillRect(22, 194 + 15 * backsetting, 4, 4);
		g.fillRect(22, 134 + 15 * Xs, 4, 4);
		if (size == 16)
			g.fillRect(22, 269, 4, 4);
		else if (size == 8)
			g.fillRect(22, 284, 4, 4);
		else if (size == 4)
			g.fillRect(22, 299, 4, 4);
		else if (size == 2)
			g.fillRect(22, 314, 4, 4);
		else if (size == 1)
			g.fillRect(22, 329, 4, 4);
		g.fillRect(22, 359 + 15 * display, 4, 4);
	}
	public void optionsmousePressed(MouseEvent evt) {
		int posy = evt.getY();
		//save the options
		if (posy >= 5 && posy < 25) {
			try {
				filer.newfile();
				if (statson != 0)
					filer.writeline("stats=" + statson);
				if (border)
					filer.writeline("border=" + border);
				if (Xs < 1)
					filer.writeline("Xs=" + Xs);
				else if (Xs > 1)
					filer.writeline("Xs=\\" + (Xcolor.getRGB() & 0xFFFFFF));
				if (backsetting >= 3)
					filer.writeline("background=\\" + (backcolor.getRGB() & 0xFFFFFF));
				else if (backsetting != 0)
					filer.writeline("background=" + backsetting);
				if (size != 8)
					filer.writeline("size=" + size);
				if (display != 0)
					filer.writeline("display=\\" + (displaycolor.getRGB() & 0xFFFFFF));
				filer.savefile();
				System.out.println("You successfully saved your settings!");
			} catch(Exception e) {
				System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be saved.");
			}
		//clicked an option
		} else {
			posy = (posy - 24) / 15;
			switch(posy) {
				case 1:
				case 2:
				case 3:
					statson = posy - 1;
					break;
				case 5:
					border = !border;
					break;
				case 7:
					Xs = 0;
					break;
				case 8:
					Xs = 1;
					Xcolor = Color.BLACK;
					break;
				case 9:
					Xs = 2;
					Xcolor = new Color(red, green, blue);
					break;
				case 11:
					backsetting = 0;
					backcolor = null;
					break;
				case 12:
					backsetting = 1;
					backcolor = Color.WHITE;
					break;
				case 13:
					backsetting = 2;
					backcolor = Color.BLACK;
					break;
				case 14:
					backsetting = 3;
					backcolor = new Color(red, green, blue);
					break;
				case 16:
					size = 16;
					break;
				case 17:
					size = 8;
					break;
				case 18:
					size = 4;
					break;
				case 19:
					size = 2;
					break;
				case 20:
					size = 1;
					break;
				case 22:
					display = 0;
					displaycolor = standarddisplaycolor;
					setBackground(displaycolor);
					break;
				case 23:
					display = 1;
					displaycolor = new Color(red, green, blue);
					setBackground(displaycolor);
					break;
			}
			if (posy >= 7 && posy <= 20 && posy != 10 && posy != 15)
				bgsetup();
		}
	}
	public void panelspaintComponent(Graphics g) {
//panel background
		int innerwidth = innerwidth();
		int innerheight = innerheight();
		int realwidth = innerwidth + 100;
		int realheight = innerheight + 200;
		int newx = (realwidth - 170) / 2;
		int newy = (realheight - panelsize) / 2;
		g.setColor(new Color(0, 0, 0, 128));
		g.fillRect(0, 0, realwidth, newy - 5);
		g.fillRect(0, newy - 5, newx - 5, panelsize + 10);
		g.fillRect(newx + 175, newy - 5, realwidth - newx - 175, panelsize + 10);
		g.fillRect(0, newy + panelsize + 5, realwidth, realheight - newy - panelsize - 5);
		g.setColor(new Color(128, 128, 128));
		g.drawRect(newx - 1, newy - 1, 171, panelsize + 1);
		g.drawRect(newx - 2, newy - 2, 173, panelsize + 3);
		g.drawRect(newx - 3, newy - 3, 175, panelsize + 5);
		g.drawRect(newx - 4, newy - 4, 177, panelsize + 7);
		g.drawRect(newx - 5, newy - 5, 179, panelsize + 9);
		g.setColor(new Color(255, 192, 0));
		g.fillRect(newx, newy, 170, panelsize);
		g.translate(newx, newy);
//main panel drawing
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
		//choose a panel to use
		if (posy >= 0 && posy <= panels.length) {
			panelnum = posy;
			if (panelnum != 0) {
				int pnum = panelnum - 1;
				panel = panels[pnum];
				paint = methods[pnum][0];
				click = methods[pnum][1];
				release = methods[pnum][2];
				scroll = methods[pnum][3];
				if (mainimage != null && (undo == null || undo.type != 9)) {
//Type 9: switch images: [imagex, imagey] [image]
					redo = null;
					undo = new Edit(9, undo);
					undo.ints = new int[] {imagex, imagey};
					undo.image = imagecopy(mainimage);
				}
			}
		}
		repaint();
	}
	public static class Edit {
		public int type;
		public Edit next;
		public int[] ints = null;
		public BufferedImage image = null;
		public Edit(int i, Edit l) {
			type = i;
			next = l;
		}
	}
	public class StandardPanel {
		private BufferedImage rightpanel = new BufferedImage(100, 600, BufferedImage.TYPE_INT_ARGB);
		private BufferedImage bottompanel = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
		private int origin2 = 0;
		public StandardPanel() {
			Graphics g = rightpanel.createGraphics();
//color selection
			int level = 0;
			for (int y = 422; y >= 122; y -= 20) {
				g.setColor(new Color(level << 16));
				g.fillRect(6, y, 16, 16);
				g.setColor(new Color(level << 8));
				g.fillRect(30, y, 16, 16);
				g.setColor(new Color(level));
				g.fillRect(54, y, 16, 16);
				g.setColor(new Color(192, 192, 192));
				g.fillRect(78, y, 16, 16);
				g.setColor(Color.BLACK);
				g.fillRect(80, y + 2, 2, 2);
				g.fillRect(90, y + 2, 2, 2);
				g.fillRect(82, y + 4, 2, 2);
				g.fillRect(88, y + 4, 2, 2);
				g.fillRect(84, y + 6, 4, 4);
				g.fillRect(82, y + 10, 2, 2);
				g.fillRect(88, y + 10, 2, 2);
				g.fillRect(80, y + 12, 2, 2);
				g.fillRect(90, y + 12, 2, 2);
				g.setColor(new Color(255, 255, 255, level));
				g.fillRect(78, y, 16, 16);
				level += 16;
			}
			level = 0;
			g.setColor(Color.RED);
			g.fillRect(6, 102, 16, 16);
			g.setColor(Color.GREEN);
			g.fillRect(30, 102, 16, 16);
			g.setColor(Color.BLUE);
			g.fillRect(54, 102, 16, 16);
			g.setColor(Color.WHITE);
			g.fillRect(78, 102, 16, 16);
//color display square
			for (int count = 0; count < 9; count += 1) {
				g.setColor(new Color(count * 16, count * 16, count * 16));
				g.drawRect(25 - count, 25 - count, 49 + 2 * count, 49 + 2 * count);
			}
			g.setColor(new Color(192, 192, 192));
			g.fillRect(26, 26, 48, 48);
			g.setColor(Color.BLACK);
			g.fillRect(32, 32, 6, 6);
			g.fillRect(62, 32, 6, 6);
			g.fillRect(38, 38, 6, 6);
			g.fillRect(56, 38, 6, 6);
			g.fillRect(44, 44, 12, 12);
			g.fillRect(38, 56, 6, 6);
			g.fillRect(56, 56, 6, 6);
			g.fillRect(32, 62, 6, 6);
			g.fillRect(62, 62, 6, 6);
//draw tools and such
			g.setColor(Color.BLUE);
			g.fillRect(12, 465, 75, 30);
			g.fillRect(12, 500, 75, 30);
			g.fillRect(12, 535, 75, 30);
			g.setColor(new Color(128, 0, 0));
			g.fillRect(0, 570, 100, 30);
			g.setColor(Color.GREEN);
			g.drawRect(13, 466, 72, 27);
			g.drawRect(13, 501, 72, 27);
			g.drawRect(13, 536, 72, 27);
			g.drawRect(1, 571, 97, 27);
			g.setColor(new Color(192, 192, 192));
			g.setFont(new Font("Monospaced", Font.BOLD, 24));
			g.drawString("Draw", 22, 487);
			g.drawString("Erase", 15, 522);
			g.drawString("Save", 22, 592);
			g.setColor(new Color(192, 192, 192));
			g.setFont(new Font("Monospaced", Font.BOLD, 16));
			g.drawString("Absorb", 20, 554);

			g = bottompanel.createGraphics();
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, 75, 30);
			g.setColor(Color.GREEN);
			g.drawRect(1, 1, 72, 27);
			g.fillRect(310, 5, 75, 30);
			g.fillRect(310, 40, 75, 30);
			g.fillRect(310, 75, 75, 30);
			g.setColor(Color.BLUE);
			g.drawRect(311, 6, 72, 27);
			g.drawRect(311, 41, 72, 27);
			g.drawRect(311, 76, 72, 27);
			g.setColor(new Color(128, 128, 128));
			g.fillRect(0, 35, 75, 30);
			g.fillRect(0, 70, 75, 30);
			g.fillRect(310, 110, 20, 20);
			g.fillRect(335, 110, 20, 20);
			g.fillRect(360, 110, 20, 20);
			g.fillRect(0, 105, 35, 20);
			g.fillRect(40, 105, 35, 20);
			g.setColor(Color.BLACK);
			g.drawRect(1, 36, 72, 27);
			g.drawRect(1, 71, 72, 27);
			g.drawRect(311, 111, 17, 17);
			g.drawRect(336, 111, 17, 17);
			g.drawRect(361, 111, 17, 17);
			g.drawRect(1, 106, 32, 17);
			g.drawRect(41, 106, 32, 17);
			g.setColor(new Color(192, 192, 192));
			g.setFont(new Font("Monospaced", Font.BOLD, 24));
			g.drawString("Reset", 3, 22);
			g.setColor(new Color(64, 64, 64));
			g.drawString("Copy", 320, 27);
			g.drawString("Cut", 327, 62);
			g.drawString("Paste", 313, 97);
			g.setFont(new Font("Monospaced", Font.BOLD, 16));
			g.setColor(Color.WHITE);
			g.drawString("Import", 8, 54);
			g.drawString("Options", 3, 89);
//undo
			g.fillRect(7, 114, 21, 2);
			g.drawLine(8, 116, 12, 120);
			g.drawLine(8, 113, 12, 109);
			g.drawLine(9, 116, 13, 120);
			g.drawLine(9, 113, 13, 109);
			g.drawLine(10, 116, 13, 119);
			g.drawLine(10, 113, 13, 110);
//redo
			g.fillRect(47, 114, 21, 2);
			g.drawLine(62, 109, 66, 113);
			g.drawLine(62, 120, 66, 116);
			g.drawLine(61, 109, 65, 113);
			g.drawLine(61, 120, 65, 116);
			g.drawLine(61, 110, 64, 113);
			g.drawLine(61, 119, 64, 116);
//copyimage changing
//rotate
//g.drawArc(LeftX, TopY, Width, Height, StartAngle, ExtendAngle)
			g.drawArc(314, 114, 10, 10, 0, 270);
			g.drawLine(324, 120, 326, 118);
			g.drawLine(324, 120, 322, 118);
//vertical flip
			g.drawLine(345, 115, 345, 125);
			g.drawLine(345, 115, 343, 117);
			g.drawLine(345, 115, 347, 117);
			g.drawLine(345, 125, 343, 123);
			g.drawLine(345, 125, 347, 123);
//horizontal flip
			g.drawLine(365, 120, 375, 120);
			g.drawLine(365, 120, 367, 118);
			g.drawLine(365, 120, 367, 122);
			g.drawLine(375, 120, 373, 118);
			g.drawLine(375, 120, 373, 122);
//nudging
//nudge background
			g.setColor(Color.BLUE);
			g.fillRect(320, 140, 48, 48);
			g.setColor(Color.GREEN);
			g.drawLine(320, 140, 367, 187);
			g.drawLine(320, 187, 367, 140);
//nudge up
			g.setColor(new Color(192, 192, 192));
			g.fillRect(343, 142, 2, 17);
			g.drawLine(345, 143, 349, 147);
			g.drawLine(342, 143, 338, 147);
			g.drawLine(345, 144, 349, 148);
			g.drawLine(342, 144, 338, 148);
			g.drawLine(345, 145, 348, 148);
			g.drawLine(342, 145, 339, 148);
//nudge right
			g.fillRect(349, 163, 17, 2);
			g.drawLine(360, 158, 364, 162);
			g.drawLine(360, 169, 364, 165);
			g.drawLine(359, 158, 363, 162);
			g.drawLine(359, 169, 363, 165);
			g.drawLine(359, 159, 362, 162);
			g.drawLine(359, 168, 362, 165);
//nudge down
			g.fillRect(343, 169, 2, 17);
			g.drawLine(349, 180, 345, 184);
			g.drawLine(338, 180, 342, 184);
			g.drawLine(349, 179, 345, 183);
			g.drawLine(338, 179, 342, 183);
			g.drawLine(348, 179, 345, 182);
			g.drawLine(339, 179, 342, 182);
//nudge left
			g.fillRect(322, 163, 17, 2);
			g.drawLine(323, 165, 327, 169);
			g.drawLine(323, 162, 327, 158);
			g.drawLine(324, 165, 328, 169);
			g.drawLine(324, 162, 328, 158);
			g.drawLine(325, 165, 328, 168);
			g.drawLine(325, 162, 328, 159);
		}
		public void paint(Graphics g) {
			g.drawImage(rightpanel, innerwidth(), 0, null);
			g.drawImage(bottompanel, 0, innerheight(), null);
			int innerwidth = innerwidth();
			int innerheight = innerheight();
//color selection
			g.setColor(new Color(red, green, blue, alpha));
			g.fillRect(26 + innerwidth, 26, 48, 48);
			g.setColor(Color.CYAN);
			g.drawRect(5 + innerwidth, 421 - (red + 8) / 16 * 20, 17, 17);
			g.drawRect(4 + innerwidth, 420 - (red + 8) / 16 * 20, 19, 19);
			g.drawRect(29 + innerwidth, 421 - (green + 8) / 16 * 20, 17, 17);
			g.drawRect(28 + innerwidth, 420 - (green + 8) / 16 * 20, 19, 19);
			g.drawRect(53 + innerwidth, 421 - (blue + 8) / 16 * 20, 17, 17);
			g.drawRect(52 + innerwidth, 420 - (blue + 8) / 16 * 20, 19, 19);
			g.drawRect(77 + innerwidth, 421 - (alpha + 8) / 16 * 20, 17, 17);
			g.drawRect(76 + innerwidth, 420 - (alpha + 8) / 16 * 20, 19, 19);
//draw option
			if (action < 4) {
				g.drawRect(11 + innerwidth, 429 + action * 35, 76, 31);
				g.drawRect(10 + innerwidth, 428 + action * 35, 78, 33);
			} else {
				g.setColor(Color.RED);
				g.drawRect(309, -136 + action * 35 + innerheight, 76, 31);
				g.drawRect(308, -137 + action * 35 + innerheight, 78, 33);
			}
//show copy/cut red rect
			if ((action == 4 || action == 5) && clicked && inbounds) {
				g.setColor(new Color(255, 0, 0, 128));
				g.fillRect((copyleft + imagex) * size + 1, (copytop + imagey) * size + 1, (copyright - copyleft) * size - 2, (copybottom - copytop) * size - 2);
				g.setColor(new Color(0, 255, 0, 128));
				g.drawRect((copyleft + imagex) * size, (copytop + imagey) * size, (copyright - copyleft) * size - 1, (copybottom - copytop) * size - 1);
//show paste image
			} else if (action == 6 && clicked && copyimage != null && inbounds) {
				int pixel = 0;
				int cwidth = copyimage.getWidth();
				int cheight = copyimage.getHeight();
				int maxx = Math.min(cwidth, innerwidth() / size - imagex - copyleft);
				int maxy = Math.min(cheight, innerheight() / size - imagey - copytop);
				for (int y = 0; y < maxy; y += 1) {
					for (int x = 0; x < maxx; x += 1) {
						pixel = copyimage.getRGB(x, y);
						g.setColor(new Color(pixel >> 16 & 255, pixel >> 8 & 255, pixel & 255, (pixel >> 24 & 255) >> 1));
						g.fillRect((x + imagex + copyleft) * size, (y + imagey + copytop) * size, size, size);
					}
				}
			}
//hiding undo/redo
			if (undo == null || redo == null) {
				g.setColor(new Color(displaycolor.getRed(), displaycolor.getGreen(), displaycolor.getBlue(), 192));
				if (undo == null)
					g.fillRect(0, 105 + innerheight, 35, 20);
				if (redo == null)
					g.fillRect(40, 105 + innerheight, 35, 20);
			}
//what's getting clicked on
			if (greenrect) {
				g.setColor(new Color(0, 255, 0, 128));
				if (origin2 == 101)
					g.fillRect(0, innerheight, 75, 30);
				else if (origin2 == 102)
					g.fillRect(0, 35 + innerheight, 75, 30);
				else if (origin2 == 103)
					g.fillRect(310, 110 + innerheight, 20, 20);
				else if (origin2 == 104)
					g.fillRect(335, 110 + innerheight, 20, 20);
				else if (origin2 == 105)
					g.fillRect(360, 110 + innerheight, 20, 20);
				else if (origin2 == 106) {
					int dy = 163 + innerheight;
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(344 - count, dy - count, 343 + count, dy - count);
					}
				} else if (origin2 == 107) {
					int dy = 163 + innerheight;
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(344 + count, dy + 1 - count, 344 + count, dy + count);
					}
				} else if (origin2 == 108) {
					int dy = 163 + innerheight;
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(343 - count, dy + 1 - count, 343 - count, dy + count);
					}
				} else if (origin2 == 109) {
					int dy = 164 + innerheight;
					for (int count = 1; count < 24; count += 1) {
						g.drawLine(344 - count, dy + count, 343 + count, dy + count);
					}
				} else if (origin2 == 110)
					g.fillRect(0, 105 + innerheight, 35, 20);
				else if (origin2 == 111)
					g.fillRect(40, 105 + innerheight, 35, 20);
			}
		}
		public void click(MouseEvent evt) {
			int mousex = evt.getX();
			int mousey = evt.getY();
			int innerwidth = innerwidth();
			int innerheight = innerheight();
//main area
			if (mousex >= 0 && mousex < innerwidth - innerwidth % size && mousey >= 0 && mousey < innerheight - innerheight % size && (origin2 == 0 || origin2 == 1)) {
				origin2 = 1;
				int posx = mousex / size;
				int posy = mousey / size;
//draw
				if (action == 1) {
					if (mainimage != null) {
						int colorx = posx - imagex;
						int colory = posy - imagey;
						//resize the image to accomodate the pixel
						if (colorx < 0 || colorx >= mainimage.getWidth() || colory < 0 || colory >= mainimage.getHeight()) {
							int placex = Math.max(0, -colorx);
							int placey = Math.max(0, -colory);
							int mwidth = mainimage.getWidth();
							int mheight = mainimage.getHeight();
//Type 2: draw + resize: [oldw, oldh, oldx, oldy, drawx, drawy]
							redo = null;
							undo = new Edit(2, undo);
							colorx = Math.max(0, colorx);
							colory = Math.max(0, colory);
							undo.ints = new int[] {mwidth, mheight, placex, placey, colorx, colory};
							//transfer the image
							BufferedImage tempimage = new BufferedImage(Math.max(mwidth + placex, colorx + 1), Math.max(mheight + placey, colory + 1), BufferedImage.TYPE_INT_ARGB);
							tempimage.setData(mainimage.getRaster().createTranslatedChild(placex, placey));
							mainimage = tempimage;
							imagex = Math.min(imagex, posx);
							imagey = Math.min(imagey, posy);
						//store undo information if the color changes
						} else if (mainimage.getRGB(colorx, colory) != (alpha << 24 | red << 16 | green << 8 | blue)) {
//Type 1: draw: [x, y, oldcolor]
							redo = null;
							undo = new Edit(1, undo);
							undo.ints = new int[] {colorx, colory, mainimage.getRGB(colorx, colory)};
						}
						mainimage.setRGB(colorx, colory, alpha << 24 | red << 16 | green << 8 | blue);
					} else {
//Type 3: draw + new image: -
						redo = null;
						undo = new Edit(3, undo);
						mainimage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
						imagex = posx;
						imagey = posy;
						mainimage.setRGB(0, 0, alpha << 24 | red << 16 | green << 8 | blue);
					}
//erase
				} else if (action == 2 && mainimage != null) {
					//only erase if you clicked within the image
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
						//find the top opaque pixel
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
						//image is empty
						if (!leave) {
//Type 5: erase + nullify: [imagex, imagey] [image]
							redo = null;
							undo = new Edit(5, undo);
							undo.ints = new int[] {imagex, imagey};
							undo.image = mainimage;
							mainimage = null;
							imagex = 0;
							imagey = 0;
							return;
						}
						leave = false;
						//find the bottom opaque pixel
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
						//find the left opaque pixel
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
						//find the right opaque pixel
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
						//areas are getting removed
						if (left != 0 || right != mwidth || up != 0 || down != mheight) {
//Type 4: erase + resize: [oldw, oldh, placex, placey, colorx, colory, oldcolor] [links:[x, y] [image]]
							if (left != 0) {
								undo = new Edit(0, undo);
								undo.ints = new int[] {0, 0};
								undo.image = imagecopy(mainimage.getSubimage(0, 0, left, mheight));
							}
							if (right != mwidth) {
								undo = new Edit(0, undo);
								undo.ints = new int[] {right, 0};
								undo.image = imagecopy(mainimage.getSubimage(right, 0, mwidth - right, mheight));
							}
							if (up != 0) {
								undo = new Edit(0, undo);
								undo.ints = new int[] {left, 0};
								undo.image = imagecopy(mainimage.getSubimage(left, 0, right - left, up));
							}
							if (down != mheight) {
								undo = new Edit(0, undo);
								undo.ints = new int[] {left, down};
								undo.image = imagecopy(mainimage.getSubimage(left, down, right - left, mheight - down));
							}
							redo = null;
							undo = new Edit(4, undo);
							undo.ints = new int[] {mwidth, mheight, left, up, colorx, colory, mainimage.getRGB(colorx, colory)};
							mainimage.setRGB(colorx, colory, 0);
							mainimage = imagecopy(mainimage.getSubimage(left, up, right - left, down - up));
							imagex += left;
							imagey += up;
						//no areas removed
						} else if (mainimage.getRGB(colorx, colory) != 0) {
//Type 1: erase: [x, y, oldcolor]
							redo = null;
							undo = new Edit(1, undo);
							undo.ints = new int[] {colorx, colory, mainimage.getRGB(colorx, colory)};
							mainimage.setRGB(colorx, colory, 0);
						}
					}
//absorb
				} else if (action == 3 && mainimage != null) {
					//check that the spot is within image bounds
					if (posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey) {
						posx -= imagex;
						posy -= imagey;
						alpha = mainimage.getRGB(posx, posy) >> 24 & 255;
						red = mainimage.getRGB(posx, posy) >> 16 & 255;
						green = mainimage.getRGB(posx, posy) >> 8 & 255;
						blue = mainimage.getRGB(posx, posy) & 255;
					}
//cutting + copying
				} else if ((action == 4 || action == 5) && mainimage != null) {
					//check if it's a click or drag
					if (!clicked) {
						clicked = true;
						initialy = posy;
						initialx = posx;
					}
					finaly = posy;
					finalx = posx;
					//get the bounds of the copy image
					copyleft = Math.min(Math.max(Math.min(initialx, finalx) - imagex, 0), mainimage.getWidth() - 1);
					copyright = Math.max(Math.min(Math.max(initialx, finalx) + 1 - imagex, mainimage.getWidth()), 1);
					copytop = Math.min(Math.max(Math.min(initialy, finaly) - imagey, 0), mainimage.getHeight() - 1);
					copybottom = Math.max(Math.min(Math.max(initialy, finaly) + 1 - imagey, mainimage.getHeight()), 1);
					inbounds = true;
//pasting
				} else if (action == 6 && copyimage != null) {
					clicked = true;
					//get the bounds of where the copyimage goes
					copytop = posy - imagey;
					copyleft = posx - imagex;
					copybottom = copytop + copyimage.getHeight();
					copyright = copyleft + copyimage.getWidth();
					inbounds = true;
				}
//select draw, erase, or absorb
			} else if (mousex >= 12 + innerwidth && mousex < 87 + innerwidth && mousey >= 465 && mousey < 565 && (mousey - 465) % 35 < 30 && (origin2 == 0 || origin2 == 2)) {
				origin2 = 2;
				action = (byte)((mousey - 430) / 35);
//select copy, cut, or paste
			} else if (mousex >= 310 && mousex < 385 && mousey >= 5 + innerheight && mousey < 105 + innerheight && (mousey - 5 - innerheight) % 35 < 30 && (origin2 == 0 || origin2 == 2)) {
				origin2 = 2;
				action = (byte)((mousey + 135 - innerheight) / 35);
//change color selection
			} else if (mousex >= 6 + innerwidth && mousex < 94 + innerwidth && (mousex - 6 - innerwidth) % 24 < 16 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 < 16 && (origin2 == 0 || origin2 == 3)) {
				origin2 = 3;
				int posx = (mousex - 6 - innerwidth) / 24;
				int posy = (mousey - 102) / 20;
				if (posx == 0) {
					if (posy == 0)
						red = 255;
					else
						red = 256 - 16 * posy;
				} else if (posx == 1) {
					if (posy == 0)
						green = 255;
					else
						green = 256 - 16 * posy;
				} else if (posx == 2) {
					if (posy == 0)
						blue = 255;
					else
						blue = 256 - 16 * posy;
				} else {
					if (posy == 0)
						alpha = 255;
					else
						alpha = 256 - 16 * posy;
				}
//save
			} else if (mousex >= innerwidth && mousex < 100 + innerwidth && mousey >= 570 && mousey < 600 && origin2 == 0 && mainimage != null)
				done = 1;
//reset
			else if (mousex >= 0 && mousex < 75 && mousey >= innerheight && mousey < 30 + innerheight && origin2 == 0) {
				imagex = 0;
				imagey = 0;
				origin2 = 101;
				greenrect = true;
//import
			} else if (mousex >= 0 && mousex < 75 && mousey >= 35 + innerheight && mousey < 65 + innerheight && origin2 == 0) {
				done = 2;
				origin2 = 102;
				greenrect = true;
//rotate, vertical flip, or horizontal flip
			} else if (mousex >= 310 && mousex < 380 && (mousex - 310) % 25 < 20 && mousey >= 110 + innerheight && mousey < 130 + innerheight && origin2 == 0 && copyimage != null) {
				int cwidth = copyimage.getWidth();
				int cheight = copyimage.getHeight();
				//25 * 103 - 310 = 2265
				origin2 = (byte)((mousex + 2265) / 25);
				BufferedImage tempimage;
				//rotate
				if (origin2 == 103) {
					tempimage = new BufferedImage(cheight, cwidth, BufferedImage.TYPE_INT_ARGB);
					for (int y = 0; y < cheight; y += 1) {
						for (int x = 0; x < cwidth; x += 1) {
							tempimage.setRGB(cheight - y - 1, x, copyimage.getRGB(x, y));
						}
					}
				//vertical flip
				} else if (origin2 == 104) {
					tempimage = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
					for (int y = 0; y < cheight; y += 1) {
						for (int x = 0; x < cwidth; x += 1) {
							tempimage.setRGB(x, cheight - y - 1, copyimage.getRGB(x, y));
						}
					}
				//horizontal flip
				} else {
					tempimage = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
					for (int y = 0; y < cheight; y += 1) {
						for (int x = 0; x < cwidth; x += 1) {
							tempimage.setRGB(cwidth - x - 1, y, copyimage.getRGB(x, y));
						}
					}
				}
				copyimage = tempimage;
				greenrect = true;
//nudge
			} else if (mousex >= 320 && mousex < 368 && mousey >= 140 + innerheight && mousey < 188 + innerheight && origin2 == 0) {
				int posx = mousex - 320;
				int posy = mousey - 140 - innerheight;
				if (posx > posy) {
					if (posx + posy < 47) {
						imagey -= 1;
						origin2 = 106;
					} else if (posx + posy > 47) {
						imagex += 1;
						origin2 = 107;
					}
				} else if (posy > posx) {
					if (posx + posy < 47) {
						imagex -= 1;
						origin2 = 108;
					} else if (posx + posy > 47) {
						imagey += 1;
						origin2 = 109;
					}
				}
				greenrect = true;
//options
			} else if (mousex >= 0 && mousex < 75 && mousey >= 70 + innerheight && mousey < 100 + innerheight && origin2 == 0) {
				showoptions = true;
				origin2 = -1;
				origin = -1;
//undo
			} else if (mousex >= 0 && mousex < 35 && mousey >= 105 + innerheight && mousey < 125 + innerheight && undo != null && origin2 == 0) {
				undo();
				origin2 = 110;
				greenrect = true;
//redo
			} else if (mousex >= 40 && mousex < 75 && mousey >= 105 + innerheight && mousey < 125 + innerheight && redo != null && origin2 == 0) {
				redo();
				origin2 = 111;
				greenrect = true;
			} else
				inbounds = false;
		}
		public void release(MouseEvent evt) {
			origin2 = 0;
			greenrect = false;
			if (clicked && inbounds) {
//finished selecting copy/cut area
//copy
				if (action == 4)
					copyimage = imagecopy(mainimage.getSubimage(copyleft, copytop, copyright - copyleft, copybottom - copytop));
//cut
				else if (action == 5) {
					int cwidth = copyright - copyleft;
					int cheight = copybottom - copytop;
					copyimage = imagecopy(mainimage.getSubimage(copyleft, copytop, cwidth, cheight));
//Type 6: cut: [x, y] [image]
					redo = null;
					undo = new Edit(6, undo);
					undo.ints = new int[] {copyleft, copytop};
					undo.image = imagecopy(copyimage);
					mainimage.setRGB(copyleft, copytop, cwidth, cheight, new int[cwidth * cheight], 0, cwidth);
//ready to paste the image
				} else if (action == 6) {
					int cwidth = copyimage.getWidth();
					int cheight = copyimage.getHeight();
					if (mainimage != null) {
						if (copyleft < 0 || copyright > mainimage.getWidth() || copytop < 0 || copybottom > mainimage.getHeight()) {
							int mwidth = mainimage.getWidth();
							int mheight = mainimage.getHeight();
							int posleft = Math.max(0, copyleft);
							int postop = Math.max(0, copytop);
							int negleft = Math.max(0, -copyleft);
							int negtop = Math.max(0, -copytop);
//Type 7: paste + resize: [oldw, oldh, oldx, oldy, pastew, pasteh, pastex, pastey] [placeimage]
							redo = null;
							undo = new Edit(7, undo);
							undo.ints = new int[] {mwidth, mheight, negleft, negtop, cwidth, cheight, posleft, postop};
							//save the overwritten image
							if (copyleft < mwidth && copytop < mheight && copyright > 0 && copybottom > 0) {
								int poswidth = Math.min(mwidth, copyright) - posleft;
								int posheight = Math.min(mheight, copybottom) - postop;
								undo.image = imagecopy(mainimage.getSubimage(posleft, postop, poswidth, posheight));
							}
							//make the new image
							BufferedImage tempimage = new BufferedImage(Math.max(copyright, mwidth) - Math.min(copyleft, 0), Math.max(copybottom, mheight) - Math.min(copytop, 0), BufferedImage.TYPE_INT_ARGB);
							tempimage.setData(mainimage.getRaster().createTranslatedChild(negleft, negtop));
							pasteon(copyimage, posleft, postop, tempimage);
							mainimage = tempimage;
							imagex -= negleft;
							imagey -= negtop;
						} else {
//Type 6: paste: [x, y] [image]
							redo = null;
							undo = new Edit(6, undo);
							undo.ints = new int[] {copyleft, copytop};
							undo.image = imagecopy(mainimage.getSubimage(copyleft, copytop, cwidth, cheight));
							pasteon(copyimage, copyleft, copytop, mainimage);
						}
					} else {
//Type 8: paste + new image: -
						redo = null;
						undo = new Edit(8, undo);
						mainimage = imagecopy(copyimage);
						imagex = copyleft;
						imagey = copytop;
					}
				}
			}
			clicked = false;
		}
		public void scroll(MouseWheelEvent evt) {
			int mousex = evt.getX();
			int mousey = evt.getY();
			int add = evt.getWheelRotation();
			int innerwidth = innerwidth();
			int innerheight = innerheight();
			//scroll on one of the color selection columns, change the value by the scroll amount
			if (mousex >= 6 + innerwidth && mousex < 94 + innerwidth && (mousex - 6 - innerwidth) % 24 < 16 && mousey >= 102 && mousey < 438 && origin2 == 0) {
				int posx = (mousex - 6 - innerwidth) / 24;
				if (posx == 0 && red - add < 256 && red - add >= 0)
					red -= add;
				else if (posx == 1 && green - add <= 255 && green - add >= 0)
					green -= add;
				else if (posx == 2 && blue - add <= 255 && blue - add >= 0)
					blue -= add;
				else if (posx == 3 && alpha - add <= 255 && alpha - add >= 0)
					alpha -= add;
			//scroll in the main area, undo or redo
			} else if (mousex >= 0 && mousex < innerwidth - innerwidth % size && mousey >= 0 && mousey < innerheight - innerheight % size && origin2 == 0) {
				if (add == 1 && undo != null)
					undo();
				else if (add == -1 && redo != null)
					redo();
			//scroll in the nudge area, nudge in the direction
			} else if (mousex >= 320 && mousex < 368 && mousey >= 140 + innerheight && mousey < 188 + innerheight && origin2 == 0) {
				int posx = mousex - 320;
				int posy = mousey - 140 - innerheight;
				if (posx > posy) {
					if (posx + posy < 47)
						imagey += add;
					else if (posx + posy > 47)
						imagex += add;
				} else if (posy > posx) {
					if (posx + posy < 47)
						imagex += add;
					else if (posx + posy > 47)
						imagey += add;
				}
			}
		}
	}
}