package cl.telematica.android.usmvende;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 27-10-2016.
 */

public class Comprador extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManagerComprador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //comentar cuando sea la lista dinamica
        recyclerView.setHasFixedSize(true);
        layoutManagerComprador = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,
                                                        false);
        recyclerView.setLayoutManager(layoutManagerComprador);
        adapter = new MyAdapterComprador(getListProduct());
        recyclerView.setAdapter(adapter);
    }

    public List<Producto> getListProduct(){
        List<Producto> producto = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            Producto  p = new Producto();
            p.setDescripcion("Producto "+i+" es muy bonito");
            p.setNombreP("Producto"+ i);
            p.setPrecio("$22"+i);
            producto.add(p);
        }
        return producto;
    }
}
