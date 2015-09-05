import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
//lines start at 1
public class Filer {
	public class Stringlink {
		String line = "";
		Stringlink next = null;
		Stringlink last = null;
	}
	private Stringlink thefile = null;
	private File file = new File("null");
	private Stringlink rowlink = null;
	private File[] filelist = null;
	private int row = 0;
	private int lines = 0;
	private int column = 0;
	public Filer() {
		newfile();
	}
	public Filer(String name) {
		setfile(new File(name));
		newfile();
	}
	public Filer(File newfile) {
		setfile(newfile);
		newfile();
	}
	public void setfile(String name) {
		setfile(new File(name));
	}
	public void setfile(File newfile) {
		file = newfile.getAbsoluteFile();
	}
	public boolean deletefile() {
		return file.delete();
	}
//file information stuff
//contains:
//	lines
//	files
//	getfile
//	fileisthere
	public int lines() {
		return lines;
	}
	public int files() {
		if (filelist != null)
			return filelist.length;
		else
			return 0;
	}
	public File getfile() {
		return file;
	}
	public boolean fileisthere() {
		return file.exists();
	}
//folder changing stuff
//contains:
//	goup
//	goupfull
//	godown
	public void goup() {
		if (file.getParentFile() != null)
			setfile(file.getParentFile());
	}
	public void goupfull() {
		while (file.getParentFile() != null)
			setfile(file.getParentFile());
	}
	public void godown(String folder) {
		setfile(new File(file, folder));
	}
//reader stuff
//contains:
//	readfile
//	getline
//	getline
//	getlines
//	row
//	column
	public void readfile() {
		int next;
		newfile();
		Stringlink templink = thefile;
		try {
			FileInputStream input = new FileInputStream(file);
			next = input.read();
			while (next >= 0) {
				if (next != 13 && next != 10)
					templink.line = templink.line + (char)(next);
				else if (next == 10) {
					templink.next = new Stringlink();
					templink.next.last = templink;
					templink = templink.next;
					lines = lines + 1;
				}
				next = input.read();
			}
			input.close();
		} catch(Exception e) {
			System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be read.");
		}
	}
	public String getline() {
		return rowlink.line;
	}
	public String getline(int line) {
		moveto(line, 0);
		return rowlink.line;
	}
	public String[] getlines() {
		Stringlink templink = thefile;
		String[] thelines = new String[lines];
		for (int pos = 1; pos < thelines.length; pos += 1) {
			thelines[pos - 1] = templink.line;
			templink = templink.next;
		}
		thelines[thelines.length - 1] = templink.line;
		return thelines;
	}
	public int row() {
		return row;
	}
	public int column() {
		return column;
	}
//writer stuff
//contains:
//	newfile
//	write
//	writeline
//	move
//	moveto
//	delete
//	savefile
//	replace
	public void newfile() {
		thefile = new Stringlink();
		rowlink = thefile;
		row = 1;
		lines = 1;
		column = 0;
	}
	public void write(String text) {
		rowlink.line = rowlink.line.substring(0, column) + text + rowlink.line.substring(column, rowlink.line.length());
		column = column + text.length();
	}
	public void writeline(String text) {
		Stringlink templink = new Stringlink();
		templink.line = rowlink.line.substring(column, rowlink.line.length());
		templink.next = rowlink.next;
		if (templink.next != null)
			templink.next.last = templink;
		templink.last = rowlink;
		rowlink.next = templink;
		rowlink.line = rowlink.line.substring(0, column) + text;
		rowlink = templink;
		column = 0;
		row = row + 1;
	}
	public void move(String direction, int amount) {
		if (amount <= 0)
			return;
		if (direction.equals("up")) {
			while (amount > 0) {
				if (rowlink.last == null) {
					column = 0;
					return;
				}
				rowlink = rowlink.last;
				row = row - 1;
				amount = amount - 1;
			}
			column = Math.min(column, rowlink.line.length());
		} else if (direction.equals("down")) {
			while (amount > 0) {
				if (rowlink.next == null) {
					column = rowlink.line.length();
					return;
				}
				rowlink = rowlink.next;
				row = row + 1;
				amount = amount - 1;
			}
			column = Math.min(column, rowlink.line.length());
		} else if (direction.equals("left")) {
			while (column - amount < 0) {
				if (rowlink.last == null) {
					column = 0;
					return;
				}
				amount = amount - column - 1;
				rowlink = rowlink.last;
				row = row - 1;
				column = rowlink.line.length();
			}
			column = column - amount;
		} else if (direction.equals("right")) {
			while (amount + column > rowlink.line.length()) {
				if (rowlink.next == null) {
					column = rowlink.line.length();
					return;
				}
				amount = amount - rowlink.line.length() + column - 1;
				rowlink = rowlink.next;
				row = row + 1;
				column = 0;
			}
			column = column + amount;
		}
	}
	public void moveto(int r, int c) {
		if (r < 1 || r > lines)
			throw new NullPointerException("Row " + r + " is not between 1 and " + lines + ".");
		while (row < r) {
			rowlink = rowlink.next;
			row = row + 1;
		}
		while (row > r) {
			rowlink = rowlink.last;
			row = row - 1;
		}
		if (c < 0 || c > rowlink.line.length())
			throw new NullPointerException("Column " + c + " is not between 0 and " + rowlink.line.length() + ".");
		column = c;
	}
	public void delete(int amount) {
		if (amount <= 0)
			return;
		if (amount <= column) {
			rowlink.line = rowlink.line.substring(0, column - amount) + rowlink.line.substring(column, rowlink.line.length());
			return;
		}
		rowlink.line = rowlink.line.substring(column, rowlink.line.length());
		amount = amount - column;
		column = 0;
		if (rowlink.last == null)
			return;
		Stringlink eraseto = rowlink.last;
		int length = eraseto.line.length() + 1;
		while (amount >= length) {
			if (eraseto.last == null) {
				rowlink.last = null;
				thefile = rowlink;
				return;
			}
			amount = amount - length;
			eraseto = eraseto.last;
			length = eraseto.line.length() + 1;
		}
		if (amount == 0) {
			eraseto.next = rowlink;
			rowlink.last = eraseto;
			return;
		}
		eraseto.line = eraseto.line.substring(0, length - amount) + rowlink.line;
		eraseto.next = rowlink.next;
		if (rowlink.next != null)
			rowlink.next.last = eraseto;
		rowlink = eraseto;
	}
	public boolean savefile() {
		try {
			FileWriter writer = new FileWriter(file);
			rowlink = thefile;
			while (rowlink.next != null) {
				writer.write(rowlink.line + "\r\n");
				rowlink = rowlink.next;
			}
			writer.write(rowlink.line);
			writer.close();
			rowlink = thefile;
			row = 1;
			column = 0;
		} catch(Exception e) {
			System.out.println("Sorry, an error occured:\n" + e + "\nYour file could not be written.");
			return false;
		}
		return true;
	}
	public void replace(String[] newlines) {
		thefile = new Stringlink();
		for (int pos = 0; pos < newlines.length - 1; pos += 1) {
			writeline(newlines[pos]);
		}
		write(newlines[newlines.length - 1]);
	}
//file stuff
//	storefilelist
//	getlistfile
//	getlistfile
//	getlistfiles
	public void storefilelist() {
		filelist = file.getParentFile().listFiles();
	}
	public File getlistfile(int pos) {
		return filelist[pos];
	}
	public File getlistfile(String filename) {
		String thefile;
		for (int pos = 0; pos < filelist.length; pos += 1) {
			thefile = filelist[pos].getName();
			if (filename.equalsIgnoreCase(thefile))
				return filelist[pos];
		}
		return null;
	}
	public File[] getlistfiles() {
		return filelist;
	}
}