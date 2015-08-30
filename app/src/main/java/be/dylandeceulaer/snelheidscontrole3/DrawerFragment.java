package be.dylandeceulaer.snelheidscontrole3;


import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import be.dylandeceulaer.loader.contract;
import be.dylandeceulaer.loader.snelheidscontrolesLoader;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor origineleData;
    private Cursor cursor;
    private SnelheidsControleAdapter sAdapter;
    onItemPosListener mMainActivity;

    public interface onItemPosListener{
        public void onItemPosClick(float x, float y,String title,String Snippet);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (onItemPosListener) activity;
    }

    public DrawerFragment() {
        // Required empty public constructor
    }

    public void FilterByMonth(snelheidscontrolesLoader.MAAND maand){
        if(sAdapter.getCursor() != null)
        sAdapter.swapCursor(filterByMonth(maand));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MatrixCursor obj = (MatrixCursor) sAdapter.getItem(position);

        float x = obj.getFloat(obj.getColumnIndex(contract.COLUMN_X));
        float y = obj.getFloat(obj.getColumnIndex(contract.COLUMN_Y));
        String titel = "Aantal controles in "+obj.getString(obj.getColumnIndex(contract.COLUMN_STRAAT))+": "+obj.getInt(obj.getColumnIndex(contract.COLUMN_AANTAL_CONTROLES)) ;
        int ov = obj.getInt(obj.getColumnIndex(contract.COLUMN_OVERTREDINGEN));
        int aantal = obj.getInt(obj.getColumnIndex(contract.COLUMN_GEPASSEERDE_VOERTUIGEN));
        float ratio = (float)ov / (float) aantal;

        String sub = "Overtredingsgraad: "+ratio*100+"%";

        mMainActivity.onItemPosClick(x,y,titel,sub);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sAdapter = new SnelheidsControleAdapter(getActivity(),R.layout.cel_drawer,null,new String[]{
                contract.COLUMN_STRAAT,
                contract.COLUMN_GEMEENTE,
                contract.COLUMN_MAAND
        },new int[]{
                R.id.textViewStraat,
                R.id.textViewGemeente,
                R.id.textViewMaand
        },0);
        setListAdapter(sAdapter);
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_drawer, container, false);



        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new snelheidscontrolesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        sAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        sAdapter.swapCursor(null);
    }


    class SnelheidsControleAdapter extends SimpleCursorAdapter {


        public SnelheidsControleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {

            super(context, layout, c, from, to, flags);
            cursor = c;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            int indexGepasseerd = cursor.getColumnIndex(contract.COLUMN_GEPASSEERDE_VOERTUIGEN);
            int indexOvertredingen = cursor.getColumnIndex(contract.COLUMN_OVERTREDINGEN);
            int indexControles = cursor.getColumnIndex(contract.COLUMN_AANTAL_CONTROLES);
            int gepasseerd = cursor.getInt(indexGepasseerd);
            int overtredingen = cursor.getInt(indexOvertredingen);
            int controles = cursor.getInt(indexControles);
            float ratio = (float)overtredingen/(float)gepasseerd;

            if(ratio > 0.3)
                view.setBackgroundColor(Color.RED);
            else if(ratio > 0.2)
                view.setBackgroundColor(Color.parseColor("#ffa500"));
            else
                view.setBackgroundColor(Color.GREEN);


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            if(cursor != null){
                ViewHolder holder = (ViewHolder) row.getTag();

                if(holder == null){
                    holder = new ViewHolder(row);
                    row.setTag(holder);
                }

                TextView textViewStraat = holder.textViewStraat;
                textViewStraat.setText(cursor.getString(cursor.getColumnIndex(contract.COLUMN_STRAAT)));

                TextView textViewGemeente = holder.textViewGemeente;
                textViewGemeente.setText(cursor.getString(cursor.getColumnIndex(contract.COLUMN_GEMEENTE)));

                TextView textViewControles = holder.textViewControles;
                textViewControles.setText("# controles: "+cursor.getInt(cursor.getColumnIndex(contract.COLUMN_AANTAL_CONTROLES)));

                TextView textViewMaand = holder.textViewMaand;
                textViewMaand.setText(cursor.getString(cursor.getColumnIndex(contract.COLUMN_MAAND)));

            }
            return row;
        }
    }

    public Cursor filterByMonth(snelheidscontrolesLoader.MAAND maand){
        if(origineleData == null) origineleData = sAdapter.getCursor();



        final String[] mColumnNames = new String[]{
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

        MatrixCursor newCursor = new MatrixCursor(mColumnNames);

        int indexID = origineleData.getColumnIndex(BaseColumns._ID);
        int indexGepasseerd = origineleData.getColumnIndex(contract.COLUMN_GEPASSEERDE_VOERTUIGEN);
        int indexOvertredingen = origineleData.getColumnIndex(contract.COLUMN_OVERTREDINGEN);
        int indexControles = origineleData.getColumnIndex(contract.COLUMN_AANTAL_CONTROLES);
        int indexStraat = origineleData.getColumnIndex(contract.COLUMN_STRAAT);
        int indexGemeente = origineleData.getColumnIndex(contract.COLUMN_GEMEENTE);
        int indexMaand = origineleData.getColumnIndex(contract.COLUMN_MAAND);
        int indexX = origineleData.getColumnIndex(contract.COLUMN_X);
        int indexY = origineleData.getColumnIndex(contract.COLUMN_Y);

        if(origineleData.moveToFirst()){
            do{
                String m = origineleData.getString(indexMaand);


                if(origineleData.getString(indexMaand).toLowerCase().equals(maand.getNaam())){
                    MatrixCursor.RowBuilder row = newCursor.newRow();
                    row.add(origineleData.getInt(indexID));
                    row.add(origineleData.getString(indexMaand));
                    row.add(origineleData.getString(indexStraat));
                    row.add(origineleData.getString(indexGemeente));
                    row.add(origineleData.getInt(indexControles));
                    row.add(origineleData.getInt(indexGepasseerd));
                    row.add(origineleData.getInt(indexOvertredingen));
                    row.add(origineleData.getFloat(indexX));
                    row.add(origineleData.getFloat(indexY));
                }
            }while (origineleData.moveToNext());
        }


        return newCursor;
    }


    class ViewHolder {
        public TextView textViewStraat = null;
        public TextView textViewGemeente = null;
        public TextView textViewControles = null;
        public TextView textViewMaand = null;

        public ViewHolder(View row) {
            this.textViewStraat = (TextView) row.findViewById(R.id.textViewStraat);
            this.textViewGemeente = (TextView) row.findViewById(R.id.textViewGemeente);
            this.textViewControles = (TextView) row.findViewById(R.id.textViewControles);
            this.textViewMaand = (TextView) row.findViewById(R.id.textViewMaand);
        }

    }
}
