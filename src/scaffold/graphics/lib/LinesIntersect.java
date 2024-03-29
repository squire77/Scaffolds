package scaffold.graphics.lib;

public class LinesIntersect {
    public static final int DONT_INTERSECT = 0, COLLINEAR = 1, DO_INTERSECT = 2;

    public static double x =0, y=0; //result

    public static int check(double  x1, double  y1, double  x2, double  y2,
                            double  x3, double  y3, double  x4, double  y4) {
        double  a1, a2, b1, b2, c1, c2;
        double  r1, r2 , r3, r4;
        double  denom, offset, num;

        //Compute a1, b1, c1, where line joining points 1 and 2 is "a1 x + b1 y + c1 = 0".
        a1 = y2 - y1;
        b1 = x1 - x2;
        c1 = (x2 * y1) - (x1 * y2);

        //Compute r3 and r4.
        r3 = ((a1 * x3) + (b1 * y3) + c1);
        r4 = ((a1 * x4) + (b1 * y4) + c1);

        //Check signs of r3 and r4. If both point 3 and point 4 lie on
        //same side of line 1, the line segments do not intersect.
        if ((r3 != 0) && (r4 != 0) && same_sign(r3, r4))
            return DONT_INTERSECT;

        //Compute a2, b2, c2
        a2 = y4 - y3;
        b2 = x3 - x4;
        c2 = (x4 * y3) - (x3 * y4);

        //Compute r1 and r2
        r1 = (a2 * x1) + (b2 * y1) + c2;
        r2 = (a2 * x2) + (b2 * y2) + c2;

        //Check signs of r1 and r2. If both point 1 and point 2 lie
        //on same side of second line segment, the line segments do not intersect.
        if ((r1 != 0) && (r2 != 0) && (same_sign(r1, r2)))
            return DONT_INTERSECT;

        //Line segments intersect: compute intersection point.
        denom = (a1 * b2) - (a2 * b1);

        if (denom == 0)
            return COLLINEAR;

        offset = (denom < 0) ? -denom / 2 : denom / 2 ;

        //The denom/2 is to get rounding instead of truncating. It
        //is added or subtracted to the numerator, depending upon the sign of the numerator.
        num = (b1 * c2) - (b2 * c1);
        x = (num < 0) ? (num - offset) / denom : (num + offset) / denom;
        num = (a2 * c1) - (a1 * c2);
        y = (num < 0) ? ( num - offset) / denom : (num + offset) / denom;

        return DO_INTERSECT;
    }

    private static boolean same_sign(double a, double b) {
        return (( a * b) >= 0);
    }
}



