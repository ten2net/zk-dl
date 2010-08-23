package cz.datalite.helpers;

/**
 * Logicke funkce
 */
public abstract class BooleanHelper
{

    /**
     * Prevod ze znaku
     *
     * @param value Prevadena hodnota
     * @return true pokud text je a nebo y
     */
    public static boolean isTrue(Character value)
    {
        if (value == null)
        {
            return false;
        }

        return value.equals('Y') || value.equals('y') || value.equals('A') || value.equals('a');
    }

    /**
     * Prevod z textoveho retezce
     *
     * @param value Prevadena hodnota
     * @return true pokud text obsahuje true, yes, ano nebo 1
     */
    public static boolean isTrue(String value)
    {
        if (value == null)
        {
            return false;
        }

       value = value.trim() ;

        return (
                StringHelper.isEqualsIgnoreCase(value, "true")
                        || StringHelper.isEqualsIgnoreCase(value, "yes")
                        || StringHelper.isEqualsIgnoreCase(value, "y")
                        || StringHelper.isEqualsIgnoreCase(value, "ano")
                        || StringHelper.isEqualsIgnoreCase(value, "a")
                        || StringHelper.isEqualsIgnoreCase(value, "1")
        );
    }

    /**
     * Prevod z textoveho retezce
     *
     * @param value Prevadena hodnota
     * @return true pokud text obsahuje false, no, ne nebo 0
     */
    public static boolean isFalse(String value)
    {
        value = value.trim();

        return (
                StringHelper.isEqualsIgnoreCase(value, "false")
                        || StringHelper.isEqualsIgnoreCase(value, "no")
                        || StringHelper.isEqualsIgnoreCase(value, "ne")
                        || StringHelper.isEqualsIgnoreCase(value, "0")
        );
    }
}