package rest.androidObjecten;

import data.Customeigenschapingevuld;

public class AndroidCustomEigenschapIngevuld {
    private int customEigenscahpIngevuld_id;
    private int categorieId;
    private int filiaalId;
    private String inhoud;
    private String eenheid;
    private String naam;
    private String type;

    public AndroidCustomEigenschapIngevuld(Customeigenschapingevuld c) {
        this.customEigenscahpIngevuld_id = c.getCustomEigenschapIngevuld_id();
        this.categorieId = c.getCategorieId();
        this.filiaalId = c.getFiliaal().getFiliaalid();
        this.inhoud = c.getInhoud();
        this.eenheid = c.getEenheid();
        this.naam = c.getNaam();
        this.type = c.getType();
    }

    public int getCustomEigenscahpIngevuld_id() {
        return customEigenscahpIngevuld_id;
    }

    public void setCustomEigenscahpIngevuld_id(int customEigenscahpIngevuld_id) {
        this.customEigenscahpIngevuld_id = customEigenscahpIngevuld_id;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public int getFiliaalId() {
        return filiaalId;
    }

    public void setFiliaalId(int filiaalId) {
        this.filiaalId = filiaalId;
    }

    public String getInhoud() {
        return inhoud;
    }

    public void setInhoud(String inhoud) {
        this.inhoud = inhoud;
    }

    public String getEenheid() {
        return eenheid;
    }

    public void setEenheid(String eenheid) {
        this.eenheid = eenheid;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
