
package ggc.cgm.device;


public class CGMException extends Exception
{

	
    /**
     * Constructor for ImportException.
     */
    public CGMException()
    {
        super();
    }

    /**
     * Constructor for ImportException.
     * @param message
     */
    public CGMException(String message)
    {
        super(message);
    }

    /**
     * Constructor for ImportException.
     * @param message
     * @param cause
     */
    public CGMException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for ImportException.
     * @param cause
     */
    public CGMException(Throwable cause)
    {
        super(cause);
    }

}
