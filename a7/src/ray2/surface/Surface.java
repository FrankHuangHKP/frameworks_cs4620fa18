package ray2.surface;

import java.util.ArrayList;

import egl.math.Matrix4d;
import egl.math.Vector3d;
import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.light.Light;
import ray2.material.BSDF;

/**
 * Abstract base class for all surfaces.  Provides access for shader and
 * intersection uniformly for all surfaces.
 *
 * @author ags, ss932
 */
public abstract class Surface {
	/* tMat, tMatInv, tMatTInv are calculated and stored in each instance to avoid recomputing */
	
	/** The transformation matrix. */
	public Matrix4d tMat;
	
	/** The inverse of the transformation matrix. */
	protected Matrix4d tMatInv;
	
	/** The inverse of the transpose of the transformation matrix. */
	protected Matrix4d tMatTInv;
	
	/** The average position of the surface. Usually calculated by taking the average of 
	 * all the vertices. This point will be used in AABB tree construction. */
	public Vector3d averagePosition;
	
	/** The smaller coordinate (x, y, z) of the bounding box of this surface */
	public Vector3d minBound;
	
	/** The larger coordinate (x, y, z) of the bounding box of this surface */
	public Vector3d maxBound; 
	
	/** Shader to be used to shade this surface. */
	protected BSDF bsdf = BSDF.DEFAULT_BSDF;
	public void setBSDF(BSDF bsdf) { this.bsdf = bsdf; }
	public BSDF getBSDF() { return bsdf; }
	
	/** Light source associated with this surface.  This is  
	 * only used for surfaces generated by light sources. */
	protected Light light;
	public void setLight(Light light) { this.light = light; }
	public Light getLight() { return light; }
	
	public Vector3d getAveragePosition() { return averagePosition; } 
	public Vector3d getMinBound() { return minBound; }
	public Vector3d getMaxBound() { return maxBound; }	
	
	// initialization method
	public void init() {
		// do nothing
	}

	/**
	 * Un-transform rayIn using tMatInv 
	 * @param rayIn Input ray
	 * @return tMatInv * rayIn
	 */
	public Ray untransformRay(Ray rayIn) {
		Ray ray = new Ray(rayIn.origin, rayIn.direction);
		ray.start = rayIn.start;
		ray.end = rayIn.end;

		tMatInv.mulDir(ray.direction);
		tMatInv.mulPos(ray.origin);
		return ray;
	}
	
	public void setTransformation(Matrix4d a, Matrix4d aInv, Matrix4d aTInv) {
		tMat = a;
		tMatInv = aInv;
		tMatTInv = aTInv;
		computeBoundingBox();
	}
	
	/**
	 * Tests this surface for intersection with ray. If an intersection is found
	 * record is filled out with the information about the intersection and the
	 * method returns true. It returns false otherwise and the information in
	 * outRecord is not modified.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intersect
	 * @return true if the surface intersects the ray
	 */
	public abstract boolean intersect(IntersectionRecord outRecord, Ray ray);

	/**
	 * Compute the bounding box and store the result in
	 * averagePosition, minBound, and maxBound.
	 */
	public abstract void computeBoundingBox();
	
	/**
	 * Add this surface to the array list in. This array list will be used
	 * in the AABB tree construction.
	 */
	public void appendRenderableSurfaces(ArrayList<Surface> in) {
		in.add(this);
	}
}
