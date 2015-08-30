package be.dylandeceulaer.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by dylan on 17/05/2015.
 */
public class snelheidscontrolesLoader extends AsyncTaskLoader<Cursor> {
    private Cursor myCursor;
    private static Object lock = new Object();
    private String Email;

    private final String[] mColumnNames = new String[]{
            BaseColumns._ID,
            contract.COLUMN_MAAND,
            contract.COLUMN_STRAAT,
            contract.COLUMN_GEMEENTE,
            contract.COLUMN_AANTAL_CONTROLES,
            contract.COLUMN_GEPASSEERDE_VOERTUIGEN,
            contract.COLUMN_OVERTREDINGEN,
            contract.COLUMN_X,
            contract.COLUMN_Y
    };

    public snelheidscontrolesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if(myCursor != null) deliverResult(myCursor);
        if(takeContentChanged() || myCursor == null) forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        if(myCursor == null) loadCursor();
        return myCursor;
    }

    private void loadCursor(){
        synchronized (lock){
            MatrixCursor cursor = new MatrixCursor(mColumnNames);
            InputStream input = null;
            JsonReader reader = null;

            try{
                input = new URL("http://data.kortrijk.be/snelheidsmetingen/pz_vlas.json").openStream();
                reader = new JsonReader(new InputStreamReader(input,"UTF-8"));

                int id = 0;
                reader.beginArray();
                while (reader.hasNext()){
                    reader.beginObject();

                    MAAND maand = MAAND.JANUARI;
                    String straat = "";
                    int postcode = 0;
                    String gemeente = "";
                    int aantalcontroles = 0;
                    int aantalOvertredingen = 0;
                    int aantalVoertuigen = 0;
                    float x = 0;
                    float y = 0;

                    while (reader.hasNext()){
                        String name = reader.nextName();
                        if(name.equals("Maand")){
                            maand = MAAND.get(reader.nextInt());
                        }else if(name.equals("Straat")){
                            straat = reader.nextString();
                        }else if(name.equals("Postcode")){
                            postcode = reader.nextInt();
                        }else if(name.equals("Gemeente")){
                            gemeente = reader.nextString();
                        }else if(name.equals("Aantal controles")){
                            aantalcontroles = reader.nextInt();
                        }else if(name.equals("Vtg in overtreding")){
                            aantalOvertredingen = reader.nextInt();
                        }else if(name.equals("Gepasseerde voertuigen")){
                            aantalVoertuigen = reader.nextInt();
                        }else if(name.equals("X")){
                            x = (float) reader.nextDouble();
                        }else if(name.equals("Y")){
                            y = (float) reader.nextDouble();
                        }else{
                            reader.skipValue();
                        }
                    }
                    MatrixCursor.RowBuilder row = cursor.newRow();
                    row.add(id);
                    row.add(maand);
                    row.add(straat);
                    row.add(gemeente);
                    row.add(aantalcontroles);
                    row.add(aantalVoertuigen);
                    row.add(aantalOvertredingen);
                    row.add(x);
                    row.add(y);


                    id++;
                    reader.endObject();
                }
                reader.endArray();
                myCursor = cursor;
            }catch (IOException ex){
                ex.printStackTrace();
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            try {
                input.close();
            }catch (IOException e){
            }
        }
    }

    public enum MAAND{
        JANUARI("januari",1),
        FEBRUARI("februari",2),
        MAART("maart",3),
        APRIL("april",4),
        MEI("mei",5),
        JUNI("juni",6),
        JULI("juli",7),
        AUGUSTUS("augustus",8),
        SEPTEMBER("september",9),
        OKTOBER("oktober",10),
        NOVEMBER("november",11),
        DECEMBER("december",12);

        private String naam;
        private int nummer;

        MAAND(String naam, int nummer){
            this.naam = naam;
            this.nummer = nummer;
        }

        public int getNummer() {
            return nummer;
        }

        public String getNaam() {
            return naam;
        }

        public static MAAND get(int i){
            for(MAAND ii : MAAND.values()){
                if(ii.getNummer() == i){
                    return ii;
                }
            }
            return MAART;
        }
    }

    public class Snelheidscontrole{
        private MAAND maand;
        private String straat;
        private int postcode;
        private String gemeente;
        private int aantalcontroles;
        private int aantalOvertredingen;
        private int aantalVoertuigen;
        private float x;
        private float y;


        public Snelheidscontrole(MAAND maand, String straat, String gemeente, int aantalcontroles, int aantalOvertredingen, int aantalVoertuigen, float x, float y) {
            this.maand = maand;
            this.straat = straat;
            this.gemeente = gemeente;
            this.aantalcontroles = aantalcontroles;
            this.aantalOvertredingen = aantalOvertredingen;
            this.aantalVoertuigen = aantalVoertuigen;
            this.x = x;
            this.y = y;
        }

        public MAAND getMaand() {
            return maand;
        }

        public String getStraat() {
            return straat;
        }

        public int getPostcode() {
            return postcode;
        }

        public String getGemeente() {
            return gemeente;
        }

        public int getAantalcontroles() {
            return aantalcontroles;
        }

        public int getAantalOvertredingen() {
            return aantalOvertredingen;
        }

        public int getAantalVoertuigen() {
            return aantalVoertuigen;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

}
