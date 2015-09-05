//Version 1
import Gstuff.stuff;
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
public class imagemaker {
	public static int width = 500;
	public static int height = 600;
	public static BufferedImage mainimage = null;
	public static BufferedImage copyimage = null;
	public static imagepanel thepanel = null;
	public static optionpanel theoptions = new optionpanel();
	public static int imagex = 0;
	public static int imagey = 0;
	public static byte action = 1;
	public static int red = 255;
	public static int green = 255;
	public static int blue = 255;
	public static int alpha = 255;
	public static byte origin = 0;
	public static int movex = 0;
	public static int movey = 0;
	public static boolean clicked = false;
	public static int cornertop = 0;
	public static int cornerleft = 0;
	public static int cornerright = 0;
	public static int cornerbottom = 0;
	public static boolean inbounds = false;
	public static int initialx = 0;
	public static int initialy = 0;
	public static int finalx = 0;
	public static int finaly = 0;
	public static boolean importing = false;
	public static boolean greenrect = false;
	public static boolean statson = false;
	public static byte backsetting = 0;
	public static Color backcolor = new Color(0, 0, 0);
	public static boolean border = true;
	public static void main(String[] args) {
		String filename = "";
		for (int pos = 0; pos < args.length; pos += 1) {
			if (args[pos].startsWith("/")) {
				if (args[pos].equals("/stats"))
					statson = true;
				else if (args[pos].equals("/white"))
					backsetting = 1;
				else if (args[pos].equals("/black"))
					backsetting = 2;
				else if (args[pos].equals("/noborder"))
					border = false;
			} else {
				filename = args[pos];
				try{
					mainimage = ImageIO.read(new File("images\\" + filename + ".png"));
					if (mainimage.getType() != BufferedImage.TYPE_INT_ARGB) {
						BufferedImage tempimage = new BufferedImage(mainimage.getWidth(), mainimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = tempimage.createGraphics();
						g.drawImage(mainimage, 0, 0, mainimage.getWidth(), mainimage.getHeight(), 0, 0, mainimage.getWidth(), mainimage.getHeight(), null);
						mainimage = tempimage;
						g.dispose();
					}
				} catch(Exception e) {
					System.out.println("File \"" + filename + "\" could not be loaded. Continuing without file.");
				}
			}
		}
		JFrame window = new JFrame("Image Maker");
		thepanel = new imagepanel();
		window.setContentPane(thepanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width + 16, height + 38);
		window.setLocation((stuff.screenwidth() - width) / 2 - 8, (stuff.screenheight() - height) / 2 - 30);
		window.setVisible(true);
		String name = "";
		while (!thepanel.done) {
			while (!thepanel.done);
			if (importing) {
				System.out.println("Enter the name of the file you wish to import to the copy image:");
				try {
					name = stuff.line();
					copyimage = ImageIO.read(new File("images\\" + name + ".png"));
					System.out.println("The image has been imported.  Press \"PASTE\" to place it.");
				} catch(Exception e) {
					System.out.println("Sorry, your file could not be loaded.");
					copyimage = null;
				}
				importing = false;
				thepanel.done = false;
				thepanel.requestFocus();
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
		} catch(Exception e) {
			System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be saved.");
			System.exit(0);
		}
		File file = new File("images\\" + name + ".png");
		try {
			ImageIO.write(mainimage, "png", file);
			System.out.println("You successfully saved your image!");
		} catch(Exception e) {
			System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be saved.");
		}
	}
	public static class imagepanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
		public boolean done = false;
		public imagepanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
			setBackground(new Color(255, 192, 0));
		};
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setup(g);
			g.setColor(new Color(red, green, blue, alpha));
			g.fillRect(426, 26, 48, 48);
			if (mainimage != null) {
				if (imagex + mainimage.getWidth() > -75 && imagex < 125 && imagey + mainimage.getHeight() > -75 && imagey < 125)
					g.drawImage(mainimage, max(-75, imagex) + 175, max(-75, imagey) + 475, min(125, mainimage.getWidth() + imagex) + 175, min(125, mainimage.getHeight() + imagey) + 475, max(0, -imagex - 75), max(0, -imagey - 75), min(mainimage.getWidth(), 125 - imagex), min(mainimage.getHeight(), 125 - imagey), null);
				for (int y = max(0, imagey); y < min(50, imagey + mainimage.getHeight()); y += 1) {
					for (int x = max(0, imagex); x < min(50, imagex + mainimage.getWidth()); x += 1) {
						g.setColor(new Color(mainimage.getRGB(x - imagex, y - imagey) >> 16 & 255, mainimage.getRGB(x - imagex, y - imagey) >> 8 & 255, mainimage.getRGB(x - imagex, y - imagey) & 255, mainimage.getRGB(x - imagex, y - imagey) >> 24 & 255));
						g.fillRect(x * 8, y * 8, 8, 8);
					}
				}
			}
			g.setColor(new Color(255, 255, 0));
//small border
			if (mainimage != null && border) {
//left
				if (imagex > -75 && imagex <= 125 && imagey > -75 - mainimage.getHeight() - 1 && imagey <= 126) {
					int top = max(imagey - 1, -75);
					int bottom = min(imagey + mainimage.getHeight(), 124);
					g.setColor(Color.YELLOW);
					g.drawLine(imagex + 174, top + 475, imagex + 174, bottom + 475);
				}
//right
				if (imagex >= -75 - mainimage.getWidth() && imagex < 125 - mainimage.getWidth() && imagey > -75 - mainimage.getHeight() - 1 && imagey <= 126) {
					int top = max(imagey - 1, -75);
					int bottom = min(imagey + mainimage.getHeight(), 124);
					g.setColor(Color.YELLOW);
					g.drawLine(imagex + mainimage.getWidth() + 175, top + 475, imagex + mainimage.getWidth() + 175, bottom + 475);
				}
//top
				if (imagex > -75 - mainimage.getWidth() - 1 && imagex <= 126 && imagey > -75 && imagey <= 125) {
					int left = max(imagex - 1, -75);
					int right = min(imagex + mainimage.getWidth(), 124);
					g.setColor(Color.YELLOW);
					g.drawLine(left + 176, imagey + 474, right + 174, imagey + 474);
				}
//bottom
				if (imagex > -75 - mainimage.getWidth() - 1 && imagex <= 126 && imagey >= -75 - mainimage.getHeight() && imagey < 125 - mainimage.getHeight()) {
					int left = max(imagex - 1, -75);
					int right = min(imagex + mainimage.getWidth(), 124);
					g.setColor(Color.YELLOW);
					g.drawLine(left + 176, imagey + mainimage.getHeight() + 475, right + 174, imagey + mainimage.getHeight() + 475);
				}
			}
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRect(100, 400, 200, 75);
			g.fillRect(100, 475, 75, 50);
			g.fillRect(225, 475, 75, 50);
			g.fillRect(100, 525, 200, 75);
			g.setColor(Color.CYAN);
			g.drawRect(405, 101 + (16 - (red + 1) / 16) * 20, 17, 17);
			g.drawRect(404, 100 + (16 - (red + 1) / 16) * 20, 19, 19);
			g.drawRect(429, 101 + (16 - (green + 1) / 16) * 20, 17, 17);
			g.drawRect(428, 100 + (16 - (green + 1) / 16) * 20, 19, 19);
			g.drawRect(453, 101 + (16 - (blue + 1) / 16) * 20, 17, 17);
			g.drawRect(452, 100 + (16 - (blue + 1) / 16) * 20, 19, 19);
			g.drawRect(477, 101 + (16 - (alpha + 1) / 16) * 20, 17, 17);
			g.drawRect(476, 100 + (16 - (alpha + 1) / 16) * 20, 19, 19);
			if (action < 4) {
				g.drawRect(411, 429 + action * 35, 76, 31);
				g.drawRect(410, 428 + action * 35, 78, 33);
			} else {
				g.setColor(Color.RED);
				g.drawRect(309, 264 + action * 35, 76, 31);
				g.drawRect(308, 263 + action * 35, 78, 33);
			}
//large border
			if (mainimage != null && border) {
//left
				if (imagex > 0 && imagex <= 50 && imagey > 0 - mainimage.getHeight() && imagey <= 50) {
					int top = max(imagey, 0);
					int bottom = min(imagey + mainimage.getHeight() - 1, 49);
					g.setColor(Color.RED);
					g.drawLine(imagex * 8 - 8, top * 8, imagex * 8 - 8, bottom * 8 + 7);
					g.drawLine(imagex * 8 - 7, top * 8, imagex * 8 - 7, bottom * 8 + 7);
					g.drawLine(imagex * 8 - 2, top * 8, imagex * 8 - 2, bottom * 8 + 7);
					g.drawLine(imagex * 8 - 1, top * 8, imagex * 8 - 1, bottom * 8 + 7);
					g.setColor(Color.GREEN);
					g.drawLine(imagex * 8 - 6, top * 8, imagex * 8 - 6, bottom * 8 + 7);
					g.drawLine(imagex * 8 - 5, top * 8, imagex * 8 - 5, bottom * 8 + 7);
					g.drawLine(imagex * 8 - 4, top * 8, imagex * 8 - 4, bottom * 8 + 7);
					g.drawLine(imagex * 8 - 3, top * 8, imagex * 8 - 3, bottom * 8 + 7);
				}
//right
				if (imagex >= 0 - mainimage.getWidth() && imagex < 50 - mainimage.getWidth() && imagey > 0 - mainimage.getHeight() && imagey <= 50) {
					int top = max(imagey, 0);
					int bottom = min(imagey + mainimage.getHeight() - 1, 49);
					g.setColor(Color.RED);
					g.drawLine((imagex + mainimage.getWidth()) * 8, top * 8, (imagex + mainimage.getWidth()) * 8, bottom * 8 + 7);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 1, top * 8, (imagex + mainimage.getWidth()) * 8 + 1, bottom * 8 + 7);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 6, top * 8, (imagex + mainimage.getWidth()) * 8 + 6, bottom * 8 + 7);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 7, top * 8, (imagex + mainimage.getWidth()) * 8 + 7, bottom * 8 + 7);
					g.setColor(Color.GREEN);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 2, top * 8, (imagex + mainimage.getWidth()) * 8 + 2, bottom * 8 + 7);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 3, top * 8, (imagex + mainimage.getWidth()) * 8 + 3, bottom * 8 + 7);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 4, top * 8, (imagex + mainimage.getWidth()) * 8 + 4, bottom * 8 + 7);
					g.drawLine((imagex + mainimage.getWidth()) * 8 + 5, top * 8, (imagex + mainimage.getWidth()) * 8 + 5, bottom * 8 + 7);
				}
//top
				if (imagey > 0 && imagey <= 50 && imagex > 0 - mainimage.getWidth() && imagex <= 50) {
					int left = max(imagex, 0);
					int right = min(imagex + mainimage.getWidth() - 1, 49);
					g.setColor(Color.RED);
					g.drawLine(left * 8, imagey * 8 - 8, right * 8 + 7, imagey * 8 - 8);
					g.drawLine(left * 8, imagey * 8 - 7, right * 8 + 7, imagey * 8 - 7);
					g.drawLine(left * 8, imagey * 8 - 2, right * 8 + 7, imagey * 8 - 2);
					g.drawLine(left * 8, imagey * 8 - 1, right * 8 + 7, imagey * 8 - 1);
					g.setColor(Color.GREEN);
					g.drawLine(left * 8, imagey * 8 - 6, right * 8 + 7, imagey * 8 - 6);
					g.drawLine(left * 8, imagey * 8 - 5, right * 8 + 7, imagey * 8 - 5);
					g.drawLine(left * 8, imagey * 8 - 4, right * 8 + 7, imagey * 8 - 4);
					g.drawLine(left * 8, imagey * 8 - 3, right * 8 + 7, imagey * 8 - 3);
				}
//bottom
				if (imagey >= 0 - mainimage.getHeight() && imagey < 50 - mainimage.getHeight() && imagex > 0 - mainimage.getWidth() && imagex <= 50) {
					int left = max(imagex, 0);
					int right = min(imagex + mainimage.getWidth() - 1, 49);
					g.setColor(Color.RED);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8, right * 8 + 7, (imagey + mainimage.getHeight()) * 8);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 1, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 1);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 6, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 6);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 7, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 7);
					g.setColor(Color.GREEN);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 2, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 2);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 3, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 3);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 4, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 4);
					g.drawLine(left * 8, (imagey + mainimage.getHeight()) * 8 + 5, right * 8 + 7, (imagey + mainimage.getHeight()) * 8 + 5);
				}
//topleft
				if (imagex > 0 && imagex <= 50 && imagey > 0 && imagey <= 50) {
					int left = imagex * 8 - 8;
					int top = imagey * 8 - 8;
					g.setColor(Color.RED);
					g.fillRect(left, top, 8, 2);
					g.fillRect(left, top + 2, 2, 6);
					g.fillRect(left + 6, top + 6, 2, 2);
					g.setColor(Color.GREEN);
					g.fillRect(left + 2, top + 2, 6, 4);
					g.fillRect(left + 2, top + 6, 4, 2);
				}
//topright
				if (imagex >= 0 - mainimage.getWidth() && imagex < 50 - mainimage.getWidth() && imagey > 0 && imagey <= 50) {
					int left = (imagex + mainimage.getWidth()) * 8;
					int top = imagey * 8 - 8;
					g.setColor(Color.RED);
					g.fillRect(left, top, 8, 2);
					g.fillRect(left + 6, top + 2, 2, 6);
					g.fillRect(left, top + 6, 2, 2);
					g.setColor(Color.GREEN);
					g.fillRect(left, top + 2, 6, 4);
					g.fillRect(left + 2, top + 6, 4, 2);
				}
//bottomleft
				if (imagex > 0 && imagex <= 50 && imagey >= 0 - mainimage.getHeight() && imagey < 50 - mainimage.getHeight()) {
					int left = imagex * 8 - 8;
					int top = (imagey + mainimage.getHeight()) * 8;
					g.setColor(Color.RED);
					g.fillRect(left, top + 6, 8, 2);
					g.fillRect(left, top, 2, 6);
					g.fillRect(left + 6, top, 2, 2);
					g.setColor(Color.GREEN);
					g.fillRect(left + 2, top + 2, 6, 4);
					g.fillRect(left + 2, top, 4, 2);
				}
//bottomright
				if (imagex >= 0 - mainimage.getWidth() && imagex < 50 - mainimage.getWidth() && imagey >= 0 - mainimage.getHeight() && imagey < 50 - mainimage.getHeight()) {
					int left = (imagex + mainimage.getWidth()) * 8;
					int top = (imagey + mainimage.getHeight()) * 8;
					g.setColor(Color.RED);
					g.fillRect(left, top + 6, 8, 2);
					g.fillRect(left + 6, top, 2, 6);
					g.fillRect(left, top, 2, 2);
					g.setColor(Color.GREEN);
					g.fillRect(left, top + 2, 6, 4);
					g.fillRect(left + 2, top, 4, 2);
				}
			}
			if ((action == 4 || action == 5) && clicked && inbounds) {
				g.setColor(new Color(255, 0, 0, 128));
				g.fillRect(cornerleft * 8, cornertop * 8, (cornerright - cornerleft + 1) * 8, (cornerbottom - cornertop + 1) * 8);
			} else if (action == 6 && clicked && copyimage != null && inbounds) {
				for (int y = max(0, cornertop); y < min(50, cornerbottom + 1); y += 1) {
					for (int x = max(0, cornerleft); x < min(50, cornerright + 1); x += 1) {
						g.setColor(new Color(copyimage.getRGB(x - cornerleft, y - cornertop) >> 16 & 255, copyimage.getRGB(x - cornerleft, y - cornertop) >> 8 & 255, copyimage.getRGB(x - cornerleft, y - cornertop) & 255, (copyimage.getRGB(x - cornerleft, y - cornertop) >> 24 & 255) / 2));
						g.fillRect(x * 8, y * 8, 8, 8);
					}
				}
			}
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
				}
			}
//stats
			if (statson) {
				g.setFont(new Font("Monospaced", Font.BOLD, 12));
				g.setColor(new Color(64, 64, 64));
				g.drawString("W: ", 4, 537);
				g.drawString("H: ", 4, 552);
				g.drawString("X:", 4, 567);
				g.drawString("Y:", 4, 582);
				if (mainimage != null) {
					g.drawString("" + mainimage.getWidth(), 19, 537);
					g.drawString("" + mainimage.getHeight(), 19, 552);
					g.drawString("" + imagex, 19, 567);
					g.drawString("" + imagey, 19, 582);
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
		}
		public void mousePressed(MouseEvent evt) {
			clickaction(evt);
			repaint();
		}
		public void mouseReleased(MouseEvent evt) {
			origin = 0;
			greenrect = false;
			if (clicked && inbounds) {
				if (action == 4 || action == 5) {
					copyimage = new BufferedImage(cornerright - cornerleft + 1, cornerbottom - cornertop + 1, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = copyimage.createGraphics();
					g.drawImage(mainimage, 0, 0, copyimage.getWidth(), copyimage.getHeight(), cornerleft - imagex, cornertop - imagey, cornerright - imagex + 1, cornerbottom - imagey + 1, null);
					if (action == 5) {
						g = mainimage.createGraphics();
						for (int picy = cornertop - imagey; picy <= cornerbottom - imagey; picy += 1) {
							for (int picx = cornerleft - imagex; picx <= cornerright - imagex; picx += 1) {
								mainimage.setRGB(picx, picy, 0);
							}
						}
					}
					repaint();
				} else if (action == 6) {
					if (mainimage != null) {
						if (cornerleft < imagex || cornerright >= mainimage.getWidth() + imagex || cornertop < imagey || cornerbottom >= mainimage.getHeight() + imagey) {
							BufferedImage tempimage = new BufferedImage(max(cornerright - imagex + 1, mainimage.getWidth()) - min(cornerleft - imagex, 0), max(cornerbottom - imagey + 1, mainimage.getHeight()) - min(cornertop - imagey, 0), BufferedImage.TYPE_INT_ARGB);
							Graphics2D g = tempimage.createGraphics();
							g.drawImage(mainimage, max(0, imagex - cornerleft), max(0, imagey - cornertop), max(0, imagex - cornerleft) + mainimage.getWidth(), max(0, imagey - cornertop) + mainimage.getHeight(), 0, 0, mainimage.getWidth(), mainimage.getHeight(), null);
							mainimage = tempimage;
							if (cornerleft < imagex || cornertop < imagey) {
								if (cornerleft < imagex && cornertop >= imagey)
									g.drawImage(copyimage, 0, cornertop - imagey, copyimage.getWidth(), cornerbottom - imagey + 1, 0, 0, copyimage.getWidth(), copyimage.getHeight(), null);
								if (cornerleft >= imagex && cornertop < imagey)
									g.drawImage(copyimage, cornerleft - imagex, 0, cornerright - imagex + 1, copyimage.getHeight(), 0, 0, copyimage.getWidth(), copyimage.getHeight(), null);
								if (cornerleft < imagex && cornertop < imagey)
									g.drawImage(copyimage, 0, 0, copyimage.getWidth(), copyimage.getHeight(), 0, 0, copyimage.getWidth(), copyimage.getHeight(), null);
								imagex = min(imagex, cornerleft);
								imagey = min(imagey, cornertop);
							} else if (cornerright == mainimage.getWidth() + imagex - 1 || cornerbottom == mainimage.getHeight() + imagey - 1)
								g.drawImage(copyimage, cornerleft - imagex, cornertop - imagey, cornerright - imagex + 1, cornerbottom - imagey + 1, 0, 0, copyimage.getWidth(), copyimage.getHeight(), null);
						} else {
							Graphics2D g = mainimage.createGraphics();
							g.drawImage(copyimage, cornerleft - imagex, cornertop - imagey, cornerright - imagex + 1, cornerbottom - imagey + 1, 0, 0, copyimage.getWidth(), copyimage.getHeight(), null);
						}
						repaint();
					} else {
						imagex = cornerleft;
						imagey = cornertop;
						mainimage = copyimage;
					}
				}
			}
			clicked = false;
			repaint();
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
//backgrounds
			if (backsetting == 0) {
				g.setColor(new Color(192, 192, 192));
				g.fillRect(0, 0, 400, 400);
				g.setColor(Color.BLACK);
				for (int y = 0; y < 400; y += 8) {
					for (int x = 0; x < 400; x += 8) {
						g.drawLine(x + 1, y + 1, x + 6, y + 6);
						g.drawLine(x + 1, y + 6, x + 6, y + 1);
					}
				}
			} else if (backsetting == 1) {
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 400, 400);
			} else if (backsetting == 2) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 400, 400);
			} else if (backsetting == 3) {
				g.setColor(backcolor);
				g.fillRect(0, 0, 400, 400);
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
				level = level + 16;
			}
			level = 0;
			g.setColor(new Color(255 << 16));
			g.fillRect(406, 102, 16, 16);
			g.setColor(new Color(255 << 8));
			g.fillRect(430, 102, 16, 16);
			g.setColor(new Color(255));
			g.fillRect(454, 102, 16, 16);
			for (int y = 422; y >= 122; y -= 20) {
				g.setColor(new Color(192, 192, 192));
				g.fillRect(478, y, 16, 16);
				g.setColor(Color.BLACK);
				g.fillRect(480, y + 2, 2, 2);
				g.fillRect(490, y + 2, 2, 2);
				g.fillRect(482, y + 4, 2, 2);
				g.fillRect(488, y + 4, 2, 2);
				g.fillRect(484, y + 6, 2, 2);
				g.fillRect(486, y + 6, 2, 2);
				g.fillRect(484, y + 8, 2, 2);
				g.fillRect(486, y + 8, 2, 2);
				g.fillRect(482, y + 10, 2, 2);
				g.fillRect(488, y + 10, 2, 2);
				g.fillRect(480, y + 12, 2, 2);
				g.fillRect(490, y + 12, 2, 2);
				g.setColor(new Color(255, 255, 255, level));
				g.fillRect(478, y, 16, 16);
				level = level + 16;
			}
			g.setColor(new Color(192, 192, 192));
			g.fillRect(478, 102, 16, 16);
			g.setColor(Color.BLACK);
			g.fillRect(480, 104, 2, 2);
			g.fillRect(490, 104, 2, 2);
			g.fillRect(482, 106, 2, 2);
			g.fillRect(488, 106, 2, 2);
			g.fillRect(484, 108, 2, 2);
			g.fillRect(486, 108, 2, 2);
			g.fillRect(484, 110, 2, 2);
			g.fillRect(486, 110, 2, 2);
			g.fillRect(482, 112, 2, 2);
			g.fillRect(488, 112, 2, 2);
			g.fillRect(480, 114, 2, 2);
			g.fillRect(490, 114, 2, 2);
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
			g.fillRect(444, 44, 6, 6);
			g.fillRect(450, 44, 6, 6);
			g.fillRect(444, 50, 6, 6);
			g.fillRect(450, 50, 6, 6);
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
			g.setColor(Color.BLACK);
			g.drawRect(1, 436, 72, 27);
			g.drawRect(1, 471, 72, 27);
			g.drawRect(311, 511, 17, 17);
			g.drawRect(336, 511, 17, 17);
			g.drawRect(361, 511, 17, 17);
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
//copyimage changing
//g.drawArc(PosX, PosY, Width, Height, StartAngle, ExtendAngle)
			g.drawArc(314, 514, 10, 10, 0, 270);
			g.drawLine(324, 520, 326, 518);
			g.drawLine(324, 520, 322, 518);
			g.drawLine(345, 515, 345, 525);
			g.drawLine(365, 520, 375, 520);
			g.drawLine(345, 515, 343, 517);
			g.drawLine(345, 515, 347, 517);
			g.drawLine(345, 525, 343, 523);
			g.drawLine(345, 525, 347, 523);
			g.drawLine(365, 520, 367, 518);
			g.drawLine(365, 520, 367, 522);
			g.drawLine(375, 520, 373, 518);
			g.drawLine(375, 520, 373, 522);
//nudge back
			g.setColor(Color.BLUE);
			g.fillRect(320, 540, 48, 48);
			g.setColor(Color.GREEN);
			g.drawLine(320, 540, 367, 587);
			g.drawLine(320, 587, 367, 540);
//nudge up
			g.setColor(new Color(192, 192, 192));
			g.drawLine(344, 542, 344, 558);
			g.drawLine(343, 542, 343, 558);
			g.drawLine(345, 543, 349, 547);
			g.drawLine(342, 543, 338, 547);
			g.drawLine(345, 544, 349, 548);
			g.drawLine(342, 544, 338, 548);
			g.drawLine(345, 545, 348, 548);
			g.drawLine(342, 545, 339, 548);
//nudge right
			g.drawLine(349, 564, 365, 564);
			g.drawLine(349, 563, 365, 563);
			g.drawLine(360, 558, 364, 562);
			g.drawLine(360, 569, 364, 565);
			g.drawLine(359, 558, 363, 562);
			g.drawLine(359, 569, 363, 565);
			g.drawLine(359, 559, 362, 562);
			g.drawLine(359, 568, 362, 565);
//nudge down
			g.drawLine(344, 569, 344, 585);
			g.drawLine(343, 569, 343, 585);
			g.drawLine(349, 580, 345, 584);
			g.drawLine(338, 580, 342, 584);
			g.drawLine(349, 579, 345, 583);
			g.drawLine(338, 579, 342, 583);
			g.drawLine(348, 579, 345, 582);
			g.drawLine(339, 579, 342, 582);
//nudge left
			g.drawLine(322, 564, 338, 564);
			g.drawLine(322, 563, 338, 563);
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
			int posx;
			int posy;
			if (mousex >= 0 && mousex < 400 && mousey >= 0 && mousey < 400 && (origin == 0 || origin == 1)) {
				origin = 1;
				posx = mousex / 8;
				posy = mousey / 8;
				if (action == 1) {
					if (mainimage != null) {
						if (posx < imagex || posx >= mainimage.getWidth() + imagex || posy < imagey || posy >= mainimage.getHeight() + imagey) {
							BufferedImage tempimage = new BufferedImage(max(posx - imagex + 1, mainimage.getWidth()) - min(posx - imagex, 0), max(posy - imagey + 1, mainimage.getHeight()) - min(posy - imagey, 0), BufferedImage.TYPE_INT_ARGB);
							Graphics2D g = tempimage.createGraphics();
							g.drawImage(mainimage, max(0, imagex - posx), max(0, imagey - posy), max(0, imagex - posx) + mainimage.getWidth(), max(0, imagey - posy) + mainimage.getHeight(), 0, 0, mainimage.getWidth(), mainimage.getHeight(), null);
							mainimage = tempimage;
							g.dispose();
							imagex = min(imagex, posx);
							imagey = min(imagey, posy);
						}
						mainimage.setRGB(posx - imagex, posy - imagey, (alpha << 24) | (red << 16) | (green << 8) | blue);
					} else {
						mainimage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
						imagex = posx;
						imagey = posy;
						mainimage.setRGB(0, 0, (alpha << 24) | (red << 16) | (green << 8) | blue);
					}
				} else if (action == 2 && mainimage != null) {
					if (posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey)
						mainimage.setRGB(posx - imagex, posy - imagey, 0);
					int left = 0;
					int right = mainimage.getWidth() - 1;
					int up = 0;
					int down = mainimage.getHeight() - 1;
					boolean leave = false;
					boolean empty = false;
					for (int y = 0; y < mainimage.getHeight(); y += 1) {
						for (int x = 0; x < mainimage.getWidth(); x += 1) {
							if (((mainimage.getRGB(x, y) >> 24) & 255) > 0) {
								up = y;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					if (!leave)
						empty = true;
					leave = false;
					for (int y = mainimage.getHeight() - 1; y >= 0; y -= 1) {
						for (int x = mainimage.getWidth() - 1; x >= 0; x -= 1) {
							if (((mainimage.getRGB(x, y) >> 24) & 255) > 0) {
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
							if (((mainimage.getRGB(x, y) >> 24) & 255) > 0) {
								left = x;
								leave = true;
								break;
							}
						}
						if (leave)
							break;
					}
					if (empty) {
						mainimage = null;
						imagex = 0;
						imagey = 0;
						return;
					}
					leave = false;
					for (int x = mainimage.getWidth() - 1; x >= 0; x -= 1) {
						for (int y = mainimage.getHeight() - 1; y >= 0; y -= 1) {
							if (((mainimage.getRGB(x, y) >> 24) & 255) > 0) {
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
				} else if (action == 3 && mainimage != null) {
					if (posx >= imagex && posx < mainimage.getWidth() + imagex && posy >= imagey && posy < mainimage.getHeight() + imagey) {
						if (((mainimage.getRGB(posx - imagex, posy - imagey) >> 24) & 255) > 0) {
							alpha = (mainimage.getRGB(posx - imagex, posy - imagey) >> 24) & 255;
							red = (mainimage.getRGB(posx - imagex, posy - imagey) >> 16) & 255;
							green = (mainimage.getRGB(posx - imagex, posy - imagey) >> 8) & 255;
							blue = mainimage.getRGB(posx - imagex, posy - imagey) & 255;
						}
					}
				} else if ((action == 4 || action == 5) && mainimage != null) {
					if (!clicked) {
						clicked = true;
						initialy = posy;
						initialx = posx;
					}
					finaly = posy;
					finalx = posx;
					cornerleft = min(max(min(initialx, finalx), imagex), mainimage.getWidth() + imagex - 1);
					cornerright = max(min(max(initialx, finalx), mainimage.getWidth() + imagex - 1), imagex);
					cornertop = min(max(min(initialy, finaly), imagey), mainimage.getHeight() + imagey - 1);
					cornerbottom = max(min(max(initialy, finaly), mainimage.getHeight() + imagey - 1), imagey);
					inbounds = true;
				} else if (action == 6 && copyimage != null) {
					clicked = true;
					cornertop = posy;
					cornerleft = posx;
					cornerbottom = posy + copyimage.getHeight() - 1;
					cornerright = posx + copyimage.getWidth() - 1;
					inbounds = true;
				}
			} else if (mousex >= 412 && mousex < 487 && mousey >= 465 && mousey < 495 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 1;
			} else if (mousex >= 412 && mousex < 487 && mousey >= 500 && mousey < 530 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 2;
			} else if (mousex >= 412 && mousex < 487 && mousey >= 535 && mousey < 565 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 3;
			} else if (mousex >= 310 && mousex < 385 && mousey >= 405 && mousey < 435 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 4;
			} else if (mousex >= 310 && mousex < 385 && mousey >= 440 && mousey < 470 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 5;
			} else if (mousex >= 310 && mousex < 385 && mousey >= 475 && mousey < 505 && (origin == 0 || origin == 2)) {
				origin = 2;
				action = 6;
			} else if (mousex >= 406 && mousex < 470 && (mousex - 406) % 24 >= 0 && (mousex - 406) % 24 < 16 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 >= 0 && (mousey - 102) % 20 < 16 && (origin == 0 || origin == 3)) {
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
				}
			} else if (mousex >= 478 && mousex < 494 && mousey >= 102 && mousey < 438 && (mousey - 102) % 20 >= 0 && (mousey - 102) % 20 < 16 && (origin == 0 || origin == 3)) {
				origin = 3;
				posy = (mousey - 102) / 20;
				if (posy == 0) {
					alpha = 255;
				} else {
					alpha = 256 - 16 * posy;
				}
			} else if (mousex >= 400 && mousex < 500 && mousey >= 570 && mousey < 600 && origin == 0 && mainimage != null)
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
			} else if (mousex >= 0 && mousex < 75 && mousey >= 400 && mousey < 430 && origin == 0) {
				imagex = 0;
				imagey = 0;
				origin = 101;
				greenrect = true;
			} else if (mousex >= 0 && mousex < 75 && mousey >= 435 && mousey < 465 && origin == 0) {
				importing = true;
				done = true;
				origin = 102;
				greenrect = true;
			} else if (mousex >= 310 && mousex < 330 && mousey >= 510 && mousey < 530 && origin == 0 && copyimage != null) {
				BufferedImage tempimage = new BufferedImage(copyimage.getHeight(), copyimage.getWidth(), BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < copyimage.getHeight(); y += 1) {
					for (int x = 0; x < copyimage.getWidth(); x += 1) {
						tempimage.setRGB(copyimage.getHeight() - 1 - y, x, copyimage.getRGB(x, y));
					}
				}
				copyimage = tempimage;
				origin = 103;
				greenrect = true;
			} else if (mousex >= 335 && mousex < 355 && mousey >= 510 && mousey < 530 && origin == 0 && copyimage != null) {
				BufferedImage tempimage = new BufferedImage(copyimage.getWidth(), copyimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < copyimage.getHeight(); y += 1) {
					for (int x = 0; x < copyimage.getWidth(); x += 1) {
						tempimage.setRGB(x, copyimage.getHeight() - 1 - y, copyimage.getRGB(x, y));
					}
				}
				copyimage = tempimage;
				origin = 104;
				greenrect = true;
			} else if (mousex >= 360 && mousex < 380 && mousey >= 510 && mousey < 530 && origin == 0 && copyimage != null) {
				BufferedImage tempimage = new BufferedImage(copyimage.getWidth(), copyimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < copyimage.getHeight(); y += 1) {
					for (int x = 0; x < copyimage.getWidth(); x += 1) {
						tempimage.setRGB(copyimage.getWidth() - 1 - x, y, copyimage.getRGB(x, y));
					}
				}
				copyimage = tempimage;
				origin = 105;
				greenrect = true;
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
			} else if (mousex >= 0 && mousex < 75 && mousey >= 470 && mousey < 500 && origin == 0) {
				if (!theoptions.isShowing()) {
					JFrame options = new JFrame("Options");
					options.setContentPane(theoptions);
					options.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					options.setSize(170 + 16, 140 + 38);
					options.setLocation((stuff.screenwidth() - 170) / 2 - 8, (stuff.screenheight() - 140) / 2 - 30);
					options.setVisible(true);
					origin = 110;
				} else
					theoptions.requestFocus();
			} else {
				inbounds = false;
			}
		}
		public void mouseWheelMoved(MouseWheelEvent evt) {
			int mousex = evt.getX();
			int mousey = evt.getY();
			int add = evt.getWheelRotation();
			if (mousex >= 406 && mousex < 470 && (mousex - 406) % 24 >= 0 && (mousex - 406) % 24 < 16 && mousey >= 102 && mousey < 438) {
				int posx = (mousex - 406) / 24;
				if (posx == 0) {
					if (red - add <= 255 && red - add >= 0)
						red = red - add;
				} else if (posx == 1) {
					if (green - add <= 255 && green - add >= 0)
						green = green - add;
				} else if (posx == 2) {
					if (blue - add <= 255 && blue - add >= 0)
						blue = blue - add;
				}
			}
			repaint();
		}
	}
	public static int max(int num1, int num2) {
		if (num1 > num2)
			return num1;
		return num2;
	}
	public static int min(int num1, int num2) {
		if (num1 < num2)
			return num1;
		return num2;
	}
	public static class optionpanel extends JPanel implements MouseListener{
		public optionpanel() {
			addMouseListener(this);
			setBackground(new Color(255, 192, 0));
		};
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.WHITE);
			g.fillRect(19, 21, 9, 9);
			g.fillRect(19, 51, 9, 9);
			g.fillOval(19, 81, 9, 9);
			g.fillOval(19, 96, 9, 9);
			g.fillOval(19, 111, 9, 9);
			g.fillOval(19, 126, 9, 9);
			g.setColor(new Color(96, 96, 96));
			g.setFont(new Font("Monospaced", Font.BOLD, 12));
			g.drawString("Stats:", 5, 15);
			g.drawString("    Stats", 5, 30);
			g.drawString("Border:", 5, 45);
			g.drawString("    Border", 5, 60);
			g.drawString("Background:", 5, 75);
			g.drawString("    Boxes", 5, 90);
			g.drawString("    White", 5, 105);
			g.drawString("    Black", 5, 120);
			g.drawString("    Custom Color", 5, 135);
			g.drawRect(18, 20, 10, 10);
			g.drawRect(18, 50, 10, 10);
			g.drawOval(19, 81, 9, 9);
			g.drawOval(19, 96, 9, 9);
			g.drawOval(19, 111, 9, 9);
			g.drawOval(19, 126, 9, 9);
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
			g.fillRect(22, 84 + 15 * backsetting, 4, 4);
		}
		public void mousePressed(MouseEvent evt) {
			int posy = (evt.getY() - 4) / 15;
			switch(posy) {
				case 1:
					statson = !statson;
					break;
				case 3:
					border = !border;
					break;
				case 5:
					backsetting = 0;
					break;
				case 6:
					backsetting = 1;
					break;
				case 7:
					backsetting = 2;
					break;
				case 8:
					backsetting = 3;
					backcolor = new Color(red, green, blue);
					break;
			}
			repaint();
			thepanel.repaint();
		}
		public void mouseReleased(MouseEvent evt) {}
		public void mouseClicked(MouseEvent evt) {}
		public void mouseEntered(MouseEvent evt) {}
		public void mouseExited(MouseEvent evt) {}
	}
}