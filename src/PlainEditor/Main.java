package PlainEditor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static PlainEditor.util.appendWithStyle;

public class Main
{

    public static int ser=1;

    public static File folder = new File("C:\\PE");
    public static File classFolder = new File("C:\\PE\\classFiles");
    public static File wpmFolder = new File("C:\\PE\\wpmFiles");
    public static File pdfFolder = new File("C:\\PE\\pdfFiles");


    public static void main(String[] args) {
        try
        {
            if(!folder.exists())
            {
                folder.mkdir();
            }
            if(!classFolder.exists())
            {
                classFolder.mkdir();
            }
            if(!wpmFolder.exists())
            {
                wpmFolder.mkdir();
            }
            if(!pdfFolder.exists())
            {
                pdfFolder.mkdir();
            }
        }
        catch (Exception e)
        {
            JFrame errors=new JFrame("Warning");
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            errors.setSize(500,500);
            errors.setLocation(dim.width/2-errors.getSize().width/2, dim.height/2-errors.getSize().height/2);
            JTextPane errorText=new JTextPane();
            JScrollPane sp = new JScrollPane(errorText);
            errors.getContentPane().add( sp );
            appendWithStyle(errorText,"\t\tWarning!!!\n\n","Plain Editor doesn't have full access to file system\n" +
                    "Some features may not be available\n" +
                    "\nTo enjoy full usage , please run it as Admin or change Drive-C content write/read/edit previlage ",true);
            errorText.setEditable(false);
            errors.setVisible(true);
        }
        new ui();


    }

}
