import CalculatriceApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
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
            // args:
            // [0]=pdf input
            // [1]=outExtraction.pdf
            // [2]=outSuppression.pdf
            // [3]=page extraction (debut)
            // [4]=debut suppression
            // [5]=fin suppression

            String inPath = (args.length > 0 ? args[0] : "input.pdf");
            String outExtractPath = (args.length > 1 ? args[1] : "output_extraction.pdf");
            String outSuppPath = (args.length > 2 ? args[2] : "output_suppression.pdf");

            int page = (args.length > 3 ? Integer.parseInt(args[3]) : 1);
            int supDeb = (args.length > 4 ? Integer.parseInt(args[4]) : 1);
            int supFin = (args.length > 5 ? Integer.parseInt(args[5]) : 1);

            ORB orb = ORB.init(args, null);

            PdfService pdf = null;
            try {
                // 1) Essayer NamingService
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
                org.omg.CORBA.Object refObj = ncRef.resolve_str("PdfService");
                pdf = PdfServiceHelper.narrow(refObj);
            } catch (Exception e) {
                // 2) Fallback via IOR file
                try {
                    java.nio.file.Path p = java.nio.file.Paths.get("c:/RONDOMNUMBER9/TP_Corba/ior.txt");
                    String txt = new String(java.nio.file.Files.readAllBytes(p), java.nio.charset.StandardCharsets.UTF_8);
                    String iorPdf = null;
                    for (String line : txt.split("\\r?\\n")) {
                        if (line.startsWith("IOR_PDF=")) {
                            iorPdf = line.substring("IOR_PDF=".length()).trim();
                        }
                    }
                    if (iorPdf == null) throw new RuntimeException("IOR_PDF absent dans ior.txt");

                    org.omg.CORBA.Object iorObj = orb.string_to_object(iorPdf);
                    pdf = PdfServiceHelper.narrow(iorObj);
                    System.out.println("[INFO] PdfService récupéré via ior.txt");
                } catch (Exception e2) {
                    throw new RuntimeException("Impossible de contacter NamingService et impossible de lire ior.txt", e2);
                }
            }


            String inB64 = readFileToBase64(inPath);


            // extraction
            PlagePages plageExtract = new PlagePages(page, page);
            ResultatPdf extract = pdf.extractionPage(inB64, plageExtract);
            if (!extract.succes) {
                System.out.println("Extraction KO: " + extract.message);
                return;
            }
            Files.write(new File(outExtractPath).toPath(), Base64.getDecoder().decode(extract.nomFichierSortie));

            // suppression
            PlagePages plageSup = new PlagePages(supDeb, supFin);
            ResultatPdf sup = pdf.suppressionPage(inB64, plageSup);
            if (!sup.succes) {
                System.out.println("Suppression KO: " + sup.message);
                return;
            }
            Files.write(new File(outSuppPath).toPath(), Base64.getDecoder().decode(sup.nomFichierSortie));

            System.out.println("OK. Extraction->" + outExtractPath + " / Suppression->" + outSuppPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

