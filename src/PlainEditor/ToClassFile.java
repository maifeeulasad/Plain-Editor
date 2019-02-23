package PlainEditor;

import javax.swing.*;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

import static PlainEditor.util.appendWithStyle;

public class ToClassFile {

    public static String err;

    public static void toClassFile(File file) throws IOException
    {
        JavaCompiler javaCompiler=ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null,null,null);
        File javaFile=file;
        Iterable fileObject=standardJavaFileManager.getJavaFileObjects(javaFile);
        javaCompiler.getTask(null,standardJavaFileManager,null,null,null,fileObject).call();
        standardJavaFileManager.close();
    }
    public static void toClassFile(JTextPane jTextPane) throws IOException
    {
        err="";
        JavaCompiler javaCompiler=ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null,null,null);

        String fileName="";

        int intPos=jTextPane.getText().indexOf("class ")+6;
        int lasPos=Math.min(Math.min(jTextPane.getText().indexOf("{",intPos),jTextPane.getText().indexOf(" ",intPos)),jTextPane.getText().indexOf("\n",intPos));

        fileName=jTextPane.getText().substring(intPos,lasPos);
        File javaFile=new File("C:\\PE\\classFiles\\"+fileName+".java");
        File temFile=new File("C:\\PE\\classFiles\\"+fileName+".class");

        Files.deleteIfExists(javaFile.toPath());
        Files.deleteIfExists(temFile.toPath());

        PrintWriter writer;
        try{
            writer = new PrintWriter(javaFile, "UTF-8");
            writer.print(jTextPane.getText());
            writer.close();
            Iterable fileObject=standardJavaFileManager.getJavaFileObjects(javaFile);
            err = javaCompiler.getTask(null,standardJavaFileManager,null,null,null,fileObject).call().toString();

            standardJavaFileManager.close();

            if(err.equals("true"))
                new Decompiler("C:\\PE\\classFiles\\"+fileName+".class");
            else
            {
                JFrame errors=new JFrame("Errors");
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                errors.setSize(500,500);
                errors.setLocation(dim.width/2-errors.getSize().width/2, dim.height/2-errors.getSize().height/2);
                JTextPane errorText=new JTextPane();
                JScrollPane sp = new JScrollPane(errorText);
                errors.getContentPane().add( sp );
                appendWithStyle(errorText,"Error Accourd!\nGiven below : \n","\n\n\n\n\t\tCompilation Error",true);
                errorText.setEditable(false);
                errors.setVisible(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JFrame errors=new JFrame("Errors");
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            errors.setSize(500,500);
            errors.setLocation(dim.width/2-errors.getSize().width/2, dim.height/2-errors.getSize().height/2);
            JTextPane errorText=new JTextPane();
            JScrollPane sp = new JScrollPane(errorText);
            errors.getContentPane().add( sp );
            appendWithStyle(errorText,"Error Accourd!\nGiven below : \n",e.toString(),false);
            errorText.setEditable(false);
            errors.setVisible(true);
        }
    }
}
