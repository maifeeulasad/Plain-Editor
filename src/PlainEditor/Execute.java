package PlainEditor;

import javax.swing.*;
import java.io.File;

public class Execute {

    public static String fileName;

    public static void runProgramme(JTextPane jTextPane)
    {
        Runtime tem = Runtime.getRuntime();
        try
        {
            ToClassFile.toClassFile(jTextPane);

            int intPos=jTextPane.getText().indexOf("class ")+6;
            int lasPos=Math.min(Math.min(jTextPane.getText().indexOf("{",intPos),jTextPane.getText().indexOf(" ",intPos)),jTextPane.getText().indexOf("\n",intPos));

            fileName=jTextPane.getText().substring(intPos,lasPos);

            Process p = tem.exec(new String[] {"cmd","/C", "Start","cmd","/K","java",fileName},null,new File("C://PE//classFiles"));//.getOutputStream();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
