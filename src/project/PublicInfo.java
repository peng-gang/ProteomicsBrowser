package project;

/**
 * Created by gpeng on 4/19/17.
 */
public class PublicInfo {
    public enum ScaleType {
        Regular, Log2, Log10
    }

    public enum ProteinIntegrationType {
        Raw, iBAQ, NSAF, Top3
    }

    public enum ProteinStatus{
        Unnormalized, Median, SelProtein
    }

    public enum ErrorMsgType{
        ProteinNotFound, PepNotMatch, PepInMultipleProtein
    }
}
