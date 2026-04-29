import CalculatriceApp.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

/**
 * Implémentation de l'interface Calculatrice
 */
public class CalculatriceImpl extends CalculatricePOA 
{
    private ORB orb;
    private long compteurOperations = 0;

    public CalculatriceImpl() 
    {
    }

    public void setORB(ORB orb_val) 
    {
        orb = orb_val;
    }

    @Override
    public double add(double a, double b) 
    {
        compteurOperations++;
        return a + b;
    }

    @Override
    public double substract(double a, double b) 
    {
        compteurOperations++;
        return a - b;
    }

    @Override
    public double multiply(double a, double b) 
    {
        compteurOperations++;
        return a * b;
    }

    @Override
    public double divide(double numerateur, double denominateur) 
        throws DivisionParZeroException 
    {
        if (denominateur == 0) 
            throw new DivisionParZeroException("Division par zéro");

        compteurOperations++;
        return numerateur / denominateur;
    }

    // ✅ CORRECTION CORBA : int et pas long
    @Override
    public int modulo(int dividende, int diviseur) 
        throws DivisionParZeroException 
    {
        if (diviseur == 0) 
            throw new DivisionParZeroException("Modulo par zéro");

        compteurOperations++;
        return dividende % diviseur;
    }

    // ✅ CORRECTION CORBA : exposant = int
    @Override
    public double power(double base, int exposant) 
    {
        compteurOperations++;
        return Math.pow(base, exposant);
    }

    @Override
    public ResultatCalcul operationGenerique(
        TypeOperation operation, 
        double a, 
        double b) 
        throws DivisionParZeroException, OperationNonSupporteeException 
    {
        ResultatCalcul resultat = new ResultatCalcul();

        // ✅ CORRECTION TYPE int
        resultat.timestampUnix = (int)(System.currentTimeMillis() / 1000);

        switch (operation.value()) 
        {
            case TypeOperation._ADDITION:
                resultat.valeur = a + b;
                resultat.operationEffectuee = "Addition";
                break;

            case TypeOperation._SOUSTRACTION:
                resultat.valeur = a - b;
                resultat.operationEffectuee = "Soustraction";
                break;

            case TypeOperation._MULTIPLICATION:
                resultat.valeur = a * b;
                resultat.operationEffectuee = "Multiplication";
                break;

            case TypeOperation._DIVISION:
                if (b == 0) 
                    throw new DivisionParZeroException("Division par zéro");

                resultat.valeur = a / b;
                resultat.operationEffectuee = "Division";
                break;

            case TypeOperation._PUISSANCE:
                resultat.valeur = Math.pow(a, (int)b);
                resultat.operationEffectuee = "Puissance";
                break;

            default:
                throw new OperationNonSupporteeException(
                    "Operation inconnue",
                    "Type non supporté"
                );
        }

        compteurOperations++;
        return resultat;
    }

    // ✅ CORRECTION : interface attend int
    @Override
    public int getNombreOperations() 
    {
        return (int) compteurOperations;
    }

    @Override
    public void reinitialiserCompteur() 
    {
        compteurOperations = 0;
    }

    @Override
    public void shutdown() 
    {
        orb.shutdown(false);
    }
}
