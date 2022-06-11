package com.github.ZombifiedPatato.pumpkin_patch.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A immutable box which is not axis-aligned. The origin coordinate is
 * the point from which you describe the box using the height, width and length vectors.
 */
public class Box {

    private final Vec3d origin;
    private final Vec3d height;
    private final Vec3d width;
    private final Vec3d length;

    /**
     * Creates a box with origin point and vectors.
     * @param originX x coordinate of origin point
     * @param originY y coordinate of origin point
     * @param originZ z coordinate of origin point
     * @param height the height of the box from the origin point
     * @param width the width of the box from the origin point
     * @param length the length of the box from the origin
     */
    public Box(double originX, double originY, double originZ, Vec3d height, Vec3d width, Vec3d length) {
        this.origin = new Vec3d(originX, originY, originZ);
        this.height = height;
        this.width = width;
        this.length = length;
    }

    /**
     * Creates a box with origin point and vectors.
     * @param origin the origin point
     * @param height the height of the box from the origin point
     * @param width the width of the box from the origin point
     * @param length the length of the box from the origin
     */
    public Box(Vec3d origin, Vec3d height, Vec3d width, Vec3d length) {
        this.origin = origin;
        this.height = height;
        this.width = width;
        this.length = length;
    }

    /**
     * Get all entities except the given one which positions are inside this box.
     * @param entity The entity which needs to be excepted.
     * @return A linked list of all entities inside this box except the given entity
     */
    public List<LivingEntity> getOtherLivingEntities(LivingEntity entity) {
        net.minecraft.util.math.Box box = new net.minecraft.util.math.Box(this.origin, this.origin.add(this.height)
                .add(this.width).add(this.length));
        List<Entity> entityList = entity.getEntityWorld().getOtherEntities(entity, box);

        List<LivingEntity> validEntities = new LinkedList<>();
        for (Entity e : entityList) {
            if (e instanceof LivingEntity living) {
                if (isPosInBox(living.getEyePos()) || isPosInBox(living.getPos())) {
                    validEntities.add(living);
                }
            }

        }
        return validEntities;
    }

    /**
     * Check if pos is inside box.
     * @param pos The position to check if it is inside this box
     * @return True if this pos is inside this box, false otherwise
     */
    private boolean isPosInBox (Vec3d pos) {
        //for now pos checking later collision box checking using SAT
        Vec3d heightNeg = this.height.negate();
        Vec3d widthNeg = this.width.negate();
        Vec3d lengthNeg = this.length.negate();
        double heightDotProduct1 = heightNeg.dotProduct(this.origin.add(this.height));
        double heightDotProduct2 = heightNeg.dotProduct(this.origin);
        double heightDotProductPos = heightNeg.dotProduct(pos);
        double widthDotProduct1 = widthNeg.dotProduct(this.origin.add(this.width));
        double widthDotProduct2 = widthNeg.dotProduct(this.origin);
        double widthDotProductPos = widthNeg.dotProduct(pos);
        double lengthDotProduct1 = lengthNeg.dotProduct(this.origin.add(this.length));
        double lengthDotProduct2 = lengthNeg.dotProduct(this.origin);
        double lengthDotProductPos = lengthNeg.dotProduct(pos);
        return ((heightDotProduct1 <= heightDotProductPos && heightDotProduct2 >= heightDotProductPos) ||
                (heightDotProduct1 >= heightDotProductPos && heightDotProduct2 <= heightDotProductPos)) &&
                ((widthDotProduct1 <= widthDotProductPos && widthDotProduct2 >= widthDotProductPos) ||
                        (widthDotProduct1 >= widthDotProductPos && widthDotProduct2 <= widthDotProductPos)) &&
                ((lengthDotProduct1 <= lengthDotProductPos && lengthDotProduct2 >= lengthDotProductPos) ||
                        (lengthDotProduct1 >= lengthDotProductPos && lengthDotProduct2 <= lengthDotProductPos));
    }

    public Vec3d getOrigin() {
        return origin;
    }

    public Vec3d getHeight() {
        return height;
    }

    public Vec3d getWidth() {
        return width;
    }

    public Vec3d getLength() {
        return length;
    }

    /**
     * Method to check if two boxes are exactly the same in origin point and vectors.
     * @param o The object to compare this object to
     * @return True if the objects are the same, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Objects.equals(origin, box.origin) && Objects.equals(height, box.height) && Objects.equals(width, box.width) && Objects.equals(length, box.length);
    }

    /**
     * Method to generate a hashcode for this object.
     * @return This object's hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(origin, height, width, length);
    }

    /**
     * Method to convert this object to a string.
     * @return This box as a String
     */
    @Override
    public String toString() {
        return "Box{" +
                "origin=" + origin +
                ", height=" + height +
                ", width=" + width +
                ", length=" + length +
                '}';
    }
}
