package rest.androidObjecten;

import data.Eigenschappenbedrijfingevuld;

public class AndroidEigenschappenIngevuld {
    private int eigenschappenBedrijfIngevuldId;
    private int filiaalId;
    private int categorieId;
    private String inhoud;
    private String eenheid;
    private String naam;
    private String type;

    public AndroidEigenschappenIngevuld(Eigenschappenbedrijfingevuld e) {
        this.eigenschappenBedrijfIngevuldId = e.getEigenschappenbedrijfingevuldId();
        this.filiaalId = e.getFiliaal().getFiliaalid();
        this.categorieId = e.getCategorieId();
        this.inhoud = e.getInhoud();
        this.eenheid = e.getEenheid();
        this.naam = e.getNaam();
        this.type = e.getType();
    }

    public int getEigenschappenBedrijfIngevuldId() {
        return eigenschappenBedrijfIngevuldId;
    }

    public void setEigenschappenBedrijfIngevuldId(int eigenschappenBedrijfIngevuldId) {
        this.eigenschappenBedrijfIngevuldId = eigenschappenBedrijfIngevuldId;
    }

    public int getFiliaalId() {
        return filiaalId;
    }

    public void setFiliaalId(int filiaalId) {
        this.filiaalId = filiaalId;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
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
