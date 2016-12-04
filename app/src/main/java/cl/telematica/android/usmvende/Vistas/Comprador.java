package cl.telematica.android.usmvende.Vistas;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import cl.telematica.android.usmvende.Adapters.PagerAdapter;
import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.Adapters.MyAdapterComprador;
import cl.telematica.android.usmvende.R;


public class Comprador extends AppCompatActivity {


   /* private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManagerComprador;*/
    private ArrayList<Producto> producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador2);

      /*  recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //comentar cuando sea la lista dinamica
        recyclerView.setHasFixedSize(true);
        layoutManagerComprador = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManagerComprador);
        adapter = new MyAdapterComprador(this,getListProduct());
        recyclerView.setAdapter(adapter);*/
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
      /*  Intent intent = new Intent(this, BlankFragment.class);
        //intent.putParcelableArrayListExtra("mykey", producto);
        Producto producto  = new Producto();
        producto.setNombreP("auto");
        producto.setDescripcion("lindo");
        producto.setPrecio("1234");
        producto.setNombreV("lula");
        intent.putExtra("data", producto);*/
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                                    Comprador.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
    }
    @Override
    public void onResume(){
        super.onResume();
    }


   /* public List<Producto> getListProduct(){
        producto = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            Producto  p = new Producto();
            p.setDescripcion("Producto "+i+" es muy bonito");
            p.setNombreP("Producto"+ i);
            p.setFav("NO");
            p.setPrecio("$22"+i);
            producto.add(p);
        }
        return producto;
    }*/

}
