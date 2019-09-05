package ftc.vision.Glyph;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import ftc.vision.ImageUtil;

public class GlyphResult {

    private final GlyphColor glyphColor;

    private final Point loc;

    public GlyphResult(GlyphColor glyphColor, Point loc) {
        this.glyphColor = glyphColor;
        this.loc = loc;
    }

    public GlyphColor getGlyphColor() {return glyphColor;}

    public Point getLoc() {
        return loc;
    }

    public enum GlyphColor {
        BROWN (ImageUtil.BROWN),
        GRAY (ImageUtil.GRAY);

        public final Scalar color;

        GlyphColor(Scalar color) {this.color = color;}
    }

    @Override
    public String toString() {
        if (glyphColor != null) {
            return glyphColor + " Glyph at (" + loc.x + ", " + loc.y + ")";
        } else {
            return "Unable to find glyph.";
        }
    }
}
