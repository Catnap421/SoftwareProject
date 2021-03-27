package hw1;

public class HW1 {
    private static double computeArea(Point3d p1, Point3d p2, Point3d p3) {
        // Compute the contained area using Heron's formula
        double sideA = p1.distanceTo(p2);
        double sideB = p2.distanceTo(p3);
        double sideC = p1.distanceTo(p3);
        double s = 0.5 * (sideA + sideB + sideC);
        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }

    private static void checkAndComputeArea(Point3d p1, Point3d p2, Point3d p3) {
        // If two of the points are equal, bail
        if (p1.equals(p2) || p2.equals(p3) || p3.equals(p1)) {
            System.out.println("Two points are equal; not computing area");
            return;
        }
        // Otherwise print out the area of the triangle
        else {
            String output = String.format("The area is %f", computeArea(p1, p2, p3));
            System.out.println(output);
        }
    }

    public static void main(String[] args) {
        checkAndComputeArea(
                new Point3d(1, 2, 3),
                new Point3d(3.14, 2.71828, 1.414),
                new Point3d(0, 5, 1.72)
        );
        checkAndComputeArea(
                new Point3d(3.14, 2.71828, 1.414),
                new Point3d(1, 2, 3),
                new Point3d(3.14, 2.71828, 1.414)
        );
    }
}