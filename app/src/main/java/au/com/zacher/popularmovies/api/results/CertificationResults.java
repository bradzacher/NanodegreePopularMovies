package au.com.zacher.popularmovies.api.results;

import au.com.zacher.popularmovies.model.Certification;

/**
 * Created by Brad on 11/07/2015.
 */
public class CertificationResults {
    public CertificationList certifications;

    public class CertificationList {
        public Certification[] RU;
        public Certification[] US;
        public Certification[] CA;
        public Certification[] AU;
        public Certification[] FR;
        public Certification[] DE;
        public Certification[] TH;
        public Certification[] KR;
        public Certification[] GB;
        public Certification[] BR;
    }
}
