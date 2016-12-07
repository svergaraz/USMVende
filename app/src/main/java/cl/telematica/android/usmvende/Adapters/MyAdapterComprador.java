package cl.telematica.android.usmvende.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cl.telematica.android.usmvende.EnvioData;
import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.R;


public class MyAdapterComprador extends RecyclerView.Adapter<MyAdapterComprador.ViewHolder> {

    private List<Producto> producto;
    private Activity activity;
    private int swValue ;
    String mlatitude="buscando",mlongitude="buscando";

    public MyAdapterComprador(Activity activity, List<Producto> producto){
        this.producto = producto;
        this.activity = activity;
    }

    public void actualizarUbicacion(String lat, String lng){
            this.mlatitude = lat;
            this.mlongitude = lng;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //CardView cv;
        private TextView productName;
        private TextView productDesc;
        private TextView productPrecio;
        private Switch sw;
        private View container;

        public ViewHolder (View itemView){
            super(itemView);
            // cv = (CardView)itemView.findViewById(R.id.cv);
            container = itemView.findViewById(R.id.cv);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            sw = (Switch) itemView.findViewById(R.id.switch1);
            //productDesc = (TextView)itemView.findViewById(R.id.product_description);
            productPrecio =(TextView)itemView.findViewById(R.id.product_precio);
        }
    }

    @Override
    public int getItemCount() {return producto.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_view_comprador, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Producto produc = producto.get(position);
        holder.productName.setText(produc.getNombreP());
       // holder.productDesc.setText(produc.getDescripcion());
        holder.productPrecio.setText(produc.getPrecio());
        holder.container.setOnClickListener(onClickListener(position));

        if(swValue == 1){
            holder.sw.setClickable(false);
        }
        else {
            holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        EnvioData envio = new EnvioData(produc.getNombreP(), activity);
                        System.out.println(mlatitude+","+mlongitude);
                        envio.send(mlatitude,mlongitude);

                    }
                    else{
                        EnvioData envio2 = new EnvioData(produc.getNombreP(),activity);
                        envio2.sendNovender();
                    }

                }
            });
        }
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.conteiner_cell);
                //dialog.setTitle("Vendedor" + position);
                dialog.setTitle(producto.get(position).getNombreV());
                dialog.setCancelable(true);

                TextView productName = (TextView) dialog.findViewById(R.id.product_name2);
                TextView productDesc = (TextView) dialog.findViewById(R.id.product_description2);
                TextView productPrecio = (TextView) dialog.findViewById(R.id.product_precio2);
                //View btnFav = dialog.findViewById(R.id.btn_ok);
                productName.setText(producto.get(position).getNombreP());
                productPrecio.setText(producto.get(position).getPrecio());
                productDesc.setText(producto.get(position).getDescripcion());

                View btnFav = dialog.findViewById(R.id.btn_ok);
                btnFav.setOnClickListener(onFavoriteListener(position));

                dialog.show();
            }
        };
    }

    private View.OnClickListener onFavoriteListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (producto.get(position).getFav() == "0") {
                    producto.get(position).setFav("1");
                    EnvioData envio = new EnvioData(producto.get(position).getNombreP(),producto.get(position).getNombreV(), activity);
                    envio.sendFav();
                }
                else {
                    producto.get(position).setFav("0");
                }
                Toast.makeText(activity, producto.get(position).getFav(), Toast.LENGTH_SHORT).show();
            }


        };
    }

   /* @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/
    public void setSwitch(int swValue){
        this.swValue = swValue;
    }

}
