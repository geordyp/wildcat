package edu.ksu.wildcat;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Defines the colors used for syntax highlighting
 * 
 * @author geordypaul
 */
public class ColorProvider {
	
	// environment, method, model, variables, interface, responses
	public static final RGB MAIN_KEYWORDS = new RGB(100, 0, 200);		// purple
	
	// tabular_graphics_data, continuous_design, no_gradients, ...
	public static final RGB REGULAR_KEYWORDS = new RGB(0, 0, 200);		// blue
	
	public static final RGB COMMENTS = new RGB(140, 180, 0);			// yellow
	public static final RGB STRINGS = new RGB(0, 150, 0);				// green
	public static final RGB DEFAULT = new RGB(0, 0, 0);					// black
	
	protected Map<RGB, Color> colorTable = new HashMap<RGB, Color>(10);

	
	/**
	 * Given a rgb value, returns the color
	 * 
	 * @param rgb - the color to be checked
	 * @return Color - the color with the given rgb
	 */
	public Color getColor(RGB rgb) {
		Color color = (Color) colorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}
		return color;
	}

}
