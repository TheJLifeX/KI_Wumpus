package de.fh.suche;

import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.util.Vector2;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by daniel on 22.09.16.
 * Klasse für unsere Suchalgorithmen
 */
public class Knoten {

    //Knoten haben bis auf die Wurzel Vorgänger
    protected Knoten vorgaenger;

    /**
     * Knotenbewertung eines Knoten x nach f(x)=g(x)+h(x) mit
     * g(x) die bisherigen Kosten vom Startknoten aus
     * h(x) die geschätzten Kosten von x bis zum Zielknoten
     * @return die heuristische Bewertung des Knoten x
     */
    protected float pfadkosten = 0f;
    protected float schaetzwert = 0f;

    //Die Felderbelegung der (virtuellen) aktuellen Welt
    //allerdings ohne den Pacman (Pacmanposition wird in pos gespeichert)
    private PacmanTileType[][] view;

    //Die Felderbelegung wird zusätzlich als Stringarray gespeichert
    //um Knoten effizienter zu vergleichen
    private String sView;

    //Die Position des Pacmans, während der Suche
    private Vector2 pos = new Vector2(0,0);

    //Ein Knoten erzeugt sich aus dem Vorgänger und Bewegungsrichtung
    private LinkedBlockingQueue<PacmanAction> pacmanActionList = new LinkedBlockingQueue<PacmanAction>();

    public Knoten(PacmanTileType[][] view, Vector2 pos){
        //Konstruktor für den Zielzustand und das Wurzelelement
        //Der Wurzelknoten und Zielzustand hat keinen Vorgänger
        this.vorgaenger = null;
        //Die Wurzel kennt die Ausgangsposition des Pacmans
        this.pos = pos;

        //Erzeuge neues view auf Basis des gegebenen Views
        this.view = new PacmanTileType[view.length][view[0].length];
        this.view = copyArray(view);

        //Der Zielzustand kennt keinen Position
        if(this.pos != null){
            //..die Wurzel schon
            //Lösche den Agenten aus dem View
            this.view[this.pos.getX()][this.pos.getY()] = PacmanTileType.EMPTY;
        }

        //Erzeuge view as String
        sView = view2String(this.view);

    }

    public Knoten(Knoten vorgaenger, PacmanAction bewegungsRichtung){
        this.vorgaenger = vorgaenger;

        //Erzeuge neues view auf Basis des Vorgängers
        this.view = new PacmanTileType[vorgaenger.getView().length][vorgaenger.getView()[0].length];
        this.view = copyArray(vorgaenger.getView());

        //Berechne die neue Position auf Basis der Bewegungsrichtung
        this.pos = berechneNeuePosition(vorgaenger.getPos(), bewegungsRichtung);

        //Lasse dort den virtuellen Pacman das Dot fressen
        this.view[this.pos.getX()][this.pos.getY()] = PacmanTileType.EMPTY;

        //Erzeuge view as String
        sView = view2String(this.view);

    }



    public LinkedBlockingQueue<PacmanAction> getPacmanActionList() {
        return pacmanActionList;
    }

    public float getPfadkosten() {
        return pfadkosten;
    }

    public void setPfadkosten(float pfadkosten) {
        this.pfadkosten = pfadkosten;
    }

    public Knoten getVorgaenger() {
        return vorgaenger;
    }

    public void setSchaetzwert(float schaetzwert){
        this.schaetzwert = schaetzwert;
    }

    public float getSchaetzwert(){
        return this.schaetzwert;
    }

    public float getBewertung(){
        return pfadkosten + schaetzwert;
    }

    public PacmanTileType[][] getView() {
        return view;
    }

    public Vector2 getPos() {
        return pos;
    }


    String view2String(PacmanTileType[][] view){

        char[] cView = new char[this.view.length * view[0].length];
        int index = 0;
        //Erzeuge ein String für Debugzwecke
        for(int row = 0; row <  this.view.length; row++){
            for(int coloum = 0; coloum <  this.view[0].length; coloum++){
                cView[index] = this.view[row][coloum].toString().charAt(0);
                index++;
            }
        }

        return new String(cView);

    }


    //Kopieren von 2-dimensionalen Arrays
    private PacmanTileType[][] copyArray(PacmanTileType[][] view) {
        // Kopieren manuell
        PacmanTileType[][] newView = new PacmanTileType[view.length][view[0].length];

        for (int spalte = 0; spalte < view.length; spalte++) {
            for (int zeile = 0; zeile < view[0].length; zeile++) {
                newView[spalte][zeile] = view[spalte][zeile];
            }
        }

        return newView;
    }

    /**
     * Ist der Konten der Zielzustand
     *
     * @param knoten
     * @return
     */
    public boolean isZiel(Knoten knoten) {
        Knoten vergleichsKandidat = knoten;

        if (this.sView.equals(vergleichsKandidat.sView)){
            //Die Welt mit ihren Dots ist gleich, ergo Zielzustand erreicht
            //Die Position des Agenten spielt bei dieser Abfrage keine Rolle!
            return true;
        }
        return false;
    }

    /**
     * Berechne aus dem view und Pacmanposition einen Hashcode
     * @return
     */
    @Override
    public int hashCode() {


        String hashString = new String(sView);

        //Der Zielzustand kennt keinen Position
        if(this.pos != null) {
            hashString += pos.toString();
        }

        return hashString.hashCode();
    }

    //Gebe den Knoten auf der Konsole aus
    @Override
    public String toString() {
        String str = "";

        str += "[" + this.pos.getX()+"," + this.pos.getY() + "]\n";
        for (int row = 0; row < view[0].length; row++) {
            for (int coloum = 0; coloum < view.length; coloum++) {
                str += view[coloum][row] + " ";
            }
            str += "\n";
        }

        return str;
    }


    //Berechne die neue Positions des Pacmans auf Basis der Bewegungsrichtung
    Vector2 berechneNeuePosition(Vector2 vorgaengerPos, PacmanAction bewegungsrichtung){
        Vector2 nachfolgerPos = new Vector2(-1, -1);

        if(bewegungsrichtung == PacmanAction.GO_NORTH)
            nachfolgerPos.set(vorgaengerPos.getX(), vorgaengerPos.getY() - 1);
        else if(bewegungsrichtung == PacmanAction.GO_EAST)
            nachfolgerPos.set(vorgaengerPos.getX() + 1, vorgaengerPos.getY());
        else if(bewegungsrichtung == PacmanAction.GO_SOUTH)
            nachfolgerPos.set(vorgaengerPos.getX(), vorgaengerPos.getY() + 1);
        else if(bewegungsrichtung == PacmanAction.GO_WEST)
            nachfolgerPos.set(vorgaengerPos.getX() - 1, vorgaengerPos.getY());
        else
            throw new IllegalArgumentException("Unzulässige PacmanAction");

        return nachfolgerPos;
    }

    /**
     * Berechne die notwendigen actions um diesen Knoten vom Start aus zu erreichen
     */
	public void berechnePacmanActions() {
		Knoten vorgaenger = this.vorgaenger;
		Knoten aktueller = this;
		LinkedList<PacmanAction> list = new LinkedList<>();
		
		while(vorgaenger != null) {
			if (vorgaenger.pos.getY() < aktueller.pos.getY()) {
				//von Nord nach Sued
				list.add(0,PacmanAction.GO_SOUTH);
			} else if (vorgaenger.pos.getX() > aktueller.pos.getX()) {
				//von Ost nach West
				list.add(0,PacmanAction.GO_WEST);
			} else if (vorgaenger.pos.getY() > aktueller.pos.getY()) {
				//von Sued nach Nord
				list.add(0,PacmanAction.GO_NORTH);
			} else if (vorgaenger.pos.getX() < aktueller.pos.getX()) {
				//von West nach Ost
				list.add(0,PacmanAction.GO_EAST);
			}
			aktueller = vorgaenger;
			vorgaenger = vorgaenger.vorgaenger;
		}
		pacmanActionList.addAll(list);
	}

}
