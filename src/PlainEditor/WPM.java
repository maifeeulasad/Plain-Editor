package PlainEditor;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WPM {

    static Vector<String> data=new Vector<String>();
    static Vector<WPMtype> res=new Vector<WPMtype>();
    static Vector<Double> difs=new Vector<Double>();

    public static void resToDif()
    {
        for(int i=1;i<res.size();i++)
        {
            WPMtype a1=res.elementAt(i-1);
            WPMtype a2=res.elementAt(i);
            double sec=(a2.ho-a1.ho)*60*60+(a2.mi-a1.mi)*60+(a2.se-a1.se);
            difs.add(sec);
        }
    }

    public static double sumDifs(Vector<Double> diff)
    {
        double a=0;
        for(int i=0;i<diff.size();i++)
            a+=diff.elementAt(i);
        return a;
    }

    public static void calculate()
    {
        listing(Main.wpmFolder);

        List<Double> wpm = new ArrayList<>();

        for(int i=0;i<31;i++)
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.now().minusDays(i);
            calculateSpecific(dtf.format(localDate));

            resToDif();

            double ans;
            if(difs.size()<3)
                ans=0;
            else
                ans=sumDifs(difs)/(-1+difs.size());
            wpm.add(i,ans);
        }
        GraphPanel.createAndShowGui(wpm);
    }

    private static void calculateSpecific(String date)
    {
        try
        {
            res.clear();

            Enumeration e = data.elements();
            while (e.hasMoreElements()) {
                if(e.nextElement().toString().startsWith(date))
                    res.add(new WPMtype(e.nextElement().toString()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void listing(File folder) {
        try {
            for (File fileEntry : folder.listFiles()) {
                if (!fileEntry.isDirectory()) {
                    data.add(fileEntry.getName());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
