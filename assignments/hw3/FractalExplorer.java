package hw3;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

/**
 * This class provides a GUI for displaying fractals.
 * It also houses a main function that runs the application.
 */
public class FractalExplorer {
    /** Side-length of our square display area. **/
    private int dispSize;
    /** Image area for our fractal. **/
    private JImageDisplay img;
    /** Used to generate fractals of a specified kind. **/
    private FractalGenerator fGen;
    /** The current viewing area in our image. **/
    private Rectangle2D.Double range;
    
    /** Initializes the display image, fractal generator, and initial viewing area.
     */
    public FractalExplorer(int dispSize) {
        this.dispSize = dispSize;
        this.fGen = new Mandelbrot();
        this.range = new Rectangle2D.Double(0, 0, 0, 0);
        fGen.getInitialRange(this.range);
    }
    
    /**
     * Sets up and displays the GUI.
     */
    public void createAndShowGUI() {
        /** Create the GUI components. **/
        JFrame frame = new JFrame("Fractal Explorer");
        img = new JImageDisplay(dispSize, dispSize);
        JButton resetButton = new JButton("Reset Display");

        /** Add listeners to components. **/
        img.addMouseListener(new MouseHandler());

        /** ActionListener is rewritten by an anonymous class. **/
//        resetButton.addActionListener(new ActionHandler());
        resetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                fGen.getInitialRange(range);
                drawFractal();
            }
        });

        /** Put all of the components into the Frame. **/
        frame.setLayout(new BorderLayout());
        frame.add(img, BorderLayout.CENTER);
        frame.add(resetButton, BorderLayout.SOUTH);

        /** Display the frame and components within **/
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    /** Draw the fractal pixel by pixel. **/
    public void drawFractal() {
        for (int i = 0; i < dispSize; i++) {
            for (int j = 0; j < dispSize; j++) {
                double x = FractalGenerator.getCoord(range.x, range.x+range.width, dispSize, i);
                double y = FractalGenerator.getCoord(range.y, range.y+range.width, dispSize, j);
                double numIters = fGen.numIterations(x, y);
                if (numIters == -1) {
                    /** The pixel is not in the set. Color it black. **/
                    img.drawPixel(i, j, 0);
                }
                else {
                    /** The pixel is in the fractal set.
                     *  Color the pixel based on the number of iterations
                     *  it took to escape. 
                     */
                    float hue = 0.7f + (float) numIters / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    img.drawPixel(i, j, rgbColor);
                }
            }
        }
        img.repaint();
    }
    
    /** Simple handler to reset the zoom level. **/
    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            fGen.getInitialRange(range);
            drawFractal();
        }
    }
    
    /** Simple handler to zoom in on the clicked pixel. **/
    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            double x = FractalGenerator.getCoord(range.x, range.x+range.width, dispSize, e.getX());
            double y = FractalGenerator.getCoord(range.y, range.y+range.width, dispSize, e.getY());
            fGen.recenterAndZoomRange(range, x, y, 0.5);
            drawFractal();
        }
    }
    
    /** Run the application. **/
    public static void main(String[] args) {
        FractalExplorer fracExp = new FractalExplorer(500);
        fracExp.createAndShowGUI();
        fracExp.drawFractal();
    }
}
