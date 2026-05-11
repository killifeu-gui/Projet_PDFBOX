import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import java.io.File;

public class AddPage {
    public static void main(String[] args) throws Exception {
        PDDocument doc = PDDocument.load(new File("dummy.pdf"));
        doc.addPage(new PDPage());
        doc.save("multi_page.pdf");
        doc.close();
        System.out.println("Page ajoutée");
    }
}