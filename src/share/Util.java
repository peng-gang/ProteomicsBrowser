package share;

public class Util {
    public static String doubleFormat(double x, double cutoffHigh, double cutoffLow){
        String rlt;
        if(x>=cutoffHigh || x<cutoffLow){
            rlt = String.format("%.2e", x);
        } else {
            rlt = String.format("%.2f",x);
        }
        return rlt;
    }

    public static String doubleFormat(double x){
        return doubleFormat(x, 10000, 0.0001);
    }

    public static String percentFormat(double x){
        String rlt = String.format("%.2f", x*100);
        //rlt = rlt + "%";
        return rlt;
    }
}
