package com.pir.fastpir.component;


public class KeyInfo {
        String pubN;
        String pubE;
        String priN;
        String priD;

        public PubKeyInfo getPubKey(){
                PubKeyInfo pubKeyInfo = new PubKeyInfo();
                pubKeyInfo.setPubE(pubE);
                pubKeyInfo.setPubN(pubN);
                return pubKeyInfo;
        }

        public String getPubN() {
                return pubN;
        }

        public void setPubN(String pubN) {
                this.pubN = pubN;
        }

        public String getPubE() {
                return pubE;
        }

        public void setPubE(String pubE) {
                this.pubE = pubE;
        }

        public String getPriN() {
                return priN;
        }

        public void setPriN(String priN) {
                this.priN = priN;
        }

        public String getPriD() {
                return priD;
        }

        public void setPriD(String priD) {
                this.priD = priD;
        }
}
