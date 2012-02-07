package de.hpi.bpt.hypergraph.abs;

/**
 * Basic graph vertex interface
 * 
 * @author Artem Polyvyanyy
 */
public interface IVertex extends IGObject {
	/**
	 * Get vertex coordinate X
	 * @return x
	 */
	int getX();
	
	/**
	 * Set vertex coordinate X
	 * @param x
	 */
	void setX(int x);
	
	/**
	 * Get vertex coordinate Y
	 * @return y
	 */
	int getY();
	
	/**
	 * Set vertex coordinate Y
	 * @param y
	 */
	void setY(int y);
	
	/**
	 * Get vertex width
	 * @return Width
	 */
	int getWidth();
	
	/**
	 * Set vertex width
	 * @param w Width
	 */
	void setWidth(int w);
	
	/**
	 * Get vertex height
	 * @return Height
	 */
	int getHeight();
	
	/**
	 * Set vertex height
	 * @param h Height
	 */
	void setHeight(int h);
	
	/**
	 * Set vertex location
	 * @param x
	 * @param y
	 */
	void setLocation(int x, int y);
	
	/**
	 * Set vertex size
	 * @param w Width
	 * @param h Height
	 */
	void setSize(int w, int h);
	
	/**
	 * Set vertex layout information
	 * @param x
	 * @param y
	 * @param w Width
	 * @param h Height
	 */
	void setLayout(int x, int y, int w, int h);
}
