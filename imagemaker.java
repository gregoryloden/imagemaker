//Version 2
import Gstuff.stuff;
import Gstuff.Filer;
import Gstuff.numbers;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
//TO ADD:
//option for file format
//option for hex stats
public class imagemaker {
	public static Filer filer = new Filer("options.txt");
	public static void main(String[] args) {
		imagepanel thepanel = new imagepanel();
		if (filer.fileisthere()) {
			filer.readfile();
			String[] lines = filer.getlines();
			for (int pos = 0; pos < lines.length; pos += 1) {
				if (lines[pos].startsWith("stats="))
					thepanel.statson = lines[pos].endsWith("true");
				else if (lines[pos].startsWith("border="))
					thepanel.border = lines[pos].endsWith("true");
				else if (lines[pos].startsWith("boxes=\\")) {
					thepanel.boxes = 2;
					thepanel.boxcolor = new Color(numbers.readint(lines[pos].substring(7, lines[pos].length())));
				} else if (lines[pos].startsWith("boxes="))
					thepanel.boxes = (byte)(numbers.readint(lines[pos].substring(6, lines[pos].length())));
				else if (lines[pos].startsWith("background=\\")) {
					thepanel.backsetting = 3;
					thepanel.backcolor = new Color(numbers.readint(lines[pos].substring(12, lines[pos].length())));
				} else if (lines[pos].startsWith("background="))
					thepanel.backsetting = (byte)(numbers.readint(lines[pos].substring(11, lines[pos].length())));
				else if (lines[pos].startsWith("size="))
					thepanel.size = numbers.readint(lines[pos].substring(5, lines[pos].length()));
			}
			if (thepanel.backsetting != 0 || thepanel.boxes != 1 || thepanel.size != 8)
				thepanel.setup(thepanel.bgimage.createGraphics());
		}
		String filename = "";
		if (args.length > 0) {
			filename = args[0];
			try{
				BufferedImage tempimage = ImageIO.read(new File("images\\" + filename + ".png"));
				thepanel.mainimage = new BufferedImage(tempimage.getWidth(), tempimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				thepanel.mainimage.createGraphics().drawImage(tempimage, 0, 0, null);
			} catch(Exception e) {
				System.out.println("File \"" + filename + "\" could not be loaded. Continuing without file.");
			}
		}
		JFrame window = new JFrame("Image Maker");
		window.setContentPane(thepanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(thepanel.width + 16, thepanel.height + 38);
		window.setLocation((stuff.screenwidth() - thepanel.width) / 2 - 8, (stuff.screenheight() - thepanel.height) / 2 - 30);
		window.setVisible(true);
		thepanel.requestFocus();
		window.toFront();
		String name = "";
		while (thepanel.done == 0) {
			try {
				while (thepanel.done == 0)
					Thread.sleep(0);
			} catch(Exception e) {
			}
			if (thepanel.done == 2) {
				System.out.println("Enter the name of the file you wish to import to the copy image:");
				try {
					name = stuff.line();
					thepanel.copyimage = ImageIO.read(new File("images\\" + name + ".png"));
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
			name = stuff.line();
			File file = new File("images\\" + name + ".png");
			ImageIO.write(thepanel.mainimage, "png", file);
			filer.newfile();
			filer.writeline("stats=" + thepanel.statson);
			filer.writeline("border=" + thepanel.border);
			if (thepanel.boxes < 2)
				filer.writeline("boxes=" + thepanel.boxes);
			else
				filer.writeline("boxes=\\" + (thepanel.boxcolor.getRGB() & 16777215));
			if (thepanel.backsetting < 3)
				filer.writeline("background=" + thepanel.backsetting);
			else
				filer.writeline("background=\\" + (thepanel.backcolor.getRGB() & 16777215));
			filer.write("size=" + thepanel.size);
			filer.savefile();
			System.out.println("You successfully saved your image!");
		} catch(Exception e) {
			System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be saved.");
		}
	}
	public static class imagepanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
		public byte done = 0;
		public BufferedImage mainimage = null;
		public BufferedImage copyimage = null;
		public BufferedImage bgimage = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
		public int width = 500;
		public int height = 600;
		public int imagex = 0;
		public int imagey = 0;
		public byte action = 1;
		public int red = 255;
		public int green = 255;
		public int blue = 255;
		public int alpha = 255;
		public byte origin = 0;
		public int movex = 0;
		public int movey = 0;
		public boolean clicked = false;
		public int copytop = 0;
		public int copyleft = 0;
		public int copyright = 0;
		public int copybottom = 0;
		public boolean inbounds = false;
		public int initialx = 0;
		public int initialy = 0;
		public int finalx = 0;
		public int finaly = 0;
		public boolean greenrect = false;
		public boolean statson = false;
		public byte backsetting = 0;
		public Color backcolor = new Color(192, 192, 192);
		public boolean border = false;
		public optionpanel options = null;
		public byte boxes = 1;
		public Color boxcolor = Color.BLACK;
		public int size = 8;
		public Link undo = null;
		public Link redo = null;
		public imagepanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
			setBackground(new Color(255, 192, 0));
			setup(bgimage.createGraphics());
			options = new optionpanel(this);
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
			g.setColor(new Color(red, green, blue, alpha));
			g.fillRect(426, 26, 48, 48);
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
//color selection
			g.setColor(Color.CYAN);
			g.drawRect(405, 421 - (red + (red / 255)) / 16 * 20, 17, 17);
			g.drawRect(404, 420 - (red + (red / 255)) / 16 * 20, 19, 19);
			g.drawRect(429, 421 - (green + (green / 255)) / 16 * 20, 17, 17);
			g.drawRect(428, 420 - (green + (green / 255)) / 16 * 20, 19, 19);
			g.drawRect(453, 421 - (blue + (blue / 255)) / 16 * 20, 17, 17);
			g.drawRect(452, 420 - (blue + (blue / 255)) / 16 * 20, 19, 19);
			g.drawRect(477, 421 - (alpha + (alpha / 255)) / 16 * 20, 17, 17);
			g.drawRect(476, 420 - (alpha + (alpha / 255)) / 16 * 20, 19, 19);
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
						g.drawLine(344 - count, 564 + count, 343 + count, 564 +count);
					}
				} else if (origin == 110)
					g.fillRect(0, 505, 35, 20);
				else if (origin == 111)
					g.fillRect(40, 505, 35, 20);
			}
//stats
			if (statson) {
				g.setFont(new Font("Monospaced", Font.BOLD, 12));
				g.setColor(new Color(64, 64, 64));
				g.drawString("W:", 4, 537);
				g.drawString("H:", 4, 552);
				g.drawString("X:", 4, 567);
				g.drawString("Y:", 4, 582);
				if (mainimage != null) {
					g.drawString("" + iwidth, 18, 537);
					g.drawString("" + iheight, 18, 552);
					g.drawString("" + imagex, 18, 567);
					g.drawString("" + imagey, 18, 582);
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
//options
			if (options.showing) {
				g.setColor(new Color(0, 0, 0, 128));
				g.fillRect(0, 0, 500, 170);
				g.fillRect(0, 170, 160, 260);
				g.fillRect(340, 170, 160, 260);
				g.fillRect(0, 430, 500, 170);
				g.setColor(new Color(128, 128, 128));
				g.drawRect(164, 174, 171, 251);
				g.drawRect(163, 173, 173, 253);
				g.drawRect(162, 172, 175, 255);
				g.drawRect(161, 171, 177, 257);
				g.drawRect(160, 170, 179, 259);
				g.setColor(new Color(255, 192, 0));
				g.fillRect(165, 175, 170, 250);
				g.translate(165, 175);
				options.paintcomponent(g);
			}
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
			repaint();
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
			if (clicked && inbounds) {
//finished selecting copy/cut area
				if (action == 4 || action == 5) {
					int cwidth = copyright - copyleft;
					int cheight = copybottom - copytop;
					copyimage = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
					copyimage.createGraphics().drawImage(mainimage.getSubimage(copyleft, copytop, cwidth, cheight), 0, 0, null);
					if (action == 5) {
//Type 6: cut: [x, y] [image]
						redo = null;
						undo = new Link(6, undo);
						undo.ints = new int[] {copyleft, copytop};
						undo.image = new BufferedImage(copyimage.getWidth(), copyimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
						undo.image.createGraphics().drawImage(copyimage, 0, 0, null);
						for (int picy = copytop; picy < copybottom; picy += 1) {
							for (int picx = copyleft; picx < copyright; picx += 1) {
								mainimage.setRGB(picx, picy, 0);
							}
						}
					}
					repaint();
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
								undo.image = new BufferedImage(poswidth, posheight, BufferedImage.TYPE_INT_ARGB);
								undo.image.createGraphics().drawImage(mainimage.getSubimage(posleft, postop, poswidth, posheight), 0, 0, null);
							}
							BufferedImage tempimage = new BufferedImage(Math.max(copyright, mwidth) - Math.min(copyleft, 0), Math.max(copybottom, mheight) - Math.min(copytop, 0), BufferedImage.TYPE_INT_ARGB);
							Graphics2D g = tempimage.createGraphics();
							g.drawImage(mainimage, negleft, negtop, null);
							g.drawImage(copyimage, posleft, postop, null);
							mainimage = tempimage;
							g.dispose();
							imagex = imagex - negleft;
							imagey = imagey - negtop;
						} else {
//Type 6: paste: [x, y] [image]
							redo = null;
							undo = new Link(6, undo);
							undo.ints = new int[] {copyleft, copytop};
							undo.image = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
							undo.image.createGraphics().drawImage(mainimage.getSubimage(copyleft, copytop, cwidth, cheight), 0, 0, null);
							mainimage.createGraphics().drawImage(copyimage, copyleft, copytop, null);
						}
						repaint();
					} else {
//Type 8: paste + new image: -
						redo = null;
						undo = new Link(8, undo);
						mainimage = new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
						mainimage.createGraphics().drawImage(copyimage, 0, 0, null);
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
			repaint();
		}
		public void mouseMoved(MouseEvent evt) {}
		public void mouseClicked(MouseEvent evt) {}
		public void mouseEntered(MouseEvent evt) {}
		public void mouseExited(MouseEvent evt) {}
		public void setup(Graphics g) {
//backgrounds
			g.setColor(backcolor);
			g.fillRect(0, 0, 400, 400);
			if (boxes != 0) {
				g.setColor(boxcolor);
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
			g.setColor(Color.BLACK);
			g.drawRect(425, 25, 49, 49);
			for (int count = 1; count < 9; count += 1) {
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
			g.setColor(Color.BLACK);
			g.drawRect(1, 436, 72, 27);
			g.drawRect(1, 471, 72, 27);
			g.drawRect(311, 511, 17, 17);
			g.drawRect(336, 511, 17, 17);
			g.drawRect(361, 511, 17, 17);
			g.drawRect(1, 506, 32, 17);
			g.drawRect(41, 506, 32, 17);
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
//defer to option panel
			if (options.showing && evt.getID() == MouseEvent.MOUSE_PRESSED) {
				origin = -1;
				if (mousex >= 165 && mousex < 335 && mousey >= 175 && mousey < 425) {
					evt.translatePoint(-165, -175);
					options.mousepressed(evt);
				} else
					options.showing = false;
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
							tempimage.createGraphics().drawImage(mainimage, placex, placey, null);
							mainimage = tempimage;
							imagex = Math.min(imagex, posx);
							imagey = Math.min(imagey, posy);
						} else if (mainimage.getRGB(colorx, colory) != ((alpha << 24) | (red << 16) | (green << 8) | blue)) {
//Type 1: draw: [x, y, oldcolor]
							redo = null;
							undo = new Link(1, undo);
							undo.ints = new int[] {colorx, colory, mainimage.getRGB(colorx, colory)};
						}
						mainimage.setRGB(colorx, colory, (alpha << 24) | (red << 16) | (green << 8) | blue);
					} else {
//Type 3: draw + new image: -
						redo = null;
						undo = new Link(3, undo);
						mainimage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
						imagex = posx;
						imagey = posy;
						mainimage.setRGB(0, 0, (alpha << 24) | (red << 16) | (green << 8) | blue);
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
								if (((mainimage.getRGB(x, y) >> 24) & 255) > 0 && (x != colorx || y != colory)) {
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
								if (((mainimage.getRGB(x, y) >> 24) & 255) > 0 && (x != colorx || y != colory)) {
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
								if (((mainimage.getRGB(x, y) >> 24) & 255) > 0 && (x != colorx || y != colory)) {
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
								if (((mainimage.getRGB(x, y) >> 24) & 255) > 0 && (x != colorx || y != colory)) {
									right = x + 1;
									leave = true;
									break;
								}
							}
							if (leave)
								break;
						}
						if (left != 0 || right != mwidth || up != 0 || down != mheight) {
//Type 4: erase + resize: [oldw, oldh, placex, placey] [links:[x, y] [image]]
							if (left != 0) {
								undo = new Link(0, undo);
								undo.ints = new int[] {0, 0};
								undo.image = new BufferedImage(left, mheight, BufferedImage.TYPE_INT_ARGB);
								undo.image.createGraphics().drawImage(mainimage.getSubimage(0, 0, left, mheight), 0, 0, null);
							}
							if (right != mwidth) {
								undo = new Link(0, undo);
								undo.ints = new int[] {right, 0};
								undo.image = new BufferedImage(mwidth - right, mheight, BufferedImage.TYPE_INT_ARGB);
								undo.image.createGraphics().drawImage(mainimage.getSubimage(right, 0, mwidth - right, mheight), 0, 0, null);
							}
							if (up != 0) {
								undo = new Link(0, undo);
								undo.ints = new int[] {left, 0};
								undo.image = new BufferedImage(right - left, up, BufferedImage.TYPE_INT_ARGB);
								undo.image.createGraphics().drawImage(mainimage.getSubimage(left, 0, right - left, up), 0, 0, null);
							}
							if (down != mheight) {
								undo = new Link(0, undo);
								undo.ints = new int[] {left, down};
								undo.image = new BufferedImage(right - left, mheight - down, BufferedImage.TYPE_INT_ARGB);
								undo.image.createGraphics().drawImage(mainimage.getSubimage(left, down, right - left, mheight - down), 0, 0, null);
							}
							redo = null;
							undo = new Link(4, undo);
							undo.ints = new int[] {mwidth, mheight, left, up};
							BufferedImage tempimage = new BufferedImage(right - left, down - up, BufferedImage.TYPE_INT_ARGB);
							tempimage.createGraphics().drawImage(mainimage.getSubimage(left, up, right - left, down - up), 0, 0, null);
							mainimage = tempimage;
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
						if (((mainimage.getRGB(posx - imagex, posy - imagey) >> 24) & 255) > 0) {
							alpha = (mainimage.getRGB(posx - imagex, posy - imagey) >> 24) & 255;
							red = (mainimage.getRGB(posx - imagex, posy - imagey) >> 16) & 255;
							green = (mainimage.getRGB(posx - imagex, posy - imagey) >> 8) & 255;
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
			} else if (mousex >= 406 && mousex < 494 && (mousex - 406) % 24 >= 0 && (mousex - 406) % 24 < 16 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 >= 0 && (mousey - 102) % 20 < 16 && (origin == 0 || origin == 3)) {
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
//drag the image
			else if (mousex >= 100 && mousex < 300 && mousey >= 400 && mousey < 600 && (origin == 0 || origin == 4)) {
				if (origin == 4) {
					imagex = movex + mousex;
					imagey = movey + mousey;
				} else if (origin == 0) {
					movex = imagex - mousex;
					movey = imagey - mousey;
					origin = 4;
				}
//reset
			} else if (mousex >= 0 && mousex < 75 && mousey >= 400 && mousey < 430 && origin == 0) {
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
			} else if (mousex >= 0 && mousex < 75 && mousey >= 470 && mousey < 500 && origin == 0)
				options.showing = true;
//undo
			else if (mousex >= 0 && mousex < 35 && mousey >= 505 && mousey < 525 && undo != null && origin == 0) {
				undo();
				origin = 110;
				greenrect = true;
//redo
			} else if (mousex >= 40 && mousex < 75 && mousey >= 505 && mousey < 525 && redo != null && origin == 0) {
				redo();
				origin = 111;
				greenrect = true;
			} else
				inbounds = false;
		}
		public void mouseWheelMoved(MouseWheelEvent evt) {
			try{
				safemouseWheelMoved(evt);
			} catch(OutOfMemoryError e) {
				System.out.print("Out of memory; clearing undo, collecting garbage,");
				undo = null;
				redo = null;
				Runtime.getRuntime().gc();
				System.out.print(" and rescrolling.");
				safemouseWheelMoved(evt);
			}
		}
		public void safemouseWheelMoved(MouseWheelEvent evt) {
			int mousex = evt.getX();
			int mousey = evt.getY();
			int add = evt.getWheelRotation();
			if (mousex >= 406 && mousex < 494 && (mousex - 406) % 24 >= 0 && (mousex - 406) % 24 < 16 && mousey >= 102 && mousey < 438 && origin == 0) {
				int posx = (mousex - 406) / 24;
				if (posx == 0 && red - add < 256 && red - add >= 0)
					red = red - add;
				else if (posx == 1 && green - add <= 255 && green - add >= 0)
					green = green - add;
				else if (posx == 2 && blue - add <= 255 && blue - add >= 0)
					blue = blue - add;
				else if (alpha - add <= 255 && alpha - add >= 0)
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
				BufferedImage tempimage = new BufferedImage(undo.ints[0], undo.ints[1], BufferedImage.TYPE_INT_ARGB);
				tempimage.createGraphics().drawImage(mainimage.getSubimage(undo.ints[2], undo.ints[3], undo.ints[0], undo.ints[1]), 0, 0, null);
				mainimage = tempimage;
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
//Type 4: erase + resize: [oldw, oldh, placex, placey] [links:[x, y] [image]]
//	interpret: the old size, the location to place the new image on the old image, a chain
//		containing the locations and images that were erased
				redo = new Link(4, redo);
				redo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), undo.ints[2], undo.ints[3]};
				BufferedImage tempimage = new BufferedImage(undo.ints[0], undo.ints[1], BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = tempimage.createGraphics();
				g.drawImage(mainimage, undo.ints[2], undo.ints[3], null);
				mainimage = tempimage;
				imagex = imagex - undo.ints[2];
				imagey = imagey - undo.ints[3];
				undo = undo.next;
				while (undo != null && undo.type == 0) {
					g.drawImage(undo.image, undo.ints[0], undo.ints[1], null);
					undo = undo.next;
				}
				g.dispose();
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
				int uwidth = undo.image.getWidth();
				int uheight = undo.image.getHeight();
				redo.image = new BufferedImage(uwidth, uheight, BufferedImage.TYPE_INT_ARGB);
				redo.image.createGraphics().drawImage(mainimage.getSubimage(undo.ints[0], undo.ints[1], uwidth, uheight), 0, 0, null);
				for (int y = 0; y < uheight; y += 1) {
					for (int x = 0; x < uwidth; x += 1) {
						mainimage.setRGB(x + undo.ints[0], y + undo.ints[1], undo.image.getRGB(x, y));
					}
				}
				undo = undo.next;
			} else if (undo.type == 7) {
//Type 7: paste + resize: [oldw, oldh, oldx, oldy, pastew, pasteh, pastex, pastey] [pasteimage]
//	interpret: the size and location of the old image within the new image, the size and
//		location of the new image, the image to revert
				redo = new Link(7, redo);
				redo.ints = new int[] {mainimage.getWidth(), mainimage.getHeight(), undo.ints[2], undo.ints[3], undo.ints[6], undo.ints[7]};
				redo.image = new BufferedImage(undo.ints[4], undo.ints[5], BufferedImage.TYPE_INT_ARGB);
				redo.image.createGraphics().drawImage(mainimage.getSubimage(undo.ints[6], undo.ints[7], undo.ints[4], undo.ints[5]), 0, 0, null);
				BufferedImage tempimage = new BufferedImage(undo.ints[0], undo.ints[1], BufferedImage.TYPE_INT_ARGB);
				tempimage.createGraphics().drawImage(mainimage.getSubimage(undo.ints[2], undo.ints[3], undo.ints[0], undo.ints[1]), 0, 0, null);
				mainimage = tempimage;
				if (undo.image != null) {
					int uwidth = undo.image.getWidth();
					int uheight = undo.image.getHeight();
					for (int y = 0; y < uheight; y += 1) {
						for (int x = 0; x < uwidth; x += 1) {
							mainimage.setRGB(x + undo.ints[6], y + undo.ints[7], undo.image.getRGB(x, y));
						}
					}
				}
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
				tempimage.createGraphics().drawImage(mainimage, undo.ints[2], undo.ints[3], null);
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
//Type 4: erase + resize: [neww, newh, newx, newy]
//	interpret: the location and size of the new image within the old image
				int mwidth = mainimage.getWidth();
				int mheight = mainimage.getHeight();
				int left = redo.ints[2];
				int right = left + redo.ints[0];
				int up = redo.ints[3];
				int down = up + redo.ints[1];
				if (left != 0) {
					undo = new Link(0, undo);
					undo.ints = new int[] {0, 0};
					undo.image = new BufferedImage(left, mheight, BufferedImage.TYPE_INT_ARGB);
					undo.image.createGraphics().drawImage(mainimage.getSubimage(0, 0, left, mheight), 0, 0, null);
				}
				if (right != mwidth) {
					undo = new Link(0, undo);
					undo.ints = new int[] {right, 0};
					undo.image = new BufferedImage(mwidth - right, mheight, BufferedImage.TYPE_INT_ARGB);
					undo.image.createGraphics().drawImage(mainimage.getSubimage(right, 0, mwidth - right, mheight), 0, 0, null);
				}
				if (up != 0) {
					undo = new Link(0, undo);
					undo.ints = new int[] {left, 0};
					undo.image = new BufferedImage(redo.ints[0], up, BufferedImage.TYPE_INT_ARGB);
					undo.image.createGraphics().drawImage(mainimage.getSubimage(left, 0, redo.ints[0], up), 0, 0, null);
				}
				if (down != mheight) {
					undo = new Link(0, undo);
					undo.ints = new int[] {left, down};
					undo.image = new BufferedImage(redo.ints[0], mheight - down, BufferedImage.TYPE_INT_ARGB);
					undo.image.createGraphics().drawImage(mainimage.getSubimage(left, down, redo.ints[0], mheight - down), 0, 0, null);
				}
				undo = new Link(4, undo);
				undo.ints = new int[] {mwidth, mheight, left, up};
				BufferedImage tempimage = new BufferedImage(redo.ints[0], redo.ints[1], BufferedImage.TYPE_INT_ARGB);
				tempimage.createGraphics().drawImage(mainimage.getSubimage(left, up, redo.ints[0], redo.ints[1]), 0, 0, null);
				mainimage = tempimage;
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
				undo.image = new BufferedImage(rwidth, rheight, BufferedImage.TYPE_INT_ARGB);
				undo.image.createGraphics().drawImage(mainimage.getSubimage(redo.ints[0], redo.ints[1], rwidth, rheight), 0, 0, null);
				for (int y = 0; y < rheight; y += 1) {
					for (int x = 0; x < rwidth; x += 1) {
						mainimage.setRGB(x + redo.ints[0], y + redo.ints[1], redo.image.getRGB(x, y));
					}
				}
				redo = redo.next;
			} else if (redo.type == 7) {
//Type 7: paste + resize: [neww, newh, placex, placey, pastex, pastey] [pasteimage]
//	interpret: the image size, the location of the old image within the new image, the location
//		of the image to paste, the image
				int mwidth = mainimage.getWidth();
				int mheight = mainimage.getHeight();
				int pwidth = redo.image.getWidth();
				int pheight = redo.image.getHeight();
				undo = new Link(7, undo);
				undo.ints = new int[] {mwidth, mheight, redo.ints[2], redo.ints[3], pwidth, pheight, redo.ints[4], redo.ints[5]};
				if (redo.ints[4] < mwidth + redo.ints[2] && redo.ints[5] < mheight + redo.ints[3] && redo.ints[4] + pwidth > redo.ints[2] && redo.ints[5] + pheight > redo.ints[3]) {
					int swidth = Math.min(mwidth - redo.ints[4], pwidth - redo.ints[2]);
					int sheight = Math.min(mheight - redo.ints[5], pheight - redo.ints[3]);
					undo.image = new BufferedImage(swidth, sheight, BufferedImage.TYPE_INT_ARGB);
					undo.image.createGraphics().drawImage(mainimage.getSubimage(redo.ints[4], redo.ints[5], swidth, sheight), 0, 0, null);
				}
				BufferedImage tempimage = new BufferedImage(redo.ints[0], redo.ints[1], BufferedImage.TYPE_INT_ARGB);
				tempimage.createGraphics().drawImage(mainimage, redo.ints[2], redo.ints[3], null);
				for (int y = 0; y < pheight; y += 1) {
					for (int x = 0; x < pwidth; x += 1) {
						tempimage.setRGB(x + redo.ints[4], y + redo.ints[5], redo.image.getRGB(x, y));
					}
				}
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
			}
		}
		public static class optionpanel {
			public boolean showing = false;
			public imagepanel main = null;
			public optionpanel(imagepanel i) {
				main = i;
			}
			public void paintcomponent(Graphics g) {
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
				g.drawString("Stats:", 5, 15);
				g.drawString("    Stats On", 5, 30);
				g.drawString("Border:", 5, 45);
				g.drawString("    Border On", 5, 60);
				g.drawString("Boxes:", 5, 75);
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
				if (main.statson) {
					g.drawLine(20, 22, 26, 28);
					g.drawLine(21, 22, 26, 27);
					g.drawLine(20, 23, 25, 28);
					g.drawLine(20, 28, 26, 22);
					g.drawLine(20, 27, 25, 22);
					g.drawLine(21, 28, 26, 23);
				}
				if (main.border) {
					g.drawLine(20, 52, 26, 58);
					g.drawLine(21, 52, 26, 57);
					g.drawLine(20, 53, 25, 58);
					g.drawLine(20, 58, 26, 52);
					g.drawLine(20, 57, 25, 52);
					g.drawLine(21, 58, 26, 53);
				}
				g.fillRect(22, 144 + 15 * main.backsetting, 4, 4);
				g.fillRect(22, 84 + 15 * main.boxes, 4, 4);
				g.fillRect(22, 249 - 15 * (main.size / 4), 4, 4);
			}
			public void mousepressed(MouseEvent evt) {
				int posy = (evt.getY() - 4) / 15;
				switch(posy) {
					case 1:
						main.statson = !main.statson;
						break;
					case 3:
						main.border = !main.border;
						break;
					case 5:
						main.boxes = 0;
						break;
					case 6:
						main.boxes = 1;
						main.boxcolor = Color.BLACK;
						break;
					case 7:
						main.boxes = 2;
						main.boxcolor = new Color(main.red, main.green, main.blue);
						break;
					case 9:
						main.backsetting = 0;
						main.backcolor = new Color(192, 192, 192);
						break;
					case 10:
						main.backsetting = 1;
						main.backcolor = Color.WHITE;
						break;
					case 11:
						main.backsetting = 2;
						main.backcolor = Color.BLACK;
						break;
					case 12:
						main.backsetting = 3;
						main.backcolor = new Color(main.red, main.green, main.blue);
						break;
					case 14:
						if (main.size == 4) {
							main.size = 8;
							main.imagex = main.imagex - 25;
							main.imagey = main.imagey - 25;
						}
						break;
					case 15:
						if (main.size == 8) {
							main.size = 4;
							main.imagex = main.imagex + 25;
							main.imagey = main.imagey + 25;
						}
						break;
				}
				if (posy > 4 && posy <  16 && posy != 8 && posy != 13)
					main.setup(main.bgimage.createGraphics());
				main.repaint();
			}
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
}