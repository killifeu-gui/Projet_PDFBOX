import CalculatriceApp.*;
import CalculatriceClient.CorbaClientHelper;
import org.omg.CORBA.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class StartPdfClientExtractionSuppression {
    private static String readFileToBase64(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void main(String[] args) {
        try {
            // args[0]=pdf
            // args[1]=outputPath (pdf)
            // args[2]=page a extraire
            // args[3]=debut page a supprimer
            // args[4]=fin page a supprimer

            String pdfPath = (args.length > 0 ? args[0] : "input1.pdf");
            String outputPath = (args.length > 1 ? args[1] : "output_extraction.pdf");
            int pageToExtract = (args.length > 2 ? Integer.parseInt(args[2]) : 1);
            int startPageToDelete = (args.length > 3 ? Integer.parseInt(args[3]) : 1);
            int endPageToDelete = (args.length > 4 ? Integer.parseInt(args[4]) : 1);

            // ORB comme dans StartClient.
            ORB orb = ORB.init(args, null);

            PdfService pdf = CorbaClientHelper.getPdfService(orb);

            String pdfB64 = readFileToBase64(pdfPath);

            // 1) Extraction
            PlagePages page = new PlagePages(pageToExtract, pageToExtract);
            ResultatPdf extraction = pdf.extractionPage(pdfB64, page);
            if (!extraction.succes) {
                System.out.println("Extraction KO: " + extraction.message);
                return;
            }
            byte[] outExtraction = Base64.getDecoder().decode(extraction.nomFichierSortie);
            Files.write(new File(outputPath).toPath(), outExtraction);
            System.out.println("OK. Extraction ecrite dans: " + outputPath);


            // 2) Suppression
            PlagePages plageSuppression = new PlagePages(startPageToDelete, endPageToDelete);
            ResultatPdf suppression = pdf.suppressionPage(pdfB64, plageSuppression);
            if (!suppression.succes) {
                System.out.println("Suppression KO: " + suppression.message);
                return;
            }

            // nomFichierSortie contient le base64 du PDF résultat dans ce TP
            byte[] outSuppression = Base64.getDecoder().decode(suppression.nomFichierSortie);
            String outputSuppressionPath = "output_suppression.pdf";
            Files.write(new File(outputSuppressionPath).toPath(), outSuppression);

            System.out.println("OK. Suppression ecrite dans: " + outputSuppressionPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
