 Congratulations! You are now the proud owner of a Gregory Loden Imagemaker-class pixel editor!

 This piece of software is a simple tool that allows you to create images pixel-by-pixel through a
graphical user interface.
 All images are stored in the "images" folder included; a sample image has been provided. To be
recognized, all images must be in .png format.





	On to how to use the program!





 To run imagemaker, type "java imagemaker" in a command-line terminal. This will start up the image
maker, ready to make a new image. If you already have an image to edit, you can include that as an
argument to the program (ex. "java imagemaker sample"), leaving out the .png extension. Keep in mind
that you can have images in subfolders, but you have to include that in the image name (ex. an image
called "test" in a folder called "samples" in the images folder is called "samples/test").



	Let's begin!



================================================================
SECTION 1- The Draw Screen
================================================================

 The primary area of this program is the draw screen, the large area in the upper left. Almost all
edits to your image will be done in this area. The standard draw screen consists of a 50x50 grid of
gray boxes with Xs drawn through them. Each of these boxes is the background behind a pixel in the
image, and each box is 8 pixels wide. The currently drawable section of the image will show up in
this area as an 8x enlarged image. The 200x200 pixel box below the main draw screen displays the
image actual-sized, and can be dragged to allow you to edit a different section of the image.

 By changing the size of the window, you can change the size of the draw screen.

 There are various ways to edit the image. The manner in which the image is edited is controlled by
the control panel, which spans the rest of the Image Maker window.

================================================================
SECTION 2- Control Panels
================================================================

 Look at that, we're already into section 2! That doesn't mean that the draw screen is unimportant,
quite the contrary- the draw screen is where most control panel actions take place. Let's start off
with the main tools used to edit the image.



 The main tool, and the default when the program starts up, is the Draw tool. To draw, simply click
or drag in the draw area. The pixels that you draw over will be filled in with the current color,
which is displayed in the upper right corner. As you draw beyond the current bounds of the image,
the program will automatically expand the size of the image to fit your drawing. If there is no
image currently in place, a new one is created to hold the pixel.

 There are three pixel-wise tools available on the panel, the first one being Draw. All three tools
are listed as buttons on the right side of the screen. The second tool, Erase, sets the selected
pixel to fully-transparent black. In addition, it will shrink the borders of your image to remove
transparent pixels around the edge. If you erase, and the image consists entirely of transparent
pixels, the image is cleared.

 The third tool is Absorb, and its use is simply to select a pixel, and its color will become the
currently selected color.



 Above these three tools is the color selector, as well as the current color. There are 4 different
parts to the color: red, green, blue, and alpha, displayed left to right. The first three determine
the colors to be used, the alpha determines how transparent the color is. When drawing to the image,
the color of the pixel is set to these four values.



 To the left of the three tools are three tools used to transport different parts of the image. The
first one, Copy, will copy a portion of the image to a secondary copy image, which will be referred
to as the copy image in the rest of the README. To select an area to copy, just drag from one corner
to another in a rectangular area. The area you can highlight is bounded by the edges of both the
image and the draw area.

 The tool below it, Cut, performs exactly the same as Copy, except that all pixels in the selected
area are erased.

 The third tool, Paste, will paste the copy image on to the main image. If the copy image had any
semi-transparent pixels, those pixels comebine appropriately with the ones below them, rather than
completely replacing them.

 Below Paste are three tools to modify the copy image. The first and leftmost one rotates it
clockwise, the second one flips it vertically, and the third one flips it horizontally.



 There is a square of arrow buttons below the copy buttons, these will nudge the image one pixel in
their direction. This can be helpful for lining your image up precisely, rather than dragging it.

 On the left, there is a Reset button, which will align the topleft corner of the image with the
topleft corner of the draw screen.



 Below the Reset button is a button called Import. This button allows you to import and use many
images together. After clicking the button, the terminal you used to run the java program will
prompt for the name of an image. This image shares the same format as images that can be used at the
start of the program- they must be in the images folder and be in .png format. After typing in the
name of the image and pressing enter, the program will return you to the Image Maker window.



 The button below Import, Options, provides some useful features that can be turned on and off. They
are listed as follows:
- Stats: displays some information on the bottom left corner of the screen, in particular the width
    & height of the image, the x and y of the image in the program, and the color values of the
    currently selected color. This option defaults to off, but can be set to display stats in
    either decimal or hexadecimal.
- Border: displays a red & green border around the edges of the image in the draw area, and a yellow
    border around the image in the image dragging area. This option defaults to off.
- Xs: controls the Xs that appear in the draw area behind the image. The first option turns off the
    Xs display, the second option displays black Xs, and the third option will take the current
    draw color, store it, and use it as the X color. This option defaults to black.
- Background: just like Xs, controls the background color of the draw screen and the mini draw
    screen. The first option is a gray background, the second is a white background, the third is a
    black background, and the fourth uses the current color just like Xs. This option defaults to
    gray.
- Pixel Size: allows you to change the size of the pixels in the draw screen. You can choose between
    a grid of 16x16, 8x8, 4x4, 2x2, or 1x1 pixels. This option defaults to 8x8.
- Display: just like Xs and background, this controls the display color of the overall program
    background. The first option is an orange background, the second option uses the current color
    just like Xs and background.
 At the top of the Options menu is a Save button. This will save your options as options.txt, and
will be loaded during program startup.



 Right under the Options button, you will notice two buttons with arrows which are grayed out at the
beginning of the program. The left and right buttons are the undo/redo buttons, respectively. Any
changes to the image itself are recorded, and you can click these buttons to move between the
different edits of your image.



 Once you are done making edits to your image, there is a Save button on the bottom right corner of
the Image Maker window. If there is an image currently in place, the button will close out of the
window and prompt you a name to save the image. You do not need to include the .png extension at the
end, the program will do that for you.

================================================================
SECTION 3- Scrolling
================================================================

 This section probably doesn't need a whole section for it, but neither did the draw screens. On the
color selector, if you use your scroll wheel, you can change colors more precisely. Currenly,
clicking on one of the squares will set the color to a multiple of 16, or 255 for the top. By
hovering the mouse over one of the four color selection columns and using the scroll wheel, you can
increase or decrease that value by 1. Forward scrolls increase, backward scrolls decrease.

 Scrolling while the mouse is hovering over the draw area will move forwards and backwards along the
undo/redo list, allowing for quicker undoing and redoing. Backward scrolls undo actions, forward
scrolls redo actions.

 Scrolling while the mouse is hovering over one of the nudge arrows will nudge the image vertically
or horizontally depending on which arrow the mouse is over. Forward scrolls will nudge up or left,
backward scrolls will nudge down or right.

================================================================
SECTION 4- Alternate Panels
================================================================

 And finally, we have alternate panels! There are two buttons on the Image Maker that have not yet
been covered. The first one is labelled "Switch Panels" and is on the right side of the screen above
Draw. Clicking this will give you a list of available panels to choose. You can also scroll on the
list to change between them. All of the features we've covered so far are part of the Standard
Panel, listed at the top. The Image Maker, however, has the ability to use other panels, provided
that it follows some guidelines.

 These panels are fully separate programs created by you, myself or someone else. In order for a
panel to be recognized by the Image Maker, you must program and compile a class that has a certain
set of methods and a certain constructor, and exist in the "addons" folder. There is a template to
create panels in the folder, which has the format of a java program, in addition to other
information about how other panels are implemented.

 Panels only have access to certain parts of the Image Maker, and do not have access to undoing and
redoing. When switching from the Standard Panel, the program will automatically make a copy of the
image that you can undo to after using other panels.

 When a panel is active, it will be drawn on the screen in the place of the standard panel. The only
things that will be drawn are the draw screen & image, the drag box & image, the Switch Panels
button, and anything drawn due to the Options. In addition to drawing, the panels will receive all
mouse clicks, releases and scrolls, except for clicks on the Switch Panels button and drags on the
drag box. The Image Maker does not differentiate between clicks and drags, so the panels will need
to do that themselves.



 And that's that! You now know how to work the Image Maker program. Feel free to send an e-mail to
gregory.loden@gmail.com with suggestions, misspellings, confusions, or bug reports.