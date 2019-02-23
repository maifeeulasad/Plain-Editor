package PlainEditor;

public class WPMtype {

    private int y;
    private int m;
    private int d;
    int ho;
    int mi;
    int se;

    WPMtype(String x)
    {
        this.y=Integer.parseInt(x.substring(0,4)) ;
        this.m=Integer.parseInt(x.substring(4,6)) ;
        this.d=Integer.parseInt(x.substring(6,8)) ;
        this.ho=Integer.parseInt(x.substring(9,11)) ;
        this.mi=Integer.parseInt(x.substring(11,13)) ;
        this.se=Integer.parseInt(x.substring(14,15)) ;
    }
}
