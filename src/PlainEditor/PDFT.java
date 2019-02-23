package PlainEditor;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import java.io.FileOutputStream;

public class PDFT {

    public static String toPdf(JTextPane jTextPane)
    {
        String fileName=Main.pdfFolder+"\\~tempPE"+util.timeStamp()+".pdf";
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            if(jTextPane.getText()==null || jTextPane.getText().equals(""))
            {
                Paragraph paragraph = new Paragraph("");
                paragraph.setAlignment(Element.ALIGN_LEFT);
                document.add(paragraph);

                document.close();
            }
            Paragraph paragraph = new Paragraph(jTextPane.getText());
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
