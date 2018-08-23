package be.retailsonar.retailsonar;

import android.app.Application;

import java.security.Key;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import be.retailsonar.be.retailsonar.objects.CustomEigenschapIngevuld;
import be.retailsonar.be.retailsonar.objects.EigenschappenIngevuld;

public class GlobaleVariabelen extends Application {
    private String ipServer = "192.168.0.163";
    private int id;
    private String loggedInUser;
    private String[] filiaalNamen;
    private String[] addresLijst;
    private String[] idLijst;
    private int filiaalid;
    private List<CustomEigenschapIngevuld> customEigenschapIngevuldLijst;
    private List<EigenschappenIngevuld> eigenschappenIngevuldLijst;
    private boolean taakUitgevoerd = false;
    String[] taken;
    private int huidigFiliaalId;


    private static final String KEY_DATA = "dkz45KZADH@#!!EF684pm";
    public static Key JWT_KEY = new SecretKeySpec(KEY_DATA.getBytes(), 0, 16, "AES");


    public List<CustomEigenschapIngevuld> getCustomEigenschapIngevuldLijst() {
        return customEigenschapIngevuldLijst;
    }

    public boolean isTaakUitgevoerd() {
        return taakUitgevoerd;
    }

    public void setFiliaalid(int i){
        this.filiaalid=i;
    }
    public int getFiliaalid(){
        return this.filiaalid;
    }

    public void setTaakUitgevoerd(boolean taakUitgevoerd) {
        this.taakUitgevoerd = taakUitgevoerd;
    }

    public void setCustomEigenschapIngevuldLijst(List<CustomEigenschapIngevuld> customEigenschapIngevuldLijst) {
        this.customEigenschapIngevuldLijst = customEigenschapIngevuldLijst;
    }

    public List<EigenschappenIngevuld> getEigenschappenIngevuldLijst() {
        return eigenschappenIngevuldLijst;
    }

    public void setEigenschappenIngevuldLijst(List<EigenschappenIngevuld> eigenschappenIngevuldLijst) {
        this.eigenschappenIngevuldLijst = eigenschappenIngevuldLijst;
    }

    public String[] getAddresLijst() {
        return addresLijst;
    }

    public void setAddresLijst(String[] addresLijst) {
        this.addresLijst = addresLijst;
    }

    public String[] getIdLijst() {
        for(int i=0;i<idLijst.length;i++) {
            System.out.println("id"+idLijst[i]);


        }
        return idLijst;
    }

    public String[] getTaken() {
        return taken;
    }

    public void setTaken(String[] taken) {
        this.taken = taken;
    }

    public void setIdLijst(String[] idLijst) {
        this.idLijst = idLijst;
    }

    public String[] getFiliaalNamen() {
        return filiaalNamen;
    }

    public void setFiliaalNamen(String[] filiaalNamen) {
        this.filiaalNamen = filiaalNamen;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHuidigFiliaalId() {
        return huidigFiliaalId;
    }

    public void setHuidigFiliaalId(int huidigFiliaalId) {
        this.huidigFiliaalId = huidigFiliaalId;
    }

    public Key getJWTKey() {
        return JWT_KEY;
    }

    public String getIpServer(){
        return ipServer;
    }

    public void setIpServer(String ipServer){
        this.ipServer = ipServer;
    }

    public HashMap<String, Object> getClaimsFromEigenschappen(List<EigenschappenIngevuld> eigenschappenLijst, List<CustomEigenschapIngevuld> customEigenschappenLijst, String[] inhouden){
        int eigenschappenSize = 0;
        if(eigenschappenIngevuldLijst!=null) eigenschappenSize = eigenschappenIngevuldLijst.size();
        int customEigenschappenSize = 0;
        if(customEigenschappenLijst!=null) customEigenschappenSize = customEigenschappenLijst.size();
        int eLengte = eigenschappenSize;

        int ingevuldTeller = 0;
        String inhoud = "";

        for(int i=0; i<inhouden.length; i++){
            if(i<eLengte){
                inhoud = inhouden[i];
                if(inhoud!=null&&!inhoud.equals("")) ingevuldTeller++;
                eigenschappenLijst.get(i).setInhoud(inhoud);
            }
            else{
                inhoud = inhouden[i];
                if(inhoud!=null&&!inhoud.equals("")) ingevuldTeller++;
                customEigenschappenLijst.get(i-eLengte).setInhoud(inhoud);
            }
        }

        HashMap<String, Object> claims = new HashMap<String, Object>();
        if(ingevuldTeller==(eigenschappenSize+customEigenschappenSize)) claims.put("ingevuld", "y");
        else claims.put("ingevuld", "n");
        claims.put("eigenschappenIngevuld", eigenschappenLijst);
        claims.put("customEigenschappenIngevuld", customEigenschappenLijst);

        return claims;
    }

}
